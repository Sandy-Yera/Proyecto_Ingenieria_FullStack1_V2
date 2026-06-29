package com.logistica.ms_fleet.service;

import com.logistica.ms_fleet.dto.GpsLocationDTO;
import com.logistica.ms_fleet.dto.VehiculoRequestDTO;
import com.logistica.ms_fleet.exception.entity.EntityConflictException;
import com.logistica.ms_fleet.exception.entity.EntityNotFoundException;
import com.logistica.ms_fleet.kafka.FleetKafkaProducer;
import com.logistica.ms_fleet.model.Vehiculo;
import com.logistica.ms_fleet.repository.VehiculoRepository;
import com.logistica.ms_fleet.service.impl.VehiculoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceImplTest {

    @Mock
    VehiculoRepository vehiculoRepository;

    @Mock
    FleetKafkaProducer kafkaProducer;

    @InjectMocks
    VehiculoServiceImpl service;

    private static VehiculoRequestDTO dtoValido() {
        return new VehiculoRequestDTO("AB1234", "Toyota", "Hilux", 2022, 1000, "ACTIVO");
    }

    @Test
    void crearVehiculo_cuandoPlacaYaExiste_lanzaEntityConflictException() {
        given(vehiculoRepository.existsByPlaca("AB1234")).willReturn(true);

        assertThrows(EntityConflictException.class, () -> service.crearVehiculo(dtoValido()));
        then(vehiculoRepository).should(org.mockito.Mockito.never()).save(any());
    }

    @Test
    void crearVehiculo_cuandoDatosValidos_guardaElVehiculo() {
        given(vehiculoRepository.existsByPlaca("AB1234")).willReturn(false);
        given(vehiculoRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        service.crearVehiculo(dtoValido());

        then(vehiculoRepository).should().save(any());
    }

    @Test
    void obtenerVehiculoPorId_cuandoNoExiste_lanzaEntityNotFoundException() {
        given(vehiculoRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.obtenerVehiculoPorId(1L));
    }

    @Test
    void eliminarVehiculo_cuandoNoExiste_lanzaEntityNotFoundException() {
        given(vehiculoRepository.existsById(1L)).willReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.eliminarVehiculo(1L));
        then(vehiculoRepository).should(org.mockito.Mockito.never()).deleteById(any());
    }

    @Test
    void registrarUbicacion_cuandoVehiculoNoExiste_lanzaEntityNotFoundExceptionYNoPublicaEnKafka() {
        given(vehiculoRepository.existsById(1L)).willReturn(false);
        GpsLocationDTO gps = new GpsLocationDTO();

        assertThrows(EntityNotFoundException.class, () -> service.registrarUbicacion(1L, gps));
        then(kafkaProducer).should(org.mockito.Mockito.never()).publicarUbicacion(any(), any());
    }

    @Test
    void registrarUbicacion_cuandoVehiculoExiste_publicaEnKafka() {
        given(vehiculoRepository.existsById(1L)).willReturn(true);
        GpsLocationDTO gps = new GpsLocationDTO();

        service.registrarUbicacion(1L, gps);

        then(kafkaProducer).should().publicarUbicacion(1L, gps);
    }
}
