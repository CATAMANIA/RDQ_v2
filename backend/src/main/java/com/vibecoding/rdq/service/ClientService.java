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

    /**
     * Initialise les données de test (équivalent des mock)
     */
    public void initializeTestData() {
        if (clientRepository.count() == 0) {
            // Données équivalentes à mockClients
            clientRepository.save(new Client(null, "ACME Corp", "M. Durand", "01.55.66.77.88", "durand@acme.com"));
            clientRepository.save(new Client(null, "TechSolutions", "Mme Leblanc", "01.44.55.66.77", "leblanc@techsolutions.com"));
            clientRepository.save(new Client(null, "Digital Innovations", "M. Martin", "01.33.44.55.66", "martin@digital-innovations.com"));
            clientRepository.save(new Client(null, "StartupX", "Mme Garcia", "01.22.33.44.55", "garcia@startupx.com"));
            clientRepository.save(new Client(null, "Global Systems", "M. Rodriguez", "01.11.22.33.44", "rodriguez@global-systems.com"));
        }
    }
}