package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("accounts")
public class AccountController {

    @Autowired
    private BankAccountService bankAccountService; // Gọi Interface, không gọi thẳng Imp (Design Pattern)

    //Tạo tài khoản


    //Nạp tiền
    @PutMapping(path = "/deposit/{accountNumber}")
    public ResponseEntity<?> deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Nạp tiền tại quầy/Online") String description) {

        String result = bankAccountService.deposit(accountNumber, amount, description);

        return ResponseEntity.ok(result);
    }
}
