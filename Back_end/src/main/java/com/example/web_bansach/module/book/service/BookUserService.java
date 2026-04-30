package com.example.web_bansach.module.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.book.dto.response.BookResponse;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.mapper.BookMapper;
import com.example.web_bansach.module.book.repository.BookRepository;

@Service
public class BookUserService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAllActiveBooks(pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getBooksByCategory(Integer page, Integer size, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByCategoryIdWithJoin(categoryId, pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.searchByTitleWithJoin(keyword, pageable)
                .map(bookMapper::mapToResponseForUser);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookDetail(Long id) {
        Book book = bookRepository.findByIdWithJoin(id);
        if (book == null || book.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        return bookMapper.mapToResponseForUser(book);
    }
}
