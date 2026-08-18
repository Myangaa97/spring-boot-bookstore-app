package com.bookstore.controller;

import com.bookstore.service.BookService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final BookService bookService;

    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Pageable pageable = PageRequest.of(0, 8);
        model.addAttribute("books", bookService.findAll(pageable).getContent());
        return "home";
    }
}