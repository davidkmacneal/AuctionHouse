package de.cubeisland.AuctionHouse;

import static de.cubeisland.AuctionHouse.Translation.Translator.t;
import java.util.LinkedList;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Faithcaio
 */
public class ItemContainer
{
    LinkedList<AuctionItem> itemList;
    public final Bidder bidder;
    Economy econ = AuctionHouse.getInstance().getEconomy();

    public ItemContainer(Bidder bidder)
    {
        this.bidder = bidder;
        this.itemList = new LinkedList<AuctionItem>();
    }

    public void addItem(Auction auction)
    {
        this.itemList.add(new AuctionItem(auction));
    }

    public boolean giveNextItem()
    {
        Player player = this.bidder.getPlayer();
        Database data = AuctionHouse.getInstance().database;
        if (this.itemList.isEmpty())
        {
            return false;
        }

        AuctionItem auctionItem = this.itemList.getFirst();

        ItemStack tmp = player.getInventory().addItem(this.itemList.getFirst().clone().item).get(0);


        if (auctionItem.owner.equals(this.bidder.getName()))
        {
            player.sendMessage(t("i")+" "+t("cont_rec_ab",auctionItem.item.getType().toString()+"x"+auctionItem.item.getAmount()));
        }
        else
        {
            player.sendMessage(t("i")+" "+t("cont_rec",auctionItem.item.getType().toString()+"x"+auctionItem.item.getAmount(),
                                             econ.format(auctionItem.price),auctionItem.owner,
                                             DateFormatUtils.formatUTC(auctionItem.date, "MMM dd")));
        }

        if (tmp == null)
        {
            player.updateInventory();
            data.exec("DELETE FROM `itemcontainer` WHERE `id`=?"
                      ,this.itemList.getFirst().id);
            this.itemList.removeFirst();
            return true;
        }
        else
        {
            player.sendMessage(t("i")+" "+t("cont_rec_remain"));
            
            data.exec("UPDATE `itemcontainer` SET `amount`=? WHERE `id`=?"
                      ,tmp.getAmount(),this.itemList.getFirst().id);            
            itemList.getFirst().item.setAmount(tmp.getAmount());
            player.updateInventory();
            return true;
        }
    }
}
