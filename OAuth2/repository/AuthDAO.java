package com.example.SmartLifeTracker.repository;

import com.example.SmartLifeTracker.dto.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthDAO extends JpaRepository<AuthToken,String> {

    AuthToken findByUserEmail(String email);
}
