package main.java.com.ubo.tp.message.ihm.channel;

import com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vue représentant un canal individuel.
 * SRS-MAP-CHN-001 : consulter la liste des canaux.
 * SRS-MAP-CHN-009 : indicateur de nouveau message.
 */
public class ChannelView extends JPanel {
    private static final Color COLOR_SELECTED = new Color(210, 230, 255);
    private static final Color COLOR_DEFAULT = new Color(250, 250, 250);
    private static final Color COLOR_NEW_MSG = new Color(255, 80, 80);
    private static final Color COLOR_BORDER = new Color(210, 210, 210);

    private JLabel mNameLabel;
    private JLabel mTypeLabel;
    private JLabel mNewMsgIndicator;
    private JButton mJoinButton;
    private JButton mLeaveButton;
    private final Channel mChannel;
    private final User mConnectedUser;
    private boolean mHasNewMessage = false;

    public ChannelView(Channel channel, User connectedUser) {
        this.mChannel = channel;
        this.mConnectedUser = connectedUser;
        initGUI();
    }

    private void initGUI() {
        setLayout(new BorderLayout(8, 0));
        setOpaque(true);
        setBackground(COLOR_DEFAULT);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        mNewMsgIndicator = new JLabel(" ");
        mNewMsgIndicator.setFont(new Font("SansSerif", Font.PLAIN, 16));
        mNewMsgIndicator.setForeground(COLOR_NEW_MSG);
        mNewMsgIndicator.setPreferredSize(new Dimension(20, 20));

        // --- Infos canal ---
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);
        mNameLabel = new JLabel("#" + mChannel.getName());
        mNameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        mNameLabel.setForeground(new Color(40, 40, 40));
        String creatorText = "Créé par @" + mChannel.getCreator().getUserTag();
        JLabel creatorLabel = new JLabel(creatorText);
        creatorLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        creatorLabel.setForeground(Color.GRAY);
        infoPanel.add(mNameLabel);
        infoPanel.add(creatorLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        rightPanel.setOpaque(false);
        boolean isPrivate = mChannel.getUsers() != null && !mChannel.getUsers().isEmpty();
        mTypeLabel = new JLabel(isPrivate ? "PRIVÉ" : "PUBLIC");
        mTypeLabel.setFont(new Font("SansSerif", Font.BOLD, 9));
        mTypeLabel.setForeground(Color.WHITE);
        mTypeLabel.setBackground(isPrivate ? new Color(150, 80, 180) : new Color(50, 150, 80));
        mTypeLabel.setOpaque(true);
        mTypeLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        mJoinButton = new JButton("Rejoindre");
        mJoinButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        mJoinButton.setBackground(new Color(220, 240, 220));
        mJoinButton.setForeground(new Color(40, 120, 40));
        mJoinButton.setBorder(BorderFactory.createLineBorder(new Color(100, 180, 100), 1, true));
        mJoinButton.setFocusPainted(false);
        mJoinButton.setPreferredSize(new Dimension(90, 22));
        rightPanel.add(mTypeLabel);
        rightPanel.add(mJoinButton);

        add(mNewMsgIndicator, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    /** Active/désactive l'indicateur de nouveau message (SRS-MAP-CHN-009). */
// Dans la méthode pour gérer le join / leave d'un canal
    public void setHasNewMessage(boolean hasNew) {
        mHasNewMessage = hasNew;
        mNewMsgIndicator.setText(hasNew ? "●" : " ");
        revalidate();
        repaint();
    }

    // Assurez-vous d'avoir des boutons pour rejoindre ou quitter un canal dans la vue de chaque canal
    public void addJoinListener(ActionListener l) {
        mJoinButton.addActionListener(l);
    }

    public Channel getChannel() {
        return mChannel;
    }

}