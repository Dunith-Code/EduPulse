package com.edupulse.enrollment;

import com.edupulse.common.CurrentTenant;
import com.edupulse.enrollment.EnrollmentService.EnrollRequest;
import com.edupulse.enrollment.EnrollmentService.EnrollmentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTITUTE_ADMIN')")
    public EnrollmentResponse enroll(@Valid @RequestBody EnrollRequest request) {
        return service.enroll(CurrentTenant.instituteId(), request);
    }

    @GetMapping
    public List<EnrollmentResponse> list(@RequestParam UUID batchId) {
        return service.listForBatch(CurrentTenant.instituteId(), batchId);
    }
}