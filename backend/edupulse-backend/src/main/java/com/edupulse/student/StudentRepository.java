package com.edupulse.student;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {

    List<Student> findByInstituteIdOrderByFullNameAsc(UUID instituteId);

    Optional<Student> findByIdAndInstituteId(UUID id, UUID instituteId);
}