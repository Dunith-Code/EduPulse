package com.edupulse.student;

import com.edupulse.common.CurrentTenant;
import com.edupulse.student.StudentService.CreateStudentRequest;
import com.edupulse.student.StudentService.StudentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTITUTE_ADMIN')")
    public StudentResponse create(@Valid @RequestBody CreateStudentRequest request) {
        return service.create(CurrentTenant.instituteId(), request);
    }

    @GetMapping
    public List<StudentResponse> list() {
        return service.list(CurrentTenant.instituteId());
    }

    @GetMapping("/{id}")
    public StudentResponse get(@PathVariable UUID id) {
        return service.get(CurrentTenant.instituteId(), id);
    }
}