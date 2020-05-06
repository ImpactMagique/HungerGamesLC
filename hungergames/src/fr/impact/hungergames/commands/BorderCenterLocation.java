package fr.impact.hungergames.commands;

import fr.impact.hungergames.HungerGames;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;


public class BorderCenterLocation implements CommandExecutor
{

    private HungerGames main;
    public static Location locBorderCenter;

    public BorderCenterLocation(HungerGames main)
    {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {

        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(label.equalsIgnoreCase("setBorderCenter"))
            {
                if(args.length == 0)
                {
                    locBorderCenter = player.getLocation();

                    main.configuration.set(main.infos + ".borderCenterLocationX", locBorderCenter.getX());
                    main.configuration.set(main.infos + ".borderCenterLocationY", locBorderCenter.getY());
                    main.configuration.set(main.infos + ".borderCenterLocationZ", locBorderCenter.getZ());

                    try {
                        main.configuration.save(main.file);
                        player.sendMessage("§aTu as bien set le centre de la bordure.");
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        player.sendMessage("§aErreur lors du set de la bordure.");
                        return false;
                    }

                }
                else
                    {
                        return false;
                    }

            }
        }

        return false;
    }



}
