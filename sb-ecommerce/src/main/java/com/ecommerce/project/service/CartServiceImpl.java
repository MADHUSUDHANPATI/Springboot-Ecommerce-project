package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.utils.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtils authUtils;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        // Find existing cart or create one
        Cart cart = createCart();

        //Retrieve product details
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new  ResourceNotFoundException("product", "productId", productId));

        //Perform validations
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId( cart.getCartId(), productId);

        if(cartItem != null) {
            throw new APIException("product" +product.getProductName() + " Already exist in the cart" );
        }

        if(product.getQuantity() ==0) {
            throw new APIException("Product " + product.getProductName() + " not available");
        }

        if(product.getQuantity() < quantity) {
            throw new APIException("Please make an order of the " + product.getProductName() + " less than or equal to quantity " + product.getQuantity() + " .");
        }

        //Create cart item
        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        //Save cart Item
        cartItemRepository.save(newCartItem);
        product.setQuantity(product.getQuantity());  // if we want we can subtract quantity , means, when product added into cart , quantity will reduce.
        cart.setTotalPrice(cart.getTotalPrice()+ (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        // return updated cart item
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems= cart.getCartItems();
        Stream<ProductDTO> productStream = cartItems.stream().map( item -> {

            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtils.loggedInEmail());
        if(userCart != null) {
            return userCart;
        }

        Cart cart= new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtils.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }
}
