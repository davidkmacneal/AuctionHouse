package de.cubeisland.AuctionHouse.Commands;

import de.cubeisland.AuctionHouse.Perm;
import de.cubeisland.AuctionHouse.Arguments;
import de.cubeisland.AuctionHouse.BaseCommand;
import de.cubeisland.AuctionHouse.AbstractCommand;
import de.cubeisland.AuctionHouse.AuctionHouse;
import de.cubeisland.AuctionHouse.Bidder;
import de.cubeisland.AuctionHouse.Database;
import de.cubeisland.AuctionHouse.MyUtil;
import static de.cubeisland.AuctionHouse.Translation.Translator.t;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Faithcaio
 */
public class NotifyCommand extends AbstractCommand
{
    public NotifyCommand(BaseCommand base)
    {
        super(base, "notify", "n");
    }

    public boolean execute(CommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage("/ah notify true|false|toggle");
            sender.sendMessage("/ah notify on|off|t");
            sender.sendMessage("Aliases notify|n");
            return true;
        }
        if (!Perm.get().check(sender,"auctionhouse.notify.command")) return true;
        Arguments arguments = new Arguments(args);
        if (arguments.getString("1") == null)
        {
            return true;
        }
        if (sender instanceof ConsoleCommandSender)
        {
            AuctionHouse.log("Console can not use notification!");
            return true;
        }
        Bidder bidder = Bidder.getInstance((Player) sender);
        if (arguments.getString("1").equalsIgnoreCase("true") || arguments.getString("1").equalsIgnoreCase("on"))
        {
            bidder.playerNotification = true;
        }
        if (arguments.getString("1").equalsIgnoreCase("false") || arguments.getString("1").equalsIgnoreCase("off"))
        {
            bidder.playerNotification = false;
        }
        if (arguments.getString("1").equalsIgnoreCase("toggle") || arguments.getString("1").equalsIgnoreCase("t"))
        {
            bidder.playerNotification = !bidder.playerNotification;
        }
        if (bidder.playerNotification)
            sender.sendMessage(t("i")+" "+t("note_on"));
        else
            sender.sendMessage(t("i")+" "+t("note_off"));
        Database data = AuctionHouse.getInstance().database;
        //Update BidderNotification
        data.exec("UPDATE `bidder` SET `notify`=? WHERE `id`=?"
                      ,bidder.notifyBitMask(),bidder.id);   
        return true;
    }

    @Override
    public String getUsage()
    {
        return super.getUsage() + " true|false|toggle";
    }

    public String getDescription()
    {
        return t("command_note");
    }
}
