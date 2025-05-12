package com.betmotion.agentsmanagement.rest;

import com.betmotion.agentsmanagement.service.CloseUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CloseUsersController {

    @Autowired
    CloseUsersService closeUsersService;

    @GetMapping("/closeUsers.do")
    private ResponseEntity<String> closeAllUsers() throws Exception {
        closeUsersService.closeAllUsers();
        return ResponseEntity.ok("All users closed");
    }

}
