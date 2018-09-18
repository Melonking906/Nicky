package io.loyloy.nicky;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class Utils
{
    public static String translateColors( String text, OfflinePlayer player )
    {
        StringBuilder colorsToTranslate = new StringBuilder();

        for( ChatColor color : ChatColor.values() )
        {
            if( Nicky.getVaultPerms().playerHas( null, player, "nicky.color." + color.toString().substring( 1 ) ) || color.toString().substring( 1 ).equals( "r" ) )
            {
                colorsToTranslate.append( color.getChar() );
            }
        }

        text = text.replace( ChatColor.COLOR_CHAR, '&' ); // Remove player added hard codes;

        char[] b = text.toCharArray();
        for( int i = 0; i < b.length - 1; i++ )
        {
            if( b[i] == '&' && colorsToTranslate.toString().indexOf( b[i + 1] ) > -1 )
            {
                b[i] = ChatColor.COLOR_CHAR;
                b[i + 1] = Character.toLowerCase( b[i + 1] );
            }
        }
        return new String( b );
    }
}
