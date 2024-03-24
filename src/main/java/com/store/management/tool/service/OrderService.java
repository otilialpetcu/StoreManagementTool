package com.store.management.tool.service;

import com.store.management.tool.domain.Order;
import com.store.management.tool.domain.Product;
import com.store.management.tool.domain.User;
import com.store.management.tool.exception.ResourceNotFoundException;
import com.store.management.tool.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;

    public Order add(Order order) throws ResourceNotFoundException {
        User user = userService.getById(order.getIdUser());
        order.setIdUser(user.getId());

        BigDecimal subtotal = BigDecimal.valueOf(0);

        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            for (Product product : order.getProducts()) {
                subtotal = subtotal.add(BigDecimal.valueOf(product.getQuantity()).multiply((product.getPrice())));
            }
            order.setProducts(this.mappedProducts(order));
        }

        order.setSubtotal(subtotal);

        if (order.getSubtotal().compareTo(BigDecimal.ZERO) != 0) {
            return orderRepository.save(order);
        } else {
            throw new ResourceNotFoundException("Unable to complete order. Please add existing products.");
        }
    }

    public List<Order> getAll() {
        log.info("Retrieving all orders.");
        return orderRepository.findAll();
    }

    public Order getById(Long id) throws ResourceNotFoundException {
        log.info("Retrieving order by id: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Order with id: %d not found", id)));
    }

    public Order updateOrder(Long id, Order orderDetails) throws ResourceNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Order with id %d not found", id)));

        User user = userService.getById(orderDetails.getIdUser());
        orderDetails.setIdUser(user.getId());

        order.setOrderDate(orderDetails.getOrderDate());
        order.setStatus(orderDetails.getStatus());

        BigDecimal subtotal = BigDecimal.valueOf(0);

        order.setProducts(Set.of());
        if (orderDetails.getProducts() != null && !orderDetails.getProducts().isEmpty()) {
            order.setProducts(this.mappedProducts(orderDetails));

            for (Product product : order.getProducts()) {
                subtotal = subtotal.add(BigDecimal.valueOf(product.getQuantity()).multiply((product.getPrice())));
            }
        }

        order.setSubtotal(subtotal);

        if (order.getSubtotal().compareTo(BigDecimal.ZERO) != 0) {
            return orderRepository.save(order);
        } else {
            throw new ResourceNotFoundException("Unable to complete order. Please add existing products.");
        }
    }

    public void delete(Long id) throws ResourceNotFoundException {
        log.info("Deleting order with id: {}", id);
        Order orderToDelete = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Order with id: %d not found", id)));

        orderRepository.delete(orderToDelete);
        log.info("Order {} deleted successfully", id);
    }

    private Set<Product> mappedProducts(Order order) {
        Set<Product> orderedProducts = new HashSet<>();

        order.getProducts().forEach(orderProduct -> {
            try {
                Product product = productService.getById(orderProduct.getId());
                int availableStock = product.getQuantity();
                int requestedStock = orderProduct.getQuantity();

                if (availableStock >= requestedStock) {
                    int newStock = availableStock - requestedStock;
                    product.setQuantity(newStock);
                    productService.update(product.getId(), product);
                    log.info(String.format("Stock for product %d updated. Remaining stock: %d. Quantity ordered: %d", product.getId(), newStock, requestedStock));
                    orderedProducts.add(orderProduct);
                } else {
                    log.error("Insufficient stock to fulfill order");
                    throw new IllegalArgumentException("Insufficient stock to fulfill order");
                }
            } catch (ResourceNotFoundException e) {
                log.error("Failed to retrieve product information: " + e);
            }
        });

        return orderedProducts;
    }
}
