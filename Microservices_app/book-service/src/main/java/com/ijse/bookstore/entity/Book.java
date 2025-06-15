package com.ijse.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookid")
    private Long id;

    @Column
    private String title;

    @Column(unique = true)
    private String isbnNumber;

    @Column(columnDefinition = "BLOB")
    private byte[] image;

    @Column
    private String description;

    @Column
    private double price;

    @Column
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonBackReference
    private Author author;
  
    @ManyToOne
    @JoinColumn(name = "category_id" )
    private Category category;

    @ManyToOne
    @JoinColumn(name = "subcategory_id" )
    private Subcategory subcategory;

}
