package com.mamroune.meetingbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mamroune.meetingbook.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);
}