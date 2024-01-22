package com.m3sysoft.orderservice.service;

import com.m3sysoft.orderservice.dto.InventoryResponse;
import com.m3sysoft.orderservice.dto.OrderLineItemDto;
import com.m3sysoft.orderservice.dto.OrderRequest;
import com.m3sysoft.orderservice.model.Order;
import com.m3sysoft.orderservice.model.OrderLineItem;
import com.m3sysoft.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems = orderRequest
                .getOrderLineItemListDto()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemList(orderLineItems);

        List<String> skuCodes = order
                .getOrderLineItemList()
                .stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        //check before placing order using synchronous call

        InventoryResponse[] inventoryResponses = webClientBuilder
                .build()
                .get()
                .uri("http://INVENTORY-SERVICE/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = false;
        if (inventoryResponses != null) {

            allProductsInStock = Arrays
                    .stream(inventoryResponses)
                    .allMatch(InventoryResponse::isInStock);
        }
        if (Boolean.TRUE.equals(allProductsInStock)) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not on stock, please try again later");
        }
    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setId(orderLineItemDto.getId());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        return orderLineItem;
    }
}
