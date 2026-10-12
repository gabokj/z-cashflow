package zcashflow.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Proteção de senhas: o banco nunca guarda a senha em texto puro, apenas um hash
 * PBKDF2 (HMAC-SHA256) com salt aleatório por usuário.
 *
 * Formato armazenado: pbkdf2$iteracoes$salt(base64)$hash(base64)
 */
public final class SenhaUtil {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 120_000;
    private static final int TAMANHO_SALT = 16;
    private static final int TAMANHO_HASH_BITS = 256;

    private SenhaUtil() {
    }

    public static String gerarHash(String senha) {
        byte[] salt = new byte[TAMANHO_SALT];
        new SecureRandom().nextBytes(salt);
        byte[] hash = derivar(senha, salt, ITERACOES);
        return "pbkdf2$" + ITERACOES + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String senha, String armazenado) {
        if (senha == null || armazenado == null) {
            return false;
        }
        String[] partes = armazenado.split("\\$");
        if (partes.length != 4 || !"pbkdf2".equals(partes[0])) {
            return false;
        }
        try {
            int iteracoes = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(senha, salt, iteracoes);
            return MessageDigest.isEqual(esperado, calculado); // comparação em tempo constante
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String senha, byte[] salt, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, TAMANHO_HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Algoritmo de hash indisponível: " + ALGORITMO, e);
        } finally {
            spec.clearPassword();
        }
    }
}
