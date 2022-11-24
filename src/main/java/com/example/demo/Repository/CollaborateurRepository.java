package com.example.demo.Repository;

import com.example.demo.Model.Collaborateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollaborateurRepository extends JpaRepository<Collaborateur, Long> {
    Optional<Collaborateur> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}