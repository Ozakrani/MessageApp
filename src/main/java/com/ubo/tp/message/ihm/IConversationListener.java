package main.java.com.ubo.tp.message.ihm;

import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Interface de communication entre UserItemController et MainPanel.
 * Permet de changer la conversation affichée sans couplage direct.
 */
public interface IConversationListener {
    void onUserSelected(User targetUser);
}