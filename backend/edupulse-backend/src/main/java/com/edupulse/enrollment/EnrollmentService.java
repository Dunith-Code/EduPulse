package com.edupulse.enrollment;

import com.edupulse.batch.BatchService;
import com.edupulse.common.ConflictException;
import com.edupulse.student.StudentService;
import com.edupulse.student.StudentService.StudentResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EnrollmentService {

    public record EnrollRequest(@NotNull UUID studentId, @NotNull UUID batchId) {}

    public record EnrollmentResponse(UUID id, UUID studentId, String studentName,
                                     UUID batchId, LocalDate enrolledOn) {}

    private final EnrollmentRepository enrollments;
    private final StudentService students;
    private final BatchService batches;
    private final Clock clock;

    public EnrollmentService(EnrollmentRepository enrollments, StudentService students,
                             BatchService batches, Clock clock) {
        this.enrollments = enrollments;
        this.students = students;
        this.batches = batches;
        this.clock = clock;
    }

    public record ActiveEnrollment(UUID id, UUID studentId, UUID batchId, LocalDate enrolledOn) {}

        @Transactional(readOnly = true)
        public List<ActiveEnrollment> activeEnrollments(UUID instituteId) {
        return enrollments.findByInstituteIdAndActiveTrue(instituteId).stream()
                .map(e -> new ActiveEnrollment(e.getId(), e.getStudentId(),
                        e.getBatchId(), e.getEnrolledOn()))
                .toList();
        }

    @Transactional
    public EnrollmentResponse enroll(UUID instituteId, EnrollRequest r) {
        StudentResponse student = students.get(instituteId, r.studentId());
        batches.get(instituteId, r.batchId());
        if (enrollments.existsByInstituteIdAndStudentIdAndBatchId(
                instituteId, r.studentId(), r.batchId())) {
            throw new ConflictException("Student is already enrolled in this batch");
        }
        Enrollment saved = enrollments.save(
                new Enrollment(instituteId, r.studentId(), r.batchId(), LocalDate.now(clock)));
        return new EnrollmentResponse(saved.getId(), student.id(), student.fullName(),
                saved.getBatchId(), saved.getEnrolledOn());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> listForBatch(UUID instituteId, UUID batchId) {
        batches.get(instituteId, batchId);
        List<Enrollment> rows = enrollments.findByInstituteIdAndBatchIdAndActiveTrue(
                instituteId, batchId);
        Map<UUID, String> names = students.namesByIds(instituteId,
                rows.stream().map(Enrollment::getStudentId).toList());
        return rows.stream()
                .map(e -> new EnrollmentResponse(e.getId(), e.getStudentId(),
                        names.getOrDefault(e.getStudentId(), ""),
                        e.getBatchId(), e.getEnrolledOn()))
                .toList();
    }

    @Transactional(readOnly = true)
    public void requireActiveEnrollment(UUID instituteId, UUID studentId, UUID batchId) {
        if (!enrollments.existsByInstituteIdAndStudentIdAndBatchIdAndActiveTrue(
                instituteId, studentId, batchId)) {
            throw new ConflictException("Student is not enrolled in this batch");
        }
    }
}
