package com.example.demo.Security.Service;

import com.example.demo.Model.Collaborateur;
import com.example.demo.Repository.CollaborateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    CollaborateurRepository collaborateurRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Collaborateur user = collaborateurRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }


    public List<Collaborateur> GetAllCollaborateur() {
        System.out.println(collaborateurRepository.findAll());
        return collaborateurRepository.findAll();
    }


    public void CreateCollaborateur(Collaborateur collaborateur) {

        collaborateurRepository.save(collaborateur);
    }


    public String DeleteCollaborateur(Long id) {
        collaborateurRepository.deleteById(id);
        return "Collaborateur supprimer avec succes";
    }


    public Collaborateur UpdateCollaborateur(long id, Collaborateur collaborateur) {
        Collaborateur collaborateur1 = collaborateurRepository.findById(id).orElse(null);
        return collaborateurRepository.save(collaborateur);
    }

}
