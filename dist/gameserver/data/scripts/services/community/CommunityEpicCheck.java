package services.community;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.scripts.ScriptFile;

/**
 *
 * @author claw
 */
public class CommunityEpicCheck implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityEpicCheck.class);
	
	/* (non-Javadoc)
	 * @see l2f.gameserver.handler.bbs.ICommunityBoardHandler#getBypassCommands()
	 */
	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbsepiccheck"
		};
	}

	/* (non-Javadoc)
	 * @see l2f.gameserver.handler.bbs.ICommunityBoardHandler#onBypassCommand(l2f.gameserver.model.Player, java.lang.String)
	 */
	@Override
	public void onBypassCommand(Player player, String bypass)
	{		
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/epic_check.htm", player);
		StringTokenizer st = new StringTokenizer(bypass, "_");
		st.nextToken();
		String type = (st.hasMoreTokens() ? st.nextToken() : "0");
		
		switch(type)
		{
			case "0":
				html = html.replace("%information%", "<tr><td></td><td></td><td align=center>NO INFO</td><td></td></tr>");
				html = html.replace("%item_name%", "No Item");
				ShowBoard.separateAndSend(html, player);
				break;
				
			case "6658":
				html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/epic_control.htm", player);
				html = html.replace("%information%", fillInformation("6658"));
				html = html.replace("%item_name%", "Ring of Baium");
				ShowBoard.separateAndSend(html, player);
				break;
				
			case "10314":
				html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/epic_control.htm", player);
				html = html.replace("%information%", fillInformation("10314"));
				html = html.replace("%item_name%", "Beleth's Ring");
				ShowBoard.separateAndSend(html, player);
				break;	
				
			case "21712":
				html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/epic_control.htm", player);
				html = html.replace("%information%", fillInformation("21712"));
				html = html.replace("%item_name%", "Blessed Earring of Zaken");
				ShowBoard.separateAndSend(html, player);
				break;
				
			case "6656":
				html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/epic_control.htm", player);
				html = html.replace("%information%", fillInformation("6656"));
				html = html.replace("%item_name%", "Earring of Antharas");
				ShowBoard.separateAndSend(html, player);
				break;
				
			case "6657":
				html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/epic_control.htm", player);
				html = html.replace("%information%", fillInformation("6657"));
				html = html.replace("%item_name%", "Necklace of Valakas");
				ShowBoard.separateAndSend(html, player);
				break;
				
			case "6660":
				html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/epic_control.htm", player);
				html = html.replace("%information%", fillInformation("6660"));
				html = html.replace("%item_name%", "Ring of Queen Ant");
				ShowBoard.separateAndSend(html, player);
				break;
				
			case "6661":
				html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/epic_control.htm", player);
				html = html.replace("%information%", fillInformation("6661"));
				html = html.replace("%item_name%", "Earring of Orfen");
				ShowBoard.separateAndSend(html, player);
				break;
				
			case "6662":
				html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/epic_control.htm", player);
				html = html.replace("%information%", fillInformation("6662"));
				html = html.replace("%item_name%", "Ring of Core");
				ShowBoard.separateAndSend(html, player);
				break;
		}
	}

	/* (non-Javadoc)
	 * @see l2f.gameserver.handler.bbs.ICommunityBoardHandler#onWriteCommand(l2f.gameserver.model.Player, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}

	/* (non-Javadoc)
	 * @see l2f.gameserver.scripts.ScriptFile#onLoad()
	 */
	@Override
	public void onLoad()
	{
		_log.info("CommunityBoard: Epics control service loaded.");
		CommunityBoardManager.getInstance().registerHandler(this);
	}

	/* (non-Javadoc)
	 * @see l2f.gameserver.scripts.ScriptFile#onReload()
	 */
	@Override
	public void onReload()
	{
		CommunityBoardManager.getInstance().removeHandler(this);
	}

	/* (non-Javadoc)
	 * @see l2f.gameserver.scripts.ScriptFile#onShutdown()
	 */
	@Override
	public void onShutdown()
	{
	}
	
	private String fillInformation(String itemId)
	{
		String string = "";
		String playerClan = "";
		String itemLocation = "";
		for(Player player : GameObjectsStorage.getAllPlayersCopy())
		{
			ItemInstance whItem = player.getWarehouse().getItemByItemId(Integer.parseInt(itemId));
			
			if (player.getClan() != null) {
				playerClan = player.getClan().getName();
			}
			else
				playerClan = "No Clan";
			
			for(ItemInstance itemas : player.getInventory().getItems())
			{
				if(itemas.getItemId() == Integer.parseInt(itemId)){
					if(itemas.getLocName() == "PAPERDOLL")
						itemLocation = "EQUIPPED";
					else
						itemLocation = itemas.getLocName();
					
					string = string + "<tr><td>" + player.getName() + "</td><td>" + itemas.getCount() +"</td><td>" + itemas.getEnchantLevel() +"</td><td>" + itemLocation +"</td><td>" + playerClan +"</td></tr>";
				}
			}
			
			if(whItem != null){
				string = string + "<tr><td>" + player.getName() + "</td><td>" + whItem.getCount() +"</td><td>" + whItem.getEnchantLevel() +"</td><td>" + whItem.getLocName() +"</td><td>" + playerClan +"</td></tr>";
			}
		}
		
		if(string == "")
			string = "<tr><td></td><td></td><td align=center>NO INFO</td><td></td></tr>";
		
		return string;
	}
}
