package com.pkkulhari.inventoryservice.services;

import com.pkkulhari.inventoryservice.dtos.InventoryResponse;
import com.pkkulhari.inventoryservice.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    final private InventoryRepository inventoryRepository;
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream().map(inventory -> InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build()
        ).toList();
    }
}
