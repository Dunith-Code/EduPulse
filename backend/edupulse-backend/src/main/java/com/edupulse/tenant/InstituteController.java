package com.edupulse.tenant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/institutes")
public class InstituteController {

    public record CreateInstituteRequest(@NotBlank @Size(max = 150) String name) {}

    public record InstituteResponse(UUID id, String name) {}

    private final TenantService tenants;

    public InstituteController(TenantService tenants) {
        this.tenants = tenants;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstituteResponse create(@Valid @RequestBody CreateInstituteRequest request) {
        Institute created = tenants.create(request.name());
        return new InstituteResponse(created.getId(), created.getName());
    }
}