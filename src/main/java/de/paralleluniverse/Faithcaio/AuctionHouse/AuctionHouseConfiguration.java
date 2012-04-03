package de.paralleluniverse.Faithcaio.AuctionHouse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

public class AuctionHouseConfiguration
{
    public final long     auction_undoTime;             //in d h m s | -1 is infinite
    public final int      auction_maxAuctions_overall;  //Overall
    public final int      auction_maxAuctions_player;   //per Player
    public final boolean  auction_maxAuctions_opIgnore; //Op ignore perPlayer limit NOT Overall Limit!
    public final long     auction_maxLength;            //in d h m s | -1 is infinite
    public final boolean  auction_opCanCheat;           //Op can Cheat Items for Auction
    public final List<ItemStack> auction_blacklist;        //Blacklist Materials
    public final String   auction_timeFormat;           //Time Format Output
    public final long     auction_standardLength;       //in d h m s
    public final List<Integer>   auction_notifyTime;    //List with time in d h m s
    public final int      auction_punish;               //Punishment in % of Bid 0-100
    public final int      auction_itemContainerLength;  //in days
    //TODO blacklist einbauen
    
    public AuctionHouseConfiguration(Configuration config)
    {
        this.auction_maxAuctions_player = config.getInt("auction.maxAuctions.player");
        this.auction_maxAuctions_opIgnore = config.getBoolean("auction.maxAuctions.opIgnore");
        this.auction_maxAuctions_overall = config.getInt("auction.maxAuctions.overall");
        this.auction_opCanCheat = config.getBoolean("auction.opCanCheat");
        this.auction_timeFormat = config.getString("auction.timeFormat");
        this.auction_punish = config.getInt("auction.punish");
        this.auction_itemContainerLength = config.getInt("auction.itemContainerLength");
        //TODO Preis fuer AuktionsErstellung (Formel mit Startgebot?)
        
        this.auction_undoTime = this.convert(config.getString("auction.undoTime"));
        this.auction_maxLength = this.convert(config.getString("auction.maxLength"));
        this.auction_standardLength = this.convert(config.getString("auction.standardLength"));
        
        this.auction_notifyTime = this.convertlist(config.getStringList("auction.notifyTime"));
        
        this.auction_blacklist = getItemList(config.getStringList("auction.blacklist"));
    }
    
    private List<Integer> convertlist(List<String> str)
    {
        List<Integer> list = new ArrayList<Integer>();
        for (int i=0;i<str.size();++i)
        {
            list.add(this.convert(str.get(i)));
        }
        return list;
        
    }
    
    private List<ItemStack> getItemList(List<String> str)
    {
        int max = str.size();
        List<ItemStack> out = new ArrayList<ItemStack>();
        for(int i=0;i<max;++i)
        {
            String tmp=str.get(i);
            int parambreak = tmp.indexOf(":");
            if (parambreak == -1)
            {
                out.add(new ItemStack(Material.matchMaterial(tmp),1));
            }
            else
            {
                tmp  = tmp.substring(0, parambreak);
                short tmp2;
                try
                {
                    tmp2 = Short.parseShort(tmp.substring(parambreak+1));
                }
                catch (NumberFormatException ex)
                {
                    return null;
                }
                out.add(new ItemStack(Material.matchMaterial(tmp),1,tmp2));
            }
        }
        return out;
    }
    
    public Integer convert(String str) //ty quick_wango
    {
        Pattern pattern = Pattern.compile("^(\\d+)([smhd])?$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        matcher.find();
        int tmp;
        try
        {
            tmp = Integer.valueOf(String.valueOf(matcher.group(1)));
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        catch (IllegalStateException ex)
        {
            return null;
        }
        if (tmp==-1) return -1;
        String unitSuffix = matcher.group(2);
        if (unitSuffix == null)
        {
            unitSuffix = "m";
        }
        switch (unitSuffix.toLowerCase().charAt(0))
        {
            case 'd':
                tmp *= 24;
            case 'h':
                tmp *= 60;
            case 'm':
                tmp *= 60;
            case 's':
                tmp *= 1000;
        }
        return tmp;
    }
}
