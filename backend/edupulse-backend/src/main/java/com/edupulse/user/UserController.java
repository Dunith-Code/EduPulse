package com.edupulse.user;

import com.edupulse.common.CurrentTenant;
import com.edupulse.user.UserService.CreateStaffRequest;
import com.edupulse.user.UserService.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTITUTE_ADMIN')")
    public UserResponse createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return service.createStaff(CurrentTenant.instituteId(), request);
    }
}