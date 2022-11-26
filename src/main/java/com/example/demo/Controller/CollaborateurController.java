package com.example.demo.Controller;

import com.example.demo.Model.Collaborateur;
import com.example.demo.Model.ERole;
import com.example.demo.Model.Role;
import com.example.demo.Payload.Request.SignupRequest;
import com.example.demo.Payload.Response.MessageResponse;
import com.example.demo.Repository.CollaborateurRepository;
import com.example.demo.Repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/collaborateur")
public class CollaborateurController {

    private static final Logger Log = LoggerFactory.getLogger(CollaborateurController.class);
    @Autowired
    CollaborateurRepository collaborateurRepository;



    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;

    //METHODE PERMETTANT DE RECUPERER TOUS LES UTILISATEURS
    @GetMapping("/gettAll")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Collaborateur> allUserAccess() {
        Log.info("RECUPERATION DE TOUT LES COLLABORATEUR");
        return collaborateurRepository.findAll();
    }

    @PostMapping("/createCollaborateur")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (collaborateurRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: ce nom d'utilisateur existe deja!"));
        }

        if (collaborateurRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: Ce email existe deja!"));
        }
        Log.info("CREATION D'UN COLLABORATEUR");
        // Create new Collaborateur's account
        Collaborateur user = new Collaborateur(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

       if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.USER);
            roles.add(userRole);
        }  else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ADMIN);
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.USER);
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        collaborateurRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Collaborateur creer avec success!"));
    }

    //METHODE PERMETTANT DE METTRE A JOUR LES INFORMATION D'UN COLLABORATEUR
    @PutMapping("/updateCollaborateur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Collaborateur updateCollaborateur(@Valid @RequestBody SignupRequest signUpRequest,@PathVariable("id") Long id){
        Log.info("MODIFICATION D'UN COLLABORATEUR");


        return  collaborateurRepository.findById(id).map(

                signUpRequest1 ->{
                    signUpRequest1.setUsername(signUpRequest.getUsername());
                    signUpRequest1.setPassword(encoder.encode(signUpRequest.getPassword()));
                    signUpRequest1.setEmail(signUpRequest.getEmail());
                    Set<String> strRoles = signUpRequest.getRole();
                    Set<Role> roles = new HashSet<>();

                    //RECUPERATION DES ROLES
                    strRoles.forEach(role -> {

                        switch (role) {
                            case "admin":
                                Role adminRole = roleRepository.findByName(ERole.ADMIN);
                                roles.add(adminRole);
                                break;
                            default:
                                Role userRole = roleRepository.findByName(ERole.USER);
                                roles.add(userRole);
                        }
                    }
                    );
                    signUpRequest1.setRoles(roles);

                    return collaborateurRepository.save(signUpRequest1);
                }
        ).orElseThrow(() -> new RuntimeException("Collaborateur non trouvéé"));
        //return collaborateurService.UpdateCollaborateur(id, user);

    }



    //**************************************** METHODE PERMETTANT DE SUPPRIMER LE COLLABORATEUR
    @DeleteMapping("/deleteCollaborateur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCollaborateur(@PathVariable("id") Long id){

        Log.info("SUPPRESSION D'UN COLLABORATEUR");

        collaborateurRepository.deleteById(id);

        return "utilisateur supprimer";

    }


}
