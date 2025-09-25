/**
 * Tests unitaires pour ExternalIntegrationService
 */

import { ExternalIntegrationService } from '../ExternalIntegrationService';
import type { RDQ } from '../../types';

describe('ExternalIntegrationService', () => {
  // Mock RDQ de test
  const mockRdq: RDQ = {
    idRdq: 123,
    titre: 'Test RDQ',
    dateHeure: '2025-09-30T14:00:00Z',
    adresse: '123 Rue de Test, Paris',
    mode: 'PRESENTIEL',
    statut: 'PLANIFIE',
    description: 'Description du test',
    manager: {
      idManager: 1,
      nom: 'Manager Test',
      email: 'manager@test.com'
    },
    projet: {
      idProjet: 1,
      nom: 'Projet Test'
    },
    indications: 'Indications test',
    documents: [],
    bilans: []
  };

  describe('generateEmailUrl', () => {
    it('should generate valid email URL with manager email', () => {
      const url = ExternalIntegrationService.generateEmailUrl(mockRdq);
      
      expect(url).toContain('mailto:manager@test.com');
      expect(url).toContain('subject=RDQ%20%23123%20-%20Test%20RDQ');
      expect(url).toContain('body=');
    });

    it('should return null when no manager email', () => {
      const rdqWithoutEmail: RDQ = {
        ...mockRdq,
        manager: {
          idManager: 1,
          nom: 'Manager Sans Email',
          email: ''
        }
      };

      const url = ExternalIntegrationService.generateEmailUrl(rdqWithoutEmail);
      expect(url).toBeNull();
    });

    it('should return null when no manager', () => {
      const rdqWithoutManager: RDQ = {
        ...mockRdq,
        manager: undefined
      };

      const url = ExternalIntegrationService.generateEmailUrl(rdqWithoutManager);
      expect(url).toBeNull();
    });
  });

  describe('generateMapsUrl', () => {
    it('should generate valid Google Maps URL with address', () => {
      const url = ExternalIntegrationService.generateMapsUrl(mockRdq);
      
      expect(url).toContain('https://www.google.com/maps/search/?api=1&query=');
      expect(url).toContain('123%20Rue%20de%20Test%2C%20Paris');
    });

    it('should return null for DISTANCIEL mode', () => {
      const rdqDistanciel: RDQ = {
        ...mockRdq,
        mode: 'DISTANCIEL'
      };

      const url = ExternalIntegrationService.generateMapsUrl(rdqDistanciel);
      expect(url).toBeNull();
    });

    it('should return null when no address', () => {
      const rdqWithoutAddress: RDQ = {
        ...mockRdq,
        adresse: undefined
      };

      const url = ExternalIntegrationService.generateMapsUrl(rdqWithoutAddress);
      expect(url).toBeNull();
    });
  });

  describe('generateCalendarUrl', () => {
    it('should generate valid Google Calendar URL', () => {
      const url = ExternalIntegrationService.generateCalendarUrl(mockRdq);
      
      expect(url).toContain('https://calendar.google.com/calendar/render?action=TEMPLATE');
      expect(url).toContain('text=RDQ%20-%20Test%20RDQ');
      expect(url).toContain('dates=');
      expect(url).toContain('details=');
      expect(url).toContain('location=');
    });

    it('should return null when no date', () => {
      const rdqWithoutDate: RDQ = {
        ...mockRdq,
        dateHeure: ''
      };

      const url = ExternalIntegrationService.generateCalendarUrl(rdqWithoutDate);
      expect(url).toBeNull();
    });

    it('should use "Rendez-vous distanciel" for DISTANCIEL mode', () => {
      const rdqDistanciel: RDQ = {
        ...mockRdq,
        mode: 'DISTANCIEL'
      };

      const url = ExternalIntegrationService.generateCalendarUrl(rdqDistanciel);
      expect(url).toContain('location=Rendez-vous%20distanciel');
    });
  });

  describe('generateExternalActions', () => {
    it('should generate all three actions for complete RDQ', () => {
      const actions = ExternalIntegrationService.generateExternalActions(mockRdq);
      
      expect(actions).toHaveLength(3);
      expect(actions.map(a => a.type)).toEqual(['email', 'maps', 'calendar']);
      
      // Vérifier que toutes les actions sont activées
      const enabledActions = actions.filter(a => a.enabled);
      expect(enabledActions).toHaveLength(3);
    });

    it('should disable email action when no manager email', () => {
      const rdqWithoutEmail: RDQ = {
        ...mockRdq,
        manager: undefined
      };

      const actions = ExternalIntegrationService.generateExternalActions(rdqWithoutEmail);
      const emailAction = actions.find(a => a.type === 'email');
      
      expect(emailAction?.enabled).toBe(false);
      expect(emailAction?.tooltip).toBe('Aucun email disponible');
    });

    it('should disable maps action for DISTANCIEL mode', () => {
      const rdqDistanciel: RDQ = {
        ...mockRdq,
        mode: 'DISTANCIEL'
      };

      const actions = ExternalIntegrationService.generateExternalActions(rdqDistanciel);
      const mapsAction = actions.find(a => a.type === 'maps');
      
      expect(mapsAction?.enabled).toBe(false);
      expect(mapsAction?.tooltip).toBe('Rendez-vous distanciel - pas d\'adresse');
    });
  });

  describe('validateRdqForExternalActions', () => {
    it('should validate complete RDQ as fully compatible', () => {
      const validation = ExternalIntegrationService.validateRdqForExternalActions(mockRdq);
      
      expect(validation.hasEmail).toBe(true);
      expect(validation.hasAddress).toBe(true);
      expect(validation.hasDateTime).toBe(true);
      expect(validation.warnings).toHaveLength(0);
    });

    it('should identify missing email', () => {
      const rdqWithoutEmail: RDQ = {
        ...mockRdq,
        manager: undefined
      };

      const validation = ExternalIntegrationService.validateRdqForExternalActions(rdqWithoutEmail);
      
      expect(validation.hasEmail).toBe(false);
      expect(validation.warnings).toContain('Aucun email de manager disponible pour l\'intégration email');
    });

    it('should identify missing address for PRESENTIEL', () => {
      const rdqWithoutAddress: RDQ = {
        ...mockRdq,
        adresse: undefined
      };

      const validation = ExternalIntegrationService.validateRdqForExternalActions(rdqWithoutAddress);
      
      expect(validation.hasAddress).toBe(false);
      expect(validation.warnings).toContain('Adresse manquante pour un RDQ en présentiel');
    });
  });

  describe('handleExternalAction', () => {
    // Mock window.open pour les tests
    const originalOpen = window.open;
    let mockOpen: jest.Mock;

    beforeEach(() => {
      mockOpen = jest.fn();
      window.open = mockOpen;
    });

    afterEach(() => {
      window.open = originalOpen;
    });

    it('should open URL for enabled action', () => {
      const action = {
        type: 'email' as const,
        enabled: true,
        url: 'mailto:test@test.com',
        tooltip: 'Test tooltip'
      };

      ExternalIntegrationService.handleExternalAction(action, mockRdq);
      
      expect(mockOpen).toHaveBeenCalledWith('mailto:test@test.com', '_blank', 'noopener,noreferrer');
    });

    it('should not open URL for disabled action', () => {
      const action = {
        type: 'email' as const,
        enabled: false,
        url: undefined,
        tooltip: 'Action disabled'
      };

      ExternalIntegrationService.handleExternalAction(action, mockRdq);
      
      expect(mockOpen).not.toHaveBeenCalled();
    });

    it('should not open URL when no URL provided', () => {
      const action = {
        type: 'email' as const,
        enabled: true,
        url: undefined,
        tooltip: 'No URL'
      };

      ExternalIntegrationService.handleExternalAction(action, mockRdq);
      
      expect(mockOpen).not.toHaveBeenCalled();
    });
  });
});