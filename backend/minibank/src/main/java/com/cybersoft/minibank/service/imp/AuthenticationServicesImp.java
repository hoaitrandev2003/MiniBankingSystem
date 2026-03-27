package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.LoginDTO;
import com.cybersoft.minibank.dto.RegisterDTO;
import com.cybersoft.minibank.entity.UserEntity;
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

    private Map<String, RegisterDTO> tempUserStorage = new HashMap<>();

    @Override
    public LoginDTO login(String username, String password) {
        //Kiểm tra xem có trong database hay không
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserEntity user = userRepository.findByUserName(username);

        String jwt = jwtHelper.generateToken(username);

        LoginDTO loginDto = new LoginDTO();
        loginDto.setUsername(username);
        loginDto.setToken(jwt);

        return loginDto;
    }

    public String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    @Override
    public String register(String email, String username, String password) {

        UserEntity existingUser = userRepository.findByEmail(email);
        if(existingUser != null){
           return "Email đã tồn tại";
        }

        // Tạo OTP
        String otp = generateOtp();
        otpService.saveOtp(email, otp);  // Lưu OTP đúng email

        // Gửi mail
        String result = emailService.sendSimpleMail(email, otp);
        if(result.equals("ERROR")){
            throw new RuntimeException("Gửi mail thất bại");
        }

        // Lưu tạm user
        RegisterDTO tempUser = new RegisterDTO();
        tempUser.setUsername(username);
        tempUser.setEmail(email);
        tempUser.setPassword(password);
        tempUserStorage.put(email, tempUser);
        System.out.println("TempUserStorage: " + tempUserStorage.get(email));

        return "OTP đã được gửi về email";
    }

    public String verifyOtp(String email, String otp) {

        String storedOtp = otpService.getOtp(email);

        //Kiểm tra OTP
//        if (storedOtp == null || !storedOtp.equals(otp)) {
//            throw new RuntimeException("OTP không đúng");
//        }

        System.out.println("TempUserStorage: " + tempUserStorage.get(email));

        if(storedOtp == null || !storedOtp.equals(otp)){
            throw new RuntimeException("OTP không đúng");
        }

        RegisterDTO tempUser = tempUserStorage.get(email);

        // Kiểm tra
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
        otpService.removeOtp(email);
        tempUserStorage.remove(email);

        return "Đăng ký thành công";
    }

}
