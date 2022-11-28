package com.bezkoder.spring.login;

import com.bezkoder.spring.login.models.Collaborateur;
import com.bezkoder.spring.login.models.ERole;
import com.bezkoder.spring.login.models.Role;
import com.bezkoder.spring.login.repository.CollaborateurRepository;
import com.bezkoder.spring.login.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@SpringBootApplication
class SecuComApplication implements CommandLineRunner {

	//**************************** DECLARATION DES INSTANCE *****************
	@Autowired
	PasswordEncoder encoder;

	final private RoleRepository roleRepository;

	final private CollaborateurRepository collaborateurRepository;

	//**************************** METHODE PRINCIPALE DE L'APPLICATION ***********
	public static void main(String[] args) {
		SpringApplication.run(SecuComApplication.class, args);

	}

	//***************************** METHODE PERMETTANT DE CREER UN ADMIN PAR DEFAUT **********
	@Override
	public void run(String... args) throws Exception {
		//VERIFICATION DE L'EXISTANCE DU ROLE ADMIN AVANT SA CREATION
		if (roleRepository.findAll().size() == 0){
			roleRepository.save(new Role(ERole.ROLE_ADMIN));
			roleRepository.save(new Role(ERole.ROLE_USER));
		}
		if (collaborateurRepository.findAll().size() == 0){
			Set<Role> roles = new HashSet<>();
			Role role = roleRepository.findByName(ERole.ROLE_ADMIN);
			roles.add(role);
			Collaborateur collaborateur = new Collaborateur("admin","admin@gmail.com",encoder.encode( "12345678"));
			collaborateur.setRoles(roles);
			collaborateurRepository.save(collaborateur);

		}
	}
}

