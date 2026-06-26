package com.logistica.ms_inventory.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.ms_inventory.dto.MaterialRequestDTO;
import com.logistica.ms_inventory.dto.MaterialResponseDTO;
import com.logistica.ms_inventory.service.MaterialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de materiales e inventario del sistema BRM")
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "Crear material", description = "Registra un nuevo material en el inventario.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Material creado",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "409", description = "Nombre de material duplicado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<MaterialResponseDTO> crearMaterial(@Valid @RequestBody MaterialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materialService.crearMaterial(dto));
    }

    @Operation(summary = "Listar materiales", description = "Retorna todos los materiales registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado retornado"),
        @ApiResponse(responseCode = "204", description = "Sin materiales", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<MaterialResponseDTO>> listarMateriales() {
        List<MaterialResponseDTO> lista = materialService.listarMateriales();
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener material por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Material encontrado",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Material no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> obtenerMaterialPorId(
            @Parameter(description = "ID del material") @PathVariable Long id) {
        return ResponseEntity.ok(materialService.obtenerMaterialPorId(id));
    }

    @Operation(summary = "Actualizar material")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Material actualizado",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Material no encontrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> actualizarMaterial(
            @PathVariable Long id, @Valid @RequestBody MaterialRequestDTO dto) {
        return ResponseEntity.ok(materialService.actualizarMaterial(id, dto));
    }

    @Operation(summary = "Eliminar material")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Material eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Material no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMaterial(@PathVariable Long id) {
        materialService.eliminarMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Consumir stock",
        description = "Descuenta la cantidad indicada del stock. Falla si el resultado sería negativo."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock descontado",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Stock insuficiente o cantidad inválida", content = @Content),
        @ApiResponse(responseCode = "404", description = "Material no encontrado", content = @Content)
    })
    @PatchMapping("/{id}/consume")
    public ResponseEntity<MaterialResponseDTO> consumirStock(
            @Parameter(description = "ID del material") @PathVariable Long id,
            @Parameter(description = "Cantidad a consumir") @RequestParam Integer cantidad) {
        return ResponseEntity.ok(materialService.consumirStock(id, cantidad));
    }

    @Operation(
        summary = "Reabastecer stock",
        description = "Suma la cantidad indicada al stock. Llamado automáticamente por ms-purchase al registrar una compra."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock reabastecido",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Cantidad inválida", content = @Content),
        @ApiResponse(responseCode = "404", description = "Material no encontrado", content = @Content)
    })
    @PutMapping("/{id}/restock")
    public ResponseEntity<MaterialResponseDTO> reabastecerStock(
            @Parameter(description = "ID del material") @PathVariable Long id,
            @Parameter(description = "Cantidad a agregar") @RequestParam Integer cantidad) {
        return ResponseEntity.ok(materialService.reabastecerStock(id, cantidad));
    }
}
