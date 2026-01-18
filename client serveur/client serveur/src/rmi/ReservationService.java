package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ReservationService extends Remote {
    // Authentification
    boolean registerUser(String username, String password, String role) throws RemoteException;
    boolean authenticateUser(String username, String password) throws RemoteException;
    boolean createOrganizer(String adminUsername, String adminPassword, String newOrganizerUsername, String newOrganizerPassword) throws RemoteException;
    boolean isAdmin(String username, String password) throws RemoteException;
    String getUserRole(String username) throws RemoteException;
    
    // Gestion des événements
    boolean createEvent(String title, String date, int capacity, String organizerUsername) throws RemoteException;
    List<Event> getEvents() throws RemoteException;
    List<Event> getOrganizerEvents(String organizerUsername) throws RemoteException;
    
    // Réservation
    boolean reserveTicket(String username, String eventTitle) throws RemoteException;
    int getReservedTicketsCount(String eventTitle) throws RemoteException;
} 