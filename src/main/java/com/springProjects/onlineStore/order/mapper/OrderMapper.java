package com.springProjects.onlineStore.order.mapper;

import com.springProjects.onlineStore.order.dto.OrderResponseDTO;
import com.springProjects.onlineStore.order.dto.OrderSummaryResponseDTO;
import com.springProjects.onlineStore.order.entity.Order;
import com.springProjects.onlineStore.user.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final ModelMapper modelMapper;

    private final AddressMapper addressMapper;

    public OrderResponseDTO toResponseDTO(Order order) {
        if(order == null) {
            return null;
        }
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        OrderResponseDTO orderResponseDTO = modelMapper.map(order, OrderResponseDTO.class);
        if(order.getAddress() != null) {
            orderResponseDTO.setAddress(addressMapper.toResponseDTO(order.getAddress()));
        }
        return orderResponseDTO;
    }

    public OrderSummaryResponseDTO toSummaryResponseDTO(Order order) {
        if(order == null) {
            return null;
        }
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        OrderSummaryResponseDTO orderSummaryResponseDTO = modelMapper.map(order, OrderSummaryResponseDTO.class);
        if(order.getAddress() != null) {
            orderSummaryResponseDTO.setAddress(addressMapper.toResponseDTO(order.getAddress()));
        }
        return orderSummaryResponseDTO;
    }
}
