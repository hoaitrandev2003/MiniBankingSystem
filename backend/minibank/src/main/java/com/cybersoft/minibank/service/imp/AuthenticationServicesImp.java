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
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.request.RegisterRequest;
import com.cybersoft.minibank.repository.RoleRepostitory;
import com.cybersoft.minibank.repository.UserRepository;
import com.cybersoft.minibank.service.AuthenticationServices;
import com.cybersoft.minibank.service.EmailService;
import com.cybersoft.minibank.service.RedisService;
import com.cybersoft.minibank.service.RefreshTokenService;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
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

    private Map<String, RegisterDTO> tempUserStorage = new HashMap<>();

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

    @Override
    public String register(RegisterRequest registerRequest) {
        // Kiểm tra trùng email
        if(userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new InvalidUserRegisterException( "Email đã tồn tại");
        }if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new InvalidUserRegisterException("Username đã tồn tại");
        }

        // Sinh mã 8 ký tự (Chữ + Số)
        String randomCode = generateRandomAlphaNumeric(8);
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail(registerRequest.getEmail());
        registerDTO.setUsername(registerRequest.getUsername());
        registerDTO.setPassword(randomCode);

        // Lưu vào bộ nhớ tạm
        tempUserStorage.put(registerDTO.getEmail(), registerDTO);

        //Gửi tin nhắn sang Kafka (Để Service Mail tự lo việc gửi)
        Map<String, String> emailData = new HashMap<>();
        emailData.put("email", registerRequest.getEmail());
        emailData.put("password", randomCode);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(emailData);
        kafkaTemplate.send("password-mail-topic", jsonPayload);

        return "Mã xác thực đã được gửi!";
    }

    @Override
    public String verifyPassword(String email, String userInputOtp) {
        // "Gọi" dữ liệu từ Map ra dựa trên email
        RegisterDTO tempUser = tempUserStorage.get(email);

        if (tempUser == null) throw new InvalidUserRegisterException("Yêu cầu không tồn tại hoặc đã quá hạn");

        // So khớp mã khách nhập và mã mình đã lưu lúc nãy
        if (tempUser.getPassword().equals(userInputOtp)) {
            // Lưu chính thức vào Database
            UserEntity user = new UserEntity();
            user.setUserName(tempUser.getUsername());
            user.setEmail(tempUser.getEmail());
            user.setPassword(passwordEncoder.encode(tempUser.getPassword()));

            RoleEntity defaultRole = roleRepostitory.findByName("ROLE_USER")
                    .orElseThrow(() -> new InvalidUserRegisterException("Lỗi: Không tìm thấy Role mặc định trong hệ thống"));
            user.setRoleEntity(defaultRole);
            userRepository.save(user);

            // Xóa khỏi bộ nhớ tạm
            tempUserStorage.remove(email);
            return "Đăng ký thành công!";
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

}
