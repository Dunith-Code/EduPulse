package com.edupulse.fee;

import com.edupulse.common.CurrentTenant;
import com.edupulse.fee.FeeService.ChargeResponse;
import com.edupulse.fee.FeeService.GenerateRequest;
import com.edupulse.fee.FeeService.GenerateResult;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fees")
public class FeeController {

    private final FeeService service;

    public FeeController(FeeService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('INSTITUTE_ADMIN')")
    public GenerateResult generate(@Valid @RequestBody GenerateRequest request) {
        return service.generateMonthly(CurrentTenant.instituteId(), CurrentTenant.userId(), request);
    }

    @GetMapping("/charges")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN','ASSISTANT')")
    public List<ChargeResponse> charges(@RequestParam UUID studentId) {
        return service.chargesForStudent(CurrentTenant.instituteId(), studentId);
    }
}