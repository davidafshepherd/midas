package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionService(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
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

        // Request incentive from incentive API
        String url = "http://localhost:8080/incentive";
        Transaction apiTransaction = new Transaction(sender.getId(), recipient.getId(), transaction.getAmount());
        Incentive incentive = restTemplate.postForObject(url, apiTransaction, Incentive.class);

        // Default incentive to zero
        float incentiveAmount = incentive == null ? 0 : incentive.getAmount();

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        // Persist updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record transaction
        transactionRepository.save(new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount));
    }
}
