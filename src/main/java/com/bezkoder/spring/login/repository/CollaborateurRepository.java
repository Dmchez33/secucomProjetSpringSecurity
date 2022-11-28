package com.bezkoder.spring.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bezkoder.spring.login.models.Collaborateur;

@Repository
public interface CollaborateurRepository extends JpaRepository<Collaborateur, Long> {
  Optional<Collaborateur> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
