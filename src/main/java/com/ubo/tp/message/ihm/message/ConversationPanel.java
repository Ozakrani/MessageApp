package main.java.com.ubo.tp.message.ihm.message;

import main.java.com.ubo.tp.message.controller.MessageController;
import main.java.com.ubo.tp.message.controller.MessageInputController;
import com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.UUID;

public class ConversationPanel extends JPanel implements IDatabaseObserver {

    private JLabel mTitleLabel;
    private JPanel mMessageListPanel;
    private JScrollPane mScrollPane;
    private JPanel mInputContainer;
    private JTextField searchMessageField;

    private final DataManager mDataManager;
    private final User mConnectedUser;

    private User mTargetUser = null;
    private Channel mTargetChannel = null;

    public ConversationPanel(User connectedUser, DataManager dataManager) {
        this.mConnectedUser = connectedUser;
        this.mDataManager = dataManager;

        initGUI();

        mDataManager.addObserver(this);
        refreshMessages();
    }

    private void initGUI() {

        setLayout(new BorderLayout());

        // ===== TITRE =====

        mTitleLabel = new JLabel("# Canal public", SwingConstants.CENTER);
        mTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        mTitleLabel.setOpaque(true);
        mTitleLabel.setBackground(new Color(240,240,240));
        mTitleLabel.setBorder(new EmptyBorder(8,10,8,10));

        add(mTitleLabel, BorderLayout.NORTH);


        // ===== PANEL CENTRAL =====

        JPanel centerPanel = new JPanel(new BorderLayout());


        // ===== BARRE DE RECHERCHE =====

        searchMessageField = new JTextField();
        searchMessageField.setToolTipText("Rechercher un message...");
        searchMessageField.setPreferredSize(new Dimension(200,25));

        searchMessageField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {

                String search = searchMessageField.getText().toLowerCase();

                filterMessages(search);

            }

        });

        centerPanel.add(searchMessageField, BorderLayout.NORTH);


        // ===== LISTE DES MESSAGES =====

        mMessageListPanel = new JPanel();
        mMessageListPanel.setLayout(new BoxLayout(mMessageListPanel, BoxLayout.Y_AXIS));

        mScrollPane = new JScrollPane(mMessageListPanel);
        mScrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));
        mScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        centerPanel.add(mScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);


        // ===== ZONE DE SAISIE =====

        mInputContainer = new JPanel(new BorderLayout());

        refreshInput();

        add(mInputContainer, BorderLayout.SOUTH);
    }


    // ===== FILTRE DES MESSAGES =====

    private void filterMessages(String search) {

        mMessageListPanel.removeAll();

        for(Message message : mDataManager.getMessages()) {

            if(message.getText().toLowerCase().contains(search)) {

                mMessageListPanel.add(
                        new MessageController(
                                message,
                                mConnectedUser,
                                mDataManager
                        ).getView()
                );

            }

        }

        mMessageListPanel.revalidate();
        mMessageListPanel.repaint();
    }


    // ===== CHANGEMENT DE CANAL =====

    public void showChannelConversation(Channel channel) {

        mTargetUser = null;
        mTargetChannel = channel;

        mTitleLabel.setText("# " + channel.getName()
                + (channel.getUsers().isEmpty() ? " [public]" : " [privé]"));

        refreshInput();
        refreshMessages();
    }


    public void showPublicConversation() {

        mTargetUser = null;

        mTitleLabel.setText("# Canal public");

        refreshInput();
        refreshMessages();
    }


    public void showPrivateConversation(User targetUser) {

        mTargetUser = targetUser;

        mTitleLabel.setText("Conversation avec "
                + targetUser.getName()
                + " (@" + targetUser.getUserTag() + ")");

        refreshInput();
        refreshMessages();
    }


    // ===== RAFRAICHIR MESSAGES =====

    private void refreshMessages() {

        mMessageListPanel.removeAll();

        for(Message message : mDataManager.getMessages()) {

            boolean show = false;

            if(mTargetUser != null) {

                UUID myUUID = mConnectedUser.getUuid();
                UUID targetUUID = mTargetUser.getUuid();

                UUID recipient = message.getRecipient();
                UUID sender = message.getSender().getUuid();

                show = (sender.equals(myUUID) && recipient.equals(targetUUID))
                        || (sender.equals(targetUUID) && recipient.equals(myUUID));

            }

            else if(mTargetChannel != null) {

                show = mTargetChannel.getUuid().equals(message.getRecipient());

            }

            else {

                UUID publicUUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

                show = publicUUID.equals(message.getRecipient());

            }

            if(show) {

                mMessageListPanel.add(
                        new MessageController(
                                message,
                                mConnectedUser,
                                mDataManager
                        ).getView()
                );

            }

        }

        mMessageListPanel.add(Box.createVerticalGlue());

        mMessageListPanel.revalidate();
        mMessageListPanel.repaint();
    }


    // ===== INPUT MESSAGE =====

    private void refreshInput() {

        mInputContainer.removeAll();

        UUID recipientUUID;

        if(mTargetUser != null)
            recipientUUID = mTargetUser.getUuid();

        else if(mTargetChannel != null)
            recipientUUID = mTargetChannel.getUuid();

        else
            recipientUUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

        MessageInputController inputCtrl =
                new MessageInputController(
                        mConnectedUser,
                        recipientUUID,
                        mDataManager
                );

        mInputContainer.add(inputCtrl.getView(), BorderLayout.CENTER);

        mInputContainer.revalidate();
        mInputContainer.repaint();
    }


    // ===== OBSERVER =====

    @Override public void notifyMessageAdded(Message m) { SwingUtilities.invokeLater(this::refreshMessages); }
    @Override public void notifyMessageDeleted(Message m) { SwingUtilities.invokeLater(this::refreshMessages); }
    @Override public void notifyMessageModified(Message m) {}
    @Override public void notifyUserAdded(User u) {}
    @Override public void notifyUserDeleted(User u) {}
    @Override public void notifyUserModified(User u) {}
    @Override public void notifyChannelAdded(Channel c) {}
    @Override public void notifyChannelDeleted(Channel c) {}
    @Override public void notifyChannelModified(Channel c) {}
}