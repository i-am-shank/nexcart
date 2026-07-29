package com.springProjects.onlineStore.order.controller;

import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.common.dto.ResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.order.dto.OrderItemResponseDTO;
import com.springProjects.onlineStore.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-item")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("/{orderItemId}")
    public ResponseEntity<ResponseDTO> getOrderItemById(@PathVariable Integer orderItemId)
            throws IllegalArgumentException, ResourceNotFoundException {
        OrderItemResponseDTO orderItemResponseDTO = orderItemService.getOrderItemById(orderItemId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "OrderItem fetched successfully",
                orderItemResponseDTO );
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseDTO> getOrderItemsByOrderId(@PathVariable Integer orderId)
            throws IllegalArgumentException, ResourceNotFoundException {
        List<OrderItemResponseDTO> orderItemResponseDTOs = orderItemService.getOrderItemsByOrderId(orderId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "OrderItems fetched successfully",
                orderItemResponseDTOs);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> getOrderItemsByUserId(@PathVariable Integer userId,
                                                             @RequestParam(value = "pageNumber", required = false)
                                                             Integer pageNumber,
                                                             @RequestParam(value = "pageSize", required = false)
                                                             Integer pageSize)
            throws IllegalArgumentException, ResourceNotFoundException {
        PageableResponseDTO<OrderItemResponseDTO> userOrderItems =
                orderItemService.getOrderItemsForUser(userId, pageNumber, pageSize);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "OrderItems for user fetched successfully",
                userOrderItems);
        return ResponseEntity.ok(responseDTO);
    }
}
