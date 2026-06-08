package com.logistica.ms_buildings.service;

import java.util.List;

import com.logistica.ms_buildings.dto.EdificioRequestDTO;
import com.logistica.ms_buildings.dto.EdificioResponseDTO;

/**
 * CONTRATO DEL SERVICIO — EdificioService
 * Define las operaciones de negocio disponibles para el dominio de Edificios.
 * La implementación concreta reside en impl/EdificioServiceImpl.java.
 * Este desacoplamiento facilita el testing con mocks y la extensión futura del servicio.
 */
public interface EdificioService {

    /**
     * Crea un nuevo edificio en el sistema.
     * Valida que el RUT del administrador no esté registrado previamente.
     *
     * @param dto Datos del edificio a registrar.
     * @return EdificioResponseDTO con los datos persistidos incluyendo el ID generado.
     */
    EdificioResponseDTO crearEdificio(EdificioRequestDTO dto);

    /**
     * Retorna el listado completo de edificios registrados.
     *
     * @return Lista de EdificioResponseDTO. Puede estar vacía si no hay registros.
     */
    List<EdificioResponseDTO> listarEdificios();

    /**
     * Busca y retorna un edificio por su identificador único.
     *
     * @param id Identificador del edificio.
     * @return EdificioResponseDTO con los datos del edificio encontrado.
     */
    EdificioResponseDTO obtenerEdificioPorId(Long id);

    /**
     * Actualiza los datos de un edificio existente.
     * Valida que el ID exista y que el RUT actualizado no colisione con otro registro.
     *
     * @param id  Identificador del edificio a actualizar.
     * @param dto Nuevos datos del edificio.
     * @return EdificioResponseDTO con los datos actualizados.
     */
    EdificioResponseDTO actualizarEdificio(Long id, EdificioRequestDTO dto);

    /**
     * Elimina un edificio por su identificador.
     * Lanza excepción si el edificio no existe.
     *
     * @param id Identificador del edificio a eliminar.
     */
    void eliminarEdificio(Long id);
}
