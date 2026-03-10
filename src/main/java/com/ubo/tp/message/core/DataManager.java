package com.ubo.tp.message.core;

import java.util.*;

import main.java.com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.core.database.IDatabase;

import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.directory.IWatchableDirectory;
import main.java.com.ubo.tp.message.core.directory.WatchableDirectory;
import com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.IMessageRecipient;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Classe permettant de manipuler les données de l'application.
 *
 * @author S.Lucas
 */
public class DataManager {

	/**
	 * Base de donnée de l'application.
	 */
	protected final IDatabase mDatabase;

	/**
	 * Gestionnaire des entités contenu de la base de données.
	 */
	protected final EntityManager mEntityManager;

	/**
	 * Classe de surveillance de répertoire
	 */
	protected IWatchableDirectory mWatchableDirectory;

	/**
	 * Constructeur.
	 */
	public DataManager(IDatabase database, EntityManager entityManager) {
		mDatabase = database;
		mEntityManager = entityManager;
	}

	/**
	 * Ajoute un observateur sur les modifications de la base de données.
	 *
	 * @param observer
	 */
	public void addObserver(IDatabaseObserver observer) {
		this.mDatabase.addObserver(observer);
	}

	/**
	 * Supprime un observateur sur les modifications de la base de données.
	 *
	 * @param observer
	 */
	public void removeObserver(IDatabaseObserver observer) {
		this.mDatabase.removeObserver(observer);
	}

	/**
	 * Retourne la liste des Utilisateurs.
	 */
	public Set<User> getUsers() {
		return this.mDatabase.getUsers();
	}

	public void deleteUser(User user){
		mEntityManager.deleteUserFile(user);
	}

	public void updateUser(User user){
		mEntityManager.writeUserFile(user);
		mDatabase.modifyUser(user);
	}
	/**
	 * Retourne la liste des Messages.
	 */
	public Set<Message> getMessages() {
		return this.mDatabase.getMessages();
	}

	/**
	 * Retourne la liste des Canaux.
	 */
	public Set<Channel> getChannels() {
		return this.mDatabase.getChannels();
	}

	/**
	 * Ecrit un message.
	 *
	 * @param message
	 */
	public void sendMessage(Message message) {
		// Ecrit un message
		this.mEntityManager.writeMessageFile(message);
	}

	/**
	 * Ecrit un Utilisateur.
	 *
	 * @param user
	 */
	public void sendUser(User user) {
		// Ecrit un utilisateur
		this.mEntityManager.writeUserFile(user);
	}

	/**
	 * Ecrit un Canal.
	 *
	 * @param channel
	 */
	public void sendChannel(Channel channel) {
		// Ecrit un canal
		this.mEntityManager.writeChannelFile(channel);
	}

	/**
	 * Retourne tous les Messages d'un utilisateur.
	 *
	 * @param user utilisateur dont les messages sont à rechercher.
	 */
	public Set<Message> getMessagesFrom(User user) {
		Set<Message> userMessages = new HashSet<>();

		// Parcours de tous les messages de la base
		for (Message message : this.getMessages()) {
			// Si le message est celui recherché
			if (message.getSender().equals(user)) {
				userMessages.add(message);
			}
		}

		return userMessages;
	}

	/**
	 * Retourne tous les Messages d'un utilisateur addressé à un autre.
	 *
	 * @param user utilisateur dont les messages sont à rechercher.
	 * @param user destinataire des messages recherchés.
	 */
	public Set<Message> getMessagesFrom(User sender, IMessageRecipient recipient) {
		Set<Message> userMessages = new HashSet<>();

		// Parcours de tous les messages de l'utilisateur
		for (Message message : this.getMessagesFrom(sender)) {
			// Si le message est celui recherché
			if (message.getRecipient().equals(recipient.getUuid())) {
				userMessages.add(message);
			}
		}

		return userMessages;
	}

	/**
	 * Retourne tous les Messages adressés à un utilisateur.
	 *
	 * @param user utilisateur dont les messages sont à rechercher.
	 */
	public Set<Message> getMessagesTo(User user) {
		Set<Message> userMessages = new HashSet<>();

		// Parcours de tous les messages de la base
		for (Message message : this.getMessages()) {
			// Si le message est celui recherché
			if (message.getSender().equals(user)) {
				userMessages.add(message);
			}
		}

		return userMessages;
	}

	/**
	 * Assignation du répertoire d'échange.
	 * 
	 * @param directoryPath
	 */
	public void setExchangeDirectory(String directoryPath) {
		mEntityManager.setExchangeDirectory(directoryPath);

		mWatchableDirectory = new WatchableDirectory(directoryPath);
		mWatchableDirectory.initWatching();
		mWatchableDirectory.addObserver(mEntityManager);
	}

	/**
	 * Crée un nouvel utilisateur.
	 * SRS-MAP-USR-001
	 *
	 * @param tag Tag de l'utilisateur (unique)
	 * @param name Nom de l'utilisateur
	 * @param password Mot de passe (peut être null)
	 * @return Le nouvel utilisateur créé
	 */
	public User createUser(String tag, String name, String password) {

		// Vérification champs obligatoires
		if (tag == null || tag.trim().isEmpty()) {
			throw new IllegalArgumentException("Le tag est obligatoire.");
		}

		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Le nom est obligatoire.");
		}

		// Vérification unicité
		if (getUser(tag) != null) {
			throw new IllegalArgumentException("Ce tag existe déjà.");
		}

		User newUser = new User(tag, password, name);
		this.mEntityManager.writeUserFile(newUser);

		return newUser;
	}


	/**
	 * Récupère un utilisateur par son tag.
	 * SRS-MAP-USR-008
	 *
	 * @param tag Tag de l'utilisateur
	 * @return L'utilisateur trouvé ou null
	 */
	public User getUser(String tag) {
		for (User user : getUsers()) {
			if (user.getUserTag().equals(tag)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Modifie un utilisateur existant (pour le statut en ligne).
	 *
	 * @param uuid UUID de l'utilisateur
	 * @param modifiedUser Utilisateur avec les nouvelles données
	 */
	public void modifyUser(java.util.UUID uuid, User modifiedUser) {
		this.mEntityManager.writeUserFile(modifiedUser);
	}

	/**
	 * Supprime un message du répertoire d'échange.
	 * SRS-MAP-MSG-006
	 *
	 * @param message Le message à supprimer.
	 */
	public void deleteMessage(Message message) {

		if (message == null) return;

		// suppression du fichier
		mEntityManager.deleteMessageFile(message);

		// suppression immédiate en mémoire
		mDatabase.deleteMessage(message);
	}

	private final List<Channel> channels = new ArrayList<>();

	public void addChannel(Channel channel) {
		channels.add(channel);
	}

	public void removeChannel(Channel channel) {
		channels.remove(channel);
	}
	public void deleteChannel(Channel channel) {
		this.mEntityManager.deleteChannelFile(channel);
	}
	public Set<Message> searchMessages(UUID channelId, String keyword) {

		Set<Message> result = new HashSet<>();

		for (Message message : mDatabase.getMessages()) {

			if (message.getRecipient().equals(channelId)) {

				if (message.getText().toLowerCase().contains(keyword.toLowerCase())) {
					result.add(message);
				}

			}
		}

		return result;
	}



}
