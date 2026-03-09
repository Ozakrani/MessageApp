package main.java.com.ubo.tp.message.ihm.user;
import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Vue de la liste des utilisateurs.
 * SRS-MAP-USR-007 : consulter la liste des utilisateurs.
 * SRS-MAP-USR-008 : rechercher un utilisateur.
 */
public class UserListView extends JPanel {

    private JTextField mSearchField;
    private JPanel     mListPanel;
    private JLabel     mTitleLabel;

    public UserListView() {
        initGUI();
    }

    private void initGUI() {
        setLayout(new BorderLayout(0, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setPreferredSize(new Dimension(220, 0));
        setBackground(new Color(245, 245, 245));

        // --- Titre ---
        mTitleLabel = new JLabel("Utilisateurs", SwingConstants.CENTER);
        mTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        mTitleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));

        // --- Champ de recherche (SRS-MAP-USR-008) ---
        mSearchField = new JTextField();
        mSearchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                new EmptyBorder(4, 6, 4, 6)));
        mSearchField.putClientProperty("JTextField.placeholderText", "Rechercher...");

        JPanel topPanel = new JPanel(new BorderLayout(0, 4));
        topPanel.setOpaque(false);
        topPanel.add(mTitleLabel,   BorderLayout.NORTH);
        topPanel.add(mSearchField,  BorderLayout.SOUTH);

        // --- Liste scrollable ---
        mListPanel = new JPanel();
        mListPanel.setLayout(new BoxLayout(mListPanel, BoxLayout.Y_AXIS));
        mListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        add(topPanel,   BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------
    // API publique
    // ---------------------------------------------------------------

    /**
     * Vide et recharge la liste avec les vues fournies.
     */
    public void setUserViews(java.util.List<UserView> userViews) {
        mListPanel.removeAll();
        for (UserView view : userViews) {
            mListPanel.add(view);
        }
        // Remplissage du bas pour éviter l'étirement
        mListPanel.add(Box.createVerticalGlue());
        mListPanel.revalidate();
        mListPanel.repaint();
    }

    /**
     * Branche un listener sur le champ de recherche.
     * Appelé par UserListController.
     */
    public void addSearchListener(KeyAdapter adapter) {
        mSearchField.addKeyListener(adapter);
    }

    /**
     * @return Le texte saisi dans le champ de recherche.
     */
    public String getSearchText() {
        return mSearchField.getText().trim();
    }
}