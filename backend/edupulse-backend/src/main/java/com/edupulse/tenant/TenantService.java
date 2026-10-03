package com.edupulse.tenant;

import com.edupulse.common.NotFoundException;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TenantService {

    private final InstituteRepository institutes;

    public TenantService(InstituteRepository institutes) {
        this.institutes = institutes;
    }

    public Institute create(String name) {
        return institutes.save(new Institute(name));
    }

    public void requireInstitute(UUID instituteId) {
        if (!institutes.existsById(instituteId)) {
            throw new NotFoundException("Institute not found: " + instituteId);
        }
    }
}