package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CompensationController {

    private final CompensationService compensationService;

    @GetMapping("/compensation/{id}")
    public List<Compensation> read(@PathVariable String id) {
        log.debug("Received compensation read request for [{}]", id);

        return compensationService.read(id);
    }

    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation compensation) {
        log.debug("Received compensation create request for [{}]", compensation);

        return compensationService.create(compensation);
    }

}
