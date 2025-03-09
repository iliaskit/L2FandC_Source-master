package l2f.gameserver.model.entity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fandc.votingengine.VotingSettings;
import javolution.util.FastMap;
import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.utils.ItemFunctions;

public class VoteRewardTopzone
{
	private static final Logger _log = LoggerFactory.getLogger(VoteRewardTopzone.class);
	
	// Configurations.
	private static int voteRewardVotesDifference = Config.TOPZONE_VOTES_DIFFERENCE;
	private static int checkTime = 60 * 1000 * Config.TOPZONE_REWARD_CHECK_TIME;
	
	// Don't-touch variables.
	private static int lastVotes = 0;
	private static FastMap<String, Integer> playerIps = new FastMap<String, Integer>();
	
	public static void updateConfigurations()
	{
		voteRewardVotesDifference = Config.TOPZONE_VOTES_DIFFERENCE;
		checkTime = 60 * 1000 * Config.TOPZONE_REWARD_CHECK_TIME;
	}
	
	public static void getInstance()
	{
		_log.info("Topzone: Vote reward system initialized.");
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				if (Config.ALLOW_TOPZONE_VOTE_REWARD)
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
		
		if (currentVotes == -1) {
                _log.info("Topzone: There was a problem on getting server votes.");
                return;
		}
 
		if (lastVotes == 0)
		{
			lastVotes = currentVotes;
			Announcements.getInstance().announceToAll("-------------- Topzone --------------", ChatType.CRITICAL_ANNOUNCE);
			Announcements.getInstance().announceToAll("Current Votes: " + currentVotes, ChatType.BATTLEFIELD);
			Announcements.getInstance().announceToAll("We need " + ((lastVotes + voteRewardVotesDifference) - currentVotes) + " vote(s) for mass reward.", ChatType.BATTLEFIELD);

			if (Config.ALLOW_TOPZONE_GAME_SERVER_REPORT)
			{
				_log.info("Server votes on topzone: " + currentVotes);
				_log.info("Votes needed for reward: " + ((lastVotes + voteRewardVotesDifference) - currentVotes));
			}
			
			return;
		}
		
		if (currentVotes >= (lastVotes + voteRewardVotesDifference))
		{
			if (Config.ALLOW_TOPZONE_GAME_SERVER_REPORT)
			{
				_log.info("Server votes on topzone: " + currentVotes);
				_log.info("Server is on the first page of topzone.");
				_log.info("Votes needed for next reward: " + ((currentVotes + voteRewardVotesDifference) - currentVotes));
			}
			Announcements.getInstance().announceToAll("-------------- Topzone --------------", ChatType.CRITICAL_ANNOUNCE);
			Announcements.getInstance().announceToAll("Everyone has been rewarded");
			Announcements.getInstance().announceToAll("Current Votes: " + currentVotes, ChatType.BATTLEFIELD);
			
			for (Player player : GameObjectsStorage.getAllPlayers())
			{
				boolean canReward = false;
				String pIp = player.getIP();
				
				if (playerIps.containsKey(pIp))
				{
					int count = playerIps.get(pIp);
					if (count < Config.TOPZONE_DUALBOXES_ALLOWED)
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
					addItem(player, Config.TOPZONE_REWARD_ID, Config.TOPZONE_REWARD_COUNT);
					player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Topzone", "You have received " + Config.TOPZONE_REWARD_COUNT + " coins for voting"));
				}
				else
				{
					player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "Topzone", "Already " + Config.TOPZONE_DUALBOXES_ALLOWED + " character(s) of your ip have been rewarded, so this character won't be rewarded."));
				}
			}
			playerIps.clear();
			lastVotes = currentVotes;
		}
		else
		{
			if (currentVotes <= (lastVotes + voteRewardVotesDifference)) 
			{
				if (Config.ALLOW_TOPZONE_GAME_SERVER_REPORT)
				{
					_log.info("Server votes on topzone: " + currentVotes);
					_log.info("Server is on the first page of topzone.");
					_log.info("Votes needed for next reward: " + ((lastVotes + voteRewardVotesDifference) - currentVotes));
				}
				Announcements.getInstance().announceToAll("-------------- Topzone --------------", ChatType.CRITICAL_ANNOUNCE);
				Announcements.getInstance().announceToAll("Current Votes: " + currentVotes, ChatType.BATTLEFIELD);
				Announcements.getInstance().announceToAll("We need " + ((lastVotes + voteRewardVotesDifference) - currentVotes) + " vote(s) for mass reward.", ChatType.BATTLEFIELD);
			}
			else
			{
				if (Config.ALLOW_TOPZONE_GAME_SERVER_REPORT)
				{
					_log.info("Server votes on topzone: " + currentVotes);
					_log.info("Votes needed for next reward: " + ((lastVotes + voteRewardVotesDifference) - currentVotes));
				}
				Announcements.getInstance().announceToAll("-------------- Topzone --------------", ChatType.CRITICAL_ANNOUNCE);
				Announcements.getInstance().announceToAll("Current Votes: " + currentVotes, ChatType.BATTLEFIELD);
				Announcements.getInstance().announceToAll("We need " + ((lastVotes + voteRewardVotesDifference) - currentVotes) + " vote(s) for mass reward.", ChatType.BATTLEFIELD);
			}
		}
	}
	
	private static int getVotes() {
        try {
            String endpoint = getApiEndpoint();
            if (endpoint.startsWith("err"))
                return -1;
           
            JSONTokener tokener = new JSONTokener(getApiResponse(endpoint));
            JSONObject obj = new JSONObject(tokener);
            JSONObject data = obj.getJSONObject("result");
            String voted = data.getString("totalVotes");
            
            return Integer.parseInt(voted);
            
        } catch (Exception e) {
            _log.warn("Mass Vote Reward for Topzone: Problem with getting votes from Topzone website.");
            e.printStackTrace();
        }
        return -1;
    }

    private static String getApiResponse(String endpoint) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.76");
            connection.setRequestMethod("GET");

            connection.setReadTimeout(5 * 1000);
            connection.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
            }
            connection.disconnect();
            System.out.println(stringBuilder.toString());//
            return stringBuilder.toString();
        } catch (Exception e) {
            System.out.println("Something went wrong in VoteBase::getApiResponse");
            e.printStackTrace();
            return "err";
        }
    }

    private static String getApiEndpoint() {
    	final String apiKey = VotingSettings.getInstance().getAPIKey("Topzone");
        return String.format("https://api.l2topzone.com/v1/server_%s/getServerData", apiKey);
    }

    public static void addItem(Player player, int itemId, long count) {
        ItemFunctions.addItem(player, itemId, count, true, "MassVoteRewardTopzone");
    }
}