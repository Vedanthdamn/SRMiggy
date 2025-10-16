package com.srmiggy.config;

import com.srmiggy.model.*;
import com.srmiggy.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliverySlotRepository deliverySlotRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // NOTE: Restaurant and menu data has been migrated to Supabase
        // To populate the database, run the SQL script: supabase_migration.sql
        
        // Create essential users only
        User customer = createUser("customer", "customer@srmiggy.com", "password", "John Doe", 
                "9876543210", "Hostel A, Room 101", UserRole.CUSTOMER);
        User admin = createUser("admin", "admin@srmiggy.com", "password", "Admin User", 
                "9876543211", "Admin Office", UserRole.ADMIN);

        // Create delivery slots from 11:00 AM to 7:00 PM (hourly slots)
        createDeliverySlot(LocalTime.of(11, 0), LocalTime.of(12, 0), "11:00 AM - 12:00 PM");
        createDeliverySlot(LocalTime.of(12, 0), LocalTime.of(13, 0), "12:00 PM - 1:00 PM");
        createDeliverySlot(LocalTime.of(13, 0), LocalTime.of(14, 0), "1:00 PM - 2:00 PM");
        createDeliverySlot(LocalTime.of(14, 0), LocalTime.of(15, 0), "2:00 PM - 3:00 PM");
        createDeliverySlot(LocalTime.of(15, 0), LocalTime.of(16, 0), "3:00 PM - 4:00 PM");
        createDeliverySlot(LocalTime.of(16, 0), LocalTime.of(17, 0), "4:00 PM - 5:00 PM");
        createDeliverySlot(LocalTime.of(17, 0), LocalTime.of(18, 0), "5:00 PM - 6:00 PM");
        createDeliverySlot(LocalTime.of(18, 0), LocalTime.of(19, 0), "6:00 PM - 7:00 PM");

        // Create settings
        createSetting("MINIMUM_ORDER_VALUE", "100", "Minimum order value in rupees");
        createSetting("PLATFORM_FEE", "2", "Platform fee in rupees");
        createSetting("ORDER_CUTOFF_MINUTES", "50", "Minutes before slot closing when orders stop");

        System.out.println("Essential data initialization completed successfully!");
        System.out.println("NOTE: To populate restaurants and menu items, please run: supabase_migration.sql");
    }

    private User createUser(String username, String email, String password, String fullName, 
                           String phone, String address, UserRole role) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setAddress(address);
            user.setRole(role);
            user.setEnabled(true);
            return userRepository.save(user);
        }
        return userRepository.findByUsername(username).get();
    }

    private void createDeliverySlot(LocalTime startTime, LocalTime endTime, String displayName) {
        DeliverySlot slot = new DeliverySlot();
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setDisplayName(displayName);
        slot.setActive(true);
        deliverySlotRepository.save(slot);
    }

    private void createSetting(String key, String value, String description) {
        if (settingsRepository.findBySettingKey(key).isEmpty()) {
            Settings setting = new Settings();
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            setting.setDescription(description);
            settingsRepository.save(setting);
        }
    }
}
