export type UserRole = 'admin' | 'manager' | 'collaborateur';

export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  role: UserRole;
}

export interface Client {
  idClient: number;
  nom: string;
  contact?: string;
  telephone?: string;
  email?: string;
}

export interface Projet {
  idProjet: number;
  nom: string;
}

export interface Document {
  idDocument: number;
  nomFichier: string;
  type: 'CV' | 'fiche_poste' | 'autre';
  url: string;
  idRDQ: number;
}

export interface Bilan {
  idBilan: number;
  note: number; // 1 à 10
  commentaire?: string;
  auteur: 'manager' | 'collaborateur';
  idRDQ: number;
  dateCreation: Date;
}

export interface ManagerInfo {
  idManager: number;
  nom: string;
  email: string;
}

export interface ProjetInfo {
  idProjet: number;
  nom: string;
  client?: string;
}

export interface CollaborateurInfo {
  idCollaborateur: number;
  nom: string;
  email: string;
}

export interface RDQ {
  idRdq: number;
  titre: string;
  dateHeure: string; // ISO string from API
  adresse?: string;
  mode: 'PRESENTIEL' | 'DISTANCIEL' | 'HYBRIDE';
  statut?: 'PLANIFIE' | 'EN_COURS' | 'TERMINE' | 'ANNULE';
  description?: string;
  manager?: ManagerInfo;
  projet?: ProjetInfo;
  collaborateurs?: CollaborateurInfo[];
  indications?: string;
  dateCreation?: string;
  documents?: Document[];
  bilans?: Bilan[];
  
  // Legacy fields for backward compatibility with existing components
  idRDQ?: number;
  idManager?: number;
  idCollaborateur?: number;
  idClient?: number;
  idProjet?: number;
  client?: Client;
}

export interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => boolean;
  logout: () => void;
  isAuthenticated: boolean;
}

export interface AdminUser extends User {
  role: 'admin';
  dateCreation?: Date;
  statut?: 'actif' | 'inactif';
}