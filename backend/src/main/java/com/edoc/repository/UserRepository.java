package com.edoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.edoc.models.User;

import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
  Optional<User> findByUsernameOrEmail(String username, String email);

  @Query(value = "SELECT ag.name FROM auth_user_groups aug " +"JOIN auth_group ag ON aug.group_id = ag.id " +"WHERE aug.user_id = :userId", nativeQuery = true)
  Set<String> findGroupNamesByUserId(@Param("userId") Long userId);

  @Query(value = "SELECT c.path_photo FROM candidat c " +"WHERE c.user_id = :userId", nativeQuery = true)
  Set<String> findPhotoPathByUserId(@Param("userId") Long userId);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO auth_user_groups (user_id, group_id) VALUES (:userId, :groupId)", nativeQuery = true)
  void addUserToGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);
}