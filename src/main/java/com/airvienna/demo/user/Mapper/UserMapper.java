package com.airvienna.demo.user.Mapper;

import com.airvienna.demo.user.domain.User;
import com.airvienna.demo.user.dto.RequestUserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User requestToEntity(RequestUserDto requestUserDto) {
        return User.builder()
                .lastName(requestUserDto.getLastName())
                .firstName(requestUserDto.getFirstName())
                .birth(requestUserDto.getBirth())
                .email(requestUserDto.getEmail())
                .phone(requestUserDto.getPhone())
                .build();
    }
}
