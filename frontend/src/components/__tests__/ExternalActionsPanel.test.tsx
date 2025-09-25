/**
 * Tests unitaires pour ExternalActionsPanel
 */

import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import type { RDQ } from '../../types';
import { ExternalIntegrationService } from '../../services/ExternalIntegrationService';

// Mock des composants UI
jest.mock('../ui/button', () => ({
  Button: ({ children, onClick, disabled, className, ...props }: any) => (
    <button onClick={onClick} disabled={disabled} className={className} {...props}>
      {children}
    </button>
  )
}));

jest.mock('../ui/card', () => ({
  Card: ({ children, className, ...props }: any) => (
    <div className={className} {...props}>
      {children}
    </div>
  ),
  CardContent: ({ children, className, ...props }: any) => (
    <div className={className} {...props}>
      {children}
    </div>
  )
}));

jest.mock('../ui/alert', () => ({
  Alert: ({ children, className, ...props }: any) => (
    <div className={className} {...props}>
      {children}
    </div>
  ),
  AlertDescription: ({ children, className, ...props }: any) => (
    <div className={className} {...props}>
      {children}
    </div>
  )
}));

// Mock des icônes Lucide
jest.mock('lucide-react', () => ({
  Mail: () => <span>Mail</span>,
  Map: () => <span>Map</span>,
  Calendar: () => <span>Calendar</span>,
  AlertTriangle: () => <span>AlertTriangle</span>
}));

// Mock du service
jest.mock('../../services/ExternalIntegrationService');

// Import du composant après les mocks
import { ExternalActionsPanel } from '../ExternalActionsPanel';
const mockExternalIntegrationService = ExternalIntegrationService as jest.Mocked<typeof ExternalIntegrationService>;

describe('ExternalActionsPanel', () => {
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
    documents: [],
    bilans: []
  };

  beforeEach(() => {
    jest.clearAllMocks();
    
    // Mock des méthodes du service
    mockExternalIntegrationService.generateExternalActions.mockReturnValue([
      {
        type: 'email',
        enabled: true,
        url: 'mailto:manager@test.com?subject=Test',
        tooltip: 'Envoyer un email à manager@test.com'
      },
      {
        type: 'maps',
        enabled: true,
        url: 'https://www.google.com/maps/search/?api=1&query=test',
        tooltip: 'Ouvrir l\'adresse dans Google Maps'
      },
      {
        type: 'calendar',
        enabled: true,
        url: 'https://calendar.google.com/calendar/render?action=TEMPLATE',
        tooltip: 'Ajouter au calendrier Google'
      }
    ]);

    mockExternalIntegrationService.validateRdqForExternalActions.mockReturnValue({
      hasEmail: true,
      hasAddress: true,
      hasDateTime: true,
      warnings: []
    });

    mockExternalIntegrationService.handleExternalAction.mockImplementation(() => {});
  });

  it('should render the component with title', () => {
    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    expect(screen.getByText('Actions rapides')).toBeInTheDocument();
  });

  it('should display all three action buttons when actions are enabled', () => {
    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    expect(screen.getByText('Email')).toBeInTheDocument();
    expect(screen.getByText('Maps')).toBeInTheDocument();
    expect(screen.getByText('Calendrier')).toBeInTheDocument();
  });

  it('should call handleExternalAction when email button is clicked', () => {
    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    const emailButton = screen.getByText('Email').closest('button');
    fireEvent.click(emailButton!);
    
    expect(mockExternalIntegrationService.handleExternalAction).toHaveBeenCalledWith(
      expect.objectContaining({
        type: 'email',
        enabled: true,
        url: 'mailto:manager@test.com?subject=Test'
      }),
      mockRdq
    );
  });

  it('should call handleExternalAction when maps button is clicked', () => {
    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    const mapsButton = screen.getByText('Maps').closest('button');
    fireEvent.click(mapsButton!);
    
    expect(mockExternalIntegrationService.handleExternalAction).toHaveBeenCalledWith(
      expect.objectContaining({
        type: 'maps',
        enabled: true
      }),
      mockRdq
    );
  });

  it('should call handleExternalAction when calendar button is clicked', () => {
    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    const calendarButton = screen.getByText('Calendrier').closest('button');
    fireEvent.click(calendarButton!);
    
    expect(mockExternalIntegrationService.handleExternalAction).toHaveBeenCalledWith(
      expect.objectContaining({
        type: 'calendar',
        enabled: true
      }),
      mockRdq
    );
  });

  it('should display warning when no actions are enabled', () => {
    mockExternalIntegrationService.generateExternalActions.mockReturnValue([
      {
        type: 'email',
        enabled: false,
        url: undefined,
        tooltip: 'Aucun email disponible'
      },
      {
        type: 'maps',
        enabled: false,
        url: undefined,
        tooltip: 'Aucune adresse disponible'
      },
      {
        type: 'calendar',
        enabled: false,
        url: undefined,
        tooltip: 'Aucune date disponible'
      }
    ]);

    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    expect(screen.getByText('Aucune intégration externe disponible')).toBeInTheDocument();
  });

  it('should show disabled state for disabled actions', () => {
    mockExternalIntegrationService.generateExternalActions.mockReturnValue([
      {
        type: 'email',
        enabled: false,
        url: undefined,
        tooltip: 'Aucun email disponible'
      },
      {
        type: 'maps',
        enabled: true,
        url: 'https://maps.google.com',
        tooltip: 'Ouvrir dans Maps'
      },
      {
        type: 'calendar',
        enabled: true,
        url: 'https://calendar.google.com',
        tooltip: 'Ajouter au calendrier'
      }
    ]);

    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    const emailButton = screen.getByText('Email').closest('button');
    const mapsButton = screen.getByText('Maps').closest('button');
    
    expect(emailButton).toBeDisabled();
    expect(mapsButton).not.toBeDisabled();
  });

  it('should display warnings when validation has issues', () => {
    mockExternalIntegrationService.validateRdqForExternalActions.mockReturnValue({
      hasEmail: false,
      hasAddress: true,
      hasDateTime: true,
      warnings: ['Aucun email de manager disponible pour l\'intégration email']
    });

    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    expect(screen.getByText('Informations complémentaires')).toBeInTheDocument();
    expect(screen.getByText('• Aucun email de manager disponible pour l\'intégration email')).toBeInTheDocument();
  });

  it('should show phone integration note', () => {
    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    expect(screen.getByText('📞 Intégration téléphone')).toBeInTheDocument();
    expect(screen.getByText(/L'intégration directe avec l'application téléphone n'est pas possible/)).toBeInTheDocument();
    expect(screen.getByText(/Solution mobile à venir/)).toBeInTheDocument();
  });

  it('should apply custom className', () => {
    const { container } = render(<ExternalActionsPanel rdq={mockRdq} className="custom-class" />);
    
    expect(container.firstChild).toHaveClass('custom-class');
  });

  it('should show available actions details when actions are enabled', () => {
    render(<ExternalActionsPanel rdq={mockRdq} />);
    
    expect(screen.getByText('Actions disponibles :')).toBeInTheDocument();
    expect(screen.getByText('Email vers manager@test.com')).toBeInTheDocument();
    expect(screen.getByText('Navigation vers 123 Rue de Test, Paris')).toBeInTheDocument();
  });
});