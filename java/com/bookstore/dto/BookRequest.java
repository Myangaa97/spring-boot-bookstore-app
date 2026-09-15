package com.bookstore.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(

        @NotBlank
        @Size(max = 200)
        String title,

        @NotBlank
        @Size(max = 20)
        String isbn,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stockQuantity,

        boolean active,

        @NotNull
        Long categoryId,

        @NotNull
        Long authorId

) {}