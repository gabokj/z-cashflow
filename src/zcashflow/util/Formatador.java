package zcashflow.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Conversões e formatação no padrão brasileiro: dinheiro (R$ 1.234,56)
 * e datas (dd/mm/aaaa).
 */
public final class Formatador {

    public static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    // Modo STRICT: rejeita datas inexistentes (ex.: 31/02/2026).
    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Formatador() {
    }

    public static String moeda(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat("'R$' #,##0.00", DecimalFormatSymbols.getInstance(PT_BR));
        return df.format(valor);
    }

    /** Número sem casas decimais para escala de gráficos, ex.: 1.234 */
    public static String inteiro(BigDecimal valor) {
        DecimalFormat df = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(PT_BR));
        return df.format(valor);
    }

    /** Valor com vírgula decimal e sem separador de milhar (para campos de edição e CSV). */
    public static String valorEditavel(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }

    public static String data(LocalDate data) {
        return data.format(DATA);
    }

    public static LocalDate lerData(String texto) throws AppException {
        if (texto == null || texto.isBlank()) {
            throw new AppException("Informe a data no formato dd/mm/aaaa.");
        }
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new AppException("Data inválida. Use o formato dd/mm/aaaa.");
        }
    }

    /** Data opcional (filtros): texto vazio significa "sem filtro". */
    public static LocalDate lerDataOpcional(String texto) throws AppException {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return lerData(texto);
    }

    /** Aceita "1250,50", "1.250,50", "1250.50" e "R$ 1.250,50". */
    public static BigDecimal lerValor(String texto) throws AppException {
        if (texto == null || texto.isBlank()) {
            throw new AppException("Informe o valor.");
        }
        String s = texto.trim().replace("R$", "").replace(" ", "");
        if (s.contains(",")) {
            s = s.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(s).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new AppException("Valor inválido. Use apenas números, por exemplo 1250,50.");
        }
    }
}
