package com.edupulse.attendance;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "institute_id", nullable = false, updatable = false)
    private UUID instituteId;

    @Column(name = "batch_id", nullable = false, updatable = false)
    private UUID batchId;

    @Column(name = "student_id", nullable = false, updatable = false)
    private UUID studentId;

    @Column(name = "session_date", nullable = false, updatable = false)
    private LocalDate sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AttendanceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AttendanceMethod method;

    @Column(name = "marked_by", nullable = false)
    private UUID markedBy;

    @Column(name = "marked_at", nullable = false)
    private Instant markedAt = Instant.now();

    protected Attendance() {
    }

    public Attendance(UUID instituteId, UUID batchId, UUID studentId, LocalDate sessionDate,
                      AttendanceStatus status, AttendanceMethod method, UUID markedBy) {
        this.instituteId = instituteId;
        this.batchId = batchId;
        this.studentId = studentId;
        this.sessionDate = sessionDate;
        this.status = status;
        this.method = method;
        this.markedBy = markedBy;
    }

    public void correct(AttendanceStatus status, AttendanceMethod method, UUID markedBy) {
        this.status = status;
        this.method = method;
        this.markedBy = markedBy;
        this.markedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getBatchId() { return batchId; }
    public UUID getStudentId() { return studentId; }
    public LocalDate getSessionDate() { return sessionDate; }
    public AttendanceStatus getStatus() { return status; }
    public AttendanceMethod getMethod() { return method; }
    public Instant getMarkedAt() { return markedAt; }
}