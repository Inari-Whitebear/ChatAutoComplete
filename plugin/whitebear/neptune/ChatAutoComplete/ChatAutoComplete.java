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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.anjocaido.groupmanager.permissions.NijikoPermissionsProxy;

import com.nijikokun.bukkit.Permissions.Permissions;

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
        prefix = "[" + pluginDesc.getName() + " (" + pluginDesc.getVersion() + ")] ";
        mcLogger = Logger.getLogger( "Minecraft" );
        readConfig();

        if( maxReplace <= 0 )
        {
            consoleMsg( "Can't replace // Max Relace Amount is set to 0 or lower." );
            super.getPluginLoader().disablePlugin( this );
            return;
        }

        PluginManager pgnMng = this.getServer().getPluginManager();

        if( useEssentials )
        {
            Plugin essentialsBridge = pgnMng.getPlugin( "Permissions" );
            if( essentialsBridge != null && essentialsBridge.isEnabled() )
            {
                try
                {
                    essentialsProxy = (NijikoPermissionsProxy) ((Permissions) essentialsBridge).getHandler();
                    consoleMsg( "Using essentials" );
                }
                catch(NoClassDefFoundError exception)
                {
                    essentialsProxy = null;
                }
            }
        }

        playerListener = new ChatAutoCompletePlayerListener( this, chatPrefix, maxReplace, atSignColor, essentialsProxy );

        pgnMng.registerEvent( Type.PLAYER_CHAT, playerListener, Priority.High, this );

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
        atSignColor = config.getString( "atSignColor" );
        useEssentials = config.getBoolean( "useEssentials", false );

    }

    private void setDefaults( Configuration config )
    {
        Map<String, Object> nodeMap = config.getAll();
        config.setHeader( "#ChatAutoComplete Config",
                "#chatPrefix = prefix to use before names so they get auto-completed",
                "#maxReplace = maximum unique names replaced in a single chat message",
                "#atSignColor = Color used for the @ sign; use '-1' (in quotes) to disable."
                        + "#useEssentials = using essentials for name prefixing" );

        if( !nodeMap.containsKey( "chatPrefix" ) ) config.setProperty( "chatPrefix", "@" );
        if( !nodeMap.containsKey( "maxReplace" ) ) config.setProperty( "maxReplace", 10 );
        if( !nodeMap.containsKey( "atSignColor" ) ) config.setProperty( "atSignColor", "4" );
        if( !nodeMap.containsKey( "useEssentials" ) ) config.setProperty( "useEssentials", false );
    }

    public void consoleMsg( String msg )
    {
        mcLogger.info( prefix + msg );

    }

    public ChatAutoComplete()
    {

    }

    ChatAutoCompletePlayerListener playerListener;
    String                         prefix          = "";
    Logger                         mcLogger;
    int                            maxReplace;
    int                            chatPrefix;
    String                         atSignColor;
    NijikoPermissionsProxy         essentialsProxy = null;
    boolean                        useEssentials;

}
