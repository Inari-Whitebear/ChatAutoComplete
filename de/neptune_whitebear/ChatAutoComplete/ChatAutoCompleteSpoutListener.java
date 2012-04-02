/*
 * This file is part of ChatAutoComplete.
 *
 *
 * ChatAutoComplete is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ChatAutoComplete is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ChatAutoComplete. If not, see <http://www.gnu.org/licenses/>.
 */


package de.neptune_whitebear.ChatAutoComplete;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;
import org.getspout.spoutapi.sound.SoundManager;

import java.util.Collection;

public class ChatAutoCompleteSpoutListener implements Listener
{

    public ChatAutoCompleteSpoutListener(ChatAutoComplete instance, boolean cUseSpout, ChatAutoCompleteConfig config)
    {
        plugin = instance;
        lastEvent = null;

        useSpout = cUseSpout;
        spoutSound = config.getSpoutSound();
        useNotification = config.getUseNotification();
        Material spoutMaterial = Material.getMaterial( config.getSpoutNotificationMaterial().toUpperCase() );

        if( spoutMaterial == null )
        {
            plugin.consoleMsg( "Spout Error // Material " + config.getSpoutNotificationMaterial()
                    .toUpperCase() + " not a valid material; Using default." );
            spoutNotificationMaterial = Material.DIAMOND_BLOCK;
        } else
        {
            spoutNotificationMaterial = spoutMaterial;
        }

        spoutNotificationMessage = config.getSpoutNotificationMessage();
        spoutNotificationTitle = config.getSpoutNotificationTitle();
    }


    public void passEvent( Event event, Collection<Player> notifyPlayers )
    {
        if( !useSpout ) return;
        lastEvent = event;
        lastPlayers = notifyPlayers;
    }

    void spoutNotifyPlayer( Player player )
    {
        SpoutPlayer spoutPlayer = SpoutManager.getPlayer(player);
        if( spoutPlayer != null )
        {
            if( !spoutSound.endsWith( "NONE" ) )
            {
                SoundManager soundMng = SpoutManager.getSoundManager();
                SoundEffect eff = SoundEffect.getSoundEffectFromName( "random." + spoutSound );
                if( eff != null ) soundMng.playSoundEffect( spoutPlayer, eff, player.getLocation(), 20, 60 );
                else plugin.consoleMsg( "Sound does not exist => SoundEff == NULL", true );
            }
            if( useNotification )
            {

                spoutPlayer.sendNotification( spoutNotificationTitle, spoutNotificationMessage, spoutNotificationMaterial );
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpoutPlayerChat( PlayerChatEvent event )
    {
        if( event != lastEvent ) return;
        if( event.isCancelled() )
        {
            lastEvent = null;
            lastPlayers = null;
            return;
        }

        for( Player player : lastPlayers )
        {
            spoutNotifyPlayer( player );
        }

        lastPlayers = null;
        lastEvent = null;


    }



    private final ChatAutoComplete plugin;
    private Event lastEvent;
    private Collection<Player> lastPlayers;

    private final boolean useSpout;
    private final String spoutSound;
    private final boolean useNotification;
    private final String spoutNotificationMessage;
    private final String spoutNotificationTitle;
    private final Material spoutNotificationMaterial;
}
