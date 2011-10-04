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
 * You should have received a copy of the GNU General Public License
 * along with ChatAutoComplete. If not, see <http://www.gnu.org/licenses/>.
 */


package plugin.whitebear.neptune.ChatAutoComplete;


import java.util.regex.Pattern;

import org.anjocaido.groupmanager.permissions.NijikoPermissionsProxy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;
import org.getspout.spoutapi.sound.SoundManager;

public class ChatAutoCompletePlayerListener extends PlayerListener
{

    public ChatAutoCompletePlayerListener( ChatAutoComplete cPlugin, ChatAutoCompleteConfig config, NijikoPermissionsProxy cEssentialsProxy, boolean cUseSpout )
    {
        plugin = cPlugin;
        charPrefix = config.getChatPrefix().charAt( 0 );
        maxReplace = config.getMaxReplace();
        // Convert color code to ChatColor
        atSignColor = ChatColor.getByCode( Integer.parseInt( config.getAtSignColor(), 16 ) );
        essentialsProxy = cEssentialsProxy;
        useSpout = cUseSpout;
        spoutSound = config.getSpoutSound();
        useNotification = config.getUseNotification();
    }

    public void onPlayerChat( PlayerChatEvent event )
    {

        if( event.isCancelled() ) return;
        Player sender = event.getPlayer();
        if( !sender.hasPermission( "autocomp.autocomp" ) ) return;
        // Escape if cancelled or doesn't have permissions

        String msg = event.getFormat();
        boolean useFormat = true;
        if( msg.contains( ( CharSequence ) "%2$s" ) )
        {
            useFormat = false;
            msg = event.getMessage();
        }

        int lastIndex = 0;
        int position = msg.indexOf( charPrefix, lastIndex );
        int safeLoop = maxReplace;
        // Loop until nothing else to replace or maximum replaces reached
        while( position != -1 && safeLoop > 0 )
        {

            // Find next space after the @ and extract the name
            int nextSpace = msg.indexOf( ' ', position );
            String subName = msg.substring( position + 1, ( nextSpace == -1 ? msg.length() : nextSpace ) );

            if( subName.length() != 0 )
            {
                replaceName( subName, msg, useFormat, event );
                safeLoop--;

            }
            // Skip @ and first letter so the same one isn't found again
            lastIndex = position + 2;
            // Exit if end of string
            if( lastIndex > msg.length() ) break;
            position = msg.indexOf( charPrefix, lastIndex );
        }

    }

    void replaceName( String subName, String msg, boolean useFormat, PlayerChatEvent event )
    {
        Player player = plugin.getServer().getPlayer( subName );

        if( player != null )
        {
            String prefix = ChatColor.WHITE.toString();
            if( essentialsProxy != null )
            {
                prefix = essentialsProxy.getUserPrefix( player.getWorld().getName(), player.getName() );
            }
            // Replace all occurences with the complete name and color

            msg = msg.replaceAll( "(^|\\s)" + Pattern.quote( ( char ) charPrefix + ( subName ) ) + "($|\\s)", "$1" + ( atSignColor == null ? "" : atSignColor
                    .toString() ) + ( ( char ) charPrefix ) + prefix + player.getName() + ChatColor.WHITE
                    .toString() + "$2" );

            if( useFormat ) event.setFormat( msg );
            else event.setMessage( msg );

            if( useSpout )
            {
                spoutNotifyPlayer( player );
            }

        }
    }

    void spoutNotifyPlayer( Player player )
    {
        SpoutPlayer spoutPlayer = SpoutManager.getPlayer( player );
        if( spoutPlayer != null )
        {
            if( spoutSound != "NONE" )
            {
                SoundManager soundMng = SpoutManager.getSoundManager();
                SoundEffect eff = SoundEffect.getSoundEffectFromName( spoutSound );
                if( eff != null )
                    soundMng.playSoundEffect( spoutPlayer, SoundEffect.getSoundEffectFromName( "random." + spoutSound ), player
                            .getLocation(), 20, 60 );
                else plugin.consoleMsg( "NULL" );
            }
            if( useNotification )
            {

                spoutPlayer.sendNotification( "Highlight", "You've been highlighted!", Material.DIAMOND_BLOCK );
            }
        }
    }

    ChatAutoComplete plugin;
    int charPrefix;
    int maxReplace;
    ChatColor atSignColor;
    NijikoPermissionsProxy essentialsProxy;
    boolean useSpout;
    String spoutSound;
    boolean useNotification;
}