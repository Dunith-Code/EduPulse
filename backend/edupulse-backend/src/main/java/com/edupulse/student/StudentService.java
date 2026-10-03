package com.edupulse.student;

import com.edupulse.common.NotFoundException;
import com.edupulse.tenant.TenantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    public record CreateStudentRequest(
            @NotBlank @Size(max = 150) String fullName,
            @NotBlank @Size(max = 150) String parentName,
            @NotBlank @Pattern(regexp = "^\\+?[0-9]{9,15}$",
                    message = "parentPhone must be 9-15 digits") String parentPhone) {}

    public record StudentResponse(UUID id, String fullName, String parentName,
                                  String parentPhone, String qrCode, boolean active) {}

    private final StudentRepository students;
    private final TenantService tenants;

    public StudentService(StudentRepository students, TenantService tenants) {
        this.students = students;
        this.tenants = tenants;
    }

    @Transactional
    public StudentResponse create(UUID instituteId, CreateStudentRequest r) {
        tenants.requireInstitute(instituteId);
        String qrCode = UUID.randomUUID().toString();
        Student saved = students.save(new Student(instituteId, r.fullName(),
                r.parentName(), r.parentPhone(), qrCode));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> list(UUID instituteId) {
        return students.findByInstituteIdOrderByFullNameAsc(instituteId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse get(UUID instituteId, UUID studentId) {
        return students.findByIdAndInstituteId(studentId, instituteId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
    }

    private StudentResponse toResponse(Student s) {
        return new StudentResponse(s.getId(), s.getFullName(), s.getParentName(),
                s.getParentPhone(), s.getQrCode(), s.isActive());
    }
}