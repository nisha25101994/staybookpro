package com.assettrackpro.controller;

import com.assettrackpro.entity.Item;

import com.assettrackpro.service.ItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:3000")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public org.springframework.data.domain.Page<Item> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        if (search != null && !search.isEmpty()) {
            return itemService.searchItems(search, pageable);
        }
        return itemService.getAllItems(pageable);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('VENDOR')")
    public org.springframework.data.domain.Page<Item> getMyItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return itemService.getItemsByVendor(authentication.getName(), pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('VENDOR')")
    public Item addItem(@RequestBody Item item, Authentication authentication) {
        return itemService.addItem(item, authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDOR')")
    public Item updateItem(@PathVariable Long id, @RequestBody Item item, Authentication authentication) {
        return itemService.updateItem(id, item, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDOR')")
    public void deleteItem(@PathVariable Long id, Authentication authentication) {
        itemService.deleteItem(id, authentication.getName());
    }
}
