package com.bezkoder.spring.login.controllers;

import com.bezkoder.spring.login.models.Collaborateur;
import com.bezkoder.spring.login.models.ERole;
import com.bezkoder.spring.login.models.Role;
import com.bezkoder.spring.login.payload.request.SignupRequest;
import com.bezkoder.spring.login.payload.response.MessageResponse;
import com.bezkoder.spring.login.repository.CollaborateurRepository;
import com.bezkoder.spring.login.repository.RoleRepository;
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
    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Collaborateur> allUserAccess() {
        Log.info("RECUPERATION DE TOUT LES COLLABORATEUR");
        return collaborateurRepository.findAll();
    }

    //METHODE PERMETTANT L'AJOUT D'UN NOUVEAU COLLABORATEUR
    @PostMapping("/createCollaborateur")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {

        //VERIFICATION DE L'EXISTANCE DU NOM D'UTILISATEUR
        if (collaborateurRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: ce nom d'utilisateur existe deja!"));
        }

        //VERIFICATION DE L'EXISTANCE DE L'EMAIL
        if (collaborateurRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: Ce email existe deja!"));
        }


        Log.info("CREATION D'UN COLLABORATEUR");

        // CREATION D'UNE INSTANCE DE COLLABORATEUR
        Collaborateur user = new Collaborateur(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        //RECUPERATION DES ROLES DU COLLABORATEUR
        Set<String> strRoles = signUpRequest.getRole();

        //CREATION D'UNE INSTANCE DE ROLE A L'INTERIEUR PERMETTANT DE STOCKER LES DIFFERENTS ENTRER PAR L'ADMIN
        Set<Role> roles = new HashSet<>();

        //VERIFICATION DU ROLE ENTREZ PAR L'UTILISATEUR
        //SI C'EST NULL ON AFFECTE DIRECTEMENT LE ROLE USER A CE COLLABORATEUR
        //SINON RECUPERE C'EST DIFFERENT ROLE ET ON VERIFIE SI CA EXISTE DANS LA BASE DE DONNEE
        // DANS LE CAS CONTRAIRE ON AFFECTE LE ROLE USER A CE COLLABORATEUR
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER);
            roles.add(userRole);
        }  else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER);
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
    public Collaborateur updateCollaborateur(@Valid @RequestBody SignupRequest signUpRequest, @PathVariable("id") Long id){

        Log.info("MODIFICATION D'UN COLLABORATEUR");

        return  collaborateurRepository.findById(id).map(

                signUpRequest1 ->{
                    signUpRequest1.setUsername(signUpRequest.getUsername());
                    signUpRequest1.setPassword(encoder.encode(signUpRequest.getPassword()));
                    signUpRequest1.setEmail(signUpRequest.getEmail());
                    Set<String> strRoles = signUpRequest.getRole();
                    Set<Role> roles = new HashSet<>();

                    //RECUPERATION DES ROLES ET VERIFICATION DANS LA BASE DE DONNEE
                    strRoles.forEach(role -> {

                                switch (role) {
                                    case "admin":
                                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
                                        roles.add(adminRole);
                                        break;
                                    default:
                                        Role userRole = roleRepository.findByName(ERole.ROLE_USER);
                                        roles.add(userRole);
                                }
                            }
                    );
                    signUpRequest1.setRoles(roles);

                    return collaborateurRepository.save(signUpRequest1);
                }
        ).orElseThrow(() -> new RuntimeException("Collaborateur non trouvéé"));


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
