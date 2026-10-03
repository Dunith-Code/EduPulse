package com.edupulse.batch;

import com.edupulse.batch.BatchService.BatchResponse;
import com.edupulse.batch.BatchService.CreateBatchRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public BatchResponse create(@RequestHeader("X-Institute-Id") UUID instituteId,
                                @Valid @RequestBody CreateBatchRequest request) {
        return service.create(instituteId, request);
    }

    @GetMapping
    public List<BatchResponse> list(@RequestHeader("X-Institute-Id") UUID instituteId) {
        return service.list(instituteId);
    }

    @GetMapping("/{id}")
    public BatchResponse get(@RequestHeader("X-Institute-Id") UUID instituteId,
                             @PathVariable UUID id) {
        return service.get(instituteId, id);
    }
}