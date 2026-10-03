package com.edupulse.student;

import com.edupulse.student.StudentService.CreateStudentRequest;
import com.edupulse.student.StudentService.StudentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public StudentResponse create(@RequestHeader("X-Institute-Id") UUID instituteId,
                                  @Valid @RequestBody CreateStudentRequest request) {
        return service.create(instituteId, request);
    }

    @GetMapping
    public List<StudentResponse> list(@RequestHeader("X-Institute-Id") UUID instituteId) {
        return service.list(instituteId);
    }

    @GetMapping("/{id}")
    public StudentResponse get(@RequestHeader("X-Institute-Id") UUID instituteId,
                               @PathVariable UUID id) {
        return service.get(instituteId, id);
    }
}