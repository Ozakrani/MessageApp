package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.message.MessageInputView;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.UUID;

/**
 * Contrôleur de la zone de saisie d'un message.
 * Contient TOUTE la logique métier liée à la saisie.
 * SRS-MAP-MSG-002, SRS-MAP-MSG-007, SRS-MAP-MSG-008
 */
public class MessageInputController {

    private static final int MAX_CHARS = 200; // SRS-MAP-MSG-008

    private final MessageInputView mView;
    private final DataManager      mDataManager;
    private final User             mConnectedUser;
    private final UUID             mRecipientUUID;

    public MessageInputController(User connectedUser, UUID recipientUUID,
                                  DataManager dataManager) {
        this.mConnectedUser = connectedUser;
        this.mRecipientUUID = recipientUUID;
        this.mDataManager   = dataManager;
        this.mView          = new MessageInputView();

        // Le contrôleur se branche sur la vue
        this.mView.setController(this);
        this.initListeners();
    }

    private void initListeners() {
        mView.addSendListener(e -> handleSend());
    }

    /**
     * Gère la touche saisie — bloque au-delà de MAX_CHARS.
     * SRS-MAP-MSG-008
     */
    public void handleKeyTyped(KeyEvent e, String currentText) {
        if (currentText.length() >= MAX_CHARS
                && e.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
            e.consume(); // Bloquer la saisie
        }
    }

    /**
     * Met à jour le compteur de caractères affiché dans la vue.
     */
    public void handleTextUpdate(String text) {
        int len = text.length();
        String countText   = len + " / " + MAX_CHARS;
        boolean isNearLimit = len >= MAX_CHARS - 20;
        mView.updateCharCount(countText, isNearLimit);
    }

    /**
     * Envoie le message après validation.
     */
    private void handleSend() {
        String text = mView.getInputText();

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(mView,
                    "Le message ne peut pas être vide.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (text.length() > MAX_CHARS) {
            JOptionPane.showMessageDialog(mView,
                    "Le message ne peut pas dépasser " + MAX_CHARS + " caractères.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Message message = new Message(
                UUID.randomUUID(),
                mConnectedUser,
                mRecipientUUID,
                System.currentTimeMillis(),
                text
        );

        mDataManager.sendMessage(message);
        mView.clearInput();
    }

    public MessageInputView getView() { return mView; }
}