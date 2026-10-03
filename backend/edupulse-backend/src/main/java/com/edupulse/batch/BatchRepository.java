package com.edupulse.batch;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {

    List<Batch> findByInstituteIdOrderByNameAsc(UUID instituteId);

    Optional<Batch> findByIdAndInstituteId(UUID id, UUID instituteId);
}