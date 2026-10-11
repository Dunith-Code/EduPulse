package com.edupulse.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    Optional<Attendance> findByInstituteIdAndBatchIdAndStudentIdAndSessionDate(
            UUID instituteId, UUID batchId, UUID studentId, LocalDate sessionDate);

    List<Attendance> findByInstituteIdAndBatchIdAndSessionDate(
            UUID instituteId, UUID batchId, LocalDate sessionDate);
}