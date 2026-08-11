package dev.interviewos.api.interview;

import dev.interviewos.api.user.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "interview")
public class Interview {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private String stage;

    @Column(nullable = false)
    private boolean prepared;

    public UUID getId() { return id; }
    public AppUser getUser() { return user; }
    public String getCompany() { return company; }
    public String getRole() { return role; }
    public Instant getStartsAt() { return startsAt; }
    public String getStage() { return stage; }
    public boolean isPrepared() { return prepared; }

    public void setUser(AppUser user) { this.user = user; }
    public void setCompany(String company) { this.company = company; }
    public void setRole(String role) { this.role = role; }
    public void setStartsAt(Instant startsAt) { this.startsAt = startsAt; }
    public void setStage(String stage) { this.stage = stage; }
    public void setPrepared(boolean prepared) { this.prepared = prepared; }
}
