package com.edupulse.student;

import com.edupulse.TestcontainersConfiguration;
import com.edupulse.common.NotFoundException;
import com.edupulse.student.StudentService.CreateStudentRequest;
import com.edupulse.student.StudentService.StudentResponse;
import com.edupulse.tenant.TenantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TenantIsolationTest {

    @Autowired
    TenantService tenants;

    @Autowired
    StudentService students;

    @Test
    void studentsAreInvisibleToOtherInstitutes() {
        UUID instituteA = tenants.create("Institute A").getId();
        UUID instituteB = tenants.create("Institute B").getId();

        StudentResponse created = students.create(instituteA,
                new CreateStudentRequest("Nimal Perera", "Kamal Perera", "0771234567"));

        assertThat(students.list(instituteB)).isEmpty();
        assertThatThrownBy(() -> students.get(instituteB, created.id()))
                .isInstanceOf(NotFoundException.class);
        assertThat(students.get(instituteA, created.id()).fullName())
                .isEqualTo("Nimal Perera");
    }
}