package com.logistica.ms_inventory.service;

import com.logistica.ms_inventory.exception.entity.EntityBadRequestException;
import com.logistica.ms_inventory.exception.entity.EntityNotFoundException;
import com.logistica.ms_inventory.model.Material;
import com.logistica.ms_inventory.repository.MaterialRepository;
import com.logistica.ms_inventory.service.impl.MaterialServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MaterialServiceImplTest {

    @Mock
    MaterialRepository materialRepository;

    @InjectMocks
    MaterialServiceImpl service;

    @Test
    void consumirStock_cuandoMaterialNoExiste_lanzaEntityNotFoundException() {
        given(materialRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.consumirStock(1L, 5));
    }

    @Test
    void consumirStock_cuandoStockMenorQueCantidadSolicitada_lanzaEntityBadRequestException() {
        Material material = new Material(1L, "Cañería PVC", "1/2 pulgada", 3, "unidad", null);
        given(materialRepository.findById(1L)).willReturn(Optional.of(material));

        assertThrows(EntityBadRequestException.class, () -> service.consumirStock(1L, 5));
    }

    @Test
    void consumirStock_cuandoStockEsSuficiente_descuentaCorrectamente() {
        Material material = new Material(1L, "Cañería PVC", "1/2 pulgada", 10, "unidad", null);
        given(materialRepository.findById(1L)).willReturn(Optional.of(material));

        service.consumirStock(1L, 4);

        assertThat(material.getStock()).isEqualTo(6);
    }

    @Test
    void reabastecerStock_cuandoCantidadEsCeroONegativa_lanzaEntityBadRequestException() {
        assertThrows(EntityBadRequestException.class, () -> service.reabastecerStock(1L, 0));
    }
}
