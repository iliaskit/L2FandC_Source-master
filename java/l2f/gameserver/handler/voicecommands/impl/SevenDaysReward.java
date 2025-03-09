/*package l2f.gameserver.handler.voicecommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

*//**
 *  @author claww
 *//*

public class SevenDaysReward implements IVoicedCommandHandler, ScriptFile
{
	private static final String[] COMMANDS_LIST = new String[] { "login" };
	
	private static final long REWARD_24H = 24 * 60 * 60 * 1000L;
	private static final long REWARD_RESET = 48 * 60 * 60 * 1000L;
	private static int _count;
	// Bloody Coin - Reward
	private static final int[] REWARD_DAY0 = { 6673, 1 };
	private static final int[] REWARD_DAY1 = { 57, 2 };
	private static final int[] REWARD_DAY2 = { 6689, 3 };
	private static final int[] REWARD_DAY3 = { 6673, 4 };
	private static final int[] REWARD_DAY4 = { 6673, 5 };
	private static final int[] REWARD_DAY5 = { 6673, 6};
	private static final int[] REWARD_DAY6 = { 6673, 7 };
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String params)
	{
		if(command.equalsIgnoreCase("login"))
		{
			try
			{
				System.out.println("first count= " + _count);
				// No connection, no reward
				if(player.getNetConnection() == null)
					return false;
				
				// Getting IP of client, here we will have to check for HWID when we have LAMEGUARD
				final String IPClient = player.getIP();
				final String HWID = (player.getHWID() != null ? player.getHWID() : "");

				// No connection, no reward
				if(player.getNetConnection() == null)
				{
					return false;
				}
				
				// Min lvl 40
				if(player.getLevel() < 40)
				{
					player.sendMessage("You need to be at least level 40 to use receive daily reward.");
					return false;
				}
				if(!checkPenaltyReset(player, IPClient, HWID, true))
				{
					return false;
				}
				
				long lastReward = System.currentTimeMillis();
				
				// Add the vote penalty to the player
				addNewPlayerPenalty(player, IPClient, HWID, lastReward, _count);
				System.out.println("after add penalty count= " + _count);
				// Give the rewards
				giveRewards(player);
				System.out.println("After give reward count= " + _count);
				player.sendMessage("You have received daily reward.");	
				increaseDay(player, IPClient, HWID, _count);
				System.out.println("After increaseDay count= " + _count);
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
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
	    	Functions.addItem(player, item[0], item[1], "Daily Random Reward");
	    }
	    

	}
	
	*//**
	 * @param player
	 * @param IPClient
	 * @param HwID
	 * @param sendMessage
	 * @return Returns true if the player doesn't have an active penalty after getting reward
	 *//*

	protected static boolean checkPenaltyReset(Player activeChar, String IPClient, String HwID, boolean sendMessage)
	{
		long lastReward = 0;
		try (Connection con = DatabaseFactory.getInstance().getConnection();
	    PreparedStatement statement = con.prepareStatement("SELECT date,count FROM daily_system WHERE account=? OR ip=? OR hwid=?"))
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
			System.out.println("Reset count");
			_count = 0;
		}
		
		System.out.println("After getCount from DB count= " + _count);
		
		System.out.println("check reset: " + System.currentTimeMillis() + " > " + lastReward +" + "+ REWARD_RESET );
		if(System.currentTimeMillis() > lastReward + REWARD_RESET)
		{
			_count=0;
			System.out.println("reset it: " + System.currentTimeMillis() + " > " + lastReward +" + "+ REWARD_RESET );
			try (Connection con = DatabaseFactory.getInstance().getConnection();
		             PreparedStatement statement = con.prepareStatement("UPDATE daily_system SET count=? WHERE account=? OR ip=? OR hwid=?")) {
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
			final int penalty = (int) (lastReward - System.currentTimeMillis() / (60 * 1000L));
			System.out.println("Penalty= : " + penalty);
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
	             PreparedStatement statement = con.prepareStatement("REPLACE INTO daily_system(account, ip, hwid, date, count) VALUES (?, ?, ?, ?, ?)")) {
					statement.setString(1, activeChar.getAccountName());
					statement.setString(2, IPClient);
					statement.setString(3, HWID);
					statement.setLong(4, lastReward);
					statement.setInt(5, count);
					statement.execute();
	        } catch (SQLException e) {
	            _log.error("Error could not store dailyreward!", e);
	        }
	}
	
	protected int getCount(Player activeChar, String IPClient, String HwID)
	{
		System.out.println("In function getCount count= " + _count);
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			    PreparedStatement statement = con.prepareStatement("SELECT date,count FROM daily_system WHERE account=? OR ip=? OR hwid=?"))
				{
					statement.setString(1, activeChar.getAccountName());
					statement.setString(2, IPClient);
					statement.setString(3, HwID);
					try (ResultSet rset = statement.executeQuery())
					{
						if (rset.next())
						{
							_count = rset.getInt("count");
						}
					}
				}
				catch (SQLException e)
				{
					_log.error("error loading dailyreward penalty: ", e);
				}
				System.out.println("After getCount from DB count= " + _count);
		
				if(_count >= 8)
				{
					_count = 1;
				}
				System.out.println("Return count= " + _count);
				return _count;
	}
	
	protected static void increaseDay(Player activeChar, String IPClient, String HWID, int count)
	{
		 System.out.println("Count before increase: " +count);
		 count++;
		 System.out.println("Count increaseday: " + count);
		 try (Connection con = DatabaseFactory.getInstance().getConnection();
	             PreparedStatement statement = con.prepareStatement("UPDATE daily_system SET count=? WHERE account=?")) {
			 		statement.setInt(1, count);
					statement.setString(2, activeChar.getAccountName());
					statement.execute();
	        } catch (SQLException e) {
	            _log.error("Error could not update count reward!", e);
	        }
	}
		
	*//**
	 * @param type
	 * @param value
	 * @return Returns the penalty of a particular type and value if it exists
	 *//*
	
	@Override
	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS_LIST;
	}
	
	public static String getItemName(int itemId)
	{
		return ItemHolder.getInstance().getTemplate(itemId).getName();
	}
	
}*/