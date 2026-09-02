package br.com.financas.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada da versão gráfica (Swing) da aplicação.
 *
 * BOA PRÁTICA (Swing): toda a construção e manipulação de componentes Swing
 * deve acontecer na Event Dispatch Thread (EDT), e não na thread "main".
 * SwingUtilities.invokeLater() agenda a criação da MainFrame para rodar
 * corretamente na EDT, evitando bugs sutis de concorrência na interface gráfica.
 *
 * O console (br.com.financas.app.Main) continua funcionando normalmente e
 * intacto — esta é apenas uma segunda forma de interação com o mesmo domínio
 * (model/service/repository), reaproveitado sem nenhuma alteração.
 */
public class MainApp {

    public static void main(String[] args) {
        // Usa a aparência nativa do sistema operacional (Windows/macOS/Linux)
        // em vez do visual "Metal" padrão do Swing, deixando a janela mais familiar.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se o Look and Feel do sistema não estiver disponível, seguimos com o
            // padrão do Swing — não é um erro crítico o suficiente para interromper o programa.
            System.out.println("Aviso: não foi possível aplicar o tema nativo do sistema.");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
