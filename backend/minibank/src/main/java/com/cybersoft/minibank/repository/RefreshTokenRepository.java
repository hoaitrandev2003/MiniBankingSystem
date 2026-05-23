package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.RefreshTokenEntity;
import com.cybersoft.minibank.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Integer> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUser(UserEntity user);
    boolean existsByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM refresh_tokens r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") int userId);
}
