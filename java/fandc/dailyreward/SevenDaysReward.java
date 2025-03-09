package fandc.dailyreward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.scripts.Functions;

/**
 *  @author claw
 */

public class SevenDaysReward extends Functions
{
	private static final Logger _log = LoggerFactory.getLogger(Functions.class);
	private static final long REWARD_24H = 24 * 60 * 60 * 1000L;
	private static final long REWARD_RESET = 48 * 60 * 60 * 1000L;
	private static int _count;
	private static final int[] REWARD_DAY0 = { Config.REWARD_DAY1_ID, Config.REWARD_DAY1_COUNT };
	private static final int[] REWARD_DAY1 = { Config.REWARD_DAY2_ID, Config.REWARD_DAY2_COUNT };
	private static final int[] REWARD_DAY2 = { Config.REWARD_DAY3_ID, Config.REWARD_DAY3_COUNT };
	private static final int[] REWARD_DAY3 = { Config.REWARD_DAY4_ID, Config.REWARD_DAY4_COUNT };
	private static final int[] REWARD_DAY4 = { Config.REWARD_DAY5_ID, Config.REWARD_DAY5_COUNT };
	private static final int[] REWARD_DAY5 = { Config.REWARD_DAY6_ID, Config.REWARD_DAY6_COUNT };
	private static final int[] REWARD_DAY6 = { Config.REWARD_DAY7_ID, Config.REWARD_DAY7_COUNT };
	
	public static final Map<Integer, Long> _sevendaysReuseObj = new ConcurrentHashMap<>();
	public static final Map<String, Long> _sevendaysReuseIP = new ConcurrentHashMap<>();
	public static final Map<String, Long> _sevendaysReuseHWID = new ConcurrentHashMap<>();
	
    
	public static void reward(Player player)
	{				
		if(!Config.ENABLE_SEVEN_DAYS_REWARD)
		{
			return;
		}
		// No connection, no reward
		if(player.getNetConnection() == null)
			return;
		
		// Getting IP of client, here we will have to check for HWID when we have LAMEGUARD
		final String IPClient = player.getIP();
		final String HWID = (player.getHWID() != null ? player.getHWID() : "");
	
		// No connection, no reward
		if(player.getNetConnection() == null)
		{
			return;
		}
		
		// Min lvl 40
		if(player.getLevel() < 40)
		{
			player.sendMessage("You need to be at least level 40 to use receive daily reward.");
			return;
		}
		
		final long currentTime = System.currentTimeMillis();
		
		// Check reuse per ObjID, IP, HWID since loading from DB is not accurate.
		if ((player.getAccessLevel() < 1 && _sevendaysReuseObj.containsKey(player.getObjectId())))
		{
			if (_sevendaysReuseObj.get(player.getObjectId()) > currentTime)
			{
				long reuseObj = (_sevendaysReuseObj.get(player.getObjectId()) - currentTime) / 1000;
				_log.info("SevenDaysReuse Obj, Name: " + player.getName() + ", Reuse: " + reuseObj);
				return;
			}
		}
		
		else if(player.getAccessLevel() < 1 && _sevendaysReuseIP.containsKey(player.getIP()))
		{
			if (_sevendaysReuseIP.get(player.getIP()) > currentTime)
			{
				long reuseIP = (_sevendaysReuseIP.get(player.getIP()) - currentTime) / 1000;
				_log.info("SevenDaysReuse IP, Name: " + player.getName() + ", Reuse: " + reuseIP);
				return;
			}
		}
		
		else if(player.getAccessLevel() < 1 && _sevendaysReuseHWID.containsKey(player.getHWID()))
		{
			if (_sevendaysReuseHWID.get(player.getHWID()) > currentTime)
			{
				long reuseHWID = (_sevendaysReuseIP.get(player.getHWID()) - currentTime) / 1000;
				_log.info("SevenDaysReuse HWID, Name: " + player.getName() + ", Reuse: " + reuseHWID);
				return;
			}
		}
		
		if(!checkPenaltyReset(player, IPClient, HWID, true))
		{
			return;
		}
		
		long lastReward = System.currentTimeMillis();
		
		// Add the vote penalty to the player
		addNewPlayerPenalty(player, IPClient, HWID, lastReward, _count);
	
		// Give the rewards
		giveRewards(player);
		player.sendMessage("You have received daily reward.");	
		increaseDay(player, IPClient, HWID, _count);
	}
	
	protected static void giveRewards(Player player)
	{
		int[] item = REWARD_DAY0;
		switch(_count)
		{
			case 0:
			{
				item = REWARD_DAY0;
				break;
			}
			case 1:
			{
				item = REWARD_DAY1;
				break;
			}
			case 2:
			{
				item = REWARD_DAY2;
				break;
			}
			case 3:
			{
				item = REWARD_DAY3;
				break;
			}
			case 4:
			{
				item = REWARD_DAY4;
				break;
			}
			case 5:
			{
				item = REWARD_DAY5;
				break;
			}
			case 6:
			{
				item = REWARD_DAY6;
				break;
			}
		}
		
		if (item != null)
	    {
	    	Functions.addItem(player, item[0], item[1], "SevenDays Reward Day: "+ _count);
	    }
	    

	}
	
	/**
	 * @param player
	 * @param IPClient
	 * @param HwID
	 * @param sendMessage
	 * @return Returns true if the player doesn't have an active penalty after getting reward
	 */

	protected static boolean checkPenaltyReset(Player activeChar, String IPClient, String HwID, boolean sendMessage)
	{
		long lastReward = 0;
		try (Connection con = DatabaseFactory.getInstance().getConnection();
	    PreparedStatement statement = con.prepareStatement("SELECT date,count FROM sevendays_reward WHERE account=? OR ip=? OR hwid=?"))
		{
			statement.setString(1, activeChar.getAccountName());
			statement.setString(2, IPClient);
			statement.setString(3, HwID);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
				    lastReward = rset.getLong("date");
				    _count = rset.getInt("count");
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("error loading dailyreward penalty: ", e);
		}
		
		if(_count >= 7)
		{
			_count = 0;
		}
	
		if(System.currentTimeMillis() > lastReward + REWARD_RESET)
		{
			_count=0;
			try (Connection con = DatabaseFactory.getInstance().getConnection();
		             PreparedStatement statement = con.prepareStatement("UPDATE sevendays_reward SET count=? WHERE account=? OR ip=? OR hwid=?")) {
						statement.setInt(1, _count);
						statement.setString(2, activeChar.getAccountName());
						statement.setString(3, IPClient);
						statement.setString(4, HwID);
						statement.execute();
		        } catch (SQLException e) {
		            _log.error("Error could not reset dailyreward count!", e);
		        }
		}
		
		if(System.currentTimeMillis() < lastReward + REWARD_24H)
		{
			final int penalty = (int) (((lastReward + REWARD_24H) - System.currentTimeMillis()) / (60 * 1000L));
			
			if(penalty > 0)
			{
				if(sendMessage)
				{
					if(penalty > 60)
					{
						activeChar.sendMessage("Daily reward in:  " + (penalty / 60) + " hours " + (penalty % 60) + " minutes.");
					}
					else
					{
						activeChar.sendMessage("Daily reward in:  " + penalty + " minutes.");
					}
				}
			}
			return false;
		}
		
		if(System.currentTimeMillis() > lastReward + REWARD_24H && System.currentTimeMillis() < lastReward + REWARD_RESET)
		{
			return true;
		}		
		return true;
	}
	

	protected static void addNewPlayerPenalty(Player activeChar, String IPClient, String HWID, long lastReward, int count)
	{
		 
		 try (Connection con = DatabaseFactory.getInstance().getConnection();
	             PreparedStatement statement = con.prepareStatement("REPLACE INTO sevendays_reward(account, ip, hwid, date, count) VALUES (?, ?, ?, ?, ?)")) {
					statement.setString(1, activeChar.getAccountName());
					statement.setString(2, IPClient);
					statement.setString(3, HWID);
					statement.setLong(4, lastReward);
					statement.setInt(5, count);
					statement.execute();
	        } catch (SQLException e) {
	            _log.error("Error could not store dailyreward!", e);
	        }
		 
		 _sevendaysReuseObj.put(activeChar.getObjectId(), lastReward + REWARD_24H);
		 _sevendaysReuseIP.put(activeChar.getIP(), lastReward + REWARD_24H);
		 _sevendaysReuseHWID.put(activeChar.getHWID(), lastReward + REWARD_24H);
	}
	
	protected static void increaseDay(Player activeChar, String IPClient, String HWID, int count)
	{
		 count++;
		 try (Connection con = DatabaseFactory.getInstance().getConnection();
	             PreparedStatement statement = con.prepareStatement("UPDATE sevendays_reward SET count=? WHERE account=?")) {
			 		statement.setInt(1, count);
					statement.setString(2, activeChar.getAccountName());
					statement.execute();
	        } catch (SQLException e) {
	            _log.error("Error could not update count reward!", e);
	        }
	}
	
	public static boolean showWindow(Player activeChar, String IPClient, String HwID)
	{
		long lastReward = 0;
		try (Connection con = DatabaseFactory.getInstance().getConnection();
	    PreparedStatement statement = con.prepareStatement("SELECT date,count FROM sevendays_reward WHERE account=? OR ip=? OR hwid=?"))
		{
			statement.setString(1, activeChar.getAccountName());
			statement.setString(2, IPClient);
			statement.setString(3, HwID);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
				    lastReward = rset.getLong("date");
				    _count = rset.getInt("count");
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("error loading dailyreward penalty: ", e);
		}
		
		if(System.currentTimeMillis() < lastReward + REWARD_24H)
		{
			return false;
		}
		return true;
	}
	
	public static String getItemName(int itemId)
	{
		return ItemHolder.getInstance().getTemplate(itemId).getName();
	}
	
}