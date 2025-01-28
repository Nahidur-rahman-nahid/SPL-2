package com.TimeWise.controller;

import com.TimeWise.model.Session;
import com.TimeWise.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    // Create a new session
    @PostMapping("/create")
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        Session createdSession = sessionService.createSession(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
    }

    // Get a session by ID
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable String id) {
        Session session = sessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }


    // Delete a session by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSession(@PathVariable String id) {
        sessionService.deleteSession(id);
        return ResponseEntity.ok("Session deleted successfully");
    }

    // Get sessions by creator
    @GetMapping("/creator")
    public ResponseEntity<List<Session>> getSessionsByCreator() {
        List<Session> sessions = sessionService.getAllSessionsOfAnUser();
        return ResponseEntity.ok(sessions);
    }

}

