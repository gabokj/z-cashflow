package zcashflow.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica de conexões JDBC com o banco MySQL "z_cashflow" (ver Z-CashFlow.sql).
 *
 * Os parâmetros de conexão são lidos do arquivo "zcashflow.properties", que fica
 * na pasta do projeto (diretório de execução). Se o arquivo não existir, são
 * usados os valores padrão criados pelo script SQL (usuário "zcashflow").
 * Assim, nenhuma credencial fica "presa" no código-fonte.
 */
public final class Conexao {

    private static final String ARQUIVO_CONFIG = "zcashflow.properties";

    private static final String URL_PADRAO = "jdbc:mysql://localhost:3306/z_cashflow"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USUARIO_PADRAO = "zcashflow";
    private static final String SENHA_PADRAO = "zcashflow123";

    private static Properties config;

    private Conexao() {
    }

    /** Abre uma nova conexão. Quem chama é responsável por fechá-la (try-with-resources). */
    public static Connection conectar() throws AppException {
        Properties p = carregarConfig();
        try {
            // Registra o driver oficial, se estiver no classpath (em JDBC 4+ isso já é automático).
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Sem problema: o DriverManager tentará localizar outro driver compatível.
        }
        try {
            return DriverManager.getConnection(p.getProperty("db.url"),
                    p.getProperty("db.user"), p.getProperty("db.password"));
        } catch (SQLException e) {
            throw new AppException(traduzir(e), e);
        }
    }

    /** Verifica se o banco está acessível (usado na inicialização do sistema). */
    public static void testar() throws AppException {
        Connection c = conectar();
        try {
            c.close();
        } catch (SQLException e) {
            // O teste já foi bem-sucedido; falha ao fechar não é relevante aqui.
        }
    }

    private static synchronized Properties carregarConfig() {
        if (config == null) {
            Properties padrao = new Properties();
            padrao.setProperty("db.url", URL_PADRAO);
            padrao.setProperty("db.user", USUARIO_PADRAO);
            padrao.setProperty("db.password", SENHA_PADRAO);

            Properties lido = new Properties(padrao);
            Path arquivo = Paths.get(System.getProperty("user.dir"), ARQUIVO_CONFIG);
            if (Files.isRegularFile(arquivo)) {
                try (InputStream in = Files.newInputStream(arquivo)) {
                    lido.load(in);
                } catch (IOException e) {
                    System.err.println("Não foi possível ler " + arquivo + ": " + e.getMessage());
                }
            }
            config = lido;
        }
        return config;
    }

    private static String traduzir(SQLException e) {
        String msg = e.getMessage() == null ? "" : e.getMessage();
        if (msg.contains("No suitable driver")) {
            return "Driver JDBC do MySQL (mysql-connector-j) não encontrado. "
                    + "Copie o arquivo .jar para a pasta lib/ do projeto.";
        }
        if (e.getErrorCode() == 1045 || "28000".equals(e.getSQLState())) {
            return "Usuário ou senha do banco de dados inválidos. Verifique o arquivo " + ARQUIVO_CONFIG + ".";
        }
        if (e.getErrorCode() == 1049) {
            return "O banco de dados 'z_cashflow' não existe. Execute o script Z-CashFlow.sql no MySQL Workbench.";
        }
        String estado = e.getSQLState();
        if (estado != null && estado.startsWith("08")) {
            return "Não foi possível conectar ao servidor MySQL. Verifique se ele está em execução (localhost:3306).";
        }
        return "Erro de banco de dados: " + msg;
    }
}
