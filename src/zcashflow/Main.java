package zcashflow;

import zcashflow.ui.LoginFrame;

import javax.swing.*;

/**
 * Classe principal do sistema Z-CashFlow.
 *
 * ETAPA 3 do Projeto Integrador: inicializa a interface gráfica (Swing),
 * implementando as telas definidas no Projeto de Interfaces (Etapa 2) —
 * Login, Dashboard, Nova Movimentação, Extrato e Categorias — e toda a
 * navegação entre elas.
 *
 * As funcionalidades usam, nesta etapa, a camada de dados em memória
 * (zcashflow.service.DadosService), que simula o banco de dados MySQL
 * descrito em Z-CashFlow.sql, dispensando um servidor de banco de dados
 * para que o sistema seja testado e demonstrado integralmente.
 *
 * O protótipo de console construído na Etapa 1 foi preservado em
 * zcashflow.legacy.ConsolePrototypeEtapa1 para referência, e as classes de
 * acesso a dados via JDBC (zcashflow.dao) continuam disponíveis no projeto,
 * prontas para substituir o DadosService quando o sistema for conectado
 * a um banco de dados MySQL real.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mantém o Look and Feel padrão (Metal) caso o do sistema não esteja disponível.
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
