package com.edupulse.attendance;

import com.edupulse.attendance.AttendanceService.AttendanceResponse;
import com.edupulse.attendance.AttendanceService.ManualMarkRequest;
import com.edupulse.attendance.AttendanceService.ScanRequest;
import com.edupulse.common.CurrentTenant;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@PreAuthorize("hasAnyRole('INSTITUTE_ADMIN','TEACHER','ASSISTANT')")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping("/scan")
    public AttendanceResponse scan(@Valid @RequestBody ScanRequest request) {
        return service.scan(CurrentTenant.instituteId(), CurrentTenant.userId(), request);
    }

    @PostMapping("/manual")
    public AttendanceResponse mark(@Valid @RequestBody ManualMarkRequest request) {
        return service.mark(CurrentTenant.instituteId(), CurrentTenant.userId(), request);
    }

    @GetMapping
    public List<AttendanceResponse> list(
            @RequestParam UUID batchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.list(CurrentTenant.instituteId(), batchId, date);
    }
}