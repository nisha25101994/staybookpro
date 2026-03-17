package com.assettrackpro.config;

import com.assettrackpro.entity.Item;
import com.assettrackpro.entity.Role;
import com.assettrackpro.entity.User;
import com.assettrackpro.repository.ItemRepository;
import com.assettrackpro.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepo, ItemRepository itemRepo, PasswordEncoder encoder) {
        return args -> {
            System.out.println("--- DATA INITIALIZER STARTING ---");

            // 1. ADMIN
            User admin = userRepo.findByEmail("admin@assettrack.com").orElse(null);
            if (admin == null) {
                admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@assettrack.com");
                admin.setRole(Role.ADMIN);
            }
            admin.setPassword(encoder.encode("admin123")); // Always reset in dev
            admin.setEnabled(true);
            userRepo.save(admin);
            System.out.println("Admin sync: admin@assettrack.com / admin123");

            // 2. VENDOR
            User vendor = userRepo.findByEmail("vendor@assettrack.com").orElse(null);
            if (vendor == null) {
                vendor = new User();
                vendor.setName("Tech Supplies Inc.");
                vendor.setEmail("vendor@assettrack.com");
                vendor.setRole(Role.VENDOR);
            }
            vendor.setPassword(encoder.encode("vendor123")); // Always reset in dev
            vendor.setEnabled(true);
            vendor = userRepo.save(vendor);
            System.out.println("Vendor sync: vendor@assettrack.com / vendor123");

            // 3. USER
            User user = userRepo.findByEmail("user@assettrack.com").orElse(null);
            if (user == null) {
                user = new User();
                user.setName("John Doe");
                user.setEmail("user@assettrack.com");
                user.setRole(Role.USER);
            }
            user.setPassword(encoder.encode("user123")); // Always reset in dev
            user.setEnabled(true);
            userRepo.save(user);
            System.out.println("User sync: user@assettrack.com / user123");

            // 4. ITEMS (only if empty)
            if (itemRepo.count() == 0) {
                createItem(itemRepo, vendor, "Dell XPS 15", "High-performance laptop", 1500.00, 5);
                createItem(itemRepo, vendor, "MacBook Pro M3", "Latest Apple Silicon laptop", 2000.00, 3);
                createItem(itemRepo, vendor, "Lenovo ThinkPad X1", "Business ultrabook", 1400.00, 8);
                createItem(itemRepo, vendor, "Dell UltraSharp 27", "4K Monitor 27-inch", 450.00, 15);
                createItem(itemRepo, vendor, "Sony WH-1000XM5", "Noise cancelling headphones", 349.00, 12);
                System.out.println("Sample items created for vendor.");
            }
            
            System.out.println("--- DATA INITIALIZER COMPLETE ---");
        };
    }

    private void createItem(ItemRepository repo, User vendor, String name, String desc, double price, int qty) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(desc);
        item.setPrice(price);
        item.setQuantityAvailable(qty);
        item.setVendor(vendor);
        repo.save(item);
    }
}
