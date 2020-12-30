package fr.impact.hungergames.commands;

import fr.impact.hungergames.HungerGames;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class ChestLocation implements CommandExecutor {

    private HungerGames main;

    public ChestLocation(HungerGames main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equalsIgnoreCase("setchestlocation")) {
                if (args.length == 1) {

                    if (main.configurationSectionChest.getInt("chest" + args[0] + "X") == player.getLocation().getX()) {
                        player.sendMessage("Il y a déjà ce bloc dans la config !");
                    }

                    main.chestFileConfig.set(main.locationsInfos + ".chest" + args[0] + "X", player.getLocation().getX());
                    main.chestFileConfig.set(main.locationsInfos + ".chest" + args[0] + "Y", player.getLocation().getY());
                    main.chestFileConfig.set(main.locationsInfos + ".chest" + args[0] + "Z", player.getLocation().getZ());
                    player.sendMessage("Tu as bien placé les coordonnées du chest numéro " + args[0]);
                    try {
                        main.chestFileConfig.save(main.chestFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    player.sendMessage("Erreur d'arguments : /setchestlocation");
                    return false;
                }
            } else {
                player.sendMessage("Commande inconnue");
            }
        }

        return false;
    }
}
