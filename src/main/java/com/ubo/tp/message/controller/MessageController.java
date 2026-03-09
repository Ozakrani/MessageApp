package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.message.MessageView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Contrôleur du composant Message individuel.
 * Contient TOUTE la logique métier liée à un message.
 * SRS-MAP-MSG-006 : suppression d'un message dont on est l'auteur.
 */
public class MessageController {

    private final MessageView mView;
    private final DataManager mDataManager;
    private final Message     mMessage;
    private final User        mConnectedUser;

    public MessageController(Message message, User connectedUser, DataManager dataManager) {
        this.mMessage       = message;
        this.mConnectedUser = connectedUser;
        this.mDataManager   = dataManager;

        // Le contrôleur décide si l'utilisateur peut supprimer et si c'est son message
        boolean canDelete = canDeleteMessage(message);
        boolean isOwn     = isOwnMessage(message);

        // La vue reçoit uniquement des booléens, sans logique
        this.mView = new MessageView(message, canDelete, isOwn);
        this.initListeners();
    }

    /**
     * Détermine si l'utilisateur connecté peut supprimer ce message.
     * SRS-MAP-MSG-006 : uniquement si il en est l'auteur.
     */
    public boolean canDeleteMessage(Message message) {
        if (mConnectedUser == null || message.getSender() == null) return false;
        return mConnectedUser.getUserTag().equals(message.getSender().getUserTag());
    }

    /**
     * Détermine si le message appartient à l'utilisateur connecté
     * (pour le style visuel — fond bleu).
     */
    public boolean isOwnMessage(Message message) {
        if (mConnectedUser == null || message.getSender() == null) return false;
        return mConnectedUser.getUserTag().equals(message.getSender().getUserTag());
    }

    private void initListeners() {
        mView.addDeleteListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteMessage();
            }
        });
    }

    private void handleDeleteMessage() {
        int choice = JOptionPane.showConfirmDialog(
                mView,
                "Voulez-vous vraiment supprimer ce message ?",
                "Supprimer le message",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            mDataManager.deleteMessage(mMessage);
        }
    }

    public MessageView getView() { return mView; }
}