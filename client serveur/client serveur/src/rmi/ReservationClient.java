package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class ReservationClient {
    private static ReservationService service;
    private static String currentUser;
    private static String currentRole;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (ReservationService) registry.lookup("ReservationService");
            
            while (true) {
                if (currentUser == null) {
                    handleMainMenu();
                } else {
                    if (currentRole.equals("ADMIN")) {
                        handleAdminMenu();
                    } else if (currentRole.equals("ORGANIZER")) {
                        handleOrganizerMenu();
                    } else {
                        handleClientMenu();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void handleMainMenu() throws Exception {
        System.out.println("\n=== Menu Principal ===");
        System.out.println("1. S'inscrire (Client uniquement)");
        System.out.println("2. Se connecter");
        System.out.print("Choix: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        if (choice == 1) {
            handleClientRegistration();
        } else {
            handleLogin();
        }
    }

    private static void handleClientRegistration() throws Exception {
        System.out.println("\n=== Inscription Client ===");
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();
        
        if (service.registerUser(username, password, "CLIENT")) {
            System.out.println("Inscription réussie!");
            currentUser = username;
            currentRole = "CLIENT";
        } else {
            System.out.println("Échec de l'inscription. L'utilisateur existe peut-être déjà.");
        }
    }

    private static void handleLogin() throws Exception {
        System.out.println("\n=== Connexion ===");
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();
        
        if (service.authenticateUser(username, password)) {
            currentUser = username;
            currentRole = service.getUserRole(username);
            System.out.println("Connexion réussie!");
            
            if (currentRole.equals("ADMIN")) {
                System.out.println("Bienvenue, Administrateur!");
            } else if (currentRole.equals("ORGANIZER")) {
                System.out.println("Bienvenue, Organisateur!");
            } else {
                System.out.println("Bienvenue, Client!");
            }
        } else {
            System.out.println("Échec de la connexion. Vérifiez vos identifiants.");
        }
    }

    private static void handleAdminMenu() throws Exception {
        System.out.println("\n=== Menu Administrateur ===");
        System.out.println("1. Créer un organisateur");
        System.out.println("2. Créer un événement");
        System.out.println("3. Voir mes événements");
        System.out.println("4. Déconnexion");
        System.out.print("Choix: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                System.out.print("Nom d'utilisateur du nouvel organisateur: ");
                String newUsername = scanner.nextLine();
                System.out.print("Mot de passe du nouvel organisateur: ");
                String newPassword = scanner.nextLine();
                
                if (service.createOrganizer(currentUser, "1234", newUsername, newPassword)) {
                    System.out.println("Organisateur créé avec succès!");
                    System.out.println("Identifiants de connexion pour l'organisateur :");
                    System.out.println("Nom d'utilisateur : " + newUsername);
                    System.out.println("Mot de passe : " + newPassword);
                } else {
                    System.out.println("Échec de la création de l'organisateur.");
                }
                break;
                
            case 2:
                handleCreateEvent();
                break;
                
            case 3:
                List<Event> events = service.getOrganizerEvents(currentUser);
                System.out.println("\nVos événements:");
                for (Event event : events) {
                    System.out.println(event);
                }
                break;
                
            case 4:
                currentUser = null;
                currentRole = null;
                break;
        }
    }

    private static void handleCreateEvent() throws Exception {
        System.out.print("Titre de l'événement: ");
        String title = scanner.nextLine();
        
        // Validation de la date
        String date;
        boolean validDate = false;
        do {
            System.out.print("Date (format jj/mm/aaaa): ");
            date = scanner.nextLine();
            if (date.matches("\\d{2}/\\d{2}/\\d{4}")) {
                validDate = true;
            } else {
                System.out.println("Format de date invalide. Veuillez utiliser le format jj/mm/aaaa");
            }
        } while (!validDate);
        
        // Validation de la capacité
        int capacity;
        do {
            System.out.print("Capacité (nombre positif): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Veuillez entrer un nombre valide");
                scanner.next();
            }
            capacity = scanner.nextInt();
            if (capacity <= 0) {
                System.out.println("La capacité doit être strictement positive");
            }
        } while (capacity <= 0);
        scanner.nextLine(); // Consume newline
        
        if (service.createEvent(title.toLowerCase(), date, capacity, currentUser)) {
            System.out.println("Événement créé avec succès!");
        } else {
            System.out.println("Échec de la création de l'événement.");
        }
    }

    private static void handleOrganizerMenu() throws Exception {
        System.out.println("\n=== Menu Organisateur ===");
        System.out.println("1. Créer un événement");
        System.out.println("2. Voir mes événements");
        System.out.println("3. Déconnexion");
        System.out.print("Choix: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                System.out.print("Titre de l'événement: ");
                String title = scanner.nextLine();
                System.out.print("Date: ");
                String date = scanner.nextLine();
                System.out.print("Capacité: ");
                int capacity = scanner.nextInt();
                
                if (service.createEvent(title, date, capacity, currentUser)) {
                    System.out.println("Événement créé avec succès!");
                } else {
                    System.out.println("Échec de la création de l'événement.");
                }
                break;
                
            case 2:
                List<Event> events = service.getOrganizerEvents(currentUser);
                System.out.println("\nVos événements:");
                for (Event event : events) {
                    System.out.println(event);
                }
                break;
                
            case 3:
                currentUser = null;
                currentRole = null;
                break;
        }
    }

    private static void handleClientMenu() throws Exception {
        System.out.println("\n=== Menu Client ===");
        System.out.println("1. Voir les événements disponibles");
        System.out.println("2. Réserver un billet");
        System.out.println("3. Déconnexion");
        System.out.print("Choix: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                List<Event> events = service.getEvents();
                System.out.println("\nÉvénements disponibles:");
                for (Event event : events) {
                    System.out.println(event);
                }
                break;
                
            case 2:
                System.out.print("Titre de l'événement: ");
                String eventTitle = scanner.nextLine();
                
                if (service.reserveTicket(currentUser, eventTitle)) {
                    System.out.println("Réservation réussie!");
                } else {
                    System.out.println("Échec de la réservation. L'événement est peut-être complet.");
                }
                break;
                
            case 3:
                currentUser = null;
                currentRole = null;
                break;
        }
    }
} 