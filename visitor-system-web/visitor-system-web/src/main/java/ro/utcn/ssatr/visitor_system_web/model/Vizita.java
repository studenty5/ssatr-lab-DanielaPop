package ro.utcn.ssatr.visitor_system_web.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visits")
public class Vizita {

    @Id
    private UUID id;

    private String visitorName;
    private String email;
    private String hostName;
    private String visitorType;

    private LocalDateTime startTime;
    private LocalDateTime expirationTime;

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private String status;

    public Vizita() {
        this.id = UUID.randomUUID();
    }

    // GETTERS & SETTERS
    public UUID getId() { return id; }
    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public String getVisitorType() { return visitorType; }
    public void setVisitorType(String visitorType) { this.visitorType = visitorType; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getExpirationTime() { return expirationTime; }
    public void setExpirationTime(LocalDateTime expirationTime) { this.expirationTime = expirationTime; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}