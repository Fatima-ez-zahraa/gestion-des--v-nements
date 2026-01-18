package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ReservationServer extends UnicastRemoteObject implements ReservationService {
    private Map<String, User> users; // username -> User object
    private List<Event> events;
    private Map<String, Set<String>> reservations; // eventTitle -> Set of usernames
    private static final String ADMIN_USERNAME = "ADMIN";
    private static final String ADMIN_PASSWORD = "1234";

    public ReservationServer() throws RemoteException {
        super();
        users = new HashMap<>();
        events = new ArrayList<>();
        reservations = new HashMap<>();
        // Créer l'administrateur par défaut
        createAdmin();
    }

    private void createAdmin() {
        try {
            String hashedPassword = hashPassword(ADMIN_PASSWORD);
            users.put(ADMIN_USERNAME, new User(ADMIN_USERNAME, hashedPassword, "ADMIN"));
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error creating admin: " + e.getMessage());
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public boolean registerUser(String username, String password, String role) throws RemoteException {
        if (users.containsKey(username)) {
            return false;
        }
        try {
            String hashedPassword = hashPassword(password);
            users.put(username, new User(username, hashedPassword, role));
            return true;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) throws RemoteException {
        User user = users.get(username);
        if (user == null) {
            return false;
        }
        try {
            String hashedPassword = hashPassword(password);
            return user.getHashedPassword().equals(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    @Override
    public boolean createOrganizer(String adminUsername, String adminPassword, String newOrganizerUsername, String newOrganizerPassword) throws RemoteException {
        // Vérifier si l'utilisateur est admin
        if (!isAdmin(adminUsername, adminPassword)) {
            return false;
        }
        // Créer le nouvel organisateur
        return registerUser(newOrganizerUsername, newOrganizerPassword, "ORGANIZER");
    }

    @Override
    public boolean isAdmin(String username, String password) throws RemoteException {
        User user = users.get(username);
        if (user == null) {
            return false;
        }
        try {
            String hashedPassword = hashPassword(password);
            return user.getRole().equals("ADMIN") && user.getHashedPassword().equals(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    @Override
    public String getUserRole(String username) throws RemoteException {
        User user = users.get(username);
        return user != null ? user.getRole() : null;
    }

    @Override
    public boolean createEvent(String title, String date, int capacity, String organizerUsername) throws RemoteException {
        if (events.stream().anyMatch(e -> e.getTitle().equals(title))) {
            return false;
        }
        
        Event event = new Event(title, date, capacity, organizerUsername);
        events.add(event);
        reservations.put(title, new HashSet<>());
        return true;
    }

    @Override
    public List<Event> getEvents() throws RemoteException {
        return new ArrayList<>(events);
    }

    @Override
    public List<Event> getOrganizerEvents(String organizerUsername) throws RemoteException {
        List<Event> organizerEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getOrganizer().equals(organizerUsername)) {
                organizerEvents.add(event);
            }
        }
        return organizerEvents;
    }

    @Override
    public boolean reserveTicket(String username, String eventTitle) throws RemoteException {
        Event event = events.stream()
                .filter(e -> e.getTitle().equals(eventTitle))
                .findFirst()
                .orElse(null);

        if (event == null || event.isFull()) {
            return false;
        }

        Set<String> eventReservations = reservations.get(eventTitle);
        if (eventReservations.contains(username)) {
            return false;
        }

        eventReservations.add(username);
        event.setReservedTickets(event.getReservedTickets() + 1);
        return true;
    }

    @Override
    public int getReservedTicketsCount(String eventTitle) throws RemoteException {
        Event event = events.stream()
                .filter(e -> e.getTitle().equals(eventTitle))
                .findFirst()
                .orElse(null);
        return event != null ? event.getReservedTickets() : 0;
    }

    public static void main(String[] args) {
        try {
            ReservationServer server = new ReservationServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ReservationService", server);
            System.out.println("Server is ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
} 