package fandc.security;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.tables.GmListTable;

/**
 * Esta clase esta creada con el fin de encontrar palabras malignas por asi decirlo en tiempo real
 * Basado en la tecnologia existente de los estados unidos que analizan en todo momento las conversaciones telefonicas, mails, mensajes, etc para encontrar
 * palabras y frases que pueden ser como matar al presidente
 *
 * En este caso yo voy a analizar palabras dichas en canal trade o en wisp solamente, que son los canales importantes, para cuando digan
 * palabras como exploit, bug, l2walker, walker, l2net, bot, etc. En el caso de encontrar uno, avisa a los gms, asi de simple
 * en ese caso el gm sabe que estan hablando o no de bugs, y puede hacerle snoop para enterarse de mas cosas
 *
 * @author Synerge
 */
public class WordsFinder
{
	/**
	 * Si se encuentra una palabra maligna en la frase, se avisa a los gms. Tambien usamos el log para escribir en la consola
	 *
	 * @param activeChar
	 * @param target
	 * @param message
	 * @param channel
	 */
	public void analizeMessage(Player activeChar, Player target, String message, String channel)
	{
		if (activeChar == null)
			return;

		// Si el que habla es un GM entonces no hay que analizarlo
		if (activeChar.getAccessLevel() > 0)
			return;

		// Si el target es un GM entonces es porque esta hablando con uno, por lo tanto no hay que analizarlo
		if (target != null && target.getAccessLevel() > 0)
			return;

		if (message == null || message.isEmpty())
			return;

		final String lowMessage = message.toLowerCase();
		final String[] text = lowMessage.split(" ");

		for (String word : text)
		{
			if (word.startsWith("hack")	|| word.startsWith("cheat") || word.startsWith("exploit") || word.equalsIgnoreCase("bug")
				|| (word.startsWith("bot") && !word.startsWith("bota") && !word.startsWith("boton") && !word.startsWith("botell") && !word.startsWith("bottle"))
				// Programas ilegales
				|| word.startsWith("l2walker") || word.startsWith("l2net")
				|| word.startsWith("wally") || word.equalsIgnoreCase("waldo")
				|| word.startsWith("radar") || word.startsWith("autopot") || word.startsWith("l2control") || word.startsWith("l2divine")
				|| word.startsWith("l2superman") || word.startsWith("l2sniper")  || word.startsWith("l2control")
				|| word.startsWith("adrenaline") || word.startsWith("l2radar") || word.startsWith("l2tower")
				// Venta Adena
				|| word.startsWith("buy adena")  || word.startsWith("sell adena") || word.startsWith("adena sell")
				|| word.contains("usd") || word.startsWith("euro") || word.startsWith("pesos") || word.startsWith("dolar") || word.startsWith("dollar")
				|| word.startsWith("skype") ||  word.startsWith("ts") ||  word.startsWith("rc")
				// Paginas
				|| ((word.contains("www.") || word.contains(".com") || word.contains(".ru")) && !word.contains(Config.SERVER_NAME.toLowerCase())))
			{
				GmListTable.broadcastSecurityToGMs("Player " + activeChar.getName() + " used the word " + word + " in the " + channel + " channel. Full message: " + message);
				return;
			}
		}
		
		// Luego buscamos frases completas que no entran o no se pueden formar con palabras simples
		if (lowMessage.contains("buy adena") || lowMessage.contains("sell adena") || lowMessage.contains("adena sell"))
			GmListTable.broadcastSecurityToGMs("Player " + activeChar.getName() + " used a dangerous phrase in the " + channel + " channel. Full message: " + message);
	}

	public static WordsFinder getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final WordsFinder _instance = new WordsFinder();
	}
}
