package com.example.web_bansach.module.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.module.book.dto.response.BookResponse;
import com.example.web_bansach.module.book.service.BookUserService;

@RestController
@RequestMapping("/user/books")
@PreAuthorize("hasAuthority('USER')")
public class BookPublicController {

    @Autowired
    private BookUserService bookUserService;

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getBooks(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(bookUserService.searchBooks(page, size, keyword.trim()));
        }
        if (categoryId != null) {
            return ResponseEntity.ok(bookUserService.getBooksByCategory(page, size, categoryId));
        }
        return ResponseEntity.ok(bookUserService.getAllBooks(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookDetail(@PathVariable Long id) {
        return ResponseEntity.ok(bookUserService.getBookDetail(id));
    }
}