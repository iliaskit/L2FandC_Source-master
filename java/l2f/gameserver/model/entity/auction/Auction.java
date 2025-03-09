package l2f.gameserver.model.entity.auction;

import l2f.gameserver.Config;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.utils.Util;

public class Auction
{
	private int _auctionId;
	private int _sellerObjectId;
	private String _sellerName;
	private ItemInstance _item;
	private long _countToSell;
	private long _pricePerItem;
	private AuctionItemTypes _itemType;
	private boolean _privateStore;
	private int _currency;
	
	public Auction(int id, int sellerObjectId, String sellerName, ItemInstance item, long pricePerItem, long countToSell, AuctionItemTypes itemType, boolean privateStore, int currency)
	{
		_auctionId = id;
		_sellerObjectId = sellerObjectId;
		_sellerName = sellerName;
		_item = item;
		_pricePerItem = pricePerItem;
		_countToSell = countToSell;
		_itemType = itemType;
		_privateStore = privateStore;
		_currency = currency;
	}
	
	public int getAuctionId()
	{
		return _auctionId;
	}
	
	public int getSellerObjectId()
	{
		return _sellerObjectId;
	}
	
	public String getSellerName()
	{
		return _sellerName;
	}
	
	public ItemInstance getItem()
	{
		return _item;
	}
	
	public void setCount(long count)
	{
		_countToSell = count;
	}
	
	public long getCountToSell()
	{
		return _countToSell;
	}
	
	public long getPricePerItem()
	{
		return _pricePerItem;
	}
	
    public int getCurrency() {
        return _currency;
    }
	
	public AuctionItemTypes getItemType()
	{
		return _itemType;
	}
	
	public boolean isPrivateStore()
	{
		return _privateStore;
	}
	
	public String getValidPricePerItem(boolean total) {
        if (_currency == Config.DONATE_ID)
            return (!total ? "<font color=FFFF00>" + Util.getNumberWithCommas(_pricePerItem) + "</font>" : "<font color=FFFF00>" + Util.getNumberWithCommas(_pricePerItem * _countToSell) + "</font>");
        else 
            return (!total ? "<font color=FFFFFF>" + Util.getNumberWithCommas(_pricePerItem) + "</font>" : "<font color=FFFFFF>" + Util.getNumberWithCommas(_pricePerItem * _countToSell) + "</font>");
    }

    public String getCurrencyName(int id) {

        if (id == Config.DONATE_ID)
            return "DC";
        else
            return "Adena";
    }
    
    public String getCurrencyIcon(int id) {

        if (id == Config.DONATE_ID)
            return "<img src=\"L2UI_CT1.Icon_DF_Common_Weight\" width=\"16\" height=\"16\">";
        else
            return "<img src=\"icon.adena\" width=\"16\" height=\"16\">";
    }

}
