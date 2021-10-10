package com.meutudo.bank.controller;

import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.dto.TransferFutureDto;
import com.meutudo.bank.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
public class TransferController {
    @Autowired
    TransferService transferService;

    @PostMapping
    public ResponseEntity create(@RequestBody TransferDto params) throws RuntimeException {
        transferService.create(params);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("revert/{id}")
    public ResponseEntity revert(@PathVariable Long id) throws RuntimeException {
        transferService.revert(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("future")
    public ResponseEntity future(@RequestBody TransferFutureDto params) throws RuntimeException {
        transferService.future(params);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
