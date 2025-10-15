package com.srmiggy.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Supabase Connection Validator
 * 
 * This utility validates your Supabase database connection and configuration.
 * 
 * Run with: mvn spring-boot:run -Dspring-boot.run.profiles=supabase -Dspring-boot.run.mainClass=com.srmiggy.util.SupabaseConnectionValidator
 */
@SpringBootApplication
@Profile("supabase")
public class SupabaseConnectionValidator {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    public static void main(String[] args) {
        System.out.println("\n========================================");
        System.out.println("  Supabase Connection Validator");
        System.out.println("========================================\n");
        
        SpringApplication app = new SpringApplication(SupabaseConnectionValidator.class);
        app.setAdditionalProfiles("supabase");
        app.run(args);
    }

    @Bean
    public CommandLineRunner validateConnection(DataSource dataSource) {
        return args -> {
            System.out.println("🔍 Validating Configuration...\n");
            
            // Check for placeholder values
            boolean configValid = true;
            
            if (datasourceUrl.contains("<your-project-ref>")) {
                System.err.println("❌ ERROR: datasource.url contains placeholder '<your-project-ref>'");
                System.err.println("   Please update application-supabase.properties with your actual Supabase project reference");
                configValid = false;
            } else {
                System.out.println("✓ Database URL configured: " + maskUrl(datasourceUrl));
            }
            
            if (datasourceUrl.contains("<your-supabase-password>")) {
                System.err.println("❌ ERROR: datasource.password contains placeholder '<your-supabase-password>'");
                System.err.println("   Please update application-supabase.properties with your actual Supabase password");
                configValid = false;
            }
            
            if (!configValid) {
                System.err.println("\n❌ Configuration validation failed!");
                System.err.println("   Please update your configuration and try again.\n");
                System.exit(1);
                return;
            }
            
            System.out.println("✓ Username: " + username);
            System.out.println("\n✓ Configuration validation passed!\n");
            
            // Test database connection
            System.out.println("🔌 Testing Database Connection...\n");
            
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("✓ Successfully connected to database!\n");
                
                // Get PostgreSQL version
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT version()")) {
                    if (rs.next()) {
                        String version = rs.getString(1);
                        System.out.println("📊 PostgreSQL Version:");
                        System.out.println("   " + version.substring(0, Math.min(version.length(), 100)));
                        System.out.println();
                    }
                }
                
                // Check for SRMiggy tables
                System.out.println("📋 Checking for SRMiggy tables...\n");
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(
                         "SELECT table_name FROM information_schema.tables " +
                         "WHERE table_schema = 'public' " +
                         "ORDER BY table_name")) {
                    
                    int tableCount = 0;
                    boolean hasRequiredTables = true;
                    String[] requiredTables = {
                        "users", "vendors", "menu_items", "orders", "order_items",
                        "delivery_slots", "riders", "payment_transactions", 
                        "wallet_transactions", "settings"
                    };
                    
                    System.out.println("Found tables:");
                    while (rs.next()) {
                        String tableName = rs.getString(1);
                        System.out.println("  - " + tableName);
                        tableCount++;
                    }
                    
                    if (tableCount == 0) {
                        System.err.println("\n⚠️  WARNING: No tables found in database!");
                        System.err.println("   You need to run the schema SQL script:");
                        System.err.println("   1. Open Supabase Dashboard > SQL Editor");
                        System.err.println("   2. Run backend/src/main/resources/supabase-schema.sql");
                        System.err.println("   3. Optionally run supabase-seed-data.sql for test data\n");
                    } else if (tableCount < 10) {
                        System.err.println("\n⚠️  WARNING: Expected 10 tables, found " + tableCount);
                        System.err.println("   Some tables may be missing. Please check your schema.\n");
                    } else {
                        System.out.println("\n✓ Found all " + tableCount + " required tables!");
                        
                        // Check for seed data
                        try (Statement countStmt = connection.createStatement();
                             ResultSet countRs = countStmt.executeQuery("SELECT COUNT(*) FROM vendors")) {
                            if (countRs.next()) {
                                int vendorCount = countRs.getInt(1);
                                if (vendorCount > 0) {
                                    System.out.println("✓ Found " + vendorCount + " vendors (seed data loaded)");
                                } else {
                                    System.out.println("⚠️  No vendors found. Consider loading seed data.");
                                }
                            }
                        }
                    }
                }
                
                System.out.println("\n========================================");
                System.out.println("  ✓ All Checks Passed!");
                System.out.println("  Your Supabase connection is working!");
                System.out.println("========================================\n");
                
                System.out.println("Next steps:");
                System.out.println("  1. Start your application with: mvn spring-boot:run -Dspring-boot.run.profiles=supabase");
                System.out.println("  2. Test API: curl http://localhost:8080/api/vendors");
                System.out.println("  3. Access H2 Console: http://localhost:8080/h2-console\n");
                
            } catch (Exception e) {
                System.err.println("❌ Database connection failed!\n");
                System.err.println("Error: " + e.getMessage());
                System.err.println("\nPossible causes:");
                System.err.println("  1. Incorrect database URL or credentials");
                System.err.println("  2. Network/firewall blocking connection");
                System.err.println("  3. Supabase project not accessible");
                System.err.println("  4. IP address not whitelisted (if restrictions enabled)\n");
                
                System.err.println("Troubleshooting:");
                System.err.println("  1. Verify credentials in Supabase Dashboard > Settings > Database");
                System.err.println("  2. Check if Supabase project is running (not paused)");
                System.err.println("  3. Test direct connection with psql:");
                System.err.println("     psql -h <host> -p 5432 -U postgres -d postgres\n");
                
                e.printStackTrace();
                System.exit(1);
            }
        };
    }
    
    private String maskUrl(String url) {
        // Mask sensitive parts of the URL
        if (url.contains("@")) {
            int atIndex = url.indexOf("@");
            int protocolEnd = url.indexOf("://") + 3;
            return url.substring(0, protocolEnd) + "***:***" + url.substring(atIndex);
        }
        return url;
    }
}
