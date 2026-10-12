package zcashflow.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária responsável por abrir e fechar a conexão com o
 * banco de dados MySQL "z_cashflow" (ver Z-CashFlow.sql).
 *
 * RNF002 - Persistência de Dados: toda a informação é armazenada de
 * forma persistente via JDBC, garantindo que os dados não se percam
 * ao fechar a aplicação.
 */
public class Conexao {

    private static final String URL = "jdbc:mysql://localhost:3306/z_cashflow?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "root";

    private Conexao() {
    }

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL não encontrado no classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void fechar(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
