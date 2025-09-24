package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.repository.RDQRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RDQServiceTest {

    @Mock
    private RDQRepository rdqRepository;

    @InjectMocks
    private RDQService rdqService;

    @Test
    void testFindAll() {
        // Given
        RDQ rdq1 = new RDQ();
        rdq1.setIdRdq(1L);
        rdq1.setTitre("RDQ Test 1");

        RDQ rdq2 = new RDQ();
        rdq2.setIdRdq(2L);
        rdq2.setTitre("RDQ Test 2");

        List<RDQ> expectedRdqs = Arrays.asList(rdq1, rdq2);

        // When
        when(rdqRepository.findAll()).thenReturn(expectedRdqs);
        List<RDQ> actualRdqs = rdqService.findAll();

        // Then
        assertEquals(2, actualRdqs.size());
        assertEquals("RDQ Test 1", actualRdqs.get(0).getTitre());
        assertEquals("RDQ Test 2", actualRdqs.get(1).getTitre());
        verify(rdqRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        Long rdqId = 1L;
        RDQ expectedRdq = new RDQ();
        expectedRdq.setIdRdq(rdqId);
        expectedRdq.setTitre("RDQ Test");

        // When
        when(rdqRepository.findById(rdqId)).thenReturn(Optional.of(expectedRdq));
        Optional<RDQ> actualRdq = rdqService.findById(rdqId);

        // Then
        assertTrue(actualRdq.isPresent());
        assertEquals("RDQ Test", actualRdq.get().getTitre());
        verify(rdqRepository, times(1)).findById(rdqId);
    }

    @Test
    void testSave() {
        // Given
        RDQ rdqToSave = new RDQ();
        rdqToSave.setTitre("Nouvelle RDQ");

        RDQ savedRdq = new RDQ();
        savedRdq.setIdRdq(1L);
        savedRdq.setTitre("Nouvelle RDQ");

        // When
        when(rdqRepository.save(rdqToSave)).thenReturn(savedRdq);
        RDQ actualRdq = rdqService.save(rdqToSave);

        // Then
        assertNotNull(actualRdq);
        assertEquals(1L, actualRdq.getIdRdq());
        assertEquals("Nouvelle RDQ", actualRdq.getTitre());
        verify(rdqRepository, times(1)).save(rdqToSave);
    }
}