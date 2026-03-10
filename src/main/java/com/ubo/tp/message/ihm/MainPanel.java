package com.ubo.tp.message.ihm;

import com.ubo.tp.message.controller.ChannelController;
import main.java.com.ubo.tp.message.controller.UserListController;
import com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.message.ConversationPanel;
import com.ubo.tp.message.ihm.channel.ChannelListView;
import main.java.com.ubo.tp.message.core.session.Session;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel implements IDatabaseObserver {

    private JLabel lblWelcome;
    private DataManager mDataManager;
    private User mConnectedUser;
    private ConversationPanel mConversationPanel;
    private Session mSession;

    public MainPanel(User user, DataManager dataManager, Session session) {

        this.mConnectedUser = user;
        this.mDataManager = dataManager;
        this.mSession = session;

        setLayout(new BorderLayout());

        /* =========================
           HEADER
           ========================= */

        JPanel headerPanel = new JPanel(new BorderLayout());

        lblWelcome = new JLabel("Bienvenue " + user.getName() + " !");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 20));

        JButton btnLogout = new JButton("Déconnexion");

        btnLogout.addActionListener(e -> {

            mConnectedUser.setOnline(false);
            mDataManager.updateUser(mConnectedUser);
            mSession.disconnect();

        });

        headerPanel.add(lblWelcome, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);


        /* =========================
           ACTIONS UTILISATEUR
           ========================= */

        JButton btnEditProfile = new JButton("Modifier mon nom");
        JButton btnDeleteAccount = new JButton("Supprimer mon compte");

        JPanel profilePanel = new JPanel();
        profilePanel.add(btnEditProfile);
        profilePanel.add(btnDeleteAccount);

        add(profilePanel, BorderLayout.SOUTH);


        /* =========================
           MODIFIER NOM
           ========================= */

        btnEditProfile.addActionListener(e -> {

            String newName = JOptionPane.showInputDialog(
                    this,
                    "Nouveau nom d'utilisateur",
                    mConnectedUser.getName()
            );

            if(newName == null || newName.trim().isEmpty()){
                return;
            }

            mConnectedUser.setName(newName);

            mDataManager.updateUser(mConnectedUser);

            lblWelcome.setText("Bienvenue " + newName + " !");
        });


        /* =========================
           SUPPRIMER COMPTE
           ========================= */

        btnDeleteAccount.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Voulez-vous vraiment supprimer votre compte ?",
                    "Suppression du compte",
                    JOptionPane.YES_NO_OPTION
            );

            if(choice == JOptionPane.YES_OPTION){

                mDataManager.deleteUser(mConnectedUser);

                JOptionPane.showMessageDialog(
                        this,
                        "Compte supprimé"
                );

                System.exit(0);
            }

        });


        /* =========================
           CONVERSATION
           ========================= */

        mConversationPanel = new ConversationPanel(user, dataManager);


        /* =========================
           USERS
           ========================= */

        UserListController userListCtrl = new UserListController(
                user,
                dataManager,
                targetUser -> mConversationPanel.showPrivateConversation(targetUser)
        );


        /* =========================
           CHANNELS
           ========================= */

        ChannelController channelController = new ChannelController(dataManager);

        ChannelListView channelListView =
                new ChannelListView(channelController, user);

        channelListView.setChannelSelectionListener(
                channel -> mConversationPanel.showChannelConversation(channel)
        );


        /* =========================
           LEFT PANEL
           ========================= */

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250,0));

        leftPanel.add(userListCtrl.getView(), BorderLayout.CENTER);
        leftPanel.add(channelListView, BorderLayout.SOUTH);


        /* =========================
           SPLIT
           ========================= */

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                mConversationPanel
        );

        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.0);

        add(splitPane, BorderLayout.CENTER);
    }


    @Override public void notifyMessageAdded(Message m) {}
    @Override public void notifyMessageDeleted(Message m) {}
    @Override public void notifyMessageModified(Message m) {}
    @Override public void notifyUserAdded(User u) {}
    @Override public void notifyUserDeleted(User u) {}
    @Override public void notifyUserModified(User u) {}
    @Override public void notifyChannelAdded(Channel c) {}
    @Override public void notifyChannelDeleted(Channel c) {}
    @Override public void notifyChannelModified(Channel c) {}
}