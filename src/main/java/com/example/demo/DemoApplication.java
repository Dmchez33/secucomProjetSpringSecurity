package com.example.demo;

import com.example.demo.Model.Collaborateur;
import com.example.demo.Model.ERole;
import com.example.demo.Model.Role;
import com.example.demo.Repository.CollaborateurRepository;
import com.example.demo.Repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
@AllArgsConstructor
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	//**************************** DECLARATION DES INSTANCE *****************
	@Autowired
	PasswordEncoder encoder;

	final private RoleRepository roleRepository;

	final private CollaborateurRepository collaborateurRepository;

	//**************************** METHODE PRINCIPALE DE L'APPLICATION ***********
	public static void main(String[] args) {
		 SpringApplication.run(DemoApplication.class, args);

	}

	//***************************** METHODE PERMETTANT DE CREER UN ADMIN PAR DEFAUT **********
	@Override
	public void run(String... args) throws Exception {
		//VERIFICATION DE L'EXISTANCE DU ROLE ADMIN AVANT SA CREATION
		if (roleRepository.findAll().size() == 0){
			roleRepository.save(new Role(ERole.ADMIN));
			roleRepository.save(new Role(ERole.USER));
		}
		if (collaborateurRepository.findAll().size() == 0){
			Set<Role> roles = new HashSet<>();
			Role role = roleRepository.findByName(ERole.ADMIN);
			roles.add(role);
			Collaborateur collaborateur = new Collaborateur("admin","admin@gmail.com",encoder.encode( "12345678"));
			collaborateur.setRoles(roles);
			collaborateurRepository.save(collaborateur);

		}
	}
}
