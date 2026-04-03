package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.RegisterDTO;
import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.dto.VerifyDTO;
import com.cybersoft.minibank.entity.UserEntity;
import com.cybersoft.minibank.exception.InvalidUserException;
import com.cybersoft.minibank.mapper.UserMapper;
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.response.BaseResponse;
import com.cybersoft.minibank.repository.UserRepository;
import com.cybersoft.minibank.service.AuthenticationServices;
import com.cybersoft.minibank.service.EmailService;
import com.cybersoft.minibank.service.OtpService;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServicesImp implements AuthenticationServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtilHelper jwtHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    private BaseResponse baseResponse = new BaseResponse();

    private Map<String, RegisterDTO> tempUserStorage = new HashMap<>();

    @Override
    public UserDTO login(LoginRequest loginRequest) {
        UserEntity user = userRepository.findByUserName(loginRequest.getUsername())
                .orElseThrow(()-> {
                    throw new InvalidUserException("Không tìm thấy Người dùng");
                });
        if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            return UserMapper.mapDTO(user);
        }else {
            throw new InvalidUserException("Đăng nhập thất bại");
        }
    }

    public String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    @Override
    public String register(RegisterDTO registerDTO) {

        UserEntity existingUser = userRepository.findByEmail(registerDTO.getEmail());
        if(existingUser != null){
           return "Email đã tồn tại";
        }

        // Tạo OTP
        String otp = generateOtp();
        otpService.saveOtp(registerDTO.getEmail(), otp);  // Lưu OTP đúng email

        // Gửi mail
        String result = emailService.sendSimpleMail(registerDTO.getEmail(), otp);
        if(result.equals("ERROR")){
            throw new RuntimeException("Gửi mail thất bại");
        }

        // Lưu tạm user
        RegisterDTO tempUser = new RegisterDTO();
        tempUser.setUsername(registerDTO.getUsername());
        tempUser.setEmail(registerDTO.getEmail());
        tempUser.setPassword(registerDTO.getPassword());
        tempUserStorage.put(registerDTO.getEmail(), tempUser);
        System.out.println("TempUserStorage: " + tempUserStorage.get(registerDTO.getEmail()));

        return "OTP đã được gửi về email";
    }

    @Override
    public String verifyOtp(VerifyDTO verifyDTO) {

        String storedOtp = otpService.getOtp(verifyDTO.getEmail());

        System.out.println("TempUserStorage: " + tempUserStorage.get(verifyDTO.getEmail()));

        //Kiểm tra otp
        if(storedOtp == null || !storedOtp.equals(verifyDTO.getOtp())){
            throw new RuntimeException("OTP không đúng");
        }

        RegisterDTO tempUser = tempUserStorage.get(verifyDTO.getEmail());

        // Kiểm tra xem còn lưu trong cái map ko
        if (tempUser == null) {
            throw new RuntimeException("Không tìm thấy dữ liệu");
        }

        // Thêm user vào database
        UserEntity user = new UserEntity();
        user.setEmail(tempUser.getEmail());
        user.setUserName(tempUser.getUsername());
        user.setPassword(passwordEncoder.encode(tempUser.getPassword()));

        userRepository.save(user);

        // Xóa OTP + dữ liệu tạm
        otpService.removeOtp(verifyDTO.getEmail());
        tempUserStorage.remove(verifyDTO.getEmail());

        return "Đăng ký thành công";
    }

}
