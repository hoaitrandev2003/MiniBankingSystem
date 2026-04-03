package com.cybersoft.minibank.payload.response;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private int code = 200;
    private String message;
    private Object data;
}
