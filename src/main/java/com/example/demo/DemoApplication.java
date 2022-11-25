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
	@Autowired
	PasswordEncoder encoder;

	final private RoleRepository roleRepository;
	final private CollaborateurRepository collaborateurRepository;
	/*@Autowired
	static

	PasswordEncoder encoder;

	static CollaborateurRepository userRepository;*/
	public static void main(String[] args) {
		 SpringApplication.run(DemoApplication.class, args);
		/*CollaborateurService collaborateurService = ctx.getBean(CollaborateurService.class);
		Role role = new Role();
		ApplicationContext ctx =
		role.setId(1);
		role.setName(ERole.ROLE_ADMIN);*/
		/*Set<Role> roles = new HashSet<>();
		roles.add(role);
		SignupRequest signupRequest = new SignupRequest();

		signupRequest.setEmail("admin@gmail.com");
		signupRequest.setPassword("12345678");
		signupRequest.setUsername("admin");
		//signupRequest.setRole(roles);

		Collaborateur user = new Collaborateur(signupRequest.getUsername(),
				signupRequest.getEmail(),
				encoder.encode(signupRequest.getPassword()));
		user.setRoles(roles);
		userRepository.save(user);
		Collaborateur user = new Collaborateur();
		user.setId(1L);
		user.setEmail("admin@gmail.com");
		user.setUsername("asmin");
		user.setPassword("12345678");

		
		collaborateurService.CreateCollaborateur(user);*/

	}


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
