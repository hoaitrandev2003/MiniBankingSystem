package com.cybersoft.minibank.dto;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionDTO {
    private String username;
    private String ipAddress;
    private String deviceId;
    private String accessToken;
    private String refreshToken;
    private Long loginTime;
}
