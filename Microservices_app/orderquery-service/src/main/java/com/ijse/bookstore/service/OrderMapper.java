package com.ijse.bookstore.service;

import com.ijse.bookstore.entity.OrderReadModel;
import com.ijse.bookstore.entity.OrderDetailReadModel;
import com.ijse.bookstore.entity.ShippingInfoReadModel;
import com.ijse.bookstore.dto.OrderDTO;
import com.ijse.bookstore.dto.OrderDetailDTO;
import com.ijse.bookstore.dto.ShippingInfoDTO;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDTO toDTO(OrderReadModel order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(
            order.getTotalPrice() instanceof BigDecimal
                ? (BigDecimal) order.getTotalPrice()
                : BigDecimal.valueOf(((Number) order.getTotalPrice()).doubleValue())
        );
        dto.setStatus(order.getStatus());
        dto.setShippingOrderId(order.getShippingOrderId());

        ShippingInfoReadModel shipping = order.getShipping();
        if (shipping != null) {
            ShippingInfoDTO shippingDTO = new ShippingInfoDTO();
            shippingDTO.setAddress(shipping.getAddress());
            shippingDTO.setCity(shipping.getCity());
            shippingDTO.setPostalCode(shipping.getPostalCode());
            shippingDTO.setEmail(shipping.getEmail());
            shippingDTO.setFirstName(shipping.getFirstName());
            shippingDTO.setLastName(shipping.getLastName());
            dto.setShipping(shippingDTO);
        }

        if (order.getOrderDetails() != null) {
            dto.setOrderDetails(
                order.getOrderDetails().stream().map(detail -> {
                    OrderDetailDTO detailDTO = new OrderDetailDTO();
                    detailDTO.setId(detail.getId());
                    detailDTO.setBookId(detail.getBookId());
                    detailDTO.setQuantity(detail.getQuantity());
                    if (detail.getSubTotal() != null) {
                        Object subTotalObj = detail.getSubTotal();
                        BigDecimal subTotalValue = null;
                        if (subTotalObj instanceof BigDecimal) {
                            subTotalValue = (BigDecimal) subTotalObj;
                        } else if (subTotalObj instanceof Number) {
                            subTotalValue = BigDecimal.valueOf(((Number) subTotalObj).doubleValue());
                        } else if (subTotalObj instanceof String) {
                            try {
                                subTotalValue = new BigDecimal((String) subTotalObj);
                            } catch (NumberFormatException e) {
                                subTotalValue = null;
                            }
                        }
                        detailDTO.setSubTotal(subTotalValue);
                    } else {
                        detailDTO.setSubTotal(null);
                    }
                    return detailDTO;
                }).collect(Collectors.toList())
            );
        }

        return dto;
    }
}
