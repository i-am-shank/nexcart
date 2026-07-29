package com.springProjects.onlineStore.order.mapper;

import com.springProjects.onlineStore.order.dto.OrderItemResponseDTO;
import com.springProjects.onlineStore.order.entity.OrderItem;
import com.springProjects.onlineStore.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {
    private final ModelMapper modelMapper;

    private final ProductMapper productMapper;

    public OrderItemResponseDTO toResponseDTO(OrderItem orderItem) {
        if(orderItem == null) {
            return null;
        }
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        OrderItemResponseDTO orderItemResponseDTO = modelMapper.map(orderItem, OrderItemResponseDTO.class);
        if(orderItem.getProduct() != null) {
            orderItemResponseDTO.setProduct(productMapper.toResponseDTO(orderItem.getProduct()));
        }
        return orderItemResponseDTO;
    }
}
