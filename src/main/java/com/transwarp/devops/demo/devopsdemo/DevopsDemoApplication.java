package com.transwarp.devops.demo.devopsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class DevopsDemoApplication {

    @GetMapping("/hello")
    public ResponseEntity hello() {
        Map<String, String> map = new HashMap<>(1);
        map.put("hello", "world");
        return ResponseEntity.ok(map);
    }


    public static void main(String[] args) {
        SpringApplication.run(DevopsDemoApplication.class, args);
    }

}
