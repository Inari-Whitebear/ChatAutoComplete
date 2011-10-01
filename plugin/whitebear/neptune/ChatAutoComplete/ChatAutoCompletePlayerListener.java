package plugin.whitebear.neptune.ChatAutoComplete;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class ChatAutoCompletePlayerListener extends PlayerListener
{

	public ChatAutoCompletePlayerListener( ChatAutoComplete cPlugin,
			int cCharPrefix, int cMaxReplace, String cColor )
	{
		plugin = cPlugin;
		charPrefix = cCharPrefix;
		maxReplace = cMaxReplace;
		color = ChatColor.getByCode( Integer.parseInt( cColor, 16 ) );

	}

	public void onPlayerChat( PlayerChatEvent event ) {
		if ( event.isCancelled() ) return;
		Player sender = event.getPlayer();
		if ( ! sender.hasPermission( "autocomp.autocomp" ) ) return;

		String msg = event.getMessage();
		int lastIndex = 0;
		int position = msg.indexOf( charPrefix, lastIndex );
		int safeLoop = maxReplace;

		while ( position != - 1 && safeLoop > 0 ) {
			lastIndex = position + 2;
			if ( lastIndex > msg.length() ) break;

			int nextSpace = msg.indexOf( ' ', position );
			String subName = msg.substring( position + 1,
					(nextSpace == - 1 ? msg.length() : nextSpace) );
			if ( subName.length() != 0 ) {
				Player player = plugin.getServer().getPlayer( subName );
				if ( player != null ) {
					plugin.consoleMsg( Pattern
							.quote( ((char) charPrefix + subName) ) );
					msg = msg.replaceAll(
							new StringBuilder( "[^a-zA-Z0-9_\\-" )
									.append( charPrefix ).append( "]" )
									.append( Pattern.quote( (subName) ) )
									.append( "\\b" ).toString(),
							new StringBuilder( ( (color == null ? "" : color
									.toString())) )
									.append( ((char) charPrefix) )
									.append( player.getName() )
									.append( ChatColor.WHITE.toString() )
									.toString() );

					event.setMessage( msg );
					safeLoop -- ;
				} else {

				}
				position = msg.indexOf( charPrefix, lastIndex );
			}
		}

	}

	ChatAutoComplete	plugin;
	int					charPrefix;
	int					maxReplace;
	ChatColor			color;

}