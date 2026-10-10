package com.edupulse.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public final class CurrentTenant {

    private CurrentTenant() {
    }

    public static UUID instituteId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return UUID.fromString(jwt.getClaimAsString("institute_id"));
        }
        throw new IllegalStateException("No authenticated tenant in security context");
    }
}