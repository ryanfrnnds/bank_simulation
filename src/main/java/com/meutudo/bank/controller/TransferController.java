package com.meutudo.bank.controller;

import com.meutudo.bank.dto.TransferDto;
import com.meutudo.bank.dto.TransferFutureDto;
import com.meutudo.bank.enums.TransferResultEnum;
import com.meutudo.bank.model.Transfer;
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
    public ResponseEntity<?> create(@RequestBody TransferDto params) throws RuntimeException {
        Transfer transfer = transferService.create(params);
        HttpStatus status = transfer.getResult().getCode().equals(TransferResultEnum.CREATED.getCode()) ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY;

        return new ResponseEntity<>(transfer.getResult().getMessage(transfer),  status);
    }

    @PostMapping("revert/{id}")
    public ResponseEntity<?> revert(@PathVariable Long id) throws RuntimeException {
        Transfer transferRevert = transferService.revert(id);
        HttpStatus status = HttpStatus.OK;
        if(!transferRevert.getResult().getCode().equals(TransferResultEnum.IS_REVERT.getCode()))
            status = transferRevert.getResult().getCode().equals(TransferResultEnum.CREATED.getCode()) ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY;

        return new ResponseEntity<>(transferRevert.getResult().getMessage(transferRevert),  status);
    }

    @PostMapping("future")
    public ResponseEntity<?> future(@RequestBody TransferFutureDto params) throws RuntimeException {
        Transfer transferFuture = transferService.future(params);
        HttpStatus status = transferFuture.getResult().getCode().equals(TransferResultEnum.CREATED.getCode()) ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY;

        return new ResponseEntity<>(transferFuture.getResult().getMessage(transferFuture),  status);
    }
}
