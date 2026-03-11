package com.ubo.tp.message.ihm.channel;

import com.ubo.tp.message.controller.ChannelController;
import com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Vue de la liste des canaux.
 * SRS-MAP-CHN-001 : consulter la liste des canaux.
 * SRS-MAP-CHN-003 : créer un canal public.
 * SRS-MAP-CHN-006 : supprimer un canal dont on est le créateur.
 * SRS-MAP-CHN-007 : ajouter un utilisateur à un canal privé.
 * SRS-MAP-CHN-087 : supprimer un utilisateur d'un canal privé.
 * SRS-MAP-CHN-005 : rechercher un canal.
 */
public class ChannelListView extends JPanel {
    private final DefaultListModel<Channel> mChannelListModel;
    private final JList<Channel> mChannelList;
    private final ChannelController mController;
    private final User mCurrentUser;
    private JTextField searchField;
    private main.java.com.ubo.tp.message.ihm.channel.IChannelSelectionListener mChannelSelectionListener;

    public ChannelListView(ChannelController controller, User currentUser) {
        this.mController = controller;
        this.mCurrentUser = currentUser;
        this.mChannelListModel = new DefaultListModel<>();
        this.mChannelList = new JList<>(mChannelListModel);
        initGUI();
        refreshChannels();
    }

    private void initGUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Canaux"));

        // ===== CHAMP RECHERCHE =====
        searchField = new JTextField();
        searchField.setToolTipText("Rechercher un canal...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String search = searchField.getText().toLowerCase();
                if (search.isEmpty()) {
                    refreshChannels();
                    return;
                }
                mChannelListModel.clear();
                List<Channel> channels = mController.getChannels(mCurrentUser);
                for (Channel c : channels) {
                    if (c.getName().toLowerCase().contains(search)) {
                        mChannelListModel.addElement(c);
                    }
                }
            }
        });
        add(searchField, BorderLayout.NORTH);

        // ===== LISTE =====
        mChannelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mChannelList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Channel channel = (Channel) value;
                String label = "#" + channel.getName() + (channel.getUsers().isEmpty() ? " [public]" : " [privé]");
                JLabel lbl = new JLabel(label);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                if (isSelected) {
                    lbl.setBackground(new Color(210, 230, 255));
                } else {
                    lbl.setBackground(Color.WHITE);
                }
                return lbl;
            }
        });
        mChannelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Channel selected = mChannelList.getSelectedValue();
                if (selected != null && mChannelSelectionListener != null) {
                    mChannelSelectionListener.onChannelSelected(selected);
                }
            }
        });
        add(new JScrollPane(mChannelList), BorderLayout.CENTER);

        // ===== BOUTONS =====
        JButton createButton = new JButton("+ Créer");
        createButton.setBackground(new Color(220, 240, 220));
        createButton.addActionListener(e -> handleCreate());
        JButton deleteButton = new JButton("✕ Suppr.");
        deleteButton.setBackground(new Color(255, 220, 220));
        deleteButton.addActionListener(e -> handleDelete());
        JButton editButton = new JButton("⚙ Modifier");
        editButton.addActionListener(e -> handleEdit());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // ===== CREATION CANAL =====
    private void handleCreate() {
        String channelName = JOptionPane.showInputDialog(this, "Nom du canal :", "Créer un canal", JOptionPane.PLAIN_MESSAGE);
        if (channelName == null || channelName.trim().isEmpty()) {
            return;
        }
        Object[] options = {"Public", "Privé"};
        int choice = JOptionPane.showOptionDialog(this, "Type de canal", "Choisir le type", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            // ===== CANAL PUBLIC =====
            Channel newChannel = mController.createChannel(mCurrentUser, channelName.trim());
            if (newChannel != null) {
                mChannelListModel.addElement(newChannel);
            }
        } else if (choice == 1) {
            // ===== CANAL PRIVE =====
            String usersTags = JOptionPane.showInputDialog(this, "Tags des utilisateurs (séparés par des virgules)\nEx: user1,user2", "Utilisateurs", JOptionPane.PLAIN_MESSAGE);
            if (usersTags == null || usersTags.trim().isEmpty()) {
                return;
            }
            String[] tags = usersTags.split(",");
            java.util.List<User> users = new java.util.ArrayList<>();
            for (String tag : tags) {
                User u = mController.findUser(tag.trim());
                if (tag.trim().equals(mCurrentUser.getUserTag())) {
                    JOptionPane.showMessageDialog(this, "Vous êtes déjà le créateur du canal.");
                    continue;
                }
                if (u != null) {
                    users.add(u);
                }
            }
            if (users.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun utilisateur valide trouvé.");
                return;
            }
            Channel newChannel = mController.createPrivateChannel(mCurrentUser, channelName.trim(), users);
            if (newChannel != null) {
                mChannelListModel.addElement(newChannel);
            }
        }
    }

    // ===== SUPPRESSION CANAL =====
    private void handleDelete() {
        Channel selected = mChannelList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un canal.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean deleted = mController.deleteChannel(selected, mCurrentUser);
        if (deleted) {
            mChannelListModel.removeElement(selected);
        }
    }

    // ===== MODIFIER MEMBRES CANAL =====
    private void handleEdit() {
        Channel selected = mChannelList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un canal.");
            return;
        }
        if (!selected.getCreator().getUserTag().equals(mCurrentUser.getUserTag())) {
            JOptionPane.showMessageDialog(this, "Seul le créateur peut modifier ce canal.");
            return;
        }
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<User> membersModel = new DefaultListModel<>();
        selected.getUsers().forEach(membersModel::addElement);
        JList<User> membersList = new JList<>(membersModel);
        JTextField tagField = new JTextField(10);
        JButton addBtn = new JButton("+ Ajouter");
        JButton removeBtn = new JButton("✕ Retirer");
        addBtn.addActionListener(e -> {
            String tag = tagField.getText().trim();
            User user = mController.findUser(tag);
            if (user == null) {
                JOptionPane.showMessageDialog(panel, "Utilisateur introuvable");
                return;
            }
            mController.addUserToChannel(selected, user, mCurrentUser);
            membersModel.addElement(user);
            refreshChannels();
        });
        removeBtn.addActionListener(e -> {
            User user = membersList.getSelectedValue();
            if (user != null) {
                mController.removeUserFromChannel(selected, user, mCurrentUser);
                membersModel.removeElement(user);
                refreshChannels();
            }
        });
        JPanel south = new JPanel();
        south.add(new JLabel("Tag:"));
        south.add(tagField);
        south.add(addBtn);
        south.add(removeBtn);
        panel.add(new JScrollPane(membersList), BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(this, panel, "Modifier canal", JOptionPane.PLAIN_MESSAGE);
    }

    // ===== API =====
    public void setChannelSelectionListener(main.java.com.ubo.tp.message.ihm.channel.IChannelSelectionListener listener) {
        this.mChannelSelectionListener = listener;
    }

    public void refreshChannels() {
        mChannelListModel.clear();
        List<Channel> channels = mController.getChannels(mCurrentUser);
        for (Channel channel : channels) {
            mChannelListModel.addElement(channel);  // Ajouter le canal à la vue
        }
        mChannelList.repaint();  // Rafraîchir l'affichage
    }
}