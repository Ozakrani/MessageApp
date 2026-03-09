package main.java.com.ubo.tp.message.controller;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.IConversationListener;
import main.java.com.ubo.tp.message.ihm.user.UserView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Contrôleur du composant UserView individuel.
 *
 * Distinct de UserController (qui gère les opérations globales
 * sur les utilisateurs) : celui-ci gère les interactions
 * sur UNE vue d'utilisateur précise.
 *
 * SRS-MAP-MSG-007 : envoyer un message privé à un utilisateur.
 */
public class UserItemController {

    private final UserView              mView;
    private final User                  mTargetUser;
    private final User                  mConnectedUser;
    private IConversationListener mConversationListener; // ← ajout

    public UserItemController(User targetUser, User connectedUser) {
        this.mTargetUser    = targetUser;
        this.mConnectedUser = connectedUser;
        this.mView          = new UserView(targetUser, connectedUser);
        this.initListeners();
    }

    /** Appelé par MainPanel pour brancher le callback. */
    public void setConversationListener(IConversationListener listener) {
        this.mConversationListener = listener;
    }

    private void initListeners() {
        mView.addPrivateMessageListener(e -> {
            if (mConversationListener != null) {
                mConversationListener.onUserSelected(mTargetUser); // ← notifie MainPanel
            }
        });
    }

    public UserView getView() { return mView; }
}