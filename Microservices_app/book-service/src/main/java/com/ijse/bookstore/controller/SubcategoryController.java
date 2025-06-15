package com.ijse.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ijse.bookstore.entity.Subcategory;
import com.ijse.bookstore.service.SubcategoryService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class SubcategoryController {

    @Autowired
    private SubcategoryService subcategoryService;

    @GetMapping("/subcategories")
    public ResponseEntity<List<Subcategory>> getAllSubcategories() {
        List<Subcategory> subcategories = subcategoryService.getAllSubcategories();
        return ResponseEntity.ok(subcategories);
    }
}