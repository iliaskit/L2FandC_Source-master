package l2f.gameserver.network.clientpackets.security;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.clientpackets.L2GameClientPacket;
import l2f.gameserver.utils.Log;

/**
 * @author DkN
 */

public abstract class AbstractEnterWorldPacket extends L2GameClientPacket
{
	// flood protection for Bypasses to server
    public static boolean checkEnterWorldTime(Player player)
    {
        if (player.getLastEnterWorldTime() * 1000L + Config.ENTER_WORLD_FLOOD_PROECTION_IN_MS > System.currentTimeMillis())
        {
            player.kick();
            Log.logIllegalActivity("Player " + player + " tried to flood EnterWorld ");
            return false;
        } else 
        {
            return true;
        }

    }
}