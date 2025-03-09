package l2f.gameserver.model.entity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;
import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.utils.ItemFunctions;

public class VoteRewardHopzone {
	private static final Logger _log = LoggerFactory.getLogger(VoteRewardHopzone.class);

	// Configurations.
	private static String hopzoneUrl = Config.HOPZONE_SERVER_LINK;
	private static int voteRewardVotesDifference = Config.HOPZONE_VOTES_DIFFERENCE;
	private static int checkTime = 60 * 1000 * Config.HOPZONE_REWARD_CHECK_TIME;

	// Don't-touch variables.
	private static int lastVotes = 0;
	private static FastMap<String, Integer> playerIps = new FastMap<String, Integer>();

	public static void updateConfigurations() 
	{
		hopzoneUrl = Config.HOPZONE_SERVER_LINK;
		voteRewardVotesDifference = Config.HOPZONE_VOTES_DIFFERENCE;
		checkTime = 60 * 1000 * Config.HOPZONE_REWARD_CHECK_TIME;
	}

	public static void getInstance() {
		_log.info("Hopzone: Vote reward system initialized.");
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run() 
			{
				if (Config.ALLOW_HOPZONE_VOTE_REWARD) 
				{
					reward();
				} 
				else 
				{
					return;
				}
			}
		}, checkTime / 2, checkTime);
	}

	private static void reward() 
	{
		int currentVotes = getVotes();

		if (currentVotes == -1) 
		{
			_log.info("Hopzone: There was a problem on getting server votes.");
			return;
		}

		if (lastVotes == 0) 
		{
			lastVotes = currentVotes;
			Announcements.getInstance().announceToAll("-------------- Hopzone --------------", ChatType.CRITICAL_ANNOUNCE);
			Announcements.getInstance().announceToAll("Current Votes: " + currentVotes, ChatType.BATTLEFIELD);
			Announcements.getInstance().announceToAll("We need " + ((lastVotes + voteRewardVotesDifference) - currentVotes) + " vote(s) for mass reward.", ChatType.BATTLEFIELD);

			if (Config.ALLOW_HOPZONE_GAME_SERVER_REPORT) 
			{
				_log.info("Server votes on Hopzone: " + currentVotes);
				_log.info("Votes needed for reward: " + ((lastVotes + voteRewardVotesDifference) - currentVotes));
			}

			return;
		}

		if (currentVotes >= (lastVotes + voteRewardVotesDifference)) 
		{
			if (Config.ALLOW_HOPZONE_GAME_SERVER_REPORT) 
			{
				_log.info("Server votes on hopzone: " + currentVotes);
				_log.info("Server is on the first page of hopzone.");
				_log.info("Votes needed for next reward: " + ((currentVotes + voteRewardVotesDifference) - currentVotes));
			}
			Announcements.getInstance().announceToAll("-------------- Hopzone --------------", ChatType.CRITICAL_ANNOUNCE);
			Announcements.getInstance().announceToAll("Everyone has been rewarded");
			Announcements.getInstance().announceToAll("Current Votes: " + currentVotes, ChatType.BATTLEFIELD);

			for (Player player : GameObjectsStorage.getAllPlayers())
			{
				boolean canReward = false;
				String pIp = player.getIP();

				if (playerIps.containsKey(pIp)) 
				{
					int count = playerIps.get(pIp);
					if (count < Config.HOPZONE_DUALBOXES_ALLOWED) 
					{
						playerIps.remove(pIp);
						playerIps.put(pIp, count + 1);
						canReward = true;
					}
				} 
				else
				{
					canReward = true;
					playerIps.put(pIp, 1);
				}
				if (canReward) 
				{
					addItem(player, Config.HOPZONE_REWARD_ID, Config.HOPZONE_REWARD_COUNT);
					player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Hopzone", "You have received " + Config.HOPZONE_REWARD_COUNT + " coins for voting"));
				} 
				else 
				{
					player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Hopzone", "Already " + Config.HOPZONE_DUALBOXES_ALLOWED + " character(s) of your ip have been rewarded, so this character won't be rewarded."));
				}
			}
			playerIps.clear();
			lastVotes = currentVotes;
		} 
		else 
		{
			if (currentVotes <= (lastVotes + voteRewardVotesDifference)) 
			{
				if (Config.ALLOW_HOPZONE_GAME_SERVER_REPORT) 
				{
					_log.info("Server votes on hopzone: " + currentVotes);
					_log.info("Server is on the first page of hopzone.");
					_log.info("Votes needed for next reward: " + ((lastVotes + voteRewardVotesDifference) - currentVotes));
				}
				Announcements.getInstance().announceToAll("-------------- Hopzone --------------", ChatType.CRITICAL_ANNOUNCE);
				Announcements.getInstance().announceToAll("Current Votes: " + currentVotes, ChatType.BATTLEFIELD);
				Announcements.getInstance().announceToAll("We need " + ((lastVotes + voteRewardVotesDifference) - currentVotes) + " vote(s) for mass reward.", ChatType.BATTLEFIELD);
			} 
			else 
			{
				if (Config.ALLOW_HOPZONE_GAME_SERVER_REPORT) 
				{
					_log.info("Server votes on hopzone: " + currentVotes);
					_log.info("Votes needed for next reward: " + ((lastVotes + voteRewardVotesDifference) - currentVotes));
				}
				Announcements.getInstance().announceToAll("-------------- Hopzone --------------", ChatType.CRITICAL_ANNOUNCE);
				Announcements.getInstance().announceToAll("Current Votes: " + currentVotes, ChatType.BATTLEFIELD);
				Announcements.getInstance().announceToAll("We need " + ((lastVotes + voteRewardVotesDifference) - currentVotes) + " vote(s) for mass reward.", ChatType.BATTLEFIELD);
			}
		}
	}

	private static int getVotes() {
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			URLConnection con = new URL(hopzoneUrl).openConnection();
			con.addRequestProperty("User-Agent", "Mozilla/4.76");
			isr = new InputStreamReader(con.getInputStream());
			br = new BufferedReader(isr);

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("Total Votes")) {
					int votes = Integer.valueOf(line.split(">")[2].replace("</span", ""));
					return votes;
				}
			}

			br.close();
			isr.close();
		} catch (Exception e) {
			_log.warn("Hopzone: Error while getting server vote count.", e);
		}

		return -1;
	}

	public static void addItem(Player player, int itemId, long count) {
		ItemFunctions.addItem(player, itemId, count, true, "VoteRewardHopzone");
	}
}