package com.srmiggy.config;

import com.srmiggy.model.*;
import com.srmiggy.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private DeliverySlotRepository deliverySlotRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create users
        User customer = createUser("customer", "customer@srmiggy.com", "password", "John Doe", 
                "9876543210", "Hostel A, Room 101", UserRole.CUSTOMER);
        User admin = createUser("admin", "admin@srmiggy.com", "password", "Admin User", 
                "9876543211", "Admin Office", UserRole.ADMIN);
        User vendor1User = createUser("vendor1", "vendor1@srmiggy.com", "password", "Raj Kumar", 
                "9876543212", "Shop 1", UserRole.VENDOR);

        // Create delivery slots from 11:00 AM to 9:00 PM
        createDeliverySlot(LocalTime.of(11, 0), LocalTime.of(11, 30), "11:00 AM - 11:30 AM");
        createDeliverySlot(LocalTime.of(11, 30), LocalTime.of(12, 0), "11:30 AM - 12:00 PM");
        createDeliverySlot(LocalTime.of(12, 0), LocalTime.of(12, 30), "12:00 PM - 12:30 PM");
        createDeliverySlot(LocalTime.of(12, 30), LocalTime.of(13, 0), "12:30 PM - 1:00 PM");
        createDeliverySlot(LocalTime.of(13, 0), LocalTime.of(13, 30), "1:00 PM - 1:30 PM");
        createDeliverySlot(LocalTime.of(13, 30), LocalTime.of(14, 0), "1:30 PM - 2:00 PM");
        createDeliverySlot(LocalTime.of(14, 0), LocalTime.of(14, 30), "2:00 PM - 2:30 PM");
        createDeliverySlot(LocalTime.of(14, 30), LocalTime.of(15, 0), "2:30 PM - 3:00 PM");
        createDeliverySlot(LocalTime.of(15, 0), LocalTime.of(15, 30), "3:00 PM - 3:30 PM");
        createDeliverySlot(LocalTime.of(15, 30), LocalTime.of(16, 0), "3:30 PM - 4:00 PM");
        createDeliverySlot(LocalTime.of(16, 0), LocalTime.of(16, 30), "4:00 PM - 4:30 PM");
        createDeliverySlot(LocalTime.of(16, 30), LocalTime.of(17, 0), "4:30 PM - 5:00 PM");
        createDeliverySlot(LocalTime.of(17, 0), LocalTime.of(17, 30), "5:00 PM - 5:30 PM");
        createDeliverySlot(LocalTime.of(17, 30), LocalTime.of(18, 0), "5:30 PM - 6:00 PM");
        createDeliverySlot(LocalTime.of(18, 0), LocalTime.of(18, 30), "6:00 PM - 6:30 PM");
        createDeliverySlot(LocalTime.of(18, 30), LocalTime.of(19, 0), "6:30 PM - 7:00 PM");
        createDeliverySlot(LocalTime.of(19, 0), LocalTime.of(19, 30), "7:00 PM - 7:30 PM");
        createDeliverySlot(LocalTime.of(19, 30), LocalTime.of(20, 0), "7:30 PM - 8:00 PM");
        createDeliverySlot(LocalTime.of(20, 0), LocalTime.of(20, 30), "8:00 PM - 8:30 PM");
        createDeliverySlot(LocalTime.of(20, 30), LocalTime.of(21, 0), "8:30 PM - 9:00 PM");

        // Create vendors with menus
        Vendor vendor1 = createVendor("Biryani House", "Authentic Hyderabadi Biryani", 
                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400", vendor1User);
        createMenuItems(vendor1, Arrays.asList(
            new String[]{"Chicken Biryani", "Delicious chicken biryani", "180.0", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=200", "Main Course"},
            new String[]{"Mutton Biryani", "Tender mutton biryani", "220.0", "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=200", "Main Course"},
            new String[]{"Veg Biryani", "Flavorful vegetable biryani", "150.0", "https://images.unsplash.com/photo-1599043513900-ed6fe01d3833?w=200", "Main Course"},
            new String[]{"Raita", "Cucumber raita", "40.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Sides"},
            new String[]{"Gulab Jamun", "Sweet gulab jamun", "50.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Dessert"},
            new String[]{"Chicken 65", "Spicy fried chicken", "160.0", "https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=200", "Starter"}
        ));

        Vendor vendor2 = createVendor("Dosa Corner", "South Indian Delicacies", 
                "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=400", null);
        createMenuItems(vendor2, Arrays.asList(
            new String[]{"Masala Dosa", "Crispy masala dosa", "100.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Main Course"},
            new String[]{"Plain Dosa", "Simple plain dosa", "80.0", "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=200", "Main Course"},
            new String[]{"Idli Sambar", "Soft idlis with sambar", "70.0", "https://images.unsplash.com/photo-1630383249896-424e482df921?w=200", "Main Course"},
            new String[]{"Vada Sambar", "Crispy vadas", "80.0", "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=200", "Main Course"},
            new String[]{"Filter Coffee", "Traditional filter coffee", "30.0", "https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?w=200", "Beverage"},
            new String[]{"Uttapam", "Thick uttapam", "90.0", "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=200", "Main Course"}
        ));

        Vendor vendor3 = createVendor("Burger Junction", "American Fast Food", 
                "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400", null);
        createMenuItems(vendor3, Arrays.asList(
            new String[]{"Classic Burger", "Juicy beef burger", "120.0", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=200", "Main Course"},
            new String[]{"Cheese Burger", "Burger with extra cheese", "140.0", "https://images.unsplash.com/photo-1550547660-d9450f859349?w=200", "Main Course"},
            new String[]{"Chicken Burger", "Grilled chicken burger", "130.0", "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=200", "Main Course"},
            new String[]{"French Fries", "Crispy fries", "60.0", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=200", "Sides"},
            new String[]{"Coke", "Chilled coke", "40.0", "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=200", "Beverage"},
            new String[]{"Chicken Wings", "Spicy wings", "150.0", "https://images.unsplash.com/photo-1608039755401-742074f0548d?w=200", "Starter"}
        ));

        Vendor vendor4 = createVendor("Pizza Paradise", "Italian Pizzas", 
                "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400", null);
        createMenuItems(vendor4, Arrays.asList(
            new String[]{"Margherita Pizza", "Classic margherita", "200.0", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=200", "Main Course"},
            new String[]{"Pepperoni Pizza", "Loaded pepperoni", "250.0", "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=200", "Main Course"},
            new String[]{"Veggie Pizza", "Fresh vegetable pizza", "220.0", "https://images.unsplash.com/photo-1511689660979-10d2b1aada49?w=200", "Main Course"},
            new String[]{"Garlic Bread", "Cheesy garlic bread", "80.0", "https://images.unsplash.com/photo-1573140401552-3fab0b24306f?w=200", "Sides"},
            new String[]{"Pasta Alfredo", "Creamy pasta", "180.0", "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=200", "Main Course"},
            new String[]{"Tiramisu", "Italian dessert", "100.0", "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=200", "Dessert"}
        ));

        Vendor vendor5 = createVendor("Thali Express", "North Indian Thali", 
                "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=400", null);
        createMenuItems(vendor5, Arrays.asList(
            new String[]{"Veg Thali", "Complete veg meal", "150.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Main Course"},
            new String[]{"Non-Veg Thali", "Complete non-veg meal", "180.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Main Course"},
            new String[]{"Paneer Butter Masala", "Creamy paneer curry", "160.0", "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=200", "Main Course"},
            new String[]{"Dal Makhani", "Black lentil curry", "120.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Main Course"},
            new String[]{"Roti (5 pcs)", "Wheat flatbread", "30.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Sides"},
            new String[]{"Lassi", "Sweet yogurt drink", "50.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Beverage"}
        ));

        // Create settings
        createSetting("MINIMUM_ORDER_VALUE", "100", "Minimum order value in rupees");
        createSetting("PLATFORM_FEE", "2", "Platform fee in rupees");
        createSetting("ORDER_CUTOFF_MINUTES", "50", "Minutes before slot closing when orders stop");

        System.out.println("Data initialization completed successfully!");
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

    private Vendor createVendor(String name, String description, String imageUrl, User owner) {
        Vendor vendor = new Vendor();
        vendor.setName(name);
        vendor.setDescription(description);
        vendor.setImageUrl(imageUrl);
        vendor.setActive(true);
        vendor.setRating(4.5);
        vendor.setOwner(owner);
        return vendorRepository.save(vendor);
    }

    private void createMenuItems(Vendor vendor, List<String[]> items) {
        for (String[] itemData : items) {
            MenuItem menuItem = new MenuItem();
            menuItem.setVendor(vendor);
            menuItem.setName(itemData[0]);
            menuItem.setDescription(itemData[1]);
            menuItem.setPrice(Double.parseDouble(itemData[2]));
            menuItem.setImageUrl(itemData[3]);
            menuItem.setCategory(itemData[4]);
            menuItem.setAvailable(true);
            menuItem.setIsVeg(!itemData[0].toLowerCase().contains("chicken") && 
                             !itemData[0].toLowerCase().contains("mutton") &&
                             !itemData[0].toLowerCase().contains("beef") &&
                             !itemData[0].toLowerCase().contains("pepperoni"));
            menuItemRepository.save(menuItem);
        }
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
