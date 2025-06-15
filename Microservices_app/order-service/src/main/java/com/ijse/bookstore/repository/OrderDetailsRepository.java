package com.ijse.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ijse.bookstore.entity.OrderDetails;
import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails,Long>{

    List<OrderDetails> findByOrder_Id(Long orderId);
    
}
