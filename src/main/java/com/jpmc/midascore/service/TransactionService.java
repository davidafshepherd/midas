package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(
            UserRepository userRepository,
            TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void process(Transaction transaction) {

        // Find sender and recipient
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Invalid sender or recipient
        if (sender == null || recipient == null) {
            return;
        }

        // Not enough money
        if (sender.getBalance() < transaction.getAmount()) {
            return;
        }

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // Persist updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record transaction
        TransactionRecord record = new TransactionRecord(
                sender,
                recipient,
                transaction.getAmount()
        );

        transactionRepository.save(record);
    }
}
