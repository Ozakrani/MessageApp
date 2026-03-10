package main.java.com.ubo.tp.message.ihm.user;

import main.java.com.ubo.tp.message.datamodel.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vue représentant un utilisateur individuel dans l'interface graphique.
 *
 * Conforme aux spécifications :
 *   - SRS-MAP-USR-007 : consultation de la liste des utilisateurs
 *   - SRS-MAP-CHN-010 : indicateur graphique de présence en ligne
 */
public class UserView extends JPanel {

    // ---------------------------------------------------------------
    // Constantes visuelles
    // ---------------------------------------------------------------

    private static final Color COLOR_ONLINE  = new Color(50,180,50);
    private static final Color COLOR_OFFLINE = new Color(180,180,180);

    private static final Color COLOR_OWN_BG   = new Color(230,245,255);
    private static final Color COLOR_OTHER_BG = new Color(250,250,250);
    private static final Color COLOR_BORDER   = new Color(210,210,210);

    private static final Font FONT_NAME   = new Font("SansSerif",Font.BOLD,13);
    private static final Font FONT_TAG    = new Font("SansSerif",Font.PLAIN,11);
    private static final Font FONT_STATUS = new Font("SansSerif",Font.ITALIC,10);

    // ---------------------------------------------------------------
    // Composants
    // ---------------------------------------------------------------

    private JLabel mOnlineIndicator;
    private JLabel mNameLabel;
    private JLabel mTagLabel;
    private JLabel mStatusLabel;
    private JButton mPrivateMessageButton;

    // ---------------------------------------------------------------
    // Données
    // ---------------------------------------------------------------

    private final User mUser;
    private final User mConnectedUser;

    // ---------------------------------------------------------------
    // Constructeur
    // ---------------------------------------------------------------

    public UserView(User user, User connectedUser) {
        this.mUser = user;
        this.mConnectedUser = connectedUser;
        initGUI();
    }

    // ---------------------------------------------------------------
    // Initialisation GUI
    // ---------------------------------------------------------------

    private void initGUI() {

        boolean isMe = isConnectedUser();

        setLayout(new BorderLayout(8,0));
        setOpaque(true);
        setBackground(isMe ? COLOR_OWN_BG : COLOR_OTHER_BG);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,COLOR_BORDER),
                new EmptyBorder(6,10,6,10)));

        setMaximumSize(new Dimension(Integer.MAX_VALUE,60));

        // Déterminer statut online
        boolean online = mUser.isOnline();

        if(isMe){
            online = true;
        }

        // ------------------------
        // Indicateur de présence
        // ------------------------

        mOnlineIndicator = new JLabel("●");
        mOnlineIndicator.setFont(new Font("SansSerif",Font.PLAIN,18));
        mOnlineIndicator.setForeground(online ? COLOR_ONLINE : COLOR_OFFLINE);
        mOnlineIndicator.setPreferredSize(new Dimension(24,24));

        // ------------------------
        // Infos utilisateur
        // ------------------------

        JPanel infoPanel = new JPanel(new GridLayout(2,1));
        infoPanel.setOpaque(false);

        mNameLabel = new JLabel(mUser.getName() + (isMe ? " (moi)" : ""));
        mNameLabel.setFont(FONT_NAME);
        mNameLabel.setForeground(isMe ? new Color(30,90,170) : Color.DARK_GRAY);

        mTagLabel = new JLabel("@" + mUser.getUserTag());
        mTagLabel.setFont(FONT_TAG);
        mTagLabel.setForeground(Color.GRAY);

        infoPanel.add(mNameLabel);
        infoPanel.add(mTagLabel);

        // ------------------------
        // Partie droite
        // ------------------------

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        mStatusLabel = new JLabel(online ? "En ligne" : "Hors ligne");
        mStatusLabel.setFont(FONT_STATUS);
        mStatusLabel.setForeground(online ? COLOR_ONLINE : COLOR_OFFLINE);
        mStatusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        rightPanel.add(mStatusLabel,BorderLayout.NORTH);

        // Bouton message privé si ce n'est pas moi
        if(!isMe){

            mPrivateMessageButton = new JButton("✉ Message");

            mPrivateMessageButton.setFont(new Font("SansSerif",Font.PLAIN,10));
            mPrivateMessageButton.setForeground(new Color(50,100,180));
            mPrivateMessageButton.setBackground(new Color(220,235,255));

            mPrivateMessageButton.setBorder(
                    BorderFactory.createLineBorder(new Color(100,150,220),1,true));

            mPrivateMessageButton.setFocusPainted(false);
            mPrivateMessageButton.setPreferredSize(new Dimension(105,22));

            rightPanel.add(mPrivateMessageButton,BorderLayout.CENTER);
        }

        // ------------------------
        // Assemblage
        // ------------------------

        add(mOnlineIndicator,BorderLayout.WEST);
        add(infoPanel,BorderLayout.CENTER);
        add(rightPanel,BorderLayout.EAST);
    }

    // ---------------------------------------------------------------
    // Actions
    // ---------------------------------------------------------------

    public void addPrivateMessageListener(ActionListener listener) {
        if(mPrivateMessageButton != null){
            mPrivateMessageButton.addActionListener(listener);
        }
    }

    // ---------------------------------------------------------------
    // Mise à jour statut
    // ---------------------------------------------------------------

    public void updateOnlineStatus(boolean online){

        if(isConnectedUser()){
            online = true;
        }

        mOnlineIndicator.setForeground(online ? COLOR_ONLINE : COLOR_OFFLINE);
        mStatusLabel.setForeground(online ? COLOR_ONLINE : COLOR_OFFLINE);
        mStatusLabel.setText(online ? "En ligne" : "Hors ligne");

        revalidate();
        repaint();
    }

    // ---------------------------------------------------------------
    // Getter
    // ---------------------------------------------------------------

    public User getUser(){
        return mUser;
    }

    private boolean isConnectedUser(){
        if(mConnectedUser == null) return false;
        return mConnectedUser.getUserTag().equals(mUser.getUserTag());
    }
}