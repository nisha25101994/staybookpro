package com.assettrackpro.service;

import com.assettrackpro.entity.Item;
import com.assettrackpro.entity.User;
import com.assettrackpro.repository.ItemRepository;
import com.assettrackpro.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    public ItemService(ItemRepository itemRepo, UserRepository userRepo) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepo.findAllActive(pageable);
    }

    public Page<Item> searchItems(String query, Pageable pageable) {
        return itemRepo.findByNameContainingIgnoreCase(query, pageable);
    }

    public Page<Item> getItemsByVendor(String email, Pageable pageable) {
        User vendor = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return itemRepo.findByVendorId(vendor.getId(), pageable);
    }

    public Item addItem(Item item, String vendorEmail) {
        User vendor = userRepo.findByEmail(vendorEmail)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        item.setVendor(vendor);
        item.setDeleted(false);
        return itemRepo.save(item);
    }

    public Item updateItem(Long id, Item itemDetails, String vendorEmail) {

        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getVendor() == null ||
                !item.getVendor().getEmail().equals(vendorEmail)) {
            throw new RuntimeException("Not authorized to update this item");
        }

        item.setName(itemDetails.getName());
        item.setDescription(itemDetails.getDescription());
        item.setPrice(itemDetails.getPrice());
        item.setQuantityAvailable(itemDetails.getQuantityAvailable());

        return itemRepo.save(item);
    }

    public void deleteItem(Long id, String vendorEmail) {

        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getVendor() == null ||
                !item.getVendor().getEmail().equals(vendorEmail)) {
            throw new RuntimeException("Not authorized to delete this item");
        }

        item.setDeleted(true);
        itemRepo.save(item);
    }
}