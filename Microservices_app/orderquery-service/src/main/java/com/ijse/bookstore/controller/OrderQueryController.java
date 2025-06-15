package com.ijse.bookstore.controller;

import com.ijse.bookstore.entity.OrderReadModel;
import com.ijse.bookstore.repository.OrderReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderQueryController {

    @Autowired
    private OrderReadRepository repository;

    @GetMapping
    public List<OrderReadModel> getOrdersBetweenDates(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        return repository.findByOrderDateBetween(startDateTime, endDateTime);
    }
}
