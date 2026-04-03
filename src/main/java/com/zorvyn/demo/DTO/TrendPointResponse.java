package com.zorvyn.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TrendPointResponse {
    private String period;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;
}
