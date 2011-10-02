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

package plugin.whitebear.neptune.ChatAutoComplete;

import java.util.regex.Pattern;

import org.anjocaido.groupmanager.permissions.NijikoPermissionsProxy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class ChatAutoCompletePlayerListener extends PlayerListener
{

    public ChatAutoCompletePlayerListener( ChatAutoComplete cPlugin, int cCharPrefix, int cMaxReplace, String cColor,
            NijikoPermissionsProxy cEssentialsProxy )
    {
        plugin = cPlugin;
        charPrefix = cCharPrefix;
        maxReplace = cMaxReplace;
        // Convert color code to ChatColor
        atSignColor = ChatColor.getByCode( Integer.parseInt( cColor, 16 ) );
        essentialsProxy = cEssentialsProxy;
    }

    public void onPlayerChat( PlayerChatEvent event )
    {
        if( event.isCancelled() ) return;
        Player sender = event.getPlayer();
        if( !sender.hasPermission( "autocomp.autocomp" ) ) return;
        // Escape if cancelled or doesn't have permissions

        String msg = event.getMessage();
        int lastIndex = 0;
        int position = msg.indexOf( charPrefix, lastIndex );
        int safeLoop = maxReplace;
        // Loop until nothing else to replace or maximum replaces reached
        while ( position != -1 && safeLoop > 0 )
        {

            // Find next space after the @ and extract the name
            int nextSpace = msg.indexOf( ' ', position );
            String subName = msg.substring( position + 1, (nextSpace == -1 ? msg.length() : nextSpace) );

            if( subName.length() != 0 )
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
                    msg = msg.replaceAll( "(^|\\s)" + Pattern.quote( (char) charPrefix + (subName) ) + "($|\\s)", "$1"
                            + (atSignColor == null ? "" : atSignColor.toString()) + ((char) charPrefix) + prefix
                            + player.getName() + ChatColor.WHITE.toString() + "$2" );

                    event.setMessage( msg );
                    safeLoop--;
                }

            }
            // Skip @ and first letter so the same one isn't found again
            lastIndex = position + 2;
            // Exit if end of string
            if( lastIndex > msg.length() ) break;
            position = msg.indexOf( charPrefix, lastIndex );
        }

    }

    ChatAutoComplete       plugin;
    int                    charPrefix;
    int                    maxReplace;
    ChatColor              atSignColor;
    NijikoPermissionsProxy essentialsProxy;

}