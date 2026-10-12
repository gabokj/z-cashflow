package zcashflow;

import zcashflow.ui.LoginFrame;
import zcashflow.util.AppException;
import zcashflow.util.Conexao;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Classe principal do sistema Z-CashFlow (controle de fluxo de caixa pessoal).
 *
 * Ao iniciar, verifica a conexão com o banco MySQL; se o banco estiver
 * inacessível, explica o problema e oferece tentar novamente ou sair.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mantém o visual padrão do Swing caso o do sistema não esteja disponível.
        }

        SwingUtilities.invokeLater(() -> {
            if (!bancoDisponivel()) {
                System.exit(1);
            }
            new LoginFrame().setVisible(true);
        });
    }

    /** Testa o banco; em caso de falha, deixa o usuário corrigir o problema e tentar de novo. */
    private static boolean bancoDisponivel() {
        while (true) {
            try {
                Conexao.testar();
                return true;
            } catch (AppException e) {
                Object[] opcoes = {"Tentar novamente", "Sair"};
                int escolha = JOptionPane.showOptionDialog(null,
                        e.getMessage() + "\n\nChecklist:\n"
                                + " 1. O servidor MySQL está em execução?\n"
                                + " 2. O script Z-CashFlow.sql foi executado no MySQL Workbench?\n"
                                + " 3. O arquivo zcashflow.properties tem o usuário e a senha corretos?\n"
                                + " 4. O driver mysql-connector-j (.jar) está na pasta lib/?",
                        "Z-CashFlow - Banco de dados indisponível", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE, null, opcoes, opcoes[0]);
                if (escolha != 0) {
                    return false;
                }
            }
        }
    }
}
