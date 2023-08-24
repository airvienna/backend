package com.airvienna.demo.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RequestRegenerateToken {
    private String refreshToken;
}
