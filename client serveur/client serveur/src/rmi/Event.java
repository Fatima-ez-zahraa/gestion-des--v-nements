package rmi;

import java.io.Serializable;

public class Event implements Serializable {
    private String title;
    private String date;
    private int capacity;
    private String organizer;
    private int reservedTickets;

    public Event(String title, String date, int capacity, String organizer) {
        this.title = title;
        this.date = date;
        this.capacity = capacity;
        this.organizer = organizer;
        this.reservedTickets = 0;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public int getCapacity() { return capacity; }
    public String getOrganizer() { return organizer; }
    public int getReservedTickets() { return reservedTickets; }
    public boolean isFull() { return reservedTickets >= capacity; }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
    public void setReservedTickets(int reservedTickets) {
        this.reservedTickets = reservedTickets;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", capacity=" + capacity +
                ", reservedTickets=" + reservedTickets +
                '}';
    }
} 