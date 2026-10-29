package org.phora.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BarcodeRulesTest {

    @Test
    void aceptaUnCodigoDelLargoMaximo() {
        String code = "A".repeat(BarcodeRules.MAX_LENGTH);
        assertEquals(code, BarcodeRules.normalize(code));
    }

    @Test
    void rechazaUnCodigoMasLargoQueElMaximo() {
        String code = "A".repeat(BarcodeRules.MAX_LENGTH + 1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> BarcodeRules.normalize(code));
        assertTrue(ex.getMessage().contains(String.valueOf(BarcodeRules.MAX_LENGTH)));
    }

    @Test
    void quitaEspaciosDeLosExtremos() {
        assertEquals("ABC-123", BarcodeRules.normalize("  ABC-123  "));
    }

    @Test
    void nuloOVacioQuedaVacio() {
        assertEquals("", BarcodeRules.normalize(null));
        assertEquals("", BarcodeRules.normalize("   "));
    }

    @Test
    void rechazaCaracteresNoImprimibles() {
        assertThrows(IllegalArgumentException.class, () -> BarcodeRules.normalize("ABC\n123"));
        assertThrows(IllegalArgumentException.class, () -> BarcodeRules.normalize("ABC\u0000XYZ"));
    }

    @Test
    void aceptaAlfanumericoDeLectorYDeTipeo() {
        assertEquals("7790001234567", BarcodeRules.normalize("7790001234567"));
        assertEquals("Code128-ABC_09.x", BarcodeRules.normalize("Code128-ABC_09.x"));
    }

    @Test
    void listaDescartaVaciosYNulos() {
        List<String> result = BarcodeRules.normalizeAll(Arrays.asList("  A-1 ", "", null, "   ", "A-2"));
        assertEquals(List.of("A-1", "A-2"), result);
    }

    @Test
    void listaNulaQuedaVacia() {
        assertTrue(BarcodeRules.normalizeAll(null).isEmpty());
    }

    @Test
    void listaRechazaDuplicadosEnElMismoProducto() {
        assertThrows(IllegalArgumentException.class,
                () -> BarcodeRules.normalizeAll(List.of("DUP", "DUP")));
    }

    @Test
    void listaRechazaUnCodigoDemasiadoLargo() {
        assertThrows(IllegalArgumentException.class,
                () -> BarcodeRules.normalizeAll(List.of("A".repeat(BarcodeRules.MAX_LENGTH + 1))));
    }
}
