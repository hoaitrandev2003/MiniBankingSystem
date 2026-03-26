package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/accounts")
public class AccountController {

    @Autowired
    private BankAccountService bankAccountService;

    //Tạo tài khoản


    //Nạp tiền
    @PutMapping("/deposit/{accountNumber}")
    public ResponseEntity<?> deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Nạp tiền tại quầy/Online") String description) {

        String result = bankAccountService.deposit(accountNumber, amount, description);

        return ResponseEntity.ok(result);
    }
    //Lấy số dư
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String accountNumber){
        double balance =  bankAccountService.getAccountBalance(accountNumber);
        return ResponseEntity.ok("Số dư của tài khoản " + accountNumber + " là: " + balance); }
}
