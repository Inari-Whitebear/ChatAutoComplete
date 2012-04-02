/*
 *   This file is part of ChatAutoComplete.
 *
 *
 *   ChatAutoComplete is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ChatAutoComplete is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ChatAutoComplete. If not, see <http://www.gnu.org/licenses/>.
 *
 */


package de.neptune_whitebear.ChatAutoComplete;

import com.dthielke.herochat.ChannelChatEvent;



import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

class HeroChatListener implements Listener
{

    public HeroChatListener( ChatAutoComplete instance, MessageProcessor cMessageProcessor )
    {
        plugin = instance;
        messageProcessor = cMessageProcessor;
    }

    @EventHandler
    void onChannelChatEvent( ChannelChatEvent event )
    {

        if( event.getBukkitEvent().isCancelled() ) return;
        if( event.getSender().getPlayer() == null ) return;

        String[] process = messageProcessor.ProcessMessage( event.getSender().getPlayer(), event.getBukkitEvent().getMessage(), event
                .getBukkitEvent().getFormat(), event );


        event.getBukkitEvent().setMessage( process[0] );
        event.getBukkitEvent().setFormat( process[1] );

    }


    private final MessageProcessor messageProcessor;
    private final ChatAutoComplete plugin;
}
