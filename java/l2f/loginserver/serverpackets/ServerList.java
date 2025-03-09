package l2f.loginserver.serverpackets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import l2f.commons.net.utils.NetUtils;
import l2f.loginserver.Config;
import l2f.loginserver.GameServerManager;
import l2f.loginserver.accounts.Account;
import l2f.loginserver.gameservercon.GameServer;

public final class ServerList extends L2LoginServerPacket {
	private List<ServerData> _tempServers = new ArrayList<ServerData>();
	private List<ServerData> _tempServersLatin = new ArrayList<ServerData>();
    private List<ServerData> _servers = new ArrayList<ServerData>();
    private  Random rnd = new Random();
    private int _lastServer;

    private static class ServerData {
        int serverId;
        InetAddress ip;
        int port;
        int online;
        int maxPlayers;
        boolean status;
        boolean pvp;
        boolean brackets;
        int type;
        int ageLimit;
        int playerSize;
        int[] deleteChars;

        ServerData(int serverId, InetAddress ip, int port, boolean pvp, boolean brackets, int type, int online, int maxPlayers, boolean status, int size, int ageLimit, int[] d) {
            this.serverId = serverId;
            this.ip = ip;
            this.port = port;
            this.pvp = pvp;
            this.brackets = brackets;
            this.type = type;
            this.online = online;
            this.maxPlayers = maxPlayers;
            this.status = status;
            this.playerSize = size;
            this.ageLimit = ageLimit;
            this.deleteChars = d;
        }
    }

    public ServerList(Account account) {
        _lastServer = account.getLastServer();

        for (GameServer gs : GameServerManager.getInstance().getGameServers()) {
            Boolean added = false;
            InetAddress ip;
            try {
                ip = NetUtils.isInternalIP(account.getLastIP()) ? gs.getInternalHost() : gs.getExternalHost();
            } catch (UnknownHostException e) {
                continue;
            }

            Pair<Integer, int[]> entry = account.getAccountInfo(gs.getId());
            /* if (gs.getAdvIP() != null) {
                for (AdvIP localAdvIP : gs.getAdvIP()) {
                    if (!added && NetUtils.CheckSubNet(account.getLastIP(), localAdvIP)) {
                        try {
                            added = true;
                            _servers.add(new ServerData(gs.getId(), InetAddress.getByName(localAdvIP.ipadress), gs.getPort(), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), gs.getOnline(), gs.getMaxPlayers(), gs.isOnline(), entry == null ? 0 : entry.getKey(), gs.getAgeLimit(), entry == null ? ArrayUtils.EMPTY_INT_ARRAY : entry.getValue()));
                        } catch (Exception localException3) {
                        }
                    }
                }
            }*/
            if (!added) {
                int _fakeOnline = gs.getOnline() * Config.FakeOnlineInLoginForL2Net;
                if (!Config.HideMainServer)
                {
                    _servers.add(new ServerData(gs.getId(), ip, gs.getPort(), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), _fakeOnline, gs.getMaxPlayers(), gs.isOnline(), entry == null ? 0 : entry.getKey(), gs.getAgeLimit(), entry == null ? ArrayUtils.EMPTY_INT_ARRAY : entry.getValue()));
                }
                if (Config.EnableProxieIps)
                {
                    Config.PROXIES.forEach((key, value) -> {
                        try {
                            _tempServers.add(new ServerData(gs.getId(), InetAddress.getByName(value.split(":")[0]), Integer.parseInt(value.split(":")[1]), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), _fakeOnline, gs.getMaxPlayers(), gs.isOnline(), entry == null ? 0 : entry.getKey(), gs.getAgeLimit(), entry == null ? ArrayUtils.EMPTY_INT_ARRAY : entry.getValue()));
                        } catch (UnknownHostException uhe) {
                            uhe.printStackTrace();
                        }
                    });

                    Collections.shuffle(_tempServers);
                    _servers.add(_tempServers.get(rnd.nextInt(_tempServers.size())));

                    
                    Config.LATINPROXIES.forEach((key, value) -> {
                        try {
                            _tempServersLatin.add(new ServerData(gs.getId(), InetAddress.getByName(value.split(":")[0]), Integer.parseInt(value.split(":")[1]), gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), _fakeOnline, gs.getMaxPlayers(), gs.isOnline(), entry == null ? 0 : entry.getKey(), gs.getAgeLimit(), entry == null ? ArrayUtils.EMPTY_INT_ARRAY : entry.getValue()));
                        } catch (UnknownHostException uhe) {
                            uhe.printStackTrace();
                        }
                    });

                    Collections.shuffle(_tempServersLatin);
                    _servers.add(_tempServersLatin.get(rnd.nextInt(_tempServersLatin.size())));

                }
            }
        }
    }

    @Override
    protected void writeImpl() {
        writeC(0x04);
        writeC(_servers.size());
        writeC(_lastServer);
        for (ServerData server : _servers) {
            writeC(server.serverId);
            InetAddress i4 = server.ip;
            byte[] raw = i4.getAddress();
            writeC(raw[0] & 0xff);
            writeC(raw[1] & 0xff);
            writeC(raw[2] & 0xff);
            writeC(raw[3] & 0xff);
            writeD(server.port);
            writeC(server.ageLimit); // age limit
            writeC(server.pvp ? 0x01 : 0x00);
            writeH(server.online);
            writeH(server.maxPlayers);
            writeC(server.status ? 0x01 : 0x00);
            writeD(server.type);
            writeC(server.brackets ? 0x01 : 0x00);
        }

        writeH(0x00); // -??
        writeC(_servers.size());
        for (ServerData server : _servers) {
            writeC(server.serverId);
            writeC(server.playerSize); // acc player size
            writeC(server.deleteChars.length);
            for (int t : server.deleteChars)
                writeD((int) (t - System.currentTimeMillis() / 1000L));
        }
    }
}
