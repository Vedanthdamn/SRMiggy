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

        // Create delivery slots (11:00 AM to 9:00 PM)
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
            new String[]{"Chicken Biryani", "Delicious chicken biryani", "180.0", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=200", "Main Course", "false"},
            new String[]{"Mutton Biryani", "Tender mutton biryani", "220.0", "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=200", "Main Course", "false"},
            new String[]{"Veg Biryani", "Flavorful vegetable biryani", "150.0", "https://images.unsplash.com/photo-1599043513900-ed6fe01d3833?w=200", "Main Course", "true"},
            new String[]{"Raita", "Cucumber raita", "40.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Sides", "true"},
            new String[]{"Gulab Jamun", "Sweet gulab jamun", "50.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Dessert", "true"},
            new String[]{"Chicken 65", "Spicy fried chicken", "160.0", "https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=200", "Starter", "false"},
            new String[]{"Egg Biryani", "Boiled egg biryani", "120.0", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=200", "Main Course", "false"},
            new String[]{"Paneer Biryani", "Cottage cheese biryani", "140.0", "https://images.unsplash.com/photo-1599043513900-ed6fe01d3833?w=200", "Main Course", "true"},
            new String[]{"Plain Rice", "Steamed basmati rice", "60.0", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=200", "Sides", "true"},
            new String[]{"Chicken Kebab", "Grilled chicken kebabs", "140.0", "https://images.unsplash.com/photo-1603360946369-dc9bb6258143?w=200", "Starter", "false"},
            new String[]{"Papad", "Crispy papad (2 pcs)", "20.0", "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=200", "Sides", "true"},
            new String[]{"Mirchi Ka Salan", "Spicy chili curry", "80.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Sides", "true"},
            new String[]{"Buttermilk", "Cool spiced buttermilk", "30.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Beverage", "true"},
            new String[]{"Double Ka Meetha", "Bread pudding", "70.0", "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=200", "Dessert", "true"},
            new String[]{"Onion Salad", "Fresh onion rings", "25.0", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=200", "Sides", "true"},
            new String[]{"Chicken Fry", "Andhra style chicken fry", "160.0", "https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=200", "Starter", "false"}
        ));

        Vendor vendor2 = createVendor("Dosa Corner", "South Indian Delicacies", 
                "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=400", null);
        createMenuItems(vendor2, Arrays.asList(
            new String[]{"Masala Dosa", "Crispy masala dosa", "100.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Main Course", "true"},
            new String[]{"Plain Dosa", "Simple plain dosa", "80.0", "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=200", "Main Course", "true"},
            new String[]{"Idli Sambar", "Soft idlis with sambar", "70.0", "https://images.unsplash.com/photo-1630383249896-424e482df921?w=200", "Main Course", "true"},
            new String[]{"Vada Sambar", "Crispy vadas", "80.0", "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=200", "Main Course", "true"},
            new String[]{"Filter Coffee", "Traditional filter coffee", "30.0", "https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?w=200", "Beverage", "true"},
            new String[]{"Uttapam", "Thick uttapam", "90.0", "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=200", "Main Course", "true"},
            new String[]{"Onion Dosa", "Dosa with onion topping", "85.0", "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=200", "Main Course", "true"},
            new String[]{"Rava Dosa", "Semolina crispy dosa", "90.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Main Course", "true"},
            new String[]{"Pongal", "Rice and lentil dish", "75.0", "https://images.unsplash.com/photo-1630383249896-424e482df921?w=200", "Main Course", "true"},
            new String[]{"Upma", "Semolina breakfast dish", "60.0", "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=200", "Main Course", "true"},
            new String[]{"Medu Vada", "Fried lentil donuts (3 pcs)", "70.0", "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=200", "Starter", "true"},
            new String[]{"Coconut Chutney", "Fresh coconut chutney", "20.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Sides", "true"},
            new String[]{"Sambar (Bowl)", "Lentil vegetable stew", "40.0", "https://images.unsplash.com/photo-1630383249896-424e482df921?w=200", "Sides", "true"},
            new String[]{"Mysore Masala Dosa", "Spicy red chutney dosa", "95.0", "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=200", "Main Course", "true"},
            new String[]{"Ghee Dosa", "Dosa with clarified butter", "90.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Main Course", "true"},
            new String[]{"Tea", "South Indian masala tea", "25.0", "https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?w=200", "Beverage", "true"}
        ));

        Vendor vendor3 = createVendor("Burger Junction", "American Fast Food", 
                "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400", null);
        createMenuItems(vendor3, Arrays.asList(
            new String[]{"Classic Burger", "Juicy beef burger", "120.0", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=200", "Main Course", "false"},
            new String[]{"Cheese Burger", "Burger with extra cheese", "140.0", "https://images.unsplash.com/photo-1550547660-d9450f859349?w=200", "Main Course", "false"},
            new String[]{"Chicken Burger", "Grilled chicken burger", "130.0", "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=200", "Main Course", "false"},
            new String[]{"Veg Burger", "Delicious veggie patty burger", "110.0", "https://images.unsplash.com/photo-1520072959219-c595dc870360?w=200", "Main Course", "true"},
            new String[]{"French Fries", "Crispy fries", "60.0", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=200", "Sides", "true"},
            new String[]{"Coke", "Chilled coke", "40.0", "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=200", "Beverage", "true"},
            new String[]{"Chicken Wings", "Spicy wings", "150.0", "https://images.unsplash.com/photo-1608039755401-742074f0548d?w=200", "Starter", "false"},
            new String[]{"Aloo Tikki Burger", "Potato patty burger", "80.0", "https://images.unsplash.com/photo-1520072959219-c595dc870360?w=200", "Main Course", "true"},
            new String[]{"Peri Peri Fries", "Spicy seasoned fries", "70.0", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=200", "Sides", "true"},
            new String[]{"Onion Rings", "Crispy battered onion rings", "80.0", "https://images.unsplash.com/photo-1639024471283-03518883512d?w=200", "Sides", "true"},
            new String[]{"Chicken Nuggets", "Crispy chicken nuggets (6 pcs)", "90.0", "https://images.unsplash.com/photo-1562967914-608f82629710?w=200", "Starter", "false"},
            new String[]{"Paneer Burger", "Indian cottage cheese burger", "95.0", "https://images.unsplash.com/photo-1520072959219-c595dc870360?w=200", "Main Course", "true"},
            new String[]{"Corn & Cheese Burger", "Sweet corn burger", "85.0", "https://images.unsplash.com/photo-1520072959219-c595dc870360?w=200", "Main Course", "true"},
            new String[]{"Pepsi", "Chilled pepsi", "40.0", "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=200", "Beverage", "true"},
            new String[]{"Sprite", "Chilled lemon soda", "40.0", "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=200", "Beverage", "true"},
            new String[]{"Coleslaw", "Creamy cabbage salad", "50.0", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=200", "Sides", "true"}
        ));

        Vendor vendor4 = createVendor("Pizza Paradise", "Italian Pizzas", 
                "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400", null);
        createMenuItems(vendor4, Arrays.asList(
            new String[]{"Margherita Pizza", "Classic margherita", "200.0", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=200", "Main Course", "true"},
            new String[]{"Pepperoni Pizza", "Loaded pepperoni", "250.0", "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=200", "Main Course", "false"},
            new String[]{"Veggie Pizza", "Fresh vegetable pizza", "220.0", "https://images.unsplash.com/photo-1511689660979-10d2b1aada49?w=200", "Main Course", "true"},
            new String[]{"Garlic Bread", "Cheesy garlic bread", "80.0", "https://images.unsplash.com/photo-1573140401552-3fab0b24306f?w=200", "Sides", "true"},
            new String[]{"Pasta Alfredo", "Creamy pasta", "180.0", "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=200", "Main Course", "true"},
            new String[]{"Tiramisu", "Italian dessert", "100.0", "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=200", "Dessert", "true"},
            new String[]{"Farmhouse Pizza", "Loaded veggie pizza", "240.0", "https://images.unsplash.com/photo-1511689660979-10d2b1aada49?w=200", "Main Course", "true"},
            new String[]{"Corn Pizza", "Sweet corn pizza", "180.0", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=200", "Main Course", "true"},
            new String[]{"Paneer Tikka Pizza", "Indian style pizza", "230.0", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=200", "Main Course", "true"},
            new String[]{"Garlic Breadsticks", "Crispy breadsticks (4 pcs)", "60.0", "https://images.unsplash.com/photo-1573140401552-3fab0b24306f?w=200", "Sides", "true"},
            new String[]{"Pasta Arrabbiata", "Spicy tomato pasta", "150.0", "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=200", "Main Course", "true"},
            new String[]{"Cheese Dip", "Creamy cheese dip", "50.0", "https://images.unsplash.com/photo-1559561853-08451507cbe7?w=200", "Sides", "true"},
            new String[]{"Garlic Dip", "Tangy garlic sauce", "40.0", "https://images.unsplash.com/photo-1472476443507-c7a5948772fc?w=200", "Sides", "true"},
            new String[]{"Cold Coffee", "Iced coffee shake", "80.0", "https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?w=200", "Beverage", "true"},
            new String[]{"Bruschetta", "Tomato basil toast", "90.0", "https://images.unsplash.com/photo-1572695157366-5e585ab2b69f?w=200", "Starter", "true"},
            new String[]{"Pizza Pocket", "Stuffed pizza pocket", "70.0", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=200", "Snacks", "true"}
        ));

        Vendor vendor5 = createVendor("Thali Express", "North Indian Thali", 
                "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=400", null);
        createMenuItems(vendor5, Arrays.asList(
            new String[]{"Veg Thali", "Complete veg meal", "150.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Main Course", "true"},
            new String[]{"Non-Veg Thali", "Complete non-veg meal", "180.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Main Course", "false"},
            new String[]{"Paneer Butter Masala", "Creamy paneer curry", "160.0", "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=200", "Main Course", "true"},
            new String[]{"Dal Makhani", "Black lentil curry", "120.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Main Course", "true"},
            new String[]{"Roti (5 pcs)", "Wheat flatbread", "30.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Sides", "true"},
            new String[]{"Lassi", "Sweet yogurt drink", "50.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Beverage", "true"},
            new String[]{"Dal Tadka", "Yellow lentil curry", "100.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Main Course", "true"},
            new String[]{"Aloo Gobi", "Potato cauliflower curry", "110.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Main Course", "true"},
            new String[]{"Chole Bhature", "Chickpea curry with fried bread", "100.0", "https://images.unsplash.com/photo-1626132647523-66f5bf380027?w=200", "Main Course", "true"},
            new String[]{"Palak Paneer", "Spinach cottage cheese curry", "140.0", "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=200", "Main Course", "true"},
            new String[]{"Jeera Rice", "Cumin flavored rice", "80.0", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=200", "Sides", "true"},
            new String[]{"Naan (2 pcs)", "Leavened flatbread", "40.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Sides", "true"},
            new String[]{"Mixed Veg Curry", "Assorted vegetable curry", "120.0", "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=200", "Main Course", "true"},
            new String[]{"Mango Lassi", "Mango yogurt drink", "60.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Beverage", "true"},
            new String[]{"Papad Roasted", "Roasted papad (2 pcs)", "25.0", "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=200", "Sides", "true"},
            new String[]{"Pickle", "Mixed Indian pickle", "15.0", "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=200", "Sides", "true"}
        ));

        Vendor vendor6 = createVendor("Roll Junction", "Delicious Rolls & Wraps", 
                "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=400", null);
        createMenuItems(vendor6, Arrays.asList(
            new String[]{"Paneer Roll", "Spicy paneer wrap", "90.0", "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=200", "Main Course", "true"},
            new String[]{"Chicken Tikka Roll", "Tandoori chicken wrap", "120.0", "https://images.unsplash.com/photo-1593504049359-74330189a345?w=200", "Main Course", "false"},
            new String[]{"Egg Roll", "Classic egg roll", "80.0", "https://images.unsplash.com/photo-1612240498434-0b7b9073e07e?w=200", "Main Course", "false"},
            new String[]{"Veg Manchurian Roll", "Indo-Chinese veggie roll", "85.0", "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=200", "Main Course", "true"},
            new String[]{"Mutton Seekh Roll", "Spiced mutton seekh wrap", "140.0", "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=200", "Main Course", "false"},
            new String[]{"Aloo Tikki Roll", "Potato patty wrap", "70.0", "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=200", "Main Course", "true"},
            new String[]{"Schezwan Paneer Roll", "Spicy paneer roll", "95.0", "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=200", "Main Course", "true"},
            new String[]{"Chicken Seekh Roll", "Minced chicken roll", "110.0", "https://images.unsplash.com/photo-1593504049359-74330189a345?w=200", "Main Course", "false"},
            new String[]{"Falafel Roll", "Middle eastern chickpea roll", "85.0", "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=200", "Main Course", "true"},
            new String[]{"Mushroom Roll", "Spiced mushroom wrap", "90.0", "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=200", "Main Course", "true"},
            new String[]{"Double Egg Roll", "Extra egg roll", "95.0", "https://images.unsplash.com/photo-1612240498434-0b7b9073e07e?w=200", "Main Course", "false"},
            new String[]{"Cheese Roll", "Cheesy veggie roll", "80.0", "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=200", "Main Course", "true"},
            new String[]{"Spring Roll (3 pcs)", "Crispy vegetable rolls", "70.0", "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=200", "Starter", "true"},
            new String[]{"Chicken Malai Roll", "Creamy chicken roll", "125.0", "https://images.unsplash.com/photo-1593504049359-74330189a345?w=200", "Main Course", "false"},
            new String[]{"Veg Frankie", "Mumbai style veggie wrap", "75.0", "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=200", "Main Course", "true"},
            new String[]{"Mint Chutney", "Fresh mint sauce", "20.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Sides", "true"}
        ));

        Vendor vendor7 = createVendor("Ice Cream Parlor", "Premium Ice Creams", 
                "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400", null);
        createMenuItems(vendor7, Arrays.asList(
            new String[]{"Vanilla Scoop", "Classic vanilla ice cream", "60.0", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=200", "Dessert", "true"},
            new String[]{"Chocolate Scoop", "Rich chocolate ice cream", "60.0", "https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=200", "Dessert", "true"},
            new String[]{"Strawberry Scoop", "Fresh strawberry ice cream", "70.0", "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=200", "Dessert", "true"},
            new String[]{"Mango Scoop", "Tropical mango ice cream", "70.0", "https://images.unsplash.com/photo-1497034825429-c343d7c6a68f?w=200", "Dessert", "true"},
            new String[]{"Butterscotch Scoop", "Crunchy butterscotch ice cream", "65.0", "https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=200", "Dessert", "true"},
            new String[]{"Sundae Special", "Ice cream with toppings", "120.0", "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=200", "Dessert", "true"},
            new String[]{"Pista Scoop", "Pistachio ice cream", "75.0", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=200", "Dessert", "true"},
            new String[]{"Kulfi", "Traditional Indian ice cream", "50.0", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=200", "Dessert", "true"},
            new String[]{"Black Current Scoop", "Tangy black current", "70.0", "https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=200", "Dessert", "true"},
            new String[]{"Chocolate Chip Scoop", "Chocolate chip ice cream", "70.0", "https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=200", "Dessert", "true"},
            new String[]{"Coffee Scoop", "Rich coffee ice cream", "65.0", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=200", "Dessert", "true"},
            new String[]{"Ice Cream Shake", "Thick ice cream milkshake", "90.0", "https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?w=200", "Beverage", "true"},
            new String[]{"Fruit Salad with Ice Cream", "Fresh fruits with ice cream", "100.0", "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=200", "Dessert", "true"},
            new String[]{"Choco Bar", "Chocolate coated ice cream bar", "40.0", "https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=200", "Dessert", "true"},
            new String[]{"Cone Ice Cream", "Classic cone ice cream", "50.0", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=200", "Dessert", "true"},
            new String[]{"Brownie Sundae", "Brownie with ice cream", "110.0", "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=200", "Dessert", "true"}
        ));

        Vendor vendor8 = createVendor("Dessert House", "Sweet Delights", 
                "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400", null);
        createMenuItems(vendor8, Arrays.asList(
            new String[]{"Brownie", "Chocolate fudge brownie", "80.0", "https://images.unsplash.com/photo-1607920591413-4ec007e70023?w=200", "Dessert", "true"},
            new String[]{"Cheesecake", "New York style cheesecake", "150.0", "https://images.unsplash.com/photo-1533134486753-c833f0ed4866?w=200", "Dessert", "true"},
            new String[]{"Rasmalai", "Traditional Indian sweet", "90.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Dessert", "true"},
            new String[]{"Gajar Halwa", "Carrot pudding", "70.0", "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=200", "Dessert", "true"},
            new String[]{"Chocolate Mousse", "Light chocolate mousse", "110.0", "https://images.unsplash.com/photo-1541599540903-216a46934c0b?w=200", "Dessert", "true"},
            new String[]{"Fruit Custard", "Mixed fruit custard", "80.0", "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=200", "Dessert", "true"},
            new String[]{"Jalebi", "Crispy sweet spirals", "60.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Dessert", "true"},
            new String[]{"Ras Gulla", "Soft cheese balls in syrup (4 pcs)", "70.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Dessert", "true"},
            new String[]{"Kheer", "Rice pudding", "65.0", "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=200", "Dessert", "true"},
            new String[]{"Chocolate Pastry", "Rich chocolate pastry", "90.0", "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=200", "Dessert", "true"},
            new String[]{"Black Forest Pastry", "Classic black forest", "100.0", "https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?w=200", "Dessert", "true"},
            new String[]{"Vanilla Pastry", "Light vanilla sponge", "85.0", "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=200", "Dessert", "true"},
            new String[]{"Mango Pastry", "Tropical mango pastry", "95.0", "https://images.unsplash.com/photo-1497034825429-c343d7c6a68f?w=200", "Dessert", "true"},
            new String[]{"Gulab Jamun (4 pcs)", "Sweet milk dumplings", "60.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Dessert", "true"},
            new String[]{"Kalakand", "Milk cake", "75.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Dessert", "true"},
            new String[]{"Peda (6 pcs)", "Traditional milk sweet", "70.0", "https://images.unsplash.com/photo-1589301773859-bb024d3f4c5f?w=200", "Dessert", "true"}
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
            // If isVeg is provided (6th element), use it; otherwise, auto-detect
            if (itemData.length > 5) {
                menuItem.setIsVeg(Boolean.parseBoolean(itemData[5]));
            } else {
                menuItem.setIsVeg(!itemData[0].toLowerCase().contains("chicken") && 
                                 !itemData[0].toLowerCase().contains("mutton") &&
                                 !itemData[0].toLowerCase().contains("beef") &&
                                 !itemData[0].toLowerCase().contains("pepperoni") &&
                                 !itemData[0].toLowerCase().contains("egg"));
            }
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
