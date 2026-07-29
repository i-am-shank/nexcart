package com.springProjects.onlineStore.cart.controller;

import com.springProjects.onlineStore.cart.dto.CartDetailsResponseDTO;
import com.springProjects.onlineStore.cart.dto.CartPaymentDetailsDTO;
import com.springProjects.onlineStore.cart.dto.CartResponseDTO;
import com.springProjects.onlineStore.cart.service.CartService;
import com.springProjects.onlineStore.common.dto.ResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> createCart(@PathVariable Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException {
        CartDetailsResponseDTO cartDetailsResponseDTO = cartService.createCartForUser(userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Cart created successfully",
                cartDetailsResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/details/user/{userId}")
    public ResponseEntity<ResponseDTO> getUserCartDetails(@PathVariable Integer userId)
            throws IllegalArgumentException, ResourceNotFoundException {
        CartDetailsResponseDTO cartDetailsResponseDTO = cartService.getCartDetails(userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK,
                "Cart details for user fetched successfully", cartDetailsResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> getUserCart(@PathVariable Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException {
        CartResponseDTO cartResponseDTO = cartService.getCart(userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK,
                "Cart for user fetched successfully", cartResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/payment/user/{userId}")
    public ResponseEntity<ResponseDTO> getCartPaymentDetails(@PathVariable Integer userId)
            throws IllegalArgumentException, ResourceNotFoundException {
        CartPaymentDetailsDTO cartPaymentDetailsDTO = cartService.getCartPaymentDetails(userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Cart payment details fetched successfully",
                cartPaymentDetailsDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/clear/user/{userId}")
    public ResponseEntity<ResponseDTO> clearCart(@PathVariable Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException {
        CartDetailsResponseDTO cartDetailsResponseDTO = cartService.clearCart(userId, Boolean.TRUE);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Cart cleared successfully",
                cartDetailsResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
