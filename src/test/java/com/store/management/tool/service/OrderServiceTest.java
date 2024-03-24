package com.store.management.tool.service;

import com.store.management.tool.domain.Order;
import com.store.management.tool.domain.Product;
import com.store.management.tool.domain.User;
import com.store.management.tool.exception.ResourceNotFoundException;
import com.store.management.tool.repository.OrderRepository;
import com.store.management.tool.utils.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Test
    void testAddOrder_EmptyProducts() throws ResourceNotFoundException {
        Order order = new Order();
        User user = new User();
        user.setId(1L);
        order.setIdUser(1L);
        order.setProducts(new HashSet<>());

        when(userService.getById(1L)).thenReturn(user);

        assertThrows(ResourceNotFoundException.class, () -> orderService.add(order));
    }

    @Test
    void testAddOrder_WithProducts_Success() throws ResourceNotFoundException {
        Product product = aProduct();
        Order order = anOrder();

        when(userService.getById(order.getIdUser())).thenReturn(new User());
        when(productService.getById(1L)).thenReturn(product);
        when(orderRepository.save(order)).thenReturn(order);

        Order addedOrder = orderService.add(order);

        assertNotNull(addedOrder);
        assertEquals(BigDecimal.valueOf(15), addedOrder.getSubtotal());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testUpdateOrder_Success() throws ResourceNotFoundException {
        long orderId = 1L;
        Order existingOrder = anOrder();
        Order updatedOrderDetails = anOrder();
        updatedOrderDetails.setSubtotal(BigDecimal.valueOf(26));
        updatedOrderDetails.setStatus(Status.IN_PROGRESS);

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);
        when(userService.getById(anyLong())).thenReturn(new User());
        when(productService.getById(anyLong())).thenReturn(aProduct());

        Order updatedOrder = orderService.updateOrder(orderId, updatedOrderDetails);

        assertNotNull(updatedOrder);
        verify(orderRepository, times(1)).save(existingOrder);
    }

    @Test
    void testUpdateOrder_OrderNotFound() {
        long orderId = 1L;
        Order updatedOrderDetails = new Order();

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(orderId, updatedOrderDetails));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void testDeleteOrder_Success() {
        long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));

        assertDoesNotThrow(() -> orderService.delete(orderId));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void testDeleteOrder_NotFound() {
        long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.delete(orderId));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).delete(any(Order.class));
    }

    private Product aProduct() {
        Product product = new Product();

        product.setId(1L);
        product.setName("product");
        product.setDescription("description");
        product.setPrice(BigDecimal.ONE);
        product.setQuantity(15);

        return product;
    }

    private Order anOrder() {
        Order order = new Order();

        order.setId(1L);
        order.setIdUser(1L);
        order.setOrderDate(LocalDate.of(2024, 3, 24));
        order.setSubtotal(BigDecimal.valueOf(3));
        order.setStatus(Status.NEW);
        order.setProducts(Set.of(aProduct()));

        return order;
    }
}
