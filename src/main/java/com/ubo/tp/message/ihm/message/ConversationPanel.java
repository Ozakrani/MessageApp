package main.java.com.ubo.tp.message.ihm.message;

import main.java.com.ubo.tp.message.controller.MessageController;
import main.java.com.ubo.tp.message.controller.MessageInputController;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.UUID;

/**
 * Panneau central de conversation.
 * Affiche soit les messages publics, soit une conversation privée.
 * Change dynamiquement selon l'utilisateur sélectionné.
 */
public class ConversationPanel extends JPanel implements IDatabaseObserver {

    private JLabel  mTitleLabel;
    private JPanel  mMessageListPanel;
    private JScrollPane mScrollPane;
    private JPanel  mInputContainer;

    private final DataManager mDataManager;
    private final User        mConnectedUser;

    /** Null = canal public, sinon = conversation privée avec cet user */
    private User mTargetUser = null;
    private Channel mTargetChannel = null;

    /**
     * Passe en mode conversation de canal.
     * SRS-MAP-MSG-001 (public) / SRS-MAP-MSG-003 (privé)
     */
    public void showChannelConversation(Channel channel) {
        mTargetUser    = null;
        mTargetChannel = channel;
        mTitleLabel.setText("# " + channel.getName()
                + (channel.getUsers().isEmpty() ? "  [public]" : "  [privé]"));
        refreshInput();
        refreshMessages();
    }
    public ConversationPanel(User connectedUser, DataManager dataManager) {
        this.mConnectedUser = connectedUser;
        this.mDataManager   = dataManager;
        initGUI();
        mDataManager.addObserver(this);
        refreshMessages();
    }

    private void initGUI() {
        setLayout(new BorderLayout(0, 0));

        // --- Titre de la conversation ---
        mTitleLabel = new JLabel("# Canal public", SwingConstants.CENTER);
        mTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        mTitleLabel.setBackground(new Color(240, 240, 240));
        mTitleLabel.setOpaque(true);
        mTitleLabel.setBorder(new EmptyBorder(8, 10, 8, 10));

        // --- Liste des messages ---
        mMessageListPanel = new JPanel();
        mMessageListPanel.setLayout(new BoxLayout(mMessageListPanel, BoxLayout.Y_AXIS));
        mScrollPane = new JScrollPane(mMessageListPanel);
        mScrollPane.setBorder(BorderFactory.createTitledBorder("Messages"));
        mScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // --- Zone de saisie (sera recréée selon le contexte) ---
        mInputContainer = new JPanel(new BorderLayout());
        refreshInput();

        add(mTitleLabel,      BorderLayout.NORTH);
        add(mScrollPane,      BorderLayout.CENTER);
        add(mInputContainer,  BorderLayout.SOUTH);
    }


    /**
     * Passe en mode canal public.
     */
    public void showPublicConversation() {
        mTargetUser = null;
        mTitleLabel.setText("# Canal public");
        refreshInput();
        refreshMessages();
    }

    /**
     * Passe en mode conversation privée avec l'utilisateur cible.
     * SRS-MAP-MSG-007
     */
    public void showPrivateConversation(User targetUser) {
        mTargetUser = targetUser;
        mTitleLabel.setText("✉ Conversation avec "
                + targetUser.getName()
                + " (@" + targetUser.getUserTag() + ")");
        refreshInput();
        refreshMessages();
    }

    private void refreshMessages() {
        mMessageListPanel.removeAll();

        for (Message message : mDataManager.getMessages()) {
            boolean show = false;

            if (mTargetUser != null) {
                // Conversation privée entre deux users
                UUID myUUID     = mConnectedUser.getUuid();
                UUID targetUUID = mTargetUser.getUuid();
                UUID recipient  = message.getRecipient();
                UUID sender     = message.getSender().getUuid();
                show = (sender.equals(myUUID)     && recipient.equals(targetUUID))
                        || (sender.equals(targetUUID) && recipient.equals(myUUID));

            } else if (mTargetChannel != null) {
                // Messages d'un canal spécifique
                show = mTargetChannel.getUuid().equals(message.getRecipient());

            } else {
                // Canal public fixe (UUID par défaut)
                UUID publicUUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
                show = publicUUID.equals(message.getRecipient());
            }

            if (show) {
                mMessageListPanel.add(new MessageController(
                        message, mConnectedUser, mDataManager).getView());
            }
        }

        mMessageListPanel.add(Box.createVerticalGlue());
        mMessageListPanel.revalidate();
        mMessageListPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = mScrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private void refreshInput() {
        mInputContainer.removeAll();

        UUID recipientUUID;
        if (mTargetUser != null) {
            recipientUUID = mTargetUser.getUuid();
        } else if (mTargetChannel != null) {
            recipientUUID = mTargetChannel.getUuid();
        } else {
            recipientUUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
        }

        MessageInputController inputCtrl = new MessageInputController(
                mConnectedUser, recipientUUID, mDataManager);
        mInputContainer.add(inputCtrl.getView(), BorderLayout.CENTER);
        mInputContainer.revalidate();
        mInputContainer.repaint();
    }
    @Override public void notifyMessageAdded(Message m)    { SwingUtilities.invokeLater(this::refreshMessages); }
    @Override public void notifyMessageDeleted(Message m)  { SwingUtilities.invokeLater(this::refreshMessages); }
    @Override public void notifyMessageModified(Message m) {}
    @Override public void notifyUserAdded(User u)          {}
    @Override public void notifyUserDeleted(User u)        {}
    @Override public void notifyUserModified(User u)       {}
    @Override public void notifyChannelAdded(Channel c)    {}
    @Override public void notifyChannelDeleted(Channel c)  {}
    @Override public void notifyChannelModified(Channel c) {}
}