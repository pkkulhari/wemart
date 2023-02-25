package com.pkkulhari.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkkulhari.orderservice.dtos.OrderLineItemDto;
import com.pkkulhari.orderservice.dtos.OrderRequest;
import com.pkkulhari.orderservice.repositories.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {
	@Container
	final private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private OrderRepository orderRepository;

	@DynamicPropertySource
	private static void addProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
	}

	@Test
	void contextLoads() {
	}

	/*
	@Test
	void shouldPlaceOrder() throws Exception {
		OrderRequest orderRequest = getOrderRequest();
		String orderRequestString = this.objectMapper.writeValueAsString(orderRequest);

		this.mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON).content(orderRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, this.orderRepository.findAll().size());
	}
	*/

	private OrderRequest getOrderRequest() {
		OrderLineItemDto orderLineItemDto = OrderLineItemDto.builder()
				.skuCode("iphone_13")
				.price(BigDecimal.valueOf(1300))
				.quantity(1)
				.build();

		return OrderRequest.builder()
				.orderLineItems(List.of(orderLineItemDto))
				.build();
	}

}
