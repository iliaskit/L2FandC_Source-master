package l2f.gameserver.listener.actor.player;

import l2f.gameserver.listener.PlayerListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.Location;

public interface OnMoveListener extends PlayerListener
{
	void onMove(Player actor, Location tPos);
}
