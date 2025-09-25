/**
 * Service de remplacement des données mock - TM-45
 * Remplace les imports de mockData.ts par des appels API
 */

import { ApiService } from './ApiService';
import type { User, Client, Projet, RDQ } from '../types';

export interface MockDataResponse {
  managers: User[];
  collaborateurs: User[];
  admins: User[];
  clients: Client[];
  projets: Projet[];
  rdqs: RDQ[];
}

export interface AdminDataResponse {
  managers: User[];
  collaborateurs: User[];
  clients: Client[];
  admins: User[];
}

export interface CollaborateurDataResponse {
  rdqs: RDQ[];
  clients: Client[];
}

export interface ManagerDataResponse {
  rdqs: RDQ[];
  collaborateurs: User[];
  clients: Client[];
}

export interface ModalDataResponse {
  collaborateurs: User[];
  clients: Client[];
  projets: Projet[];
}

export class MockDataService {
  
  /**
   * Récupère toutes les données nécessaires au frontend
   * Remplace les imports : mockManagers, mockCollaborateurs, mockAdmins, mockClients, mockProjets, mockRDQs
   */
  static async getAllMockData(): Promise<MockDataResponse> {
    return ApiService.get<MockDataResponse>('/mock-data/all');
  }

  /**
   * Données spécifiques pour AdminDashboard.tsx
   * Remplace : mockManagers, mockCollaborateurs, mockClients, mockAdmins
   */
  static async getAdminData(): Promise<AdminDataResponse> {
    return ApiService.get<AdminDataResponse>('/mock-data/admin-data');
  }

  /**
   * Données spécifiques pour CollaborateurDashboard.tsx
   * Remplace : mockRDQs, mockClients
   */
  static async getCollaborateurData(): Promise<CollaborateurDataResponse> {
    return ApiService.get<CollaborateurDataResponse>('/mock-data/collaborateur-data');
  }

  /**
   * Données spécifiques pour ManagerDashboard.tsx
   * Remplace : mockRDQs, mockCollaborateurs, mockClients
   */
  static async getManagerData(): Promise<ManagerDataResponse> {
    return ApiService.get<ManagerDataResponse>('/mock-data/manager-data');
  }

  /**
   * Données spécifiques pour CreateRDQModal.tsx et RDQModal.tsx
   * Remplace : mockCollaborateurs, mockClients, mockProjets
   */
  static async getModalData(): Promise<ModalDataResponse> {
    return ApiService.get<ModalDataResponse>('/mock-data/modal-data');
  }

  // Services individuels pour les entités

  /**
   * Service pour les utilisateurs
   */
  static async getAllUsers(): Promise<User[]> {
    return ApiService.get<User[]>('/users');
  }

  static async getManagers(): Promise<User[]> {
    return ApiService.get<User[]>('/users/managers');
  }

  static async getCollaborateurs(): Promise<User[]> {
    return ApiService.get<User[]>('/users/collaborateurs');
  }

  static async getAdmins(): Promise<User[]> {
    return ApiService.get<User[]>('/users/admins');
  }

  static async getUserById(id: number): Promise<User> {
    return ApiService.get<User>(`/users/${id}`);
  }

  /**
   * Service pour les clients
   */
  static async getAllClients(): Promise<Client[]> {
    return ApiService.get<Client[]>('/clients');
  }

  static async getClientById(id: number): Promise<Client> {
    return ApiService.get<Client>(`/clients/${id}`);
  }

  static async searchClients(nom: string): Promise<Client[]> {
    return ApiService.get<Client[]>(`/clients/search?nom=${encodeURIComponent(nom)}`);
  }

  /**
   * Service pour les projets
   */
  static async getAllProjets(): Promise<Projet[]> {
    return ApiService.get<Projet[]>('/projets');
  }

  static async getProjetById(id: number): Promise<Projet> {
    return ApiService.get<Projet>(`/projets/${id}`);
  }

  static async searchProjets(nom: string): Promise<Projet[]> {
    return ApiService.get<Projet[]>(`/projets/search?nom=${encodeURIComponent(nom)}`);
  }

  /**
   * Service pour les RDQ
   */
  static async getAllRDQs(): Promise<RDQ[]> {
    return ApiService.get<RDQ[]>('/rdq');
  }

  static async getRDQById(id: number): Promise<RDQ> {
    return ApiService.get<RDQ>(`/rdq/${id}`);
  }

  /**
   * Initialise les données de test dans la base de données
   */
  static async initializeTestData(): Promise<{ status: string; message: string }> {
    return ApiService.post<{ status: string; message: string }>('/mock-data/initialize');
  }
}