package zcashflow.util;

/**
 * Exceção de aplicação: carrega sempre uma mensagem amigável, pronta para ser
 * exibida ao usuário (erros de validação, regras de negócio ou falhas de banco).
 * A causa técnica original, quando existe, fica preservada em getCause().
 */
public class AppException extends Exception {

    private static final long serialVersionUID = 1L;

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
