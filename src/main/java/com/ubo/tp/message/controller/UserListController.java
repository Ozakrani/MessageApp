package main.java.com.ubo.tp.message.controller;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.IConversationListener;
import main.java.com.ubo.tp.message.ihm.user.UserListView;
import main.java.com.ubo.tp.message.ihm.user.UserView;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur de la liste des utilisateurs.
 * SRS-MAP-USR-007 : consulter la liste.
 * SRS-MAP-USR-008 : rechercher un utilisateur.
 */
public class UserListController implements IDatabaseObserver {

    private final UserListView mView;
    private final DataManager mDataManager;
    private final User mConnectedUser;
    private final IConversationListener mListener;

    public UserListController(User connectedUser, DataManager dataManager, IConversationListener listener) {
        this.mConnectedUser = connectedUser;
        this.mDataManager = dataManager;
        this.mListener = listener;
        this.mView = new UserListView();

        initListeners();
        mDataManager.addObserver(this);
        refreshList("");
    }

    private void initListeners() {
        mView.addSearchListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshList(mView.getSearchText());
            }
        });
    }

    /**
     * Recharge la liste en filtrant par le texte de recherche.
     */
    private void refreshList(String filter) {
        List<UserView> views = new ArrayList<>();

        for (User user : mDataManager.getUsers()) {
            if (filter.isEmpty()
                    || user.getName().toLowerCase().contains(filter.toLowerCase())
                    || user.getUserTag().toLowerCase().contains(filter.toLowerCase())) {

                UserItemController itemCtrl = new UserItemController(user, mConnectedUser);
                itemCtrl.setConversationListener(mListener);
                views.add(itemCtrl.getView());
            }
        }

        SwingUtilities.invokeLater(() -> mView.setUserViews(views));
    }

    public UserListView getView() { return mView; }

    @Override public void notifyUserAdded(User u)           { refreshList(mView.getSearchText()); }
    @Override public void notifyUserDeleted(User u)         { refreshList(mView.getSearchText()); }
    @Override public void notifyUserModified(User u)        { refreshList(mView.getSearchText()); }
    @Override public void notifyMessageAdded(Message m)     {}
    @Override public void notifyMessageDeleted(Message m)   {}
    @Override public void notifyMessageModified(Message m)  {}
    @Override public void notifyChannelAdded(Channel c)     {}
    @Override public void notifyChannelDeleted(Channel c)   {}
    @Override public void notifyChannelModified(Channel c)  {}
}