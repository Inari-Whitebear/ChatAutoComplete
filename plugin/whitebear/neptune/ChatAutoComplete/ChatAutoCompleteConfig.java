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


import org.bukkit.util.config.Configuration;

import java.util.Map;

public class ChatAutoCompleteConfig
{

    public ChatAutoCompleteConfig( ChatAutoComplete instance )
    {
        plugin = instance;
        config = instance.getConfiguration();
    }

    public void loadConfig()
    {
        if( config == null ) return;
        config.load();
        setDefaults();
        saveConfig();
    }

    private void saveConfig()
    {
        if( config != null ) config.save();
    }

    private void setDefaults()
    {
        Map<String, Object> nodeMap = config.getAll();
        config.setHeader( "#ChatAutoComplete Config", "#chatPrefix = prefix to use before names so they get auto-completed", "#maxReplace = maximum unique names replaced in a single chat message", "#atSignColor = Color used for the @ sign; use '-1' (in quotes) to disable." + "#useEssentials = using essentials for name prefixing", "#useSpout = using spout for additional effects", "#spoutSound = if using spout, specify sound that should be played for the highlighted player (use 'NONE') for none" );

        if( !nodeMap.containsKey( "debug" ) ) config.setProperty( "debug", false );
        if( !nodeMap.containsKey( "chatPrefix" ) ) config.setProperty( "chatPrefix", "@" );
        if( !nodeMap.containsKey( "maxReplace" ) ) config.setProperty( "maxReplace", 10 );
        if( !nodeMap.containsKey( "atSignColor" ) ) config.setProperty( "atSignColor", "4" );
        if( !nodeMap.containsKey( "useEssentials" ) ) config.setProperty( "useEssentials", false );
        if( !nodeMap.containsKey( "useSpout" ) ) config.setProperty( "useSpout", false );
        if( !nodeMap.containsKey( "spoutSound" ) ) config.setProperty( "spoutSound", "NONE" );
        if( !nodeMap.containsKey( "useNotification" ) ) config.setProperty( "useNotification", false );
    }

    public String getChatPrefix()
    {
        return config.getString( "chatPrefix", "@" );
    }

    public int getMaxReplace()
    {
        return config.getInt( "maxReplace", 10 );
    }

    public String getAtSignColor()
    {
        return config.getString( "atSignColor", "4" );
    }

    public boolean getUseEssentials()
    {
        return config.getBoolean( "useEssentials", false );
    }

    public boolean getUseSpout()
    {
        return config.getBoolean( "useSpout", false );
    }

    public String getSpoutSound()
    {
        return config.getString( "spoutSound", "NONE" );
    }

    public boolean getUseNotification()
    {
        return config.getBoolean( "useNotification", false );
    }

    public boolean getDebug()
    {
        return config.getBoolean( "debug", false );
    }

    private final ChatAutoComplete plugin;
    private final Configuration config;

}
