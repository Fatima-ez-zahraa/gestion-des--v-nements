# Application de Réservation de Billets

Cette application permet la gestion et la réservation de billets d'événements en utilisant Java RMI.

## Fonctionnalités

- Authentification des utilisateurs (organisateurs et clients)
- Création et gestion d'événements par les organisateurs
- Consultation et réservation de billets par les clients
- Gestion des capacités et des réservations

## Prérequis

- Java JDK 8 ou supérieur
- IDE Java (recommandé)

## Structure du projet

```
src/
  rmi/
    ReservationService.java    # Interface RMI
    Event.java                 # Classe de données pour les événements
    ReservationServer.java     # Implémentation du serveur
    ReservationClient.java     # Application cliente
```

## Compilation

Pour compiler le projet, exécutez les commandes suivantes dans le répertoire racine :

```bash
javac -d bin src/rmi/*.java
```

## Exécution

1. Démarrer le serveur :
```bash
java -cp bin rmi.ReservationServer
```

2. Démarrer le client (dans un nouveau terminal) :
```bash
java -cp bin rmi.ReservationClient
```

## Utilisation

### Authentification
- À la première connexion, choisissez l'option d'inscription
- Entrez un nom d'utilisateur et choisissez votre rôle (organisateur ou client)
- Pour les connexions suivantes, utilisez l'option de connexion

### Organisateur
- Créer des événements avec titre, date et capacité
- Consulter la liste des événements créés
- Voir le nombre de billets réservés

### Client
- Consulter la liste des événements disponibles
- Réserver des billets pour les événements
- Être notifié si un événement est complet

## Notes

- Le serveur doit être démarré avant le client
- Les données sont stockées en mémoire (pas de persistance)
- Le port par défaut est 1099 