package com.example.web_bansach.module.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.service.InventoryService;

@RestController
@RequestMapping("/admin/inventory")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminInventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(inventoryService.getAll());
    }

    @PutMapping("/{id}/set/{quantity}")
    public ResponseEntity<?> setQuantity(@PathVariable Long id, @PathVariable Integer quantity) {
        Inventory updated = inventoryService.setQuantity(id, quantity);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/adjust/{delta}")
    public ResponseEntity<?> adjustQuantity(@PathVariable Long id, @PathVariable int delta) {
        Inventory updated = inventoryService.adjustQuantity(id, delta);
        return ResponseEntity.ok(updated);
    }
}
