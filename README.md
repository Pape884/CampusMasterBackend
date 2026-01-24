# CampusMasterBackend

# Demarrer serveur spring boot
./mvnw spring-boot:run

# Supprimer le cache
rm -rf target



Ton API Spring Boot doit exposer ces endpoints :

  -------------- Users ---------------------

    GET /api/users - Liste des utilisateurs avec filtres

    GET /api/users/{id} - Détails d'un utilisateur

    POST /api/users - Création d'un utilisateur

    PUT /api/users/{id} - Mise à jour d'un utilisateur

    PATCH /api/users/{id}/status - Changement de statut

    DELETE /api/users/{id} - Suppression d'un utilisateur

    GET /api/users/stats - Statistiques

    GET /api/users/search - Recherche rapide


 -------------- Departements ----------------


    GET /api/departments - Liste des départements avec filtres

    GET /api/departments/{id} - Détails d'un département

    POST /api/departments - Création d'un département

    PUT /api/departments/{id} - Mise à jour d'un département

    PATCH /api/departments/{id}/status - Changement de statut

    DELETE /api/departments/{id} - Suppression d'un département

    GET /api/departments/stats - Statistiques

    GET /api/departments/search - Recherche rapide

    GET /api/departments/{id}/teachers - Enseignants du département

    GET /api/departments/{id}/students - Étudiants du département


