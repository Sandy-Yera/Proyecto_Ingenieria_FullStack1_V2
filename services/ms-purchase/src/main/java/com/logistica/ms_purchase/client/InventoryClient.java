package com.logistica.ms_purchase.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.logistica.ms_purchase.dto.MaterialResponseDTO;

@FeignClient(name = "ms-inventory", path = "/api/inventory")
public interface InventoryClient {

    @GetMapping("/{id}")
    MaterialResponseDTO obtenerMaterialPorId(@PathVariable("id") Long id);

    @PutMapping("/{id}/restock")
    MaterialResponseDTO reabastecerStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);
}
