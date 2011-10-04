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


    public void onDisable()
    {
        consoleMsg( "Disabled." );

    }

    public void onEnable()
    {

        PluginDescriptionFile pluginDesc = getDescription();
        prefix = "[" + pluginDesc.getName() + " (" + pluginDesc.getVersion() + ")] ";
        mcLogger = Logger.getLogger( "Minecraft" );
        config = new ChatAutoCompleteConfig( this );
        config.loadConfig();

        if( config.getMaxReplace() <= 0 )
        {
            consoleMsg( "Can't replace // Max Relace Amount is set to 0 or lower." );
            super.getPluginLoader().disablePlugin( this );
            return;
        }

        PluginManager pgnMng = this.getServer().getPluginManager();

        if( config.getUseEssentials() )
        {
            Plugin essentialsBridge = pgnMng.getPlugin( "Permissions" );
            if( essentialsBridge != null && essentialsBridge.isEnabled() )
            {
                try
                {
                    essentialsProxy = ( NijikoPermissionsProxy ) ( ( Permissions ) essentialsBridge ).getHandler();
                    consoleMsg( "Using essentials" );
                } catch( NoClassDefFoundError exception )
                {
                    essentialsProxy = null;
                }
            }
        }

        if( config.getUseSpout() )
        {
            Plugin spout = pgnMng.getPlugin( "Spout" );
            if( spout == null ) useSpout = false;
            else consoleMsg( "Using spout." );
        }

        playerListener = new ChatAutoCompletePlayerListener( this, config, essentialsProxy, useSpout );

        pgnMng.registerEvent( Type.PLAYER_CHAT, playerListener, Priority.Highest, this );

        consoleMsg( "Enabled." );
    }


    public void consoleMsg( String msg )
    {
        mcLogger.info( prefix + msg );

    }

    public ChatAutoComplete()
    {

    }

    public ChatAutoCompleteConfig getConfig()
    {
        return config;
    }

    String prefix = "";
    Logger mcLogger;
    boolean useEssentials = false;
    boolean useSpout = false;

    ChatAutoCompletePlayerListener playerListener;
    ChatAutoCompleteConfig config;

    NijikoPermissionsProxy essentialsProxy = null;





}
