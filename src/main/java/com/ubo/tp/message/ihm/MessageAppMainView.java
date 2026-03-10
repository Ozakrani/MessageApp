package com.ubo.tp.message.ihm;

import main.java.com.ubo.tp.message.controller.LoginController;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.login.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView extends JFrame implements ISessionObserver {

    private com.ubo.tp.message.ihm.MessageApp application;
    private LoginPanel loginPanel;
    private LoginController loginController;
    private JPanel mainPanel;



    /**
     * Constructeur.
     *
     * @param app L'application principale
     */
    public MessageAppMainView(com.ubo.tp.message.ihm.MessageApp app) {
        this.application = app;
        initComponents();
    }

    /**
     * Initialisation des composants de l'interface.
     */
    private void initComponents() {
        // Configuration de la fenêtre
        setTitle("Message App - M2 TIIL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Icône
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/main/resources/images/logo_50.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Logo introuvable : " + e.getMessage());
        }

        // Créer la barre de menu
        createMenuBar();

        // === MVC Setup ===
        // VIEW
        loginPanel = new LoginPanel();

        // CONTROLLER
        loginController = new LoginController(
                application.getDataManager(),
                application.getSession()
        );
        loginPanel.setController(loginController);
        // Panneau principal (après connexion)
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel("Bienvenue ! Interface principale à venir...", SwingConstants.CENTER), BorderLayout.CENTER);

        // Afficher le panneau de login au démarrage
        setContentPane(loginPanel);
    }

    /**
     * Affiche le panneau de login.
     */
    public void showLoginPanel() {
        setContentPane(loginPanel);
        revalidate();
        repaint();
    }

    /**
     * Affiche l'interface principale après connexion.
     */
    public void showMainInterface(User user) {
        mainPanel = new MainPanel(
                user,
                application.getDataManager(),
                application.getSession()
        );
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }

    /**
     * Création de la barre de menu.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu menuFichier = new JMenu("Fichier");
        menuFichier.setMnemonic(KeyEvent.VK_F);

        // Item Choisir répertoire
        JMenuItem itemChoisirRepertoire = new JMenuItem("Choisir répertoire d'échange");
        itemChoisirRepertoire.setMnemonic(KeyEvent.VK_C);

        try {
            ImageIcon folderIcon = new ImageIcon(getClass().getResource("/resources/folder.png"));
            itemChoisirRepertoire.setIcon(folderIcon);
        } catch (Exception e) {
            System.err.println("Icône Folder introuvable : " + e.getMessage());
        }

        itemChoisirRepertoire.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseExchangeDirectory();
            }
        });

        menuFichier.add(itemChoisirRepertoire);
        menuFichier.addSeparator();

        // Item Quitter
        JMenuItem itemQuitter = new JMenuItem("Quitter");
        itemQuitter.setMnemonic(KeyEvent.VK_Q);
        itemQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        try {
            ImageIcon quitIcon = new ImageIcon(getClass().getResource("/main/resources/images/exitIcon_20.png"));
            itemQuitter.setIcon(quitIcon);
        } catch (Exception e) {
            System.err.println("Icône Quitter introuvable : " + e.getMessage());
        }

        itemQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                application.exit();
            }
        });

        menuFichier.add(itemQuitter);

        // Menu Aide
        JMenu menuAide = new JMenu("?");
        menuAide.setMnemonic(KeyEvent.VK_H);

        JMenuItem itemAPropos = new JMenuItem("À propos");
        itemAPropos.setMnemonic(KeyEvent.VK_A);

        itemAPropos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });

        menuAide.add(itemAPropos);

        menuBar.add(menuFichier);
        menuBar.add(menuAide);

        setJMenuBar(menuBar);
    }

    private void chooseExchangeDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner le répertoire d'échange");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();

            if (application.isValidExchangeDirectory(selectedDirectory)) {
                application.initDirectory(selectedDirectory.getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        "Répertoire d'échange configuré : " + selectedDirectory.getAbsolutePath(),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Le répertoire sélectionné n'est pas valide.\nVeuillez choisir un répertoire accessible en lecture et écriture.",
                        "Répertoire invalide", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAboutDialog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            ImageIcon logo = new ImageIcon(getClass().getResource("/main/resources/images/logo_50.png"));
            Image img = logo.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(img));
            panel.add(logoLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.err.println("Logo introuvable pour À propos : " + e.getMessage());
        }

        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.add(new JLabel("<html><b>Message App</b></html>"));
        infoPanel.add(new JLabel("Version 1.0"));
        infoPanel.add(new JLabel("UBO M2 TIIL"));
        infoPanel.add(new JLabel("Département informatique"));

        panel.add(infoPanel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "À propos de Message App", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void notifyLogin(User user) {
        showMainInterface(user);
    }

    @Override
    public void notifyLogout() {
        showLoginPanel();
    }


}