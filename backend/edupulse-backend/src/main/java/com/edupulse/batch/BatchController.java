package com.edupulse.batch;

import com.edupulse.batch.BatchService.BatchResponse;
import com.edupulse.batch.BatchService.CreateBatchRequest;
import com.edupulse.common.CurrentTenant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final BatchService service;

    public BatchController(BatchService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTITUTE_ADMIN')")
    public BatchResponse create(@Valid @RequestBody CreateBatchRequest request) {
        return service.create(CurrentTenant.instituteId(), request);
    }

    @GetMapping
    public List<BatchResponse> list() {
        return service.list(CurrentTenant.instituteId());
    }

    @GetMapping("/{id}")
    public BatchResponse get(@PathVariable UUID id) {
        return service.get(CurrentTenant.instituteId(), id);
    }
}