package com.edupulse.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface InstituteRepository extends JpaRepository<Institute, UUID> {
}