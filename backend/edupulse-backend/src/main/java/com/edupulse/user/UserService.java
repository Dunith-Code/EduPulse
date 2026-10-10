package com.edupulse.user;

import com.edupulse.common.ConflictException;
import com.edupulse.tenant.TenantService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class UserService {

    public record CreateStaffRequest(
            @NotBlank @Size(max = 150) String fullName,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @NotNull Role role) {}

    public record UserResponse(UUID id, String fullName, String email, Role role, boolean active) {}

    private final UserRepository users;
    private final TenantService tenants;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, TenantService tenants, PasswordEncoder encoder) {
        this.users = users;
        this.tenants = tenants;
        this.encoder = encoder;
    }

    @Transactional
    public UserResponse createStaff(UUID instituteId, CreateStaffRequest r) {
        tenants.requireInstitute(instituteId);
        if (r.role() == Role.INSTITUTE_ADMIN) {
            throw new IllegalArgumentException("Staff role must be TEACHER or ASSISTANT");
        }
        String email = r.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email already registered");
        }
        User saved = users.save(new User(instituteId, r.fullName(), email,
                encoder.encode(r.password()), r.role()));
        return new UserResponse(saved.getId(), saved.getFullName(), saved.getEmail(),
                saved.getRole(), saved.isActive());
    }
}