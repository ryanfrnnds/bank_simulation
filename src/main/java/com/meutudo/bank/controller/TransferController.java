package com.meutudo.bank.controller;

import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.enums.TransferResultEnum;
import com.meutudo.bank.model.Transfer;
import com.meutudo.bank.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.SystemException;

@RestController
@RequestMapping("/transfers")
public class TransferController {
    @Autowired
    TransferService transferService;

    @PostMapping
    public ResponseEntity<?> insert(@RequestBody TransferDto params) throws RuntimeException {
        Transfer transfer = transferService.create(params);
        HttpStatus status = transfer.getResult().getCode().equals(TransferResultEnum.CREATED.getCode()) ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY;

        return new ResponseEntity<>(transfer.getResult().getMessage(transfer),  status);
    }
}
