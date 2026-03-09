package main.java.com.ubo.tp.message.ihm.message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import main.java.com.ubo.tp.message.controller.MessageInputController;

/**
 * Vue de la zone de saisie d'un message.
 * AUCUNE logique métier ici — uniquement l'affichage.
 * SRS-MAP-MSG-002, SRS-MAP-MSG-008
 */
public class MessageInputView extends JPanel {

    private JTextArea mInputArea;
    private JButton   mSendButton;
    private JLabel    mCharCountLabel;

    // Référence vers le contrôleur pour déléguer la logique
    private MessageInputController mController;

    public MessageInputView() {
        initGUI();
    }

    private void initGUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setBackground(new Color(240, 240, 240));

        mInputArea = new JTextArea(3, 40);
        mInputArea.setLineWrap(true);
        mInputArea.setWrapStyleWord(true);
        mInputArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        mInputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                new EmptyBorder(5, 8, 5, 8)));

        // Délégation au contrôleur — la vue ne connaît pas MAX_CHARS
        mInputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (mController != null) {
                    mController.handleKeyTyped(e, mInputArea.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (mController != null) {
                    mController.handleTextUpdate(mInputArea.getText());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(mInputArea);
        scrollPane.setBorder(null);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 5));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(120, 0));

        mCharCountLabel = new JLabel("0 / 200", SwingConstants.CENTER);
        mCharCountLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
        mCharCountLabel.setForeground(Color.GRAY);

        mSendButton = new JButton("Envoyer ➤");
        mSendButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        mSendButton.setBackground(new Color(50, 130, 200));
        mSendButton.setForeground(Color.WHITE);
        mSendButton.setFocusPainted(false);
        mSendButton.setBorder(BorderFactory.createLineBorder(
                new Color(30, 100, 170), 1, true));

        rightPanel.add(mCharCountLabel, BorderLayout.NORTH);
        rightPanel.add(mSendButton,     BorderLayout.CENTER);

        add(scrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    // ---------------------------------------------------------------
    // API publique — appelée par le contrôleur
    // ---------------------------------------------------------------

    /**
     * Le contrôleur se branche sur la vue.
     */
    public void setController(MessageInputController controller) {
        this.mController = controller;
    }

    /**
     * Met à jour le compteur de caractères.
     * Appelée par MessageInputController.handleTextUpdate().
     */
    public void updateCharCount(String text, boolean isNearLimit) {
        mCharCountLabel.setText(text);
        mCharCountLabel.setForeground(isNearLimit ? Color.RED : Color.GRAY);
    }

    public void addSendListener(ActionListener listener) {
        mSendButton.addActionListener(listener);
    }

    public String getInputText() { return mInputArea.getText().trim(); }

    public void clearInput() {
        mInputArea.setText("");
        if (mController != null) {
            mController.handleTextUpdate("");
        }
    }

    public void blockInput() { mInputArea.setText(
            mInputArea.getText().substring(0, Math.min(
                    mInputArea.getText().length(), 200))); }
}