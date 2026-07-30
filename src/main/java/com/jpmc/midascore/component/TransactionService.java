package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionService {

    private final DatabaseConduit databaseConduit;
    private final IncentiveService incentiveService;

    public TransactionService(DatabaseConduit databaseConduit, IncentiveService incentiveService) {
        this.databaseConduit = databaseConduit;
        this.incentiveService = incentiveService;
    }

    public void process(Transaction transaction) {
        if (databaseConduit.isValid(transaction)) {
            Incentive incentive = incentiveService.query(transaction);
            transaction.setIncentive(incentive.getAmount());
            databaseConduit.save(transaction);
        }
    }
}
