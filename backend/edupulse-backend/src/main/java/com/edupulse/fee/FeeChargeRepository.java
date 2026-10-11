package com.edupulse.fee;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeeChargeRepository extends JpaRepository<FeeCharge, UUID> {

    List<FeeCharge> findByInstituteIdAndPeriod(UUID instituteId, LocalDate period);

    List<FeeCharge> findByInstituteIdAndStudentIdOrderByPeriodDesc(UUID instituteId, UUID studentId);
}