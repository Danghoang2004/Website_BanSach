package com.example.web_bansach.module.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.inventory.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	// Find inventory record by book id
	java.util.Optional<Inventory> findByBookId(Long bookId);

}



