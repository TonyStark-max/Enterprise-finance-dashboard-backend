package com.zorvyn.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategoryTotalResponse {
    private String category;
    private BigDecimal total;
}
