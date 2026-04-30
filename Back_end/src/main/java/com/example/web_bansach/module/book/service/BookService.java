package com.example.web_bansach.module.book.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.infrastructure.cloudinary.CloudinaryService;
import com.example.web_bansach.module.book.dto.response.BookAdminResponse;
import com.example.web_bansach.module.book.dto.request.BookRequest;
import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.author.entity.Author;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.category.entity.Category;
import com.example.web_bansach.module.book.entity.Discount;
import com.example.web_bansach.module.book.mapper.BookMapper;
import com.example.web_bansach.module.author.repository.AuthorRepository;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.category.repository.CategoryRepository;
import com.example.web_bansach.module.book.repository.DiscountRepository;

/**
 * Service xử lý nghiệp vụ Book cho Admin
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private BookMapper bookMapper;

    /**
     * Thêm sách mới (Admin)
     * Transaction rollback nếu có lỗi
     */
    @Transactional(rollbackFor = Exception.class)
    public BookAdminResponse createBook(BookRequest request, MultipartFile image) throws Exception {
        // Validate ISBN
        if (bookRepository.existsByIsbn(request.getIsbn().trim())) {
            throw new BusinessException("ISBN đã tồn tại");
        }

        // Validate Author
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả"));

        // Validate Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        // Validate Discount (optional)
        Discount discount = null;
        if (request.getDiscountId() != null) {
            discount = discountRepository.findById(request.getDiscountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương trình giảm giá"));
        }

        // Upload ảnh (nếu có lỗi sẽ rollback transaction)
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(image, "books");
        }

        // Tạo Book entity
        Book book = new Book();
        book.setTitle(request.getTitle().trim());
        book.setIsbn(request.getIsbn().trim());
        book.setPublisher(request.getPublisher().trim());
        book.setPublicationYear(request.getPublicationYear());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());
        book.setCoverImage(imageUrl);
        book.setAuthor(author);
        book.setCategory(category);
        book.setDiscount(discount);
        book.setCreatedAt(LocalDateTime.now());

        // Save vào DB
        Book savedBook = bookRepository.save(book);
        return bookMapper.mapToAdminResponse(savedBook);
    }

    /**
     * Cập nhật sách (Admin)
     * Transaction rollback nếu có lỗi
     */
    @Transactional(rollbackFor = Exception.class)
    public BookAdminResponse updateBook(Long id, BookRequest request, MultipartFile image) throws Exception {
        // Tìm sách
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        // Validate ISBN (nếu thay đổi)
        if (!book.getIsbn().equals(request.getIsbn().trim())) {
            if (bookRepository.existsByIsbn(request.getIsbn().trim())) {
                throw new BusinessException("ISBN đã tồn tại");
            }
            book.setIsbn(request.getIsbn().trim());
        }

        // Validate Author
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả"));

        // Validate Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        // Validate Discount
        Discount discount = null;
        if (request.getDiscountId() != null) {
            discount = discountRepository.findById(request.getDiscountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương trình giảm giá"));
        }

        // Upload ảnh mới (nếu có)
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image, "books");
            book.setCoverImage(imageUrl);
        }

        // Cập nhật thông tin
        book.setTitle(request.getTitle().trim());
        book.setPublisher(request.getPublisher().trim());
        book.setPublicationYear(request.getPublicationYear());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());
        book.setAuthor(author);
        book.setCategory(category);
        book.setDiscount(discount);

        Book updatedBook = bookRepository.save(book);
        return bookMapper.mapToAdminResponse(updatedBook);
    }

    /**
     * Xóa mềm sách (Admin)
     */
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        book.setDeletedAt(LocalDateTime.now());
        bookRepository.save(book);
    }

    /**
     * Lấy chi tiết sách (Admin)
     */
    @Transactional(readOnly = true)
    public BookAdminResponse getBookDetail(Long id) {
        Book book = bookRepository.findByIdWithJoin(id);
        if (book == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        return bookMapper.mapToAdminResponse(book);
    }

    /**
     * Lấy danh sách sách (Admin)
     */
    @Transactional(readOnly = true)
    public Page<BookAdminResponse> getAllBooks(Pageable pageable) {
        Page<Book> page = bookRepository.findAllActiveBooks(pageable);
        return page.map(bookMapper::mapToAdminResponse);
    }

}
