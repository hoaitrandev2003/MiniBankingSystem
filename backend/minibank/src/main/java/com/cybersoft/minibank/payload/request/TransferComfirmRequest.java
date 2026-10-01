package com.cybersoft.minibank.payload.request;

import lombok.Data;

@Data
public class TransferComfirmRequest {
    private String transactionCode;
    private String otp;
}
