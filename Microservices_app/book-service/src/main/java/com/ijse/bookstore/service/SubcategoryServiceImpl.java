package com.ijse.bookstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ijse.bookstore.entity.Subcategory;
import com.ijse.bookstore.repository.SubcategoryRepository;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Override
    public List<Subcategory> getAllSubcategories() {
        return subcategoryRepository.findAll();
    }
}