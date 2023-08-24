package com.airvienna.demo.user.Mapper;

import com.airvienna.demo.user.domain.User;
import com.airvienna.demo.user.dto.RequestUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User requestToEntity(RequestUserDto requestUserDto) {
        return User.builder()
                .lastName(requestUserDto.getLastName())
                .firstName(requestUserDto.getFirstName())
                .birth(requestUserDto.getBirth())
                .email(requestUserDto.getEmail())
                .password(passwordEncoder.encode(requestUserDto.getPassword()))
                .phone(requestUserDto.getPhone())
                .build();
    }
}