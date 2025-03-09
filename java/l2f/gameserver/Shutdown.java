package l2f.gameserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Mauricio - license 
import fandc.streaming.StreamDatabaseHandler;
import l2f.commons.net.nio.impl.SelectorThread;
import l2f.commons.time.cron.SchedulingPattern;
import l2f.commons.time.cron.SchedulingPattern.InvalidPatternException;
import l2f.gameserver.dao.DatabaseBackupManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.database.ForumDatabaseFactory;
import l2f.gameserver.database.merge.MergeDatabaseFactory;
import l2f.gameserver.hwid.HwidEngine;
import l2f.gameserver.instancemanager.CoupleManager;
import l2f.gameserver.instancemanager.CursedWeaponsManager;
import l2f.gameserver.instancemanager.games.FishingChampionShipManager;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Hero;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2f.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2f.gameserver.network.GameClient;
import l2f.gameserver.network.loginservercon.AuthServerCommunication;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.scripts.Scripts;
import l2f.gameserver.utils.Util;

public class Shutdown extends Thread
{
	private static final Logger _log = LoggerFactory.getLogger(Shutdown.class);

	private static final Shutdown _instance = new Shutdown();
	private Timer counter;
	private ShutdownMode shutdownMode;
	private boolean makeBackup;
	private int shutdownCounter;

	public static final Shutdown getInstance()
	{
		return Shutdown._instance;
	}

	private Shutdown()
	{
		setName(getClass().getSimpleName());
		setDaemon(true);
		shutdownMode = ShutdownMode.NONE;
	}

	/**
	 * Ð’Ñ€ÐµÐ¼Ñ� Ð² Ñ�ÐµÐºÑƒÐ½Ð´Ð°Ñ… Ð´Ð¾ Ð¾Ñ‚ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ñ�.
	 *
	 * @return Ð²Ñ€ÐµÐ¼Ñ� Ð² Ñ�ÐµÐºÑƒÐ½Ð´Ð°Ñ… Ð´Ð¾ Ð¾Ñ‚ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ñ� Ñ�ÐµÑ€Ð²ÐµÑ€Ð°, -1 ÐµÑ�Ð»Ð¸ Ð¾Ñ‚ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ðµ Ð½Ðµ Ð·Ð°Ð¿Ð»Ð°Ð½Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¾
	 */
	public int getSeconds()
	{
		return shutdownMode == ShutdownMode.NONE ? -1 : shutdownCounter;
	}

	/**
	 * Ð ÐµÐ¶Ð¸Ð¼ Ð¾Ñ‚ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ñ�.
	 *
	 * @return <code>SHUTDOWN</code> Ð¸Ð»Ð¸ <code>RESTART</code>, Ð»Ð¸Ð±Ð¾ <code>NONE</code>, ÐµÑ�Ð»Ð¸ Ð¾Ñ‚ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ðµ Ð½Ðµ Ð·Ð°Ð¿Ð»Ð°Ð½Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¾.
	 */
	public ShutdownMode getMode()
	{
		return shutdownMode;
	}

	/**
	 * Ð—Ð°Ð¿Ð»Ð°Ð½Ð¸Ñ€Ð¾Ð²Ð°Ñ‚ÑŒ Ð¾Ñ‚ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ðµ Ñ�ÐµÑ€Ð²ÐµÑ€Ð° Ñ‡ÐµÑ€ÐµÐ· Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»ÐµÐ½Ð½Ñ‹Ð¹ Ð¿Ñ€Ð¾Ð¼ÐµÐ¶ÑƒÑ‚Ð¾Ðº Ð²Ñ€ÐµÐ¼ÐµÐ½Ð¸.
	 *
	 * @param seconds Ð²Ñ€ÐµÐ¼Ñ� Ð² Ñ„Ð¾Ñ€Ð¼Ð°Ñ‚Ðµ <code>hh:mm</code>
	 * @param shutdownMode  <code>SHUTDOWN</code> Ð¸Ð»Ð¸ <code>RESTART</code>
	 * @param makeBackup
	 */
	public synchronized void schedule(int seconds, ShutdownMode shutdownMode, boolean makeBackup)
	{
		if (seconds < 0)
			return;

		if (counter != null)
			counter.cancel();

		this.shutdownMode = shutdownMode;
		this.shutdownCounter = seconds;

		shutdownCounter = seconds;
		_log.info("Scheduled server " + (shutdownMode == ShutdownMode.SHUTDOWN ? "shutdown" : "restart") + " in " + Util.formatTime(seconds) + ".");
		(counter = new Timer("ShutdownCounter", true)).scheduleAtFixedRate(new ShutdownCounter(), 0L, 1000L);
		GameServer.getInstance().getListeners().onShutdownScheduled();
	}

	/**
	 * Ð—Ð°Ð¿Ð»Ð°Ð½Ð¸Ñ€Ð¾Ð²Ð°Ñ‚ÑŒ Ð¾Ñ‚ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ðµ Ñ�ÐµÑ€Ð²ÐµÑ€Ð° Ð½Ð° Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»ÐµÐ½Ð½Ð¾Ðµ Ð²Ñ€ÐµÐ¼Ñ�.
	 *
	 * @param time Ð²Ñ€ÐµÐ¼Ñ� Ð² Ñ„Ð¾Ñ€Ð¼Ð°Ñ‚Ðµ cron
	 * @param shutdownMode <code>SHUTDOWN</code> Ð¸Ð»Ð¸ <code>RESTART</code>
	 * @param makeBackup
	 */
	public void schedule(String time, ShutdownMode shutdownMode, boolean makeBackup)
	{
		SchedulingPattern cronTime;
		try
		{
			cronTime = new SchedulingPattern(time);
		}
		catch (InvalidPatternException e)
		{
			return;
		}

		int seconds = (int)(cronTime.next(System.currentTimeMillis()) / 1000L - System.currentTimeMillis() / 1000L);
		schedule(seconds, shutdownMode, makeBackup);
	}

	/**
	 * Cancel a scheduled shutdown.
	 */
	public synchronized void cancel()
	{
		final ShutdownMode oldMode = shutdownMode;
		final int cancelledOnSecond = shutdownCounter;
		shutdownMode = ShutdownMode.NONE;
		if (counter != null)
			counter.cancel();
		counter = null;
		Announcements.getInstance().announceToAll("Server aborts restart and continues normal operation!");
		_log.warn("Server issued shutdown  " + (ShutdownMode.SHUTDOWN != null ? "shutdown" : "restart") + " has been ABORTED!");
		if (oldMode != ShutdownMode.NONE)
			GameServer.getInstance().getListeners().onAbortShutdown(oldMode, cancelledOnSecond);
	}

	@Override
	public void run()
	{
		_log.info("Shutting down LS/GS communication...");
		AuthServerCommunication.getInstance().shutdown();

		_log.info("Shutting down scripts...");
		Scripts.getInstance().shutdown();

		_log.info("Disconnecting players...");
		disconnectAllPlayers();

		_log.info("Saving data...");
		saveData();

		_log.info("Deleting Void Items...");
		removeVoidItems();

		_log.info("Listeners...");
		GameServer.getInstance().getListeners().onShutdown(shutdownMode);

		try
		{
			_log.info("Shutting down thread pool...");
			ThreadPoolManager.getInstance().shutdown();
		}
		catch (InterruptedException e)
		{
			_log.error("Shut down Interrupted ", e);
		}

		_log.info("Shutting down selector...");
		if (GameServer.getInstance() != null)
			for (SelectorThread<GameClient> st : GameServer.getInstance().getSelectorThreads())
				try
				{
					st.shutdown();
				}
				catch (RuntimeException e)
				{
					_log.error("Error on shut down! ", e);
				}

		if (makeBackup)
		{
			_log.info("Making Backup!");
			DatabaseBackupManager.getInstance().doBackup(true);
		}

		try
		{
			_log.info("Shutting down database communication...");
			DatabaseFactory.getInstance().shutdown();
			MergeDatabaseFactory.getInstance().shutdown();
			ForumDatabaseFactory.getInstance().shutdown();
		}
		catch (SQLException e)
		{
			_log.error("Error while closing DatabaseFactory! ", e);
		}
		catch (Exception e)
		{
			_log.error("Exception while closing DatabaseFactory! ", e);
		}

		//Log.logServerShutdown();
		_log.info("Shutdown finished.");
	}

	private void saveData()
	{
		try
		{
			// Seven Signs data is now saved along with Festival data.
			if (!SevenSigns.getInstance().isSealValidationPeriod())
			{
				SevenSignsFestival.getInstance().saveFestivalData(false);
				_log.info("SevenSignsFestival: Data saved.");
			}
		}
		catch (RuntimeException e)
		{
			_log.error("Error while saving Seven Signs Period! ", e);
		}
		//ItemLogList.getInstance().saveAllLogs();
		try
		{
			SevenSigns.getInstance().saveSevenSignsData(0, true);
			_log.info("SevenSigns: Data saved.");
		}
		catch (RuntimeException e)
		{
			_log.error("Error while saving Seven Signs Data! ", e);
		}

		if (Config.ENABLE_OLYMPIAD)
			try
			{
				OlympiadDatabase.save();
				_log.info("Olympiad: Data saved.");
			}
			catch (RuntimeException e)
			{
				_log.error("Error while saving Olympiad Database! ", e);
			}

		if (Config.ALLOW_WEDDING)
			try
			{
				CoupleManager.getInstance().store();
				_log.info("CoupleManager: Data saved.");
			}
			catch (RuntimeException e)
			{
				_log.error("Error while saving Couple Manager! ", e);
			}

		try
		{
			FishingChampionShipManager.getInstance().shutdown();
			_log.info("FishingChampionShipManager: Data saved.");
		}
		catch (RuntimeException e)
		{
			_log.error("Error while saving Fishing Championship Manager! ", e);
		}

		try
		{
			Hero.getInstance().shutdown();
			_log.info("Hero: Data saved.");
		}
		catch (RuntimeException e)
		{
			_log.error("Error while saving Heroes! ", e);
		}

		try
		{
			HwidEngine.getInstance().saveAllData();
			_log.info("All HWID data saved.");
		}
		catch (RuntimeException e)
		{
			_log.error("Error while saving HWID data! ", e);
		}

		try
		{
			StreamDatabaseHandler.saveRewardTimes();
			_log.info("All stream reward times saved");
		}
		catch (RuntimeException e)
		{
			_log.error("Error while saving Stream reward times! ", e);
		}

		if (Config.ALLOW_CURSED_WEAPONS)
			try
			{
				CursedWeaponsManager.getInstance().saveData();
				_log.info("CursedWeaponsManager: Data saved,");
			}
			catch (RuntimeException e)
			{
				_log.error("Error while saving Cursed Weapons! ", e);
			}
	}

	private static void disconnectAllPlayers()
	{
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			try
			{
				player.logout();
			}
			catch (RuntimeException e)
			{
				_log.info("Error while disconnecting: " + player + '!', e);
			}
	}

	private static void removeVoidItems()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("DELETE items FROM items LEFT JOIN clan_data ON clan_data.clan_id = items.owner_id WHERE items.loc='VOID' AND items.owner_id > 0 AND clan_data.clan_id is NULL"))
		{
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("Error while removing void items!", e);
		}
	}

	public boolean isMakeBackup()
	{
		return makeBackup;
	}

	public enum ShutdownMode
	{
		NONE(-1),
		SHUTDOWN(0),
		RESTART(2);
		private final int index;

		private ShutdownMode(int index)
		{
			this.index = index;
		}

		public int getIndex()
		{
			return index;
		}
	}

	private class ShutdownCounter extends TimerTask
	{
		@Override
		public void run()
		{
			switch (shutdownCounter)
			{
				case 60:
				case 120:
				case 180:
				case 240:
				case 300:
				case 600:
				case 900:
				case 1200:
				case 1800:
				case 2700:
				case 3600:
				{
					Announcements.getInstance().announceToAll("Server is restarting in " + shutdownCounter / 60 + " minutes!");
					break;
				}
				case 5:
				case 10:
				case 20:
				case 30:
				{
					Announcements.getInstance().announceToAll(new SystemMessage(SystemMessage.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT).addNumber(shutdownCounter));
					break;
				}
				case 0:
				{
					switch (shutdownMode)
					{
						case SHUTDOWN:
						case RESTART:
						{
							Runtime.getRuntime().exit(shutdownMode.getIndex());
							break;
						}
					}
					cancel();
					return;
				}
			}
			shutdownCounter--;
		}
	}
}