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

import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class ChatAutoComplete extends JavaPlugin
{

    @Override
    public void onDisable()
    {
        consoleMsg( "Disabled." );

    }

    @Override
    public void onEnable()
    {

        PluginDescriptionFile pluginDesc = getDescription();
        prefix = new StringBuilder( "[" ).append( pluginDesc.getName() )
                .append( " (" ).append( pluginDesc.getVersion() )
                .append( ")] " ).toString();
        mcLogger = Logger.getLogger( "Minecraft" );
        readConfig();

        if ( maxReplace <= 0 )
        {
            consoleMsg( "Can't replace // Max Relace Amount is set to 0 or lower." );
            super.getPluginLoader().disablePlugin( this );
            return;
        }

        playerListener = new ChatAutoCompletePlayerListener( this, chatPrefix,
                maxReplace, colorAt );

        PluginManager pgnMng = this.getServer().getPluginManager();

        pgnMng.registerEvent( Type.PLAYER_CHAT, playerListener,
                Priority.Normal, this );

        consoleMsg( "Enabled." );
    }

    private void readConfig()
    {
        Configuration config = this.getConfiguration();
        config.load();
        setDefaults( config );
        config.save();

        chatPrefix = config.getString( "chatPrefix" ).trim().charAt( 0 );
        maxReplace = config.getInt( "maxReplace", 10 );
        colorAt = config.getString( "color" );

    }

    private void setDefaults( Configuration config )
    {
        Map<String, Object> nodeMap = config.getAll();
        config.setHeader(
                "#ChatAutoComplete Config",
                "#chatPrefix = prefix to use before names so they get auto-completed",
                "#maxReplace = maximum unique names replaced in a single chat message",
                "#color = Color to put names in; use '-1' (in quotes) to disable." );
        if ( ! nodeMap.containsKey( "chatPrefix" ) )
        {
            config.setProperty( "chatPrefix", "@" );

        }
        if ( ! nodeMap.containsKey( "maxReplace" ) )
            config.setProperty( "maxReplace", 10 );
        if ( ! nodeMap.containsKey( "color" ) )
            config.setProperty( "color", "4" );
    }

    public void consoleMsg( String msg )
    {
        mcLogger.info( prefix + msg );

    }

    public ChatAutoComplete()
    {

    }

    ChatAutoCompletePlayerListener playerListener;
    String                         prefix = "";
    Logger                         mcLogger;
    int                            maxReplace;
    int                            chatPrefix;

    String                         colorAt;

}
