package main.java.com.ubo.tp.message.ihm.message;

import main.java.com.ubo.tp.message.datamodel.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Vue représentant un message individuel.
 * AUCUNE logique métier ici — uniquement l'affichage.
 * SRS-MAP-MSG-001, SRS-MAP-MSG-003, SRS-MAP-MSG-006
 */
public class MessageView extends JPanel {

    private static final Color COLOR_OWN_BG    = new Color(220, 240, 255);
    private static final Color COLOR_OTHER_BG  = new Color(248, 248, 248);
    private static final Color COLOR_SEPARATOR = new Color(210, 210, 210);
    private static final Font  FONT_AUTHOR     = new Font("SansSerif", Font.BOLD,   12);
    private static final Font  FONT_CONTENT    = new Font("SansSerif", Font.PLAIN,  12);
    private static final Font  FONT_TIMESTAMP  = new Font("SansSerif", Font.ITALIC, 10);
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("HH:mm  dd/MM/yyyy");

    private JLabel    mAuthorLabel;
    private JTextArea mContentArea;
    private JLabel    mTimestampLabel;
    private JButton   mDeleteButton;

    private final Message mMessage;

    /**
     * @param message   Le message à afficher.
     * @param canDelete Déterminé par le contrôleur — true si l'auteur = user connecté.
     * @param isOwn     Déterminé par le contrôleur — true pour le style "mes messages".
     */
    public MessageView(Message message, boolean canDelete, boolean isOwn) {
        this.mMessage = message;
        this.initGUI(canDelete, isOwn);
    }

    private void initGUI(boolean canDelete, boolean isOwn) {
        setLayout(new BorderLayout(4, 2));
        setOpaque(true);
        setBackground(isOwn ? COLOR_OWN_BG : COLOR_OTHER_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_SEPARATOR),
                new EmptyBorder(6, 12, 6, 12)));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // --- En-tête ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        mAuthorLabel = new JLabel(
                mMessage.getSender().getName()
                        + "  (@" + mMessage.getSender().getUserTag() + ")");
        mAuthorLabel.setFont(FONT_AUTHOR);
        mAuthorLabel.setForeground(isOwn ? new Color(30, 90, 170) : new Color(80, 80, 80));

        mTimestampLabel = new JLabel(
                DATE_FORMAT.format(new Date(mMessage.getEmissionDate())));
        mTimestampLabel.setFont(FONT_TIMESTAMP);
        mTimestampLabel.setForeground(Color.GRAY);

        headerPanel.add(mAuthorLabel,    BorderLayout.WEST);
        headerPanel.add(mTimestampLabel, BorderLayout.EAST);

        // --- Corps ---
        mContentArea = new JTextArea(mMessage.getText());
        mContentArea.setFont(FONT_CONTENT);
        mContentArea.setOpaque(false);
        mContentArea.setEditable(false);
        mContentArea.setFocusable(false);
        mContentArea.setLineWrap(true);
        mContentArea.setWrapStyleWord(true);
        mContentArea.setBorder(new EmptyBorder(4, 0, 4, 0));

        add(headerPanel,  BorderLayout.NORTH);
        add(mContentArea, BorderLayout.CENTER);

        // --- Bouton suppression — affiché uniquement si canDelete=true ---
        // La décision vient du contrôleur, pas de la vue (SRS-MAP-MSG-006)
        if (canDelete) {
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
            footerPanel.setOpaque(false);

            mDeleteButton = new JButton("✕ Supprimer");
            mDeleteButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
            mDeleteButton.setForeground(new Color(180, 40, 40));
            mDeleteButton.setBackground(new Color(255, 220, 220));
            mDeleteButton.setBorder(BorderFactory.createLineBorder(
                    new Color(200, 80, 80), 1, true));
            mDeleteButton.setFocusPainted(false);
            mDeleteButton.setPreferredSize(new Dimension(115, 22));

            footerPanel.add(mDeleteButton);
            add(footerPanel, BorderLayout.SOUTH);
        }
    }

    public void addDeleteListener(ActionListener listener) {
        if (mDeleteButton != null) {
            mDeleteButton.addActionListener(listener);
        }
    }

    public Message getMessage() { return mMessage; }
}