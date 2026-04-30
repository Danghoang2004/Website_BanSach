package com.example.web_bansach.module.author.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.web_bansach.module.author.dto.request.AuthorRequest;
import com.example.web_bansach.module.author.dto.response.AuthorResponse;
import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.author.entity.Author;
import com.example.web_bansach.module.author.repository.AuthorRepository;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public Author addAuthorService(AuthorRequest request) {
        String trimmedName = request.getAuthorName().trim();
        Author autCheck = authorRepository.findByAuthorName(trimmedName);
        if (autCheck != null) {
            throw new BusinessException("Tên tác giả đã tồn tại");
        }

        Author aut = new Author();
        aut.setAuthorName(trimmedName);
        aut.setBiography(request.getBiography().trim());
        return authorRepository.save(aut);
    }

    public Author updateAuthorService(Long id, AuthorRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID tác giả không hợp lệ");
        }
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả"));

        author.setAuthorName(request.getAuthorName().trim());
        author.setBiography(request.getBiography().trim());
        return authorRepository.save(author);
    }

    public void deleAuthorService(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả cần xóa"));
        authorRepository.deleteById(author.getId());
    }

    public Page<AuthorResponse> getAllAuthorPagination(Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Author> authorPage = authorRepository.findAll(pageable);

        return authorPage.map(author -> new AuthorResponse(
                author.getId(),
                author.getAuthorName(),
                author.getBiography()));
    }

}
