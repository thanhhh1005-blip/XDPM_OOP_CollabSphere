package com.collabsphere.identity.repository;

import com.collabsphere.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user bằng tên đăng nhập
    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email);
    // Kiểm tra xem username đã tồn tại chưa (trả về true/false)
    boolean existsByUsername(String username);

    
}