package com.springProjects.onlineStore.order.controller;

import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.common.dto.ResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.order.constants.OrderPeriod;
import com.springProjects.onlineStore.order.constants.OrderStatus;
import com.springProjects.onlineStore.order.constants.PaymentStatus;
import com.springProjects.onlineStore.order.dto.OrderResponseDTO;
import com.springProjects.onlineStore.order.dto.OrderSummaryResponseDTO;
import com.springProjects.onlineStore.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/place-order/{userId}/address/{addressId}")
    public ResponseEntity<ResponseDTO> placeOrder(@PathVariable Integer userId, @PathVariable Integer addressId)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException {
        OrderResponseDTO orderResponseDTO = orderService.placeOrder(userId, addressId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Order placed successfully", orderResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<ResponseDTO> updateOrderStatus(@PathVariable Integer orderId,
                                                         @RequestParam("status") OrderStatus orderStatus)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException,
            IllegalStateException {
        OrderResponseDTO orderResponseDTO = orderService.updateOrderStatus(orderId, orderStatus);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Order status updated successfully",
                orderResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/payment-status/{orderId}")
    public ResponseEntity<ResponseDTO> updateOrderPaymentStatus(@PathVariable Integer orderId,
                                                                @RequestParam("status") PaymentStatus paymentStatus)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException,
            IllegalStateException {
        OrderResponseDTO orderResponseDTO = orderService.updateOrderPaymentStatus(orderId, paymentStatus);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Order payment status updated successfully",
                orderResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{orderId}/user/{userId}")
    public ResponseEntity<ResponseDTO> getOrderDetails(@PathVariable Integer orderId, @PathVariable Integer userId) {
        OrderResponseDTO orderResponseDTO = orderService.getOrderDetails(orderId, userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Order details retrieved successfully",
                orderResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> getOrdersForUser(@PathVariable Integer userId,
                                                        @RequestParam(value = "orderPeriod", required = false)
                                                        OrderPeriod orderPeriod,
                                                        @RequestParam(value = "pageNumber", required = false)
                                                        Integer pageNumber,
                                                        @RequestParam(value = "pageSize", required = false)
                                                        Integer pageSize) throws IllegalArgumentException {
        PageableResponseDTO<OrderSummaryResponseDTO> userOrders =
                orderService.getOrdersForUser(userId, pageNumber, pageSize, orderPeriod);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "User orders retrieved successfully",
                userOrders);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/address/{addressId}/user/{userId}")
    public ResponseEntity<ResponseDTO> getOrdersForAddress(@PathVariable Integer addressId, @PathVariable Integer userId,
                                                           @RequestParam(value = "orderPeriod", required = false)
                                                           OrderPeriod orderPeriod,
                                                           @RequestParam(value = "pageNumber", required = false)
                                                           Integer pageNumber,
                                                           @RequestParam(value = "pageSize", required = false)
                                                           Integer pageSize) throws IllegalArgumentException {
        PageableResponseDTO<OrderSummaryResponseDTO> addressOrders =
                orderService.getOrdersForAddress(addressId, userId, pageNumber, pageSize, orderPeriod);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Address orders retrieved successfully",
                addressOrders);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{orderId}/user/{userId}")
    public ResponseEntity<ResponseDTO> cancelOrder(@PathVariable Integer orderId, @PathVariable Integer userId)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException {
        OrderResponseDTO orderResponseDTO = orderService.cancelOrder(orderId, userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Order cancelled successfully",
                orderResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
