package com.meutudo.bank.service;

import com.meutudo.bank.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

    @Autowired
    TransferRepository transferRepository;
}
