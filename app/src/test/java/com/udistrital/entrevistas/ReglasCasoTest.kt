package com.udistrital.entrevistas

import com.udistrital.entrevistas.domain.ReglasCaso
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasCasoTest {
    @Test fun tituloVacioNoEsValido() = assertFalse(ReglasCaso.tituloValido("   "))
    @Test fun noSePuedeCerrarSinConclusion() = assertFalse(ReglasCaso.puedeCerrarse(null))
    @Test fun sePuedeCerrarConConclusion() = assertTrue(ReglasCaso.puedeCerrarse("Caso resuelto"))
}