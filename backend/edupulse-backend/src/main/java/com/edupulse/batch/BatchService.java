package com.edupulse.batch;

import com.edupulse.common.NotFoundException;
import com.edupulse.tenant.TenantService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class BatchService {

    public record CreateBatchRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Size(max = 100) String subject,
            @NotNull @DecimalMin("0.00") BigDecimal monthlyFee,
            @NotNull DayOfWeek dayOfWeek,
            @NotNull LocalTime startTime,
            @NotNull LocalTime endTime) {}

    public record BatchResponse(UUID id, String name, String subject, BigDecimal monthlyFee,
                                DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime,
                                boolean active) {}

    private final BatchRepository batches;
    private final TenantService tenants;

    public BatchService(BatchRepository batches, TenantService tenants) {
        this.batches = batches;
        this.tenants = tenants;
    }

    @Transactional
    public BatchResponse create(UUID instituteId, CreateBatchRequest r) {
        tenants.requireInstitute(instituteId);
        if (!r.endTime().isAfter(r.startTime())) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        Batch saved = batches.save(new Batch(instituteId, r.name(), r.subject(),
                r.monthlyFee(), r.dayOfWeek(), r.startTime(), r.endTime()));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> list(UUID instituteId) {
        return batches.findByInstituteIdOrderByNameAsc(instituteId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BatchResponse get(UUID instituteId, UUID batchId) {
        return batches.findByIdAndInstituteId(batchId, instituteId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Batch not found: " + batchId));
    }

    private BatchResponse toResponse(Batch b) {
        return new BatchResponse(b.getId(), b.getName(), b.getSubject(), b.getMonthlyFee(),
                b.getDayOfWeek(), b.getStartTime(), b.getEndTime(), b.isActive());
    }
}