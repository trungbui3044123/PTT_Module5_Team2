package com.module5.team2.repository;

import com.module5.team2.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import com.module5.team2.enums.Role;
import com.module5.team2.enums.Status;



public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UserEntity> findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
            String username, String name
    );
    List<UserEntity> findByRole(Role role);
    List<UserEntity> findByStatusAndRole(Status status, Role role);
    Page<UserEntity> findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
            String username, String name, Pageable pageable);

}
