// Configuration de base pour l'application RDQ
export const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080'
  : 'http://localhost:8080';

// Configuration de l'authentification
export const AUTH_CONFIG = {
  TOKEN_STORAGE_KEY: 'token',
  USER_STORAGE_KEY: 'user',
  SESSION_TIMEOUT: 8 * 60 * 60 * 1000, // 8 heures en millisecondes
  REFRESH_THRESHOLD: 30 * 60 * 1000, // 30 minutes avant expiration
};

// Configuration des notifications
export const NOTIFICATION_CONFIG = {
  DEFAULT_POLL_INTERVAL: 30000, // 30 secondes
  MAX_NOTIFICATIONS_PER_PAGE: 20,
  AUTO_MARK_READ_DELAY: 3000, // 3 secondes
  TOAST_DISPLAY_DURATION: 5000, // 5 secondes
};

// Configuration des RDQ
export const RDQ_CONFIG = {
  DEFAULT_PAGE_SIZE: 10,
  MAX_DESCRIPTION_LENGTH: 1000,
  MAX_TITLE_LENGTH: 100,
  AUTO_SAVE_DELAY: 2000, // 2 secondes
};

// URLs des endpoints API
export const API_ENDPOINTS = {
  // Authentification
  LOGIN: '/api/auth/login',
  LOGOUT: '/api/auth/logout',
  REFRESH: '/api/auth/refresh',
  
  // Utilisateurs
  USERS: '/api/users',
  USER_PROFILE: '/api/users/profile',
  
  // RDQ
  RDQ: '/api/rdq',
  RDQ_SEARCH: '/api/rdq/search',
  RDQ_STATS: '/api/rdq/stats',
  
  // Notifications
  NOTIFICATIONS: '/api/notifications',
  NOTIFICATION_PREFERENCES: '/api/notifications/preferences',
  NOTIFICATION_STATS: '/api/notifications/stats',
  MARK_ALL_READ: '/api/notifications/mark-all-read',
};

// Messages d'erreur par défaut
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Erreur de connexion au serveur',
  UNAUTHORIZED: 'Session expirée, veuillez vous reconnecter',
  FORBIDDEN: 'Accès non autorisé',
  NOT_FOUND: 'Ressource non trouvée',
  SERVER_ERROR: 'Erreur interne du serveur',
  VALIDATION_ERROR: 'Données invalides',
  UNKNOWN_ERROR: 'Une erreur inattendue est survenue',
};

// Messages de succès
export const SUCCESS_MESSAGES = {
  NOTIFICATION_MARKED_READ: 'Notification marquée comme lue',
  ALL_NOTIFICATIONS_MARKED_READ: 'Toutes les notifications marquées comme lues',
  NOTIFICATION_DELETED: 'Notification supprimée',
  PREFERENCES_UPDATED: 'Préférences mises à jour',
  RDQ_CREATED: 'RDQ créée avec succès',
  RDQ_UPDATED: 'RDQ mise à jour avec succès',
  RDQ_DELETED: 'RDQ supprimée avec succès',
};

// Couleurs et styles pour les types de notifications
export const NOTIFICATION_STYLES = {
  RDQ_CREATED: { color: '#4CAF50', icon: 'plus-circle' },
  RDQ_UPDATED: { color: '#2196F3', icon: 'edit' },
  RDQ_ASSIGNED: { color: '#FF9800', icon: 'user-plus' },
  RDQ_STATUS_CHANGED: { color: '#9C27B0', icon: 'refresh' },
  RDQ_DEADLINE_APPROACHING: { color: '#FF5722', icon: 'clock' },
  RDQ_OVERDUE: { color: '#F44336', icon: 'exclamation-triangle' },
  RDQ_COMMENTED: { color: '#607D8B', icon: 'comment' },
  RDQ_CANCELLED: { color: '#795548', icon: 'times-circle' },
  SYSTEM_MAINTENANCE: { color: '#9E9E9E', icon: 'tools' },
  USER_WELCOME: { color: '#E91E63', icon: 'hand-wave' },
  GENERAL_INFO: { color: '#00BCD4', icon: 'info-circle' },
};

// Formats de date
export const DATE_FORMATS = {
  FULL_DATE: 'dd/MM/yyyy HH:mm:ss',
  SHORT_DATE: 'dd/MM/yyyy',
  TIME_ONLY: 'HH:mm',
  RELATIVE: 'relative', // sera géré par des utilitaires de date
};

// Configuration de l'environnement
export const ENV_CONFIG = {
  isDevelopment: process.env.NODE_ENV === 'development',
  isProduction: process.env.NODE_ENV === 'production',
  isTest: process.env.NODE_ENV === 'test',
  appVersion: process.env.REACT_APP_VERSION || '1.0.0',
  buildDate: process.env.REACT_APP_BUILD_DATE || new Date().toISOString(),
};

// Configuration du debug
export const DEBUG_CONFIG = {
  enableConsoleLogging: ENV_CONFIG.isDevelopment,
  enableNetworkLogging: ENV_CONFIG.isDevelopment,
  enableStateLogging: ENV_CONFIG.isDevelopment && !!process.env.REACT_APP_DEBUG_STATE,
};