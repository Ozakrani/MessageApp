package main.java.com.ubo.tp.message.ihm.channel;

import main.java.com.ubo.tp.message.datamodel.Channel;

/**
 * Interface pour notifier MainPanel qu'un canal a été sélectionné.
 */
public interface IChannelSelectionListener {
    void onChannelSelected(Channel channel);
}