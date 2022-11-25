package com.example.demo.Security.Service;

import com.example.demo.Model.Collaborateur;

import java.util.List;

public interface CollaborateurService {

    List<Collaborateur> GetAllCollaborateur();

    void CreateCollaborateur(Collaborateur collaborateur);

    String DeleteCollaborateur(Long id);

    Collaborateur UpdateCollaborateur(long id, Collaborateur collaborateur);

}
