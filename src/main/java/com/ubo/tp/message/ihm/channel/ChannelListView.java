package main.java.com.ubo.tp.message.ihm.channel;

import main.java.com.ubo.tp.message.controller.ChannelController;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.IConversationListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Vue de la liste des canaux.
 * SRS-MAP-CHN-001 : consulter la liste des canaux.
 * SRS-MAP-CHN-003 : créer un canal public.
 * SRS-MAP-CHN-006 : supprimer un canal dont on est le créateur.
 * SRS-MAP-CHN-007 : ajouter un utilisateur à un canal privé.
 * SRS-MAP-CHN-087 : supprimer un utilisateur d'un canal privé.
 * SRS-MAP-MSG-002 : envoyer un message dans un canal public (via listener).
 */
public class ChannelListView extends JPanel {

    private final DefaultListModel<Channel> mChannelListModel;
    private final JList<Channel>            mChannelList;
    private final ChannelController         mController;
    private final User                      mCurrentUser;

    /** Listener pour ouvrir la conversation d'un canal dans ConversationPanel */
    private IChannelSelectionListener mChannelSelectionListener;

    public ChannelListView(ChannelController controller, User currentUser) {
        this.mController       = controller;
        this.mCurrentUser      = currentUser;
        this.mChannelListModel = new DefaultListModel<>();
        this.mChannelList      = new JList<>(mChannelListModel);
        initGUI();
        refreshChannels();
    }

    private void initGUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Canaux"));

        // --- Liste ---
        mChannelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mChannelList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                Channel channel = (Channel) value;
                String label = "#" + channel.getName()
                        + (channel.getUsers().isEmpty() ? "  [public]" : "  [privé]");
                JLabel lbl = new JLabel(label);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                lbl.setBackground(isSelected ? new Color(210, 230, 255) : Color.WHITE);
                return lbl;
            }
        });

        // Clic sur un canal → ouvrir la conversation
        mChannelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Channel selected = mChannelList.getSelectedValue();
                if (selected != null && mChannelSelectionListener != null) {
                    mChannelSelectionListener.onChannelSelected(selected);
                }
            }
        });

        // --- Boutons ---
        JButton createButton = new JButton("+ Créer");
        createButton.setBackground(new Color(220, 240, 220));
        createButton.addActionListener(e -> handleCreate());

        JButton deleteButton = new JButton("✕ Suppr.");
        deleteButton.setBackground(new Color(255, 220, 220));
        deleteButton.addActionListener(e -> handleDelete());

        JButton editButton = new JButton("✎ Modifier");
        editButton.setBackground(new Color(255, 240, 200));
        editButton.addActionListener(e -> handleEdit());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 5));
        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JScrollPane(mChannelList), BorderLayout.CENTER);
        add(buttonPanel,                   BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------
    // Handlers
    // ---------------------------------------------------------------

    private void handleCreate() {
        String channelName = JOptionPane.showInputDialog(
                this, "Nom du canal :", "Créer un canal", JOptionPane.PLAIN_MESSAGE);
        if (channelName != null && !channelName.trim().isEmpty()) {
            mController.createChannel(mCurrentUser, channelName.trim());
            refreshChannels();
        }
    }

    private void handleDelete() {
        Channel selected = mChannelList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un canal.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mController.deleteChannel(selected, mCurrentUser);
        refreshChannels();
    }

    /**
     * Ouvre une boîte de dialogue pour ajouter/supprimer des utilisateurs.
     * SRS-MAP-CHN-007 et SRS-MAP-CHN-087
     */
    private void handleEdit() {
        Channel selected = mChannelList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un canal à modifier.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérification : seul le créateur peut modifier
        if (!selected.getCreator().getUserTag().equals(mCurrentUser.getUserTag())) {
            JOptionPane.showMessageDialog(this,
                    "Seul le créateur peut modifier ce canal.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Dialogue de modification
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Liste des membres actuels
        DefaultListModel<User> membersModel = new DefaultListModel<>();
        selected.getUsers().forEach(membersModel::addElement);
        JList<User> membersList = new JList<>(membersModel);
        membersList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                User u = (User) value;
                return super.getListCellRendererComponent(
                        list, u.getName() + " (@" + u.getUserTag() + ")",
                        index, isSelected, cellHasFocus);
            }
        });

        // Champ pour ajouter un user par tag
        JTextField addTagField = new JTextField(15);
        JButton addUserBtn = new JButton("+ Ajouter");
        addUserBtn.addActionListener(e -> {
            String tag = addTagField.getText().trim();
            User found = mController.findUser(tag);
            if (found == null) {
                JOptionPane.showMessageDialog(panel,
                        "Utilisateur @" + tag + " introuvable.",
                        "Erreur", JOptionPane.WARNING_MESSAGE);
            } else {
                mController.addUserToChannel(selected, found, mCurrentUser);
                membersModel.addElement(found);
                addTagField.setText("");
                refreshChannels();
            }
        });

        JButton removeUserBtn = new JButton("✕ Retirer");
        removeUserBtn.addActionListener(e -> {
            User selectedUser = membersList.getSelectedValue();
            if (selectedUser != null) {
                mController.removeUserFromChannel(selected, selectedUser, mCurrentUser);
                membersModel.removeElement(selectedUser);
                refreshChannels();
            }
        });

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(new JLabel("Tag :"));
        addPanel.add(addTagField);
        addPanel.add(addUserBtn);
        addPanel.add(removeUserBtn);

        panel.add(new JLabel("Membres de #" + selected.getName() + " :"), BorderLayout.NORTH);
        panel.add(new JScrollPane(membersList), BorderLayout.CENTER);
        panel.add(addPanel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel,
                "Modifier le canal #" + selected.getName(),
                JOptionPane.PLAIN_MESSAGE);
    }

    // ---------------------------------------------------------------
    // API publique
    // ---------------------------------------------------------------

    public void setChannelSelectionListener(IChannelSelectionListener listener) {
        this.mChannelSelectionListener = listener;
    }

    public void refreshChannels() {
        mChannelListModel.clear();
        List<Channel> channels = mController.getChannels();
        for (Channel channel : channels) {
            mChannelListModel.addElement(channel);
        }
    }
}