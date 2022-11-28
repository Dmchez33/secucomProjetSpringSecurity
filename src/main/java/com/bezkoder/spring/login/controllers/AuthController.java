package com.bezkoder.spring.login.controllers;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.login.models.ERole;
import com.bezkoder.spring.login.models.Role;
import com.bezkoder.spring.login.models.Collaborateur;
import com.bezkoder.spring.login.payload.request.LoginRequest;
import com.bezkoder.spring.login.payload.request.SignupRequest;
import com.bezkoder.spring.login.payload.response.UserInfoResponse;
import com.bezkoder.spring.login.payload.response.MessageResponse;
import com.bezkoder.spring.login.repository.RoleRepository;
import com.bezkoder.spring.login.repository.CollaborateurRepository;
import com.bezkoder.spring.login.security.jwt.JwtUtils;
import com.bezkoder.spring.login.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final Logger Log = LoggerFactory.getLogger(AuthController.class);

  //**************************** DECLATION DES DIFFERENTES INSTANCE ******************************************

  //AUTHENTICATION MANAGER COORDONNE LES DIFFERENTS REQUETTE VERS LES BONS ANDROITS
  @Autowired
  AuthenticationManager authenticationManager;

  //CETTE CLASSE CONTIENT DES INFORMATIONS NECCESSAIRE PERMETTANT LA GENERATION DES TOKEN ET LEURS STOCKAGE
  // DANS LES COOKIES
  @Autowired
  JwtUtils jwtUtils;

  //******************* METHODE PERMETTANT D'AUTHENTIFIER UN COLLABORATEUR ***********************************
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

    //LA METHODE AUTHENTICATE PERMET D'AUTHENFIER UN UTILISATEUR EN FONCTION DU TYPE D'AUTHENTIFICATION
    //DANS NOTRE CAS ON S'AUTHENTIFIE PAR MOT DE PASSE ET LE NOM D'UTILISATEUR
    Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    //C'EST GRACE A SECURITYCONTEXTHOLDER QUE SPRING SECURITY STOCKE LES DETAILS DE CELUI QUI CEST AUTHENTIFIE
    SecurityContextHolder.getContext().setAuthentication(authentication);

    //ICI NOUS CREEONS UNE INSTANCE DE CELUI QUI S'EST AUTHENTIFIER EN UTILISANT LA CLASSE USERDETAILSIMPL
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    //ON GENERE LE TOKEN EN LE STOCKANT DIRECTEMENT DANS UN COOKIE
    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    List<String> entite = new ArrayList<>();

    roles.forEach(role ->{
      entite.add(role);
    });

    Log.info("VOUS ETES AUTHENTIFIE AVEC SUCCESS");
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body("BIENVENU "+entite.toString().substring(1, entite.toString().length()-1));

  }


  //************************************** MEHTODE PERMETTANT DE CE DECONNECTER ****************************
  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {

    Log.info("COLLABORATEUR DECONNECTER AVEC SUCCESS");

    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new MessageResponse("DECONNEXION REUSSI"));
  }

}
