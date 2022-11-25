package com.example.demo.Repository;

import com.example.demo.Model.ERole;
import com.example.demo.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(ERole name);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO roles(name) VALUES(:name);", nativeQuery = true)
    int INSERUSER(String name);
}