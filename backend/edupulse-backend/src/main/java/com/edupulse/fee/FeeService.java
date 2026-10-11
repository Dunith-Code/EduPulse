package com.edupulse.fee;

import com.edupulse.audit.AuditService;
import com.edupulse.batch.BatchService;
import com.edupulse.batch.BatchService.BatchResponse;
import com.edupulse.enrollment.EnrollmentService;
import com.edupulse.enrollment.EnrollmentService.ActiveEnrollment;
import com.edupulse.student.StudentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeeService {

    static final int DUE_DAY_OF_MONTH = 10;

    public record GenerateRequest(
            @NotBlank @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$",
                    message = "month must be yyyy-MM") String month) {}

    public record GenerateResult(String month, int created, int alreadyExisted) {}

    public record ChargeResponse(UUID id, UUID studentId, UUID batchId, LocalDate period,
                                 BigDecimal amount, LocalDate dueDate) {}

    private final FeeChargeRepository charges;
    private final EnrollmentService enrollments;
    private final BatchService batches;
    private final StudentService students;
    private final AuditService audit;

    public FeeService(FeeChargeRepository charges, EnrollmentService enrollments,
                      BatchService batches, StudentService students, AuditService audit) {
        this.charges = charges;
        this.enrollments = enrollments;
        this.batches = batches;
        this.students = students;
        this.audit = audit;
    }

    /**
     * Creates one charge per active enrollment for the month. Safe to run repeatedly:
     * existing charges are skipped, and the amount is a snapshot of the batch fee.
     */
    @Transactional
    public GenerateResult generateMonthly(UUID instituteId, UUID actorId, GenerateRequest r) {
        YearMonth month = YearMonth.parse(r.month());
        LocalDate period = month.atDay(1);
        LocalDate due = period.plusDays(DUE_DAY_OF_MONTH - 1);

        Map<UUID, BigDecimal> feeByBatch = batches.list(instituteId).stream()
                .filter(BatchResponse::active)
                .collect(Collectors.toMap(BatchResponse::id, BatchResponse::monthlyFee));

        Set<UUID> alreadyCharged = charges.findByInstituteIdAndPeriod(instituteId, period).stream()
                .map(FeeCharge::getEnrollmentId)
                .collect(Collectors.toSet());

        int created = 0;
        int existed = 0;
        for (ActiveEnrollment e : enrollments.activeEnrollments(instituteId)) {
            if (e.enrolledOn().isAfter(month.atEndOfMonth())) {
                continue;
            }
            if (alreadyCharged.contains(e.id())) {
                existed++;
                continue;
            }
            BigDecimal fee = feeByBatch.get(e.batchId());
            if (fee == null) {
                continue;
            }
            charges.save(new FeeCharge(instituteId, e.id(), e.studentId(), e.batchId(),
                    period, fee, due));
            created++;
        }

        audit.record(instituteId, actorId, "FEES_GENERATED", "FEE_CHARGE", null,
                "month=%s created=%d alreadyExisted=%d".formatted(r.month(), created, existed));
        return new GenerateResult(r.month(), created, existed);
    }

    @Transactional(readOnly = true)
    public List<ChargeResponse> chargesForStudent(UUID instituteId, UUID studentId) {
        students.get(instituteId, studentId);
        return charges.findByInstituteIdAndStudentIdOrderByPeriodDesc(instituteId, studentId)
                .stream()
                .map(c -> new ChargeResponse(c.getId(), c.getStudentId(), c.getBatchId(),
                        c.getPeriod(), c.getAmount(), c.getDueDate()))
                .toList();
    }
}