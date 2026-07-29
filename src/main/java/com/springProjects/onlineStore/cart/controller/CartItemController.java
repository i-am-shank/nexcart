package com.springProjects.onlineStore.cart.controller;

import com.springProjects.onlineStore.cart.dto.CartItemResponseDTO;
import com.springProjects.onlineStore.cart.service.CartItemService;
import com.springProjects.onlineStore.common.dto.ResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart-item")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @PostMapping("/add-to-cart/user/{userId}/product/{productId}")
    public ResponseEntity<ResponseDTO> addToCart(@PathVariable Integer userId,
                                                 @PathVariable Integer productId,
                                                 @RequestParam Integer quantity) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException, IllegalStateException {
        CartItemResponseDTO cartItemResponseDTO = cartItemService.addToCart(userId, productId, quantity);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Product added to cart", cartItemResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{cartItemId}")
    public ResponseEntity<ResponseDTO> getCartItem(@PathVariable Integer cartItemId)
            throws IllegalArgumentException, ResourceNotFoundException {
        CartItemResponseDTO cartItemResponseDTO = cartItemService.getCartItem(cartItemId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Cart Item fetched successfully",
                cartItemResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<ResponseDTO> updateCartItem(@PathVariable Integer cartItemId,
                                                      @RequestParam(value = "quantity", required = false)
                                                      Integer quantity,
                                                      @RequestParam(value = "saveForLater", required = false)
                                                      Boolean saveForLater) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException, IllegalStateException {
        CartItemResponseDTO cartItemResponseDTO = cartItemService.updateCartItem(cartItemId, quantity, saveForLater);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "CartItem updated successfully",
                cartItemResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ResponseDTO> removeCartItem(@PathVariable Integer cartItemId)
            throws IllegalArgumentException, ResourceNotFoundException, IllegalStateException {
        cartItemService.removeCartItem(cartItemId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "CartItem removed successfully");
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/saveForLater/{cartItemId}")
    public ResponseEntity<ResponseDTO> saveForLater(@PathVariable Integer cartItemId)
            throws IllegalArgumentException, ResourceNotFoundException {
        cartItemService.saveItemForLater(cartItemId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Cart Item saved for later");
        return ResponseEntity.ok(responseDTO);
    }
}
