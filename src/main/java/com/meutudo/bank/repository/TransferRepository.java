package com.meutudo.bank.repository;

import com.meutudo.bank.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    public Optional<Transfer> findByRevertTransferId(Long id);
}
