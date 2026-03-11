package com.ubo.tp.message.controller;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.datamodel.Channel;
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
    public Channel createChannel(User creator, String channelName) {
        if (channelName == null || channelName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Le nom du canal ne peut pas être vide.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Channel channel = new Channel(creator, channelName.trim()); // écrit le fichier .chn
        mDataManager.sendChannel(channel);
        return channel;
    }

    /**
     * Crée un canal privé (SRS-MAP-CHN-004).
     * Constructeur : Channel(User creator, String name, List<User> users)
     */
    public Channel createPrivateChannel(User creator, String channelName, List<User> users) {
        if (channelName == null || channelName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Le nom du canal ne peut pas être vide.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        users.removeIf(u -> u.getUserTag().equals(creator.getUserTag()));
        Channel channel = new Channel(creator, channelName.trim(), users);
        mDataManager.sendChannel(channel); // ajout immédiat
        mDataManager.addChannel(channel);
        return channel;
    }

    /**
     * Supprime un canal (SRS-MAP-CHN-006).
     * Vérifie que l'utilisateur connecté est bien le créateur.
     */
    public boolean deleteChannel(Channel channel, User connectedUser) {
        // seul le créateur peut supprimer
        if (!channel.getCreator().getUserTag().equals(connectedUser.getUserTag())) {
            JOptionPane.showMessageDialog(null, "Vous ne pouvez supprimer que les canaux dont vous êtes le créateur.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        int choice = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer le canal \"" + channel.getName() + "\" ?", "Supprimer le canal", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            // suppression fichier .chn
            mDataManager.deleteChannel(channel);
            // suppression mémoire
            mDataManager.removeChannel(channel);
            System.out.println("Canal supprimé : " + channel.getName());
            return true;
        }
        return false;
    }

    /**
     * Retourne la liste de tous les canaux.
     * SRS-MAP-CHN-001
     */
    public List<Channel> getChannels(User connectedUser) {
        List<Channel> visibleChannels = new ArrayList<>();
        for (Channel c : mDataManager.getChannels()) {
            // canal public
            if (c.getUsers().isEmpty()) {
                visibleChannels.add(c);
            }
            // canal privé : visible pour créateur ou membre
            else if (c.getCreator().getUserTag().equals(connectedUser.getUserTag()) || containsUserByTag(c.getUsers(), connectedUser.getUserTag())) {
                visibleChannels.add(c);
            }
        }
        return visibleChannels;
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

    public void addUserToChannel(Channel channel, User userToAdd, User connectedUser) {
        // Vérifie que l'utilisateur qui tente d'ajouter est bien le créateur du canal
        if (!channel.getCreator().getUserTag().equals(connectedUser.getUserTag())) {
            JOptionPane.showMessageDialog(null, "Seul le créateur peut modifier ce canal.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérifie que l'utilisateur à ajouter est valide
        if (userToAdd == null) {
            JOptionPane.showMessageDialog(null, "Utilisateur introuvable.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Assure que l'utilisateur n'est pas déjà le créateur du canal
        if (userToAdd.getUserTag().equals(channel.getCreator().getUserTag())) {
            JOptionPane.showMessageDialog(null, "Le créateur ne doit pas être ajouté comme membre.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Vérifie si l'utilisateur est déjà membre du canal
        List<User> users = new ArrayList<>(channel.getUsers());
        if (containsUserByTag(users, userToAdd.getUserTag())) {
            JOptionPane.showMessageDialog(null, "Cet utilisateur est déjà dans le canal.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Ajouter l'utilisateur au canal
        users.add(userToAdd);
        Channel updated = new Channel(channel.getUuid(), channel.getCreator(), channel.getName(), users);

        // Enregistrer le canal mis à jour
        mDataManager.sendChannel(updated);
        System.out.println("Utilisateur ajouté au canal : @" + userToAdd.getUserTag());
    }

    // Helper method to check if a user is in the channel by userTag
    private boolean containsUserByTag(List<User> users, String tag) {
        for (User u : users) {
            if (u.getUserTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Retire un utilisateur d'un canal privé (SRS-MAP-CHN-087).
     */
    public void removeUserFromChannel(Channel channel, User userToRemove, User connectedUser) {
        if (!channel.getCreator().getUserTag().equals(connectedUser.getUserTag())) {
            JOptionPane.showMessageDialog(null, "Seul le créateur peut modifier ce canal.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userToRemove.getUserTag().equals(channel.getCreator().getUserTag())) {
            JOptionPane.showMessageDialog(null, "Impossible de supprimer le créateur du canal.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<User> users = new ArrayList<>(channel.getUsers());
        if (!users.contains(userToRemove)) {
            JOptionPane.showMessageDialog(null, "Cet utilisateur n'est pas dans le canal.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        users.remove(userToRemove);
        Channel updated = new Channel(channel.getUuid(), channel.getCreator(), channel.getName(), users);
        mDataManager.sendChannel(updated);
    }

}