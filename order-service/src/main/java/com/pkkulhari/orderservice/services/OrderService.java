package com.pkkulhari.orderservice.services;

import com.pkkulhari.orderservice.dtos.InventoryResponse;
import com.pkkulhari.orderservice.dtos.OrderLineItemDto;
import com.pkkulhari.orderservice.dtos.OrderRequest;
import com.pkkulhari.orderservice.events.OrderPlacedEvent;
import com.pkkulhari.orderservice.models.Order;
import com.pkkulhari.orderservice.models.OrderLineItem;
import com.pkkulhari.orderservice.repositories.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    final private OrderRepository orderRepository;
    final private WebClient.Builder webClientBuilder;
    final private Tracer tracer;
    final private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItems().stream()
                .map(this::mapToOrderLineItem).toList();
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItems)
                .build();

        List<String> skuCodes = orderLineItems.stream().map(OrderLineItem::getSkuCode).toList();

        // Check if products are in stock or not
        Span inventoryServiceLookup = tracer.nextSpan().name("inventoryServiceLookup");
        try(Tracer.SpanInScope isLookup =  tracer.withSpan(inventoryServiceLookup.start())) {
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory", uriBuilder ->
                            uriBuilder.queryParam("skuCodes", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
            Boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

            if (!allProductsInStock) throw new IllegalArgumentException("Product are not in stock");
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed successfully";
        } finally {
            inventoryServiceLookup.end();
        }
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .skuCode(orderLineItemDto.getSkuCode())
                .price(orderLineItemDto.getPrice())
                .quantity(orderLineItemDto.getQuantity())
                .build();
    }
}
