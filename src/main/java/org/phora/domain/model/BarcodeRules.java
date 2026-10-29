package org.phora.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Reglas de validación y normalización de códigos de barras.
 *
 * <p>Es el único punto donde se define qué es un código válido, para que los
 * casos de uso y la interfaz no se desincronicen. Admite códigos alfanuméricos
 * variables (por ejemplo Code128 escaneados con lector) y los tipeados a mano.
 */
public final class BarcodeRules {

    /**
     * Largo máximo de un código. Cubre EAN/UPC (12-14) y Code128 (hasta ~48)
     * con margen, sin permitir pegar textos arbitrariamente largos.
     */
    public static final int MAX_LENGTH = 64;

    private BarcodeRules() {
    }

    /**
     * Normaliza un código: quita espacios de los extremos y valida largo y
     * caracteres. Devuelve {@code ""} si el valor es nulo o queda vacío.
     *
     * @throws IllegalArgumentException si supera {@link #MAX_LENGTH} o contiene
     *                                  caracteres no imprimibles.
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String code = raw.trim();
        if (code.isEmpty()) {
            return "";
        }
        if (code.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "El código de barras no puede superar los " + MAX_LENGTH + " caracteres");
        }
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c < 32 || c > 126) {
                throw new IllegalArgumentException(
                        "El código de barras solo admite caracteres imprimibles");
            }
        }
        return code;
    }

    /**
     * Normaliza una lista de códigos, descarta vacíos y rechaza duplicados
     * dentro de la misma lista.
     */
    public static List<String> normalizeAll(List<String> barcodes) {
        if (barcodes == null) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        HashSet<String> seen = new HashSet<>();
        for (String raw : barcodes) {
            String code = normalize(raw);
            if (code.isEmpty()) {
                continue;
            }
            if (!seen.add(code)) {
                throw new IllegalArgumentException(
                        "No se pueden repetir códigos de barras en el mismo producto");
            }
            result.add(code);
        }
        return result;
    }
}
