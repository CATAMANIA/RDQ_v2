package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.Client;
import com.vibecoding.rdq.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des clients
 * Remplace les données mock mockClients
 */
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public List<Client> findByNomContaining(String nom) {
        return clientRepository.findByNomContainingIgnoreCase(nom);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }


}