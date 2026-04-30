package com.example.web_bansach.module.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.module.book.dto.response.BookAdminResponse;
import com.example.web_bansach.module.book.service.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/books")
@PreAuthorize("hasAuthority('ADMIN')")
public class BookAdminController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<Page<BookAdminResponse>> getBooksAdmin(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookAdminResponse> getBookDetail(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookDetail(id));
    }

    @PostMapping(value = "/create-book", consumes = "multipart/form-data")
    public ResponseEntity<BookAdminResponse> createBook(
            @Valid @ModelAttribute BookRequest request,
            @RequestParam(required = false) MultipartFile image) throws Exception {
        return ResponseEntity.ok(bookService.createBook(request, image));
    }

    @PutMapping(value = "/update-book/{id}", consumes = "multipart/form-data")
    public ResponseEntity<BookAdminResponse> updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute BookRequest request,
            @RequestParam(required = false) MultipartFile image) throws Exception {
        return ResponseEntity.ok(bookService.updateBook(id, request, image));
    }

    @DeleteMapping("/delete-book/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Xóa mềm sách thành công");
    }

}