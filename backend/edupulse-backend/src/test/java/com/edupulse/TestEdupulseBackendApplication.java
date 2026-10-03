package com.edupulse;

import org.springframework.boot.SpringApplication;

public class TestEdupulseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(EdupulseBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
