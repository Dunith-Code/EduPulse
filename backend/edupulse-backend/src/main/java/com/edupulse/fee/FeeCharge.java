package com.edupulse.fee;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fee_charges")
public class FeeCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "institute_id", nullable = false, updatable = false)
    private UUID instituteId;

    @Column(name = "enrollment_id", nullable = false, updatable = false)
    private UUID enrollmentId;

    @Column(name = "student_id", nullable = false, updatable = false)
    private UUID studentId;

    @Column(name = "batch_id", nullable = false, updatable = false)
    private UUID batchId;

    @Column(nullable = false, updatable = false)
    private LocalDate period;

    @Column(nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false, updatable = false)
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected FeeCharge() {
    }

    public FeeCharge(UUID instituteId, UUID enrollmentId, UUID studentId, UUID batchId,
                     LocalDate period, BigDecimal amount, LocalDate dueDate) {
        this.instituteId = instituteId;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.batchId = batchId;
        this.period = period;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public UUID getId() { return id; }
    public UUID getInstituteId() { return instituteId; }
    public UUID getEnrollmentId() { return enrollmentId; }
    public UUID getStudentId() { return studentId; }
    public UUID getBatchId() { return batchId; }
    public LocalDate getPeriod() { return period; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
}