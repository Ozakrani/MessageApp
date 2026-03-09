package main.java.com.ubo.tp.message.controller;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Contrôleur des canaux.
 * SRS-MAP-CHN-003 : créer un canal public.
 * SRS-MAP-CHN-004 : créer un canal privé.
 * SRS-MAP-CHN-006 : supprimer un canal privé dont on est le propriétaire.
 */
public class ChannelController {

    private final DataManager mDataManager;

    public ChannelController(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    /**
     * Crée un canal public (SRS-MAP-CHN-003).
     * Constructeur : Channel(User creator, String name)
     */
    public void createChannel(User creator, String channelName) {
        if (channelName == null || channelName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Le nom du canal ne peut pas être vide.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Canal public — pas de liste d'utilisateurs
        Channel channel = new Channel(creator, channelName.trim());
        mDataManager.sendChannel(channel);
    }

    /**
     * Crée un canal privé (SRS-MAP-CHN-004).
     * Constructeur : Channel(User creator, String name, List<User> users)
     */
    public void createPrivateChannel(User creator, String channelName, List<User> users) {
        if (channelName == null || channelName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Le nom du canal ne peut pas être vide.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Channel channel = new Channel(creator, channelName.trim(), users);
        mDataManager.sendChannel(channel);
    }

    /**
     * Supprime un canal (SRS-MAP-CHN-006).
     * Vérifie que l'utilisateur connecté est bien le créateur.
     */
    public void deleteChannel(Channel channel, User connectedUser) {
        // Vérification : seul le créateur peut supprimer (SRS-MAP-CHN-006)
        if (!channel.getCreator().getUserTag().equals(connectedUser.getUserTag())) {
            JOptionPane.showMessageDialog(null,
                    "Vous ne pouvez supprimer que les canaux dont vous êtes le créateur.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(null,
                "Voulez-vous vraiment supprimer le canal \"" + channel.getName() + "\" ?",
                "Supprimer le canal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            mDataManager.deleteChannel(channel);
        }
    }

    /**
     * Retourne la liste de tous les canaux.
     * SRS-MAP-CHN-001
     */
    public List<Channel> getChannels() {
        return new ArrayList<>(mDataManager.getChannels());
    }

    /**
     * Vérifie si l'utilisateur connecté peut supprimer un canal.
     */
    public boolean canDelete(Channel channel, User connectedUser) {
        if (channel == null || connectedUser == null) return false;
        return channel.getCreator().getUserTag().equals(connectedUser.getUserTag());
    }
    public User findUser(String tag) {
        return mDataManager.getUser(tag);
    }

    /** Ajoute un utilisateur à un canal privé (SRS-MAP-CHN-007). */
    public void addUserToChannel(Channel channel, User userToAdd, User connectedUser) {
        if (!channel.getCreator().getUserTag().equals(connectedUser.getUserTag())) {
            JOptionPane.showMessageDialog(null,
                    "Seul le créateur peut modifier ce canal.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<User> users = new ArrayList<>(channel.getUsers());
        users.add(userToAdd);
        Channel updated = new Channel(channel.getUuid(), channel.getCreator(),
                channel.getName(), users);
        mDataManager.sendChannel(updated);
    }

    /** Retire un utilisateur d'un canal privé (SRS-MAP-CHN-087). */
    public void removeUserFromChannel(Channel channel, User userToRemove, User connectedUser) {
        if (!channel.getCreator().getUserTag().equals(connectedUser.getUserTag())) {
            JOptionPane.showMessageDialog(null,
                    "Seul le créateur peut modifier ce canal.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<User> users = new ArrayList<>(channel.getUsers());
        users.remove(userToRemove);
        Channel updated = new Channel(channel.getUuid(), channel.getCreator(),
                channel.getName(), users);
        mDataManager.sendChannel(updated);
    }
}