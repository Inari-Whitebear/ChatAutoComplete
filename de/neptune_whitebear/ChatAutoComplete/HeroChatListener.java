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

import com.herocraftonline.dthielke.herochat.event.ChannelChatEvent;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

class HeroChatListener extends CustomEventListener
{

    public HeroChatListener( ChatAutoComplete instance, MessageProcessor cMessageProcessor )
    {
        plugin = instance;
        messageProcessor = cMessageProcessor;
    }

    void onChannelChatEvent( ChannelChatEvent event )
    {
        if( event.isCancelled() ) return;
        if( !event.isSentByPlayer() ) return;

        String[] process = messageProcessor.ProcessMessage( plugin.getServer()
                                                                  .getPlayer( event.getSource() ), event.getMessage(), event
                .getFormat(), event );


        event.setMessage( process[0] );
        event.setFormat( process[1] );

    }

    public void onCustomEvent( Event event )
    {
        if( event instanceof ChannelChatEvent ) onChannelChatEvent( ( ChannelChatEvent ) event );
    }


    private final MessageProcessor messageProcessor;
    private final ChatAutoComplete plugin;
}
