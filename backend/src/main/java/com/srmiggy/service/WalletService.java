package com.srmiggy.service;

import com.srmiggy.dto.WalletResponse;
import com.srmiggy.model.User;
import com.srmiggy.model.WalletTransaction;
import com.srmiggy.model.WalletTransactionType;
import com.srmiggy.repository.UserRepository;
import com.srmiggy.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Transactional
    public WalletResponse addMoney(String username, Double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user wallet balance
        user.setWalletBalance(user.getWalletBalance() + amount);
        userRepository.save(user);

        // Create transaction record
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setType(WalletTransactionType.CREDIT);
        transaction.setDescription("Money added to wallet");
        transaction.setBalanceAfter(user.getWalletBalance());
        walletTransactionRepository.save(transaction);

        return new WalletResponse(user.getWalletBalance(), "Money added successfully");
    }

    @Transactional
    public WalletResponse deductMoney(String username, Double amount, String description) {
        if (amount <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getWalletBalance() < amount) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        // Update user wallet balance
        user.setWalletBalance(user.getWalletBalance() - amount);
        userRepository.save(user);

        // Create transaction record
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setType(WalletTransactionType.DEBIT);
        transaction.setDescription(description);
        transaction.setBalanceAfter(user.getWalletBalance());
        walletTransactionRepository.save(transaction);

        return new WalletResponse(user.getWalletBalance(), "Payment successful");
    }

    public Double getBalance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getWalletBalance();
    }

    public List<WalletTransaction> getTransactions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return walletTransactionRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
