package com.example.web_bansach.module.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.inventory.dto.response.InventoryResponse;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    private final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    @Transactional(readOnly = true)
    public InventoryResponse getByBookId(Long bookId) {
        Inventory inv = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi tồn kho cho sách"));

        return mapToResponse(inv);
    }

    @Transactional(readOnly = true)
    public java.util.List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    @Transactional
    public Inventory setQuantity(Long inventoryId, Integer quantity) {
        Inventory inv = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy inventory"));
        inv.setQuantity(quantity);
        return inventoryRepository.save(inv);
    }

    @Transactional
    public Inventory adjustQuantity(Long inventoryId, int delta) {
        Inventory inv = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy inventory"));
        int newQty = (inv.getQuantity() == null ? 0 : inv.getQuantity()) + delta;
        if (newQty < 0) {
            throw new IllegalArgumentException("Số lượng không thể âm");
        }
        inv.setQuantity(newQty);
        return inventoryRepository.save(inv);
    }

    private InventoryResponse mapToResponse(Inventory inv) {
        InventoryResponse r = new InventoryResponse();
        r.setInventoryId(inv.getId());
        if (inv.getBook() != null) {
            r.setBookId(inv.getBook().getId());
            r.setBookTitle(inv.getBook().getTitle());
            r.setCoverImage(inv.getBook().getCoverImage());
        }
        Integer qty = inv.getQuantity();
        r.setQuantity(qty);
        r.setInStock(qty != null && qty > 0);
        r.setLowStockThreshold(DEFAULT_LOW_STOCK_THRESHOLD);
        r.setIsLowStock(qty != null && qty <= DEFAULT_LOW_STOCK_THRESHOLD);
        return r;
    }
}
