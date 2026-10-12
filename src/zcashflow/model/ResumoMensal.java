package zcashflow.model;

import java.math.BigDecimal;
import java.time.YearMonth;

/** Total de entradas e saídas de um mês (usado no gráfico e nos cartões do dashboard). */
public record ResumoMensal(YearMonth mes, BigDecimal entradas, BigDecimal saidas) {

    public BigDecimal saldo() {
        return entradas.subtract(saidas);
    }
}
