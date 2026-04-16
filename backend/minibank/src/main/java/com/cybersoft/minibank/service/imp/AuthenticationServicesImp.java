package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.LogInDTO;
import com.cybersoft.minibank.dto.RegisterDTO;
import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.entity.RoleEntity;
import com.cybersoft.minibank.entity.UserEntity;
import com.cybersoft.minibank.exception.InvalidNotValueUserException;
import com.cybersoft.minibank.exception.InvalidUserException;
import com.cybersoft.minibank.exception.InvalidUserRegisterException;
import com.cybersoft.minibank.mapper.UserMapper;
import com.cybersoft.minibank.payload.request.UpdatePasswordRequest;
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.request.RegisterRequest;
import com.cybersoft.minibank.repository.RoleRepostitory;
import com.cybersoft.minibank.repository.UserRepository;
import com.cybersoft.minibank.service.AuthenticationServices;
import com.cybersoft.minibank.service.EmailService;
import com.cybersoft.minibank.service.RedisService;
import com.cybersoft.minibank.service.RefreshTokenService;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServicesImp implements AuthenticationServices {

    @Autowired
    private UserRepository userRepository;

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
    private KafkaTemplate <String, String> kafkaTemplate;

    // Regex: Ít nhất 1 hoa, 1 thường, 1 số, 1 ký tự đặc biệt, tối thiểu 8 ký tự
    private final String COMMON_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    public LogInDTO login(LoginRequest loginRequest) {
        UserEntity user = userRepository.findByUserName(loginRequest.getUsername())
                .orElseThrow(() -> new InvalidUserException("Không tìm thấy Người dùng"));

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

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(userDTO);

            String refreshToken = refreshTokenService.createRefreshToken(user.getUserName());
            String accessToken = jwtHelper.generateToken(data);

            return new LogInDTO(accessToken,refreshToken);
        } catch (Exception e) {
            throw new InvalidNotValueUserException("Lỗi tạo token");
        }
    }

    // Dang ky
    @Override
    public String register(RegisterRequest registerRequest) {
        // Kiểm tra trùng email
        if(userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new InvalidUserRegisterException( "Email đã tồn tại");
        }if (userRepository.existsByUserName(registerRequest.getUserName())) {
            throw new InvalidUserRegisterException("Username đã tồn tại");
        }if (redisService.isLocked("LOCK_REG:" + registerRequest.getEmail())) {
            throw new InvalidUserRegisterException("Vui lòng đợi 5 phút trước khi yêu cầu mã mới");
        }if (!registerRequest.getUserName().matches(COMMON_PATTERN)) {
            throw new InvalidUserException("UserName ko hợp lệ! Phải bao gồm chữ hoa, chữ thường, số, ký tự đặc biệt và ít nhất 8 ký tự.");
        }

        // Sinh mã 8 ký tự (Chữ + Số)
        String randomCode = generateRandomAlphaNumeric(8);
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail(registerRequest.getEmail());
        registerDTO.setUsername(registerRequest.getUserName());
        registerDTO.setPassword(randomCode);
        registerDTO.setFullName(registerRequest.getFullName());
        registerDTO.setGender(registerRequest.getGender());
        registerDTO.setPhone(registerRequest.getPhone());
        registerDTO.setDateOfBirth(registerRequest.getDateOfBirth());
        registerDTO.setIdentityNumber(registerRequest.getIdentityNumber());
        registerDTO.setAddress(registerRequest.getAddress());

        try {
            // Chuyển Object thành String JSON để khớp với RedisService của bạn
            ObjectMapper mapper = new ObjectMapper();
            String jsonRegisterDTO = mapper.writeValueAsString(registerDTO);

            // 4. Lưu dữ liệu tạm và đặt Lock 5 phút
            // Lưu data người dùng
            redisService.save("TEMP_USER:" + registerRequest.getEmail(), jsonRegisterDTO);
            // Đặt lock để hiệu lực trong 5 phút
            redisService.setLock("LOCK_REG:" + registerRequest.getEmail(), 5);

            // 5. Gửi Kafka
            Map<String, String> emailData = new HashMap<>();
            emailData.put("email", registerRequest.getEmail());
            emailData.put("password", randomCode);
            kafkaTemplate.send("password-mail-topic", mapper.writeValueAsString(emailData))
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            // Log the error but maybe don't stop the whole registration if you have a backup plan
                            System.err.println("Kafka failed to send: " + ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý dữ liệu");
        }
        return "Mã xác thực đã được gửi!";
    }

    // Xac thuc dang ky
    @Transactional
    @Override
    public String verifyPassword(String email, String userInputOtp) {
        // "Gọi" dữ liệu từ Map ra dựa trên email
        String jsonData = redisService.get("TEMP_USER:" + email);

        // Nếu Redis tự xóa sau 5 phút, jsonData sẽ null
        if (jsonData == null) {
            throw new InvalidUserRegisterException("Mã xác thực đã hết hạn hoặc không tồn tại");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            RegisterDTO tempUser = mapper.readValue(jsonData, RegisterDTO.class);

            // So khớp mã khách nhập và mã mình đã lưu lúc nãy
            if (tempUser.getPassword().equals(userInputOtp)) {
                // Lưu chính thức vào Database
                UserEntity user = new UserEntity();
                user.setUserName(tempUser.getUsername());
                user.setEmail(tempUser.getEmail());
                user.setPassword(passwordEncoder.encode(tempUser.getPassword()));
                user.setFullName(tempUser.getFullName());
                user.setGender(tempUser.getGender());
                user.setPhone(tempUser.getPhone());
                user.setDateOfBirth(tempUser.getDateOfBirth());
                user.setIdentityNumber(tempUser.getIdentityNumber());
                user.setAddress(tempUser.getAddress());

                RoleEntity defaultRole = roleRepostitory.findByName("ROLE_USER")
                        .orElseThrow(() -> new InvalidUserRegisterException("Lỗi: Không tìm thấy Role mặc định trong hệ thống"));
                user.setRoleEntity(defaultRole);
                userRepository.save(user);

                // Dọn dẹp Redis
                redisService.delete("TEMP_USER:" + email);
                redisService.delete("LOCK_REG:" + email);

                return "Đăng ký thành công!";
            }
        } catch (Exception e) {
            throw new InvalidNotValueUserException("Lỗi đọc dữ liệu xác thực");
        }

        // Ném lỗi sai mã xác thực
        throw new InvalidUserRegisterException("Mã xác thực sai!");
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
    //
    @Override
    public String updatePassword(UpdatePasswordRequest request) {
        // 1. Kiểm tra User tồn tại
        UserEntity user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new InvalidUserException("Không tìm thấy user"));

        // 2. Kiểm tra định dạng mật khẩu mới (Regex)
        if (request.getNewPassword() == null || !request.getNewPassword().matches(COMMON_PATTERN)) {
            throw new InvalidUserException("Mật khẩu không hợp lệ! Phải bao gồm chữ hoa, chữ thường, số, ký tự đặc biệt và ít nhất 8 ký tự.");
        }

        // 3. (Tùy chọn) Kiểm tra bảo mật: Mật khẩu mới không được trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidUserException("Mật khẩu mới không được giống mật khẩu cũ!");
        }

        // 4. Mã hóa và lưu vào Database
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "Đổi mật khẩu thành công";
    }
}
