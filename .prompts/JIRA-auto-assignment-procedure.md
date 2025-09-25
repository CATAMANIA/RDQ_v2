# 🔄 Procédure automatique d'assignation JIRA

**Date de création** : 25 septembre 2025  
**Développeur principal** : André-Pierre ABELLARD (`63fdcc6f7c30bbd6b33df040`)  

---

## 🎯 Objectif

Éviter les conflits entre développeurs en assignant automatiquement les tickets JIRA et en changeant le statut vers "En cours" dès le début de l'implémentation.

---

## 🔧 Procédure automatique

### Avant toute implémentation de ticket JIRA

#### 1. Assignation automatique
```javascript
// Assigner le ticket au développeur connecté
mcp_atlassian_editJiraIssue({
  cloudId: "d607f1fb-dba4-493f-97d8-759418d51cc0",
  issueIdOrKey: "TM-XX", // Remplacer XX par le numéro du ticket
  fields: {
    assignee: {
      accountId: "63fdcc6f7c30bbd6b33df040" // André-Pierre ABELLARD
    }
  }
})
```

#### 2. Transition vers "En cours"
```javascript
// Passer le statut à "En cours"
mcp_atlassian_transitionJiraIssue({
  cloudId: "d607f1fb-dba4-493f-97d8-759418d51cc0", 
  issueIdOrKey: "TM-XX",
  transition: { id: "21" } // ID pour transition "En cours"
})
```

#### 3. Commentaire de début d'implémentation
```javascript
// Ajouter commentaire de début
mcp_atlassian_addCommentToJiraIssue({
  cloudId: "d607f1fb-dba4-493f-97d8-759418d51cc0",
  issueIdOrKey: "TM-XX",
  commentBody: "🚀 **DÉBUT D'IMPLÉMENTATION**\n\nAssignation automatique au développeur et passage en statut 'En cours' pour éviter les conflits.\n\n📋 **Actions prévues :**\n- Analyse des critères d'acceptation\n- Création de la branche feature/TM-XX\n- Développement selon les spécifications\n- Tests et validation\n- Création Pull Request\n\n⏱️ Début des travaux : [DATE_ACTUELLE]"
})
```

---

## 📋 Informations de configuration

### Compte développeur principal
- **Nom** : André-Pierre ABELLARD
- **Email** : ap.abellard@catamania.com
- **Account ID** : `63fdcc6f7c30bbd6b33df040`
- **Statut** : Actif

### Instance Atlassian
- **Cloud ID** : `d607f1fb-dba4-493f-97d8-759418d51cc0`
- **URL** : https://cat-amania-sandbox.atlassian.net
- **Project** : TM (Test MCP)
- **Permissions** : read:jira-work, write:jira-work

### Transitions JIRA disponibles
- **ID 11** : "À faire" 
- **ID 21** : "En cours" ⭐ (utilisée automatiquement)
- **ID 31** : "Terminé" (utilisée en fin d'implémentation)

---

## ✅ Avantages de cette procédure

### 🛡️ Évitement des conflits
- **Visibilité** : Indication claire qu'un développeur travaille sur le ticket
- **Coordination** : Autres développeurs peuvent voir l'assignation immédiate
- **Transparence** : Historique complet des actions dans les commentaires

### ⚡ Efficacité
- **Automatisation** : Plus de risque d'oubli d'assignation
- **Rapidité** : Assignation et transition en une seule opération
- **Traçabilité** : Horodatage automatique du début des travaux

### 📊 Suivi projet
- **Métriques** : Temps de développement mieux trackés
- **Responsabilité** : Assignation claire pour chaque ticket
- **Workflow** : Respect du processus de développement

---

## 🚀 Implémentation

Cette procédure doit être exécutée **AUTOMATIQUEMENT** au début de chaque ticket JIRA par l'assistant IA avant de commencer l'analyse ou le développement.

**Ordre d'exécution obligatoire :**
1. ✅ Assignation automatique
2. ✅ Transition vers "En cours"  
3. ✅ Commentaire de début
4. 🔧 **PUIS** début de l'implémentation

---

**Note** : Cette procédure garantit une collaboration efficace et évite les doublons de travail entre développeurs travaillant sur le même projet RDQ_v2.