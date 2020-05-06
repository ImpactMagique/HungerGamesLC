package fr.impact.hungergames.commands;

import fr.impact.hungergames.HungerGames;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Feast implements CommandExecutor
{

    private HungerGames main;

    public Feast(HungerGames main)
    {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {



        if(sender instanceof Player)
        {
            Player player = (Player) sender;

            if(label.equalsIgnoreCase("feast"))
            {
                if(args.length == 0)
                {
                    if(player.getInventory().contains(Material.COMPASS))
                    {
                        player.setCompassTarget(new Location(Bukkit.getServer().getWorld("world"), main.feastLocationX, main.feastLocationY, main.feastLocationZ));
                        player.sendMessage("§eLa boussole pointe maintenant vers le feast");
                        return true;
                    }
                }
                else
                    {
                        player.sendMessage("§cErreur dans la commande : usage </feast>");
                        return false;
                    }
            }
            else if(label.equalsIgnoreCase("feed"))
            {
                if(args.length == 0)
                {
                    player.setFoodLevel(20);
                    return true;
                }
            }
        }



        return false;
    }
}
