package com.airvienna.demo.user.service;

import com.airvienna.demo.user.Mapper.UserMapper;
import com.airvienna.demo.user.domain.User;
import com.airvienna.demo.user.dto.RequestUserDto;
import com.airvienna.demo.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public Long signUp(RequestUserDto request) {
        User user = userMapper.requestToEntity(request);
        return userRepository.save(user).getId();
    }
}
