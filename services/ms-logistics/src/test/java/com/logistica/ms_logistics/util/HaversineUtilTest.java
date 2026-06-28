package com.logistica.ms_logistics.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class HaversineUtilTest {

    @Test
    void calcularDistanciaKm_coordenadasReales_retornaValorAproximado() {
        double resultado = HaversineUtil.calcularDistanciaKm(
                -33.4569, -70.6483,
                -33.4372, -70.6506);
        assertThat(resultado).isCloseTo(2.2, within(0.1));
    }

    @Test
    void calcularDistanciaKm_mismasCoordenadas_retornaCero() {
        double resultado = HaversineUtil.calcularDistanciaKm(
                -33.4569, -70.6483,
                -33.4569, -70.6483);
        assertThat(resultado).isEqualTo(0.0);
    }

    @Test
    void estimarTiempoMinutos_2_2km_retorna4Minutos() {
        assertThat(HaversineUtil.estimarTiempoMinutos(2.2)).isEqualTo(4);
    }

    @Test
    void estimarTiempoMinutos_40km_retorna60Minutos() {
        assertThat(HaversineUtil.estimarTiempoMinutos(40.0)).isEqualTo(60);
    }
}
