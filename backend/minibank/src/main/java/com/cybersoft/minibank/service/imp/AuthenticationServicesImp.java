package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.UserCreatedEvent;
import com.cybersoft.minibank.dto.LogInDTO;
import com.cybersoft.minibank.dto.RegisterDTO;
import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.dto.UserSessionDTO;
import com.cybersoft.minibank.entity.BankAccountEntity;
import com.cybersoft.minibank.entity.RoleEntity;
import com.cybersoft.minibank.entity.UserEntity;
import com.cybersoft.minibank.exception.InvalidNotValueUserException;
import com.cybersoft.minibank.exception.InvalidUserException;
import com.cybersoft.minibank.exception.InvalidUserRegisterException;
import com.cybersoft.minibank.mapper.UserMapper;
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.request.RegisterRequest;
import com.cybersoft.minibank.payload.request.VerifyRequest;
import com.cybersoft.minibank.repository.BankAccountRepository;
import com.cybersoft.minibank.repository.RefreshTokenRepository;
import com.cybersoft.minibank.repository.RoleRepostitory;
import com.cybersoft.minibank.repository.UserRepository;
import com.cybersoft.minibank.service.*;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthenticationServicesImp implements AuthenticationServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtilHelper jwtHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RoleRepostitory roleRepostitory;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private KafkaProducerService  kafkaProducerService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BacklistService backlistService;

    @Autowired
    private ObjectMapper objectMapper;

    // Regex: Ít nhất 1 hoa, 1 thường, 1 số, 1 ký tự đặc biệt, tối thiểu 8 ký tự
    private final String COMMON_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Transactional
    @Override
    public LogInDTO login(LoginRequest loginRequest, HttpServletRequest request) {
        UserEntity user = userRepository.findByUserName(loginRequest.getUsername())
                .orElseThrow(() -> new InvalidUserException("Không tìm thấy Người dùng"));

        String currentIp = request.getRemoteAddr();
        String currentDeviceId = loginRequest.getDeviceId();

        UserSessionDTO oldSession = sessionService.getSession(user.getUserName());

        if (oldSession != null) {

            boolean sameIp = oldSession.getIpAddress().equals(currentIp);

            boolean sameDevice = oldSession.getDeviceId().equals(currentDeviceId);

            // khác ip/device
//            if (!sameIp || !sameDevice) {
//
//                throw new InvalidUserException(
//                        "Tài khoản đang đăng nhập trên thiết bị khác"
//                );
//            }

            if (!sameIp || !sameDevice) {

                // blacklist access token cũ

                backlistService.blacklistToken(
                        oldSession.getAccessToken(),
                        15
                );

                // xóa refresh token cũ

                refreshTokenRepository.deleteByUser(user);

                // xóa session cũ

                sessionService.deleteSession(
                        user.getUserName()
                );
            }

            backlistService.blacklistToken(oldSession.getAccessToken(), 15);
        }

        // check khóa vĩnh viễn
        if (user.getStatus().equals("LOCKED")) {
            throw new InvalidUserException("Tài khoản đã bị khóa vĩnh viễn");
        }

        String lockKey = "LOCK:" + user.getEmail();

        // check lock Redis
        if (redisService.isLocked(lockKey)) {
            throw new InvalidUserException("Tài khoản bị khóa 15 phút");
        }

        // check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

            int attempts = user.getFailedLoginAttempt() + 1;
            user.setFailedLoginAttempt(attempts);

            if (attempts >= 3) {
                redisService.setLock(lockKey, 15);

                user.setFailedLoginAttempt(0);

                int lockCount = user.getLockCount() + 1;
                user.setLockCount(lockCount);

                if (lockCount >= 3) {
                    user.setStatus("LOCKED");
                }
            }

            userRepository.save(user);
            throw new InvalidUserException("Sai mật khẩu");
        }

        // reset khi login thành công
        user.setFailedLoginAttempt(0);
        user.setLockCount(0);
        userRepository.save(user);

        UserDTO userDTO = UserMapper.mapDTO(user);
        userDTO.setUsername(loginRequest.getUsername());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String data = objectMapper.writeValueAsString(userDTO);

            String refreshToken = refreshTokenService.createRefreshToken(user.getUserName(),currentDeviceId);
            String accessToken = jwtHelper.generateToken(data);

            UserSessionDTO session =
                    new UserSessionDTO(
                            user.getUserName(),
                            currentIp,
                            currentDeviceId,
                            accessToken,
                            refreshToken,
                            System.currentTimeMillis()
                    );

            sessionService.saveSession(user.getUserName(), session);
            return new LogInDTO(accessToken,refreshToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidNotValueUserException("Lỗi tạo token: " + e.getMessage());
        }
    }

    // Dang ky
    @Override
    public String register(RegisterRequest registerRequest) {
        // Kiểm tra trùng email
        if(userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new InvalidUserRegisterException( "Email đã tồn tại");
        }

        if(userRepository.findByIdentityNumber(registerRequest.getIdentityNumber()).isPresent()){
            throw new InvalidUserRegisterException( "CCCD đã tồn tại");
        }

        if(userRepository.findByPhone(registerRequest.getPhone()).isPresent()){
            throw new RuntimeException("Số Điện thoại đã tồn tại");
        }

        String cccdKey = "TEMP_CCCD:" + registerRequest.getIdentityNumber();

        if (redisService.exists(cccdKey)) {
            throw new InvalidUserRegisterException(
                    "CCCD đang trong quá trình đăng ký, vui lòng hoàn tất xác thực OTP"
            );
        }

//        if (redisService.isLocked("LOCK_REG:" + registerRequest.getEmail())) {
//            throw new InvalidUserRegisterException("Vui lòng đợi 5 phút trước khi yêu cầu mã mới");
//        }

        // Sinh mã 8 ký tự (Chữ + Số)
        String randomCode = generateRandomAlphaNumeric(8);
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail(registerRequest.getEmail());
        registerDTO.setPassword(randomCode);
        registerDTO.setFullName(registerRequest.getFullName());
        registerDTO.setGender(registerRequest.getGender());
        registerDTO.setPhone(registerRequest.getPhone());
        registerDTO.setDateOfBirth(registerRequest.getDateOfBirth());
        registerDTO.setIdentityNumber(registerRequest.getIdentityNumber());
        registerDTO.setAddress(registerRequest.getAddress());

        try {
            String jsonRegisterDTO = objectMapper.writeValueAsString(registerDTO);

            // 4. Lưu dữ liệu tạm và đặt Lock 5 phút
            // Lưu data người dùng
            redisService.save(cccdKey, registerRequest.getEmail(),5);
            redisService.save("TEMP_USER:" + registerRequest.getEmail(), jsonRegisterDTO,5);
            // Đặt lock để hiệu lực trong 5 phút
            redisService.setLock("LOCK_REG:" + registerRequest.getEmail(), 5);

            // 5. Gửi Kafka
            UserCreatedEvent event = new UserCreatedEvent(
                    registerRequest.getEmail(),
                    randomCode
            );
            kafkaProducerService.sendUserCreatedEvent(event);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý dữ liệu" +e.getMessage());
        }
        return "Mã xác thực đã được gửi!";
    }

    // Xac thuc dang ky
    @Transactional
    @Override
    public String verifyPassword(VerifyRequest verifyRequest) {
        // Lấy mail từ redis
        String jsonData = redisService.get("TEMP_USER:" + verifyRequest.getEmail());

        // Nếu Redis tự xóa sau 5 phút, jsonData sẽ null
        if (jsonData == null) {
            throw new InvalidUserRegisterException("Mã xác thực đã hết hạn hoặc không tồn tại");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            RegisterDTO tempUser = mapper.readValue(jsonData, RegisterDTO.class);

            // So khớp mã khách nhập và mã mình đã lưu trong redis
            if (tempUser.getPassword().equals(verifyRequest.getOldPassword())) {
                // Lưu chính thức vào Database
                UserEntity user = new UserEntity();
                user.setUserName(verifyRequest.getUserName());
                user.setEmail(tempUser.getEmail());
                user.setPassword(passwordEncoder.encode(verifyRequest.getNewPassword()));
                user.setFullName(tempUser.getFullName());
                user.setGender(tempUser.getGender());
                user.setPhone(tempUser.getPhone());
                user.setDateOfBirth(tempUser.getDateOfBirth());
                user.setIdentityNumber(tempUser.getIdentityNumber());
                user.setAddress(tempUser.getAddress());

                String accountNumber = generateAccountNumber();
                BankAccountEntity bankAccount = new BankAccountEntity();
                bankAccount.setAccountNumber(accountNumber);
                bankAccount.setAccountType("PAYMENT");
                bankAccount.setBalance(BigDecimal.ZERO);
                bankAccount.setDailyTransferLimit(new BigDecimal("10000000"));
                bankAccount.setStatus("ACTIVE");
                bankAccount.setCurrency("VND");
                bankAccount.setUserEntity(user);
                bankAccount.setCreatedAt(LocalDateTime.now());

                RoleEntity defaultRole = roleRepostitory.findByName("ROLE_USER")
                        .orElseThrow(() -> new InvalidUserRegisterException("Lỗi: Không tìm thấy Role mặc định trong hệ thống"));
                user.setRoleEntity(defaultRole);
                userRepository.save(user);
                bankAccountRepository.save(bankAccount);

                // Dọn dẹp Redis
                redisService.delete("TEMP_USER:" + verifyRequest.getEmail());
                redisService.delete("LOCK_REG:" + verifyRequest.getEmail());
                redisService.delete("TEMP_CCCD:" + tempUser.getIdentityNumber());
                return "Đăng ký thành công!";
            }
        } catch (Exception e) {
            throw new InvalidNotValueUserException("Lỗi đọc dữ liệu xác thực" + e.getMessage());
        }

        // Ném lỗi sai mã xác thực
        throw new InvalidUserRegisterException("Mã xác thực sai!");
    }

    @Override
    @Transactional
    public String logout() {
        // 1. Lấy "thẻ" Authentication từ kho lưu trữ của Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 2. Kiểm tra nếu thẻ tồn tại và đã được xác thực
        if (auth != null && auth.isAuthenticated()) {

            String username = auth.getName();

            // 3. Tìm UserEntity dựa trên username lấy được
            UserEntity user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại trong hệ thống"));

            // 4. Thực hiện xóa Refresh Token theo ID của User
            refreshTokenRepository.deleteByUserId(user.getId());

            return "Đăng xuất thành công";
        }

        throw new RuntimeException("Không tìm thấy thông tin xác thực");
    }

    private String generateRandomAlphaNumeric(int length) {
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return sb.toString();
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "9704" + (1000000000L + new SecureRandom().nextInt(900000000));
        } while (bankAccountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
