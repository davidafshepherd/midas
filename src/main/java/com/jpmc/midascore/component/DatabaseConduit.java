package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public void save(Transaction transaction) {
        // Find sender and recipient
        UserRecord sender = queryUser(transaction.getSenderId());
        UserRecord recipient = queryUser(transaction.getRecipientId());

        // Record transaction
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount(), transaction.getIncentive());
        transactionRepository.save(transactionRecord);

        // Update user balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + transaction.getIncentive());

        // Persist updated users
        save(sender);
        save(recipient);
    }

    public boolean isValid(Transaction transaction) {
        // Invalid sender
        UserRecord sender = queryUser(transaction.getSenderId());
        if (sender == null) {
            return false;
        }

        // Invalid recipient
        UserRecord recipient = queryUser(transaction.getRecipientId());
        if (recipient == null) {
            return false;
        }

        // Not enough money
        return !(sender.getBalance() < transaction.getAmount());
    }

    public UserRecord queryUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public float queryUserBalance(Long userId) {
        UserRecord userRecord = queryUser(userId);
        if (userRecord == null) {
            return 0;
        }
        else {
            return userRecord.getBalance();
        }
    }
}
