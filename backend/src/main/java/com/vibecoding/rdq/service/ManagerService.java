package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.Manager;
import com.vibecoding.rdq.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    public List<Manager> findAll() {
        return managerRepository.findAll();
    }

    public Optional<Manager> findById(Long id) {
        return managerRepository.findById(id);
    }

    public Optional<Manager> findByEmail(String email) {
        return managerRepository.findByEmail(email);
    }

    public Manager save(Manager manager) {
        return managerRepository.save(manager);
    }

    public void deleteById(Long id) {
        managerRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return managerRepository.existsByEmail(email);
    }

    public Optional<Manager> findByIdWithRdqs(Long id) {
        return managerRepository.findByIdWithRdqs(id);
    }
}