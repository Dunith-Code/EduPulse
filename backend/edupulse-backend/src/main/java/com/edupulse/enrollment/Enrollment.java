package com.edupulse.enrollment;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "institute_id", nullable = false, updatable = false)
    private UUID instituteId;

    @Column(name = "student_id", nullable = false, updatable = false)
    private UUID studentId;

    @Column(name = "batch_id", nullable = false, updatable = false)
    private UUID batchId;

    @Column(name = "enrolled_on", nullable = false)
    private LocalDate enrolledOn;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Enrollment() {
    }

    public Enrollment(UUID instituteId, UUID studentId, UUID batchId, LocalDate enrolledOn) {
        this.instituteId = instituteId;
        this.studentId = studentId;
        this.batchId = batchId;
        this.enrolledOn = enrolledOn;
    }

    public UUID getId() { return id; }
    public UUID getInstituteId() { return instituteId; }
    public UUID getStudentId() { return studentId; }
    public UUID getBatchId() { return batchId; }
    public LocalDate getEnrolledOn() { return enrolledOn; }
    public boolean isActive() { return active; }
}