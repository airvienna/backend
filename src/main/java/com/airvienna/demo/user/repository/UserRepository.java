package com.airvienna.demo.user.repository;

import com.airvienna.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 해당 email을 사용하는 유저
    Optional<User> findByEmail(String email);
}