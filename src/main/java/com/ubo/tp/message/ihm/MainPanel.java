package main.java.com.ubo.tp.message.ihm;

import main.java.com.ubo.tp.message.controller.ChannelController;
import main.java.com.ubo.tp.message.controller.UserListController;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.channel.ChannelListView;
import main.java.com.ubo.tp.message.ihm.message.ConversationPanel;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel implements IDatabaseObserver {

    private JLabel            lblWelcome;
    private DataManager       mDataManager;
    private User              mConnectedUser;
    private ConversationPanel mConversationPanel;

    public MainPanel(User user, DataManager dataManager) {
        this.mConnectedUser = user;
        this.mDataManager   = dataManager;
        setLayout(new BorderLayout());

        // NORTH
        lblWelcome = new JLabel("Bienvenue " + user.getName() + " !", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 22));
        add(lblWelcome, BorderLayout.NORTH);

        // CENTER — conversation (créé EN PREMIER)
        mConversationPanel = new ConversationPanel(user, dataManager);

        // GAUCHE — utilisateurs
        UserListController userListCtrl = new UserListController(
                user, dataManager,
                targetUser -> mConversationPanel.showPrivateConversation(targetUser)
        );

        // GAUCHE BAS — canaux
        ChannelController channelController = new ChannelController(dataManager);
        ChannelListView   channelListView   = new ChannelListView(channelController, user);

        // Clic sur canal → ouvrir dans ConversationPanel
        channelListView.setChannelSelectionListener(
                channel -> mConversationPanel.showChannelConversation(channel)
        );

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(220, 0));
        leftPanel.add(userListCtrl.getView(), BorderLayout.CENTER);
        leftPanel.add(channelListView,        BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                mConversationPanel
        );
        splitPane.setDividerLocation(220);
        splitPane.setResizeWeight(0.0);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override public void notifyMessageAdded(Message m)    {}
    @Override public void notifyMessageDeleted(Message m)  {}
    @Override public void notifyMessageModified(Message m) {}
    @Override public void notifyUserAdded(User u)          {}
    @Override public void notifyUserDeleted(User u)        {}
    @Override public void notifyUserModified(User u)       {}
    @Override public void notifyChannelAdded(Channel c)    {}
    @Override public void notifyChannelDeleted(Channel c)  {}
    @Override public void notifyChannelModified(Channel c) {}
}