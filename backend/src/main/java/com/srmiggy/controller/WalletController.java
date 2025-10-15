package com.srmiggy.controller;

import com.srmiggy.dto.AddMoneyRequest;
import com.srmiggy.dto.LoyaltyPointsResponse;
import com.srmiggy.dto.WalletResponse;
import com.srmiggy.model.WalletTransaction;
import com.srmiggy.service.LoyaltyService;
import com.srmiggy.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private LoyaltyService loyaltyService;

    @PostMapping("/add-money")
    public ResponseEntity<WalletResponse> addMoney(
            @RequestBody AddMoneyRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            WalletResponse response = walletService.addMoney(username, request.getAmount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new WalletResponse(null, e.getMessage()));
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(Authentication authentication) {
        try {
            String username = authentication.getName();
            Double balance = walletService.getBalance(username);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<WalletTransaction> transactions = walletService.getTransactions(username);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/loyalty-points")
    public ResponseEntity<Double> getLoyaltyPoints(Authentication authentication) {
        try {
            String username = authentication.getName();
            Double points = loyaltyService.getLoyaltyPoints(username);
            return ResponseEntity.ok(points);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/calculate-loyalty-points")
    public ResponseEntity<LoyaltyPointsResponse> calculateLoyaltyPoints(
            @RequestParam Double orderTotal,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Double currentPoints = loyaltyService.getLoyaltyPoints(username);
            Double pointsEarned = loyaltyService.calculatePointsEarned(orderTotal);
            return ResponseEntity.ok(new LoyaltyPointsResponse(currentPoints, pointsEarned));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
