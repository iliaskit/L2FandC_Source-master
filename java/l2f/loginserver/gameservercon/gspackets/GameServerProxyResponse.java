package l2f.loginserver.gameservercon.gspackets;

import l2f.loginserver.gameservercon.ReceivablePacket;

public class GameServerProxyResponse extends ReceivablePacket
{
	private String accountName;
	private String proxyIp;

	@Override
	protected void readImpl()
	{
		accountName = readS();
		proxyIp = readS();
	}

	@Override
	protected void runImpl()
	{
	//	ProxyWaitingList.getInstance().receiveProxy(getGameServer().getId(), accountName, proxyIp);
	}
}
