/*package l2f.gameserver.handler.voicecommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.BatchStatement;

*//**
 *  @author claww
 *//*

public class DailyReward implements IVoicedCommandHandler, ScriptFile
{
	private static enum ValueType
	{
		ACCOUNT_NAME,
		IP_ADRESS,
		HWID
	}
	
	private static final String[] COMMANDS_LIST = new String[] { "login" };
	
	public static final Map<String, Long> _accountPenalties = new ConcurrentHashMap<String, Long>();
	public static final Map<String, Long> _ipPenalties = new ConcurrentHashMap<String, Long>();
	public static final Map<String, Long> _hwidPenalties = new ConcurrentHashMap<String, Long>();
	public static final Map<String, Long> _firstReward = new ConcurrentHashMap<String, Long>();
	
	private static final long DAILY_PENALTY = 24 * 60 * 60 * 1000L; // 12 Hours
	private static final long RESET_REWARD = 48 * 60 * 60 * 1000L; // 12 Hours
	private static final long SEVEN_DAYS_REWARD = 168 * 60 * 60 * 1000L; // 12 Hours
	private static final long SECOND_DAY_REWARD = 24 * 60 * 60 * 1000L;
	private static final long THIRD_DAY_REWARD = 48 * 60 * 60 * 1000L;
	private static final long FOURTH_DAY_REWARD = 72 * 60 * 60 * 1000L;
	
	private static int item1 = 6673;
	private static int item2 = 6673;
	private static int count1 = 57;
	private static int count2 = 60;
	private static float firstreward;
	private static float lastreward;
	// Bloody Coin - Reward
	private static final int[][] REWARD = {{ item1, count1 }, {item2, count2 }};
	
	public DailyReward()
	{
		// Restore from the db all the penalties of the votes, it doesn't matter if its 0. So we can do it only once at start
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM daily_system");

			rset = statement.executeQuery();
			while(rset.next())
			{
				final String value = rset.getString("value");
				final long time = rset.getLong("penalty_time");

				switch(rset.getInt("value_type"))
				{
				// Account Name
					case 0:
					{
						_accountPenalties.put(value, time);
						break;
					}
					// Ip Address
					case 1:
					{
						_ipPenalties.put(value, time);
						break;
					}
					// Hwid
					case 2:
					{
						_hwidPenalties.put(value, time);
						break;
					}
					// firstreward
					case 3:
					{
						_hwidPenalties.put(value, time);
						break;
					}
				}
			}
		}
		catch(Exception e)
		{}
	}
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String params)
	{
		if(command.equalsIgnoreCase("login"))
		{
			try
			{
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
		
				// Check the penalties of the player to see if he can vote again
				if(!checkPlayerPenalties(player, IPClient, HWID, true))
				{
					return false;
				}
				// Add the vote penalty to the player
				addNewPlayerPenalty(player, IPClient, HWID);
			
				if(System.currentTimeMillis() > lastreward + RESET_REWARD)
				{
					//giveRewards(player);
					item1 = 6673;
					count1 = 5;
					giveRewards(player);
				}
				else
				{
					giveRewards(player);
					player.sendMessage("You have received daily reward.");	
				}
				if(System.currentTimeMillis() > firstreward + SEVEN_DAYS_REWARD)
				{
					//giveRewards(player);
					item1 = 6673;
					count1 = 5;
					giveRewards(player);
				}
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
	    int[] item = { item1, count1 };
	    
	    if(item1 == 6673)
	    {
	    	firstreward = System.currentTimeMillis();
	    }
	    
		if(System.currentTimeMillis() > firstreward + SECOND_DAY_REWARD)
		{
			item1 = 57;
			count1 = 1;
		}
		else if (System.currentTimeMillis() > firstreward + THIRD_DAY_REWARD )
		{
			item1 = 57;
			count1 = 1;
		}
		else if (System.currentTimeMillis() > firstreward + FOURTH_DAY_REWARD)
		{
			item1 = 57;
			count1 = 1;
		}
		
	    if (item != null)
	    {
	    	Functions.addItem(player, item[item1], item[count1], "Daily Random Reward");
	    }	  
	    
	    lastreward = System.currentTimeMillis();
	}

	protected static void addNewPlayerPenalty(Player activeChar, String IPClient, String HWID)
	{
		final long newPenalty = System.currentTimeMillis() + DAILY_PENALTY;
		_accountPenalties.put(activeChar.getAccountName(), newPenalty);
		_ipPenalties.put(IPClient, newPenalty);
		_hwidPenalties.put(HWID, newPenalty);
		_firstReward.put(activeChar.getAccountName(), FIRST_REWARD);

		// Also store the penalties in the db
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = BatchStatement.createPreparedStatement(con, "REPLACE INTO daily_system(value_type, value, penalty_time) VALUES (?, ?, ?)");
			final String[] values = new String[] { activeChar.getAccountName(), IPClient, HWID };
			for(ValueType type : ValueType.values())
			{
				statement.setInt(1, type.ordinal());
				statement.setString(2, values[type.ordinal()]);
				statement.setLong(3, newPenalty);
				statement.addBatch();
			}

			statement.executeBatch();
		}
		catch(Exception e)
		{}
	}

	*//**
	 * @param player
	 * @param IPClient
	 * @param HwID
	 * @param sendMessage
	 * @return Returns true if the player doesn't have an active penalty after getting reward
	 *//*
	protected static boolean checkPlayerPenalties(Player activeChar, String IPClient, String HwID, boolean sendMessage)
	{
		final long accountPenalty = checkPenalty(ValueType.ACCOUNT_NAME, activeChar.getAccountName());
		final long ipPenalty = checkPenalty(ValueType.IP_ADRESS, IPClient);
		final long hwidPenalty = checkPenalty(ValueType.HWID, HwID);

		final int penalty = (int) ((Math.max(accountPenalty, Math.max(ipPenalty, hwidPenalty)) - System.currentTimeMillis()) / (60 * 1000L));

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
			return false;
		}
		return true;
	}
	
	protected static boolean checkRewardReset(Player activeChar, String IPClient, String HwID, boolean sendMessage)
	{
		final long accountPenalty = checkPenalty(ValueType.ACCOUNT_NAME, activeChar.getAccountName());
		final long ipPenalty = checkPenalty(ValueType.IP_ADRESS, IPClient);
		final long hwidPenalty = checkPenalty(ValueType.HWID, HwID);

		final int penalty = (int) ((Math.max(accountPenalty, Math.max(ipPenalty, hwidPenalty)) - System.currentTimeMillis()) / (60 * 1000L));

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
			return false;
		}
		return true;
	}

	*//**
	 * @param type
	 * @param value
	 * @return Returns the penalty of a particular type and value if it exists
	 *//*
	
	private static long checkPenalty(ValueType type, String value)
	{
		switch(type)
		{
			case ACCOUNT_NAME:
			{
				if(_accountPenalties.containsKey(value))
					return _accountPenalties.get(value);
				break;
			}
			case IP_ADRESS:
			{
				if(_ipPenalties.containsKey(value))
					return _ipPenalties.get(value);
				break;
			}
			case HWID:
			{
				if(_hwidPenalties.containsKey(value))
					return _hwidPenalties.get(value);
				break;
			}
		}

		return 0;
	}
	
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