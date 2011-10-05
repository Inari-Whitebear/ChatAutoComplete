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


import java.util.*;


import com.nijiko.permissions.PermissionHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;


class ChatAutoCompletePlayerListener extends PlayerListener
{

    public ChatAutoCompletePlayerListener( ChatAutoComplete cPlugin, ChatAutoCompleteConfig config, PermissionHandler cPermHandler )
    {
        plugin = cPlugin;
        charPrefix = config.getChatPrefix().charAt( 0 );
        maxReplace = config.getMaxReplace();
        // Convert color code to ChatColor
        atSignColor = ChatColor.getByCode( Integer.parseInt( config.getAtSignColor(), 16 ) );
        permHandler = cPermHandler;
        spoutListener = plugin.getSpoutListener();

    }

    public void onPlayerChat( PlayerChatEvent event )
    {

        if( event.isCancelled() ) return;
        Player sender = event.getPlayer();
        // Escape if cancelled or doesn't have permissions
        if( permHandler != null )
        {
            if( !permHandler.has( sender, "autocomp.autocomp" ) ) return;
        } else if( !sender.hasPermission( "autocomp.autocomp" ) ) return;

        // Use message or if mChat or something changed the format to not include message, use format
        String msg = event.getFormat();
        boolean useFormat = true;
        if( msg.contains( "%2$s" ) )
        {
            useFormat = false;
            msg = event.getMessage();
        }

        //Escape if msg doesn't contain the prefix
        if( msg.indexOf( charPrefix ) == -1 ) return;


        String[] msgSplit = msg.split( "\\s" );
        Map<String, Player> playerMap = new HashMap<String, Player>();
        Map<String, String> nameMap = new HashMap<String, String>();
        int safeLoop = maxReplace;

        StringBuilder builder = new StringBuilder();

        for( String part : msgSplit )
        {
            if( part.charAt( 0 ) == charPrefix )
            {
                safeLoop--;
                // cut off the prefix
                String subName = part.substring( 1 );
                // check cache first
                if( nameMap.containsKey( subName ) )
                {
                    if( nameMap.get( subName ) != null ) subName = nameMap.get( subName );

                } else
                {
                    //check for player
                    Player player = plugin.getServer().getPlayer( subName );
                    if( player != null )
                    {
                        if( !playerMap.containsKey( player.getName() ) )
                        {
                            playerMap.put( player.getName(), player );

                        }
                        nameMap.put( subName, player.getName() );
                        subName = player.getName();
                    } else
                    {
                        nameMap.put( subName, null );
                    }
                }
                if( playerMap.containsKey( subName ) || ( nameMap.containsKey( subName ) && nameMap.get( subName ) != null ) )
                {
                    String prefix = getPrefix( playerMap.get( subName ) );

                    builder.append( builder.length() == 0 ? "" : " " )
                           .append( ( atSignColor == null ) ? "" : atSignColor )
                           .append( ( char ) charPrefix )
                           .append( ChatColor.WHITE )
                           .append( prefix )
                           .append( subName )
                           .append( ChatColor.WHITE );

                } else
                {
                    if( builder.length() != 0 ) part = " " + part;
                    builder.append( part );
                }
            } else
            {
                if( builder.length() != 0 ) part = " " + part;
                builder.append( part );
            }
            if( safeLoop <= 0 ) break;

        }

        if( useFormat ) event.setFormat( builder.toString() );
        else event.setMessage( builder.toString() );

        if( spoutListener != null ) spoutListener.passEvent( event, new HashSet<Player>( playerMap.values() ) );

    }

    String getPrefix( Player player )
    {
        if( permHandler != null ) return permHandler.getUserPrefix( player.getWorld().getName(), player.getName() );
        return "";
    }

    private final ChatAutoComplete plugin;
    private final int charPrefix;
    private final int maxReplace;
    private final ChatColor atSignColor;
    private final PermissionHandler permHandler;
    private final ChatAutoCompleteSpoutPlayerListener spoutListener;

}