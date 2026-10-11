package com.edupulse.attendance;

import com.edupulse.batch.BatchService;
import com.edupulse.enrollment.EnrollmentService;
import com.edupulse.student.StudentService;
import com.edupulse.student.StudentService.StudentResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AttendanceService {

    public record ScanRequest(@NotNull UUID batchId, @NotBlank String qrCode,
                              LocalDate sessionDate) {}

    public record ManualMarkRequest(@NotNull UUID batchId, @NotNull UUID studentId,
                                    @NotNull AttendanceStatus status, LocalDate sessionDate) {}

    public record AttendanceResponse(UUID studentId, String studentName, UUID batchId,
                                     LocalDate sessionDate, AttendanceStatus status,
                                     AttendanceMethod method, Instant markedAt) {}

    private final AttendanceRepository attendance;
    private final StudentService students;
    private final BatchService batches;
    private final EnrollmentService enrollments;
    private final Clock clock;

    public AttendanceService(AttendanceRepository attendance, StudentService students,
                             BatchService batches, EnrollmentService enrollments, Clock clock) {
        this.attendance = attendance;
        this.students = students;
        this.batches = batches;
        this.enrollments = enrollments;
        this.clock = clock;
    }

    /** Idempotent: scanning the same student twice for the same session never duplicates. */
    @Transactional
    public AttendanceResponse scan(UUID instituteId, UUID userId, ScanRequest r) {
        batches.get(instituteId, r.batchId());
        StudentResponse student = students.getByQrCode(instituteId, r.qrCode().trim());
        enrollments.requireActiveEnrollment(instituteId, student.id(), r.batchId());
        LocalDate date = dateOrToday(r.sessionDate());

        Attendance existing = attendance
                .findByInstituteIdAndBatchIdAndStudentIdAndSessionDate(
                        instituteId, r.batchId(), student.id(), date)
                .orElse(null);

        if (existing == null) {
            Attendance saved = attendance.save(new Attendance(instituteId, r.batchId(),
                    student.id(), date, AttendanceStatus.PRESENT, AttendanceMethod.QR, userId));
            return toResponse(saved, student.fullName());
        }
        if (existing.getStatus() == AttendanceStatus.ABSENT) {
            existing.correct(AttendanceStatus.PRESENT, AttendanceMethod.QR, userId);
            return toResponse(attendance.save(existing), student.fullName());
        }
        return toResponse(existing, student.fullName());
    }

    @Transactional
    public AttendanceResponse mark(UUID instituteId, UUID userId, ManualMarkRequest r) {
        batches.get(instituteId, r.batchId());
        StudentResponse student = students.get(instituteId, r.studentId());
        enrollments.requireActiveEnrollment(instituteId, student.id(), r.batchId());
        LocalDate date = dateOrToday(r.sessionDate());

        Attendance row = attendance
                .findByInstituteIdAndBatchIdAndStudentIdAndSessionDate(
                        instituteId, r.batchId(), student.id(), date)
                .orElse(null);
        if (row == null) {
            row = new Attendance(instituteId, r.batchId(), student.id(), date,
                    r.status(), AttendanceMethod.MANUAL, userId);
        } else {
            row.correct(r.status(), AttendanceMethod.MANUAL, userId);
        }
        return toResponse(attendance.save(row), student.fullName());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> list(UUID instituteId, UUID batchId, LocalDate date) {
        batches.get(instituteId, batchId);
        List<Attendance> rows = attendance.findByInstituteIdAndBatchIdAndSessionDate(
                instituteId, batchId, date);
        Map<UUID, String> names = students.namesByIds(instituteId,
                rows.stream().map(Attendance::getStudentId).toList());
        return rows.stream()
                .map(a -> toResponse(a, names.getOrDefault(a.getStudentId(), "")))
                .toList();
    }

    private LocalDate dateOrToday(LocalDate requested) {
        return requested != null ? requested : LocalDate.now(clock);
    }

    private AttendanceResponse toResponse(Attendance a, String studentName) {
        return new AttendanceResponse(a.getStudentId(), studentName, a.getBatchId(),
                a.getSessionDate(), a.getStatus(), a.getMethod(), a.getMarkedAt());
    }
}