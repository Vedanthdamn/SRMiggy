package com.srmiggy.service;

import com.srmiggy.model.User;
import com.srmiggy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoyaltyService {

    @Autowired
    private UserRepository userRepository;

    private static final Double POINTS_EARN_RATE = 0.5 / 100.0; // 0.5 points for every ₹100 spent
    private static final Double POINTS_TO_RUPEES = 1.0; // 1 point = ₹1

    /**
     * Calculate points that would be earned from a given amount
     */
    public Double calculatePointsEarned(Double amount) {
        return amount * POINTS_EARN_RATE;
    }

    /**
     * Get user's current loyalty points
     */
    public Double getLoyaltyPoints(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getLoyaltyPoints();
    }

    /**
     * Add loyalty points to user account
     */
    @Transactional
    public void addLoyaltyPoints(String username, Double points) {
        if (points <= 0) {
            return;
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLoyaltyPoints(user.getLoyaltyPoints() + points);
        userRepository.save(user);
    }

    /**
     * Redeem loyalty points (deduct from user account)
     * Returns the rupee value of points redeemed
     */
    @Transactional
    public Double redeemLoyaltyPoints(String username, Double pointsToRedeem) {
        if (pointsToRedeem <= 0) {
            throw new RuntimeException("Points to redeem must be greater than zero");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getLoyaltyPoints() < pointsToRedeem) {
            throw new RuntimeException("Insufficient loyalty points");
        }

        user.setLoyaltyPoints(user.getLoyaltyPoints() - pointsToRedeem);
        userRepository.save(user);

        return pointsToRedeem * POINTS_TO_RUPEES;
    }

    /**
     * Get the maximum points that can be redeemed (user's current balance)
     */
    public Double getRedeemablePoints(String username) {
        return getLoyaltyPoints(username);
    }

    /**
     * Convert points to rupees value
     */
    public Double pointsToRupees(Double points) {
        return points * POINTS_TO_RUPEES;
    }
}
