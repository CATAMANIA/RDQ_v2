/**
 * Service de gestion des intégrations externes
 * Génère les URLs pour l'intégration avec les applications externes (email, maps, calendrier)
 */

import { RDQ } from '../types';

export interface ExternalActionData {
  type: 'email' | 'maps' | 'calendar';
  enabled: boolean;
  url?: string;
  tooltip: string;
}

export class ExternalIntegrationService {
  
  /**
   * Génère l'URL pour l'intégration email
   */
  static generateEmailUrl(rdq: RDQ): string | null {
    // Prioriser l'email du manager
    const email = rdq.manager?.email;
    
    if (!email) {
      return null;
    }

    const subject = encodeURIComponent(`RDQ #${rdq.idRdq} - ${rdq.titre}`);
    const body = encodeURIComponent(
      `Bonjour,\n\n` +
      `Je vous contacte concernant le RDQ suivant :\n\n` +
      `• Titre : ${rdq.titre}\n` +
      `• Date : ${new Date(rdq.dateHeure).toLocaleDateString('fr-FR', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })}\n` +
      `• Mode : ${rdq.mode}\n` +
      (rdq.adresse ? `• Adresse : ${rdq.adresse}\n` : '') +
      `\n` +
      `Cordialement.`
    );

    return `mailto:${email}?subject=${subject}&body=${body}`;
  }

  /**
   * Génère l'URL pour l'intégration Google Maps
   */
  static generateMapsUrl(rdq: RDQ): string | null {
    if (!rdq.adresse || rdq.mode === 'DISTANCIEL') {
      return null;
    }

    const address = encodeURIComponent(rdq.adresse);
    return `https://www.google.com/maps/search/?api=1&query=${address}`;
  }

  /**
   * Génère l'URL pour l'intégration Google Calendar
   */
  static generateCalendarUrl(rdq: RDQ): string | null {
    const startDate = new Date(rdq.dateHeure);
    const endDate = new Date(startDate.getTime() + (2 * 60 * 60 * 1000)); // +2h par défaut

    // Format ISO sans les millisecondes ni le Z final pour Google Calendar
    const formatDateForGoogle = (date: Date): string => {
      return date.toISOString().replace(/\.\d{3}Z$/, 'Z').replace(/[:-]/g, '').replace('Z', '');
    };

    const title = encodeURIComponent(`RDQ - ${rdq.titre}`);
    const details = encodeURIComponent(
      `RDQ #${rdq.idRdq}\n\n` +
      `Manager : ${rdq.manager?.nom || 'Non défini'}\n` +
      `Mode : ${rdq.mode}\n` +
      (rdq.description ? `\nDescription :\n${rdq.description}` : '') +
      (rdq.indications ? `\n\nIndications :\n${rdq.indications}` : '')
    );

    const location = rdq.adresse && rdq.mode !== 'DISTANCIEL' 
      ? encodeURIComponent(rdq.adresse)
      : encodeURIComponent('Rendez-vous distanciel');

    const dates = `${formatDateForGoogle(startDate)}/${formatDateForGoogle(endDate)}`;

    return `https://calendar.google.com/calendar/render?action=TEMPLATE&text=${title}&dates=${dates}&details=${details}&location=${location}`;
  }

  /**
   * Génère les données d'action pour un RDQ donné
   */
  static generateExternalActions(rdq: RDQ): ExternalActionData[] {
    const actions: ExternalActionData[] = [];

    // Action Email
    const emailUrl = this.generateEmailUrl(rdq);
    actions.push({
      type: 'email',
      enabled: !!emailUrl,
      url: emailUrl || undefined,
      tooltip: emailUrl 
        ? `Envoyer un email à ${rdq.manager?.email}`
        : 'Aucun email disponible'
    });

    // Action Maps
    const mapsUrl = this.generateMapsUrl(rdq);
    actions.push({
      type: 'maps',
      enabled: !!mapsUrl,
      url: mapsUrl || undefined,
      tooltip: mapsUrl 
        ? `Ouvrir l'adresse dans Google Maps`
        : rdq.mode === 'DISTANCIEL' 
          ? 'Rendez-vous distanciel - pas d\'adresse'
          : 'Aucune adresse disponible'
    });

    // Action Calendar
    const calendarUrl = this.generateCalendarUrl(rdq);
    actions.push({
      type: 'calendar',
      enabled: !!calendarUrl,
      url: calendarUrl || undefined,
      tooltip: calendarUrl 
        ? 'Ajouter au calendrier Google'
        : 'Impossible de générer l\'événement calendrier'
    });

    return actions;
  }

  /**
   * Ouvre une URL externe dans une nouvelle fenêtre/onglet
   */
  static openExternalUrl(url: string): void {
    window.open(url, '_blank', 'noopener,noreferrer');
  }

  /**
   * Gestionnaire d'événement pour les actions externes
   */
  static handleExternalAction(action: ExternalActionData, rdq: RDQ): void {
    if (!action.enabled || !action.url) {
      console.warn(`Action ${action.type} non disponible pour RDQ #${rdq.idRdq}`);
      return;
    }

    // Log pour analytics (optionnel)
    console.log(`External action triggered: ${action.type} for RDQ #${rdq.idRdq}`);

    // Ouvrir l'URL
    this.openExternalUrl(action.url);
  }

  /**
   * Validation des données RDQ pour les intégrations externes
   */
  static validateRdqForExternalActions(rdq: RDQ): {
    hasEmail: boolean;
    hasAddress: boolean;
    hasDateTime: boolean;
    warnings: string[];
  } {
    const warnings: string[] = [];
    
    const hasEmail = !!(rdq.manager?.email);
    const hasAddress = !!(rdq.adresse && rdq.mode !== 'DISTANCIEL');
    const hasDateTime = !!(rdq.dateHeure);

    if (!hasEmail) {
      warnings.push('Aucun email de manager disponible pour l\'intégration email');
    }

    if (!hasAddress && rdq.mode === 'PRESENTIEL') {
      warnings.push('Adresse manquante pour un RDQ en présentiel');
    }

    if (!hasDateTime) {
      warnings.push('Date/heure manquante pour l\'intégration calendrier');
    }

    return {
      hasEmail,
      hasAddress,
      hasDateTime,
      warnings
    };
  }
}

export default ExternalIntegrationService;