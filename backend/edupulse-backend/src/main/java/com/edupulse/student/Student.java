package com.edupulse.student;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "institute_id", nullable = false, updatable = false)
    private UUID instituteId;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "parent_name", nullable = false, length = 150)
    private String parentName;

    @Column(name = "parent_phone", nullable = false, length = 20)
    private String parentPhone;

    @Column(name = "qr_code", nullable = false, updatable = false, length = 64)
    private String qrCode;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Student() {
    }

    public Student(UUID instituteId, String fullName, String parentName,
                   String parentPhone, String qrCode) {
        this.instituteId = instituteId;
        this.fullName = fullName;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.qrCode = qrCode;
    }

    public UUID getId() { return id; }
    public UUID getInstituteId() { return instituteId; }
    public String getFullName() { return fullName; }
    public String getParentName() { return parentName; }
    public String getParentPhone() { return parentPhone; }
    public String getQrCode() { return qrCode; }
    public boolean isActive() { return active; }
}