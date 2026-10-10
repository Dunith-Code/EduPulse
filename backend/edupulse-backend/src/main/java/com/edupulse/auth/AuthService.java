package com.edupulse.auth;

import com.edupulse.common.ConflictException;
import com.edupulse.tenant.Institute;
import com.edupulse.tenant.TenantService;
import com.edupulse.user.Role;
import com.edupulse.user.User;
import com.edupulse.user.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    public record RegisterRequest(
            @NotBlank @Size(max = 150) String instituteName,
            @NotBlank @Size(max = 150) String fullName,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 72) String password) {}

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record TokenResponse(String accessToken, long expiresInSeconds,
                                String role, UUID instituteId) {}

    private final UserRepository users;
    private final TenantService tenants;
    private final PasswordEncoder encoder;
    private final TokenService tokens;

    public AuthService(UserRepository users, TenantService tenants,
                       PasswordEncoder encoder, TokenService tokens) {
        this.users = users;
        this.tenants = tenants;
        this.encoder = encoder;
        this.tokens = tokens;
    }

    @Transactional
    public TokenResponse registerInstitute(RegisterRequest r) {
        String email = normalize(r.email());
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email already registered");
        }
        Institute institute = tenants.create(r.instituteName());
        User admin = users.save(new User(institute.getId(), r.fullName(), email,
                encoder.encode(r.password()), Role.INSTITUTE_ADMIN));
        return toResponse(admin);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest r) {
        User user = users.findByEmailIgnoreCase(normalize(r.email()))
                .filter(User::isActive)
                .filter(u -> encoder.matches(r.password(), u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        return toResponse(user);
    }

    private TokenResponse toResponse(User user) {
        TokenService.IssuedToken token = tokens.issue(user);
        return new TokenResponse(token.value(), token.expiresInSeconds(),
                user.getRole().name(), user.getInstituteId());
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}