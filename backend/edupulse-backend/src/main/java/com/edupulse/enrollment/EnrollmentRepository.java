package com.edupulse.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    boolean existsByInstituteIdAndStudentIdAndBatchId(UUID instituteId, UUID studentId, UUID batchId);

    boolean existsByInstituteIdAndStudentIdAndBatchIdAndActiveTrue(
            UUID instituteId, UUID studentId, UUID batchId);

    List<Enrollment> findByInstituteIdAndBatchIdAndActiveTrue(UUID instituteId, UUID batchId);

    List<Enrollment> findByInstituteIdAndActiveTrue(UUID instituteId);
}