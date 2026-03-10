package main.java.com.ubo.tp.message.controller;

import com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import java.util.Set;

public class UserController {

    private final DataManager dataManager;
    private final Session session;

    public UserController(DataManager dataManager, Session session) {
        this.dataManager = dataManager;
        this.session = session;
    }

    public Set<User> getAllUsers() {
        return dataManager.getUsers();
    }

    public User findUser(String tag) {
        return dataManager.getUser(tag);
    }

    public String updateName(String newName) {

        if (newName == null || newName.isEmpty()) {
            return "Nom invalide.";
        }

        User connected = session.getConnectedUser();

        connected.setName(newName);
        dataManager.modifyUser(connected.getUuid(), connected);

        return "SUCCESS";
    }

    public void deleteAccount() {

        User connected = session.getConnectedUser();

        dataManager.deleteUser(connected);

        session.disconnect();
    }
    public void createUser() {

        String tag = JOptionPane.showInputDialog("Tag utilisateur");

        if(tag == null || tag.trim().isEmpty()) return;

        String name = JOptionPane.showInputDialog("Nom");

        if(name == null || name.trim().isEmpty()) return;

        String password = JOptionPane.showInputDialog("Mot de passe");

        dataManager.createUser(tag, name, password);
    }

    public void deleteUser(User user){

        if(user == null){
            JOptionPane.showMessageDialog(null,"Sélectionnez un utilisateur");
            return;
        }

        dataManager.deleteUser(user);
    }
}
