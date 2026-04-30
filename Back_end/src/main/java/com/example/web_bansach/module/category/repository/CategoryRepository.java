package com.example.web_bansach.module.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.category.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}



