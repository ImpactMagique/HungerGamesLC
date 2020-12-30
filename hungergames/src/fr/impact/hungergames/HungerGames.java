package fr.impact.hungergames;

import fr.impact.hungergames.commands.BorderCenterLocation;
import fr.impact.hungergames.commands.ChestLocation;
import fr.impact.hungergames.commands.Feast;
import fr.impact.hungergames.listeners.HungerGamesListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class HungerGames extends JavaPlugin {

    public Logger logger = Logger.getLogger("Minecraft");
    PluginManager pm = getServer().getPluginManager();

    public File file = new File(this.getDataFolder(), "config.yml");
    public YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

    public File chestFile = new File(this.getDataFolder(), "chests.yml");
    public FileConfiguration chestFileConfig = YamlConfiguration.loadConfiguration(chestFile);

    public String infos = "infos.";
    public ConfigurationSection configurationSectionInfos = configuration.getConfigurationSection(infos);

    public String locationsInfos = "locations.";
    public ConfigurationSection configurationSectionChest = chestFileConfig.getConfigurationSection(locationsInfos);

    public HungerGamesState state;

    public String prefix = "§3[HungerGames] §c ";
    public int playersNeeded = 1;
    public int preparationTime = 10;
    public int invincibilityTime = 10;
    public int feastTime = 20;
    public Location locationToTeleport;
    public double borderLocationCenterX;
    public double borderLocationCenterY;
    public double borderLocationCenterZ;

    public double feastLocationX = 188.0D;
    public double feastLocationY = 64.0D;
    public double feastLocationZ = -294.0D;

    @Override
    public void onEnable() {
        logger.info(prefix + "Démarrage du plugin...");
        pm.registerEvents(new HungerGamesListener(this), this);

        this.getCommand("setBorderCenter").setExecutor(new BorderCenterLocation(this));
        this.getCommand("feast").setExecutor(new Feast(this));
        this.getCommand("feed").setExecutor(new Feast(this));
        this.getCommand("setchestlocation").setExecutor(new ChestLocation(this));

        this.state = HungerGamesState.CHOOSING;

        configuration.set(infos + ".playersNeeded", playersNeeded);
        configuration.set(infos + ".preparationTime", preparationTime);
        configuration.set(infos + ".invincibilityTime", invincibilityTime);
        configuration.set(infos + ".feastTime", feastTime);

        if (configurationSectionInfos.get("borderCenterLocationX") != null && configurationSectionInfos.get("borderCenterLocationY") != null && configurationSectionInfos.get("borderCenterLocationZ") != null) {
            borderLocationCenterX = configurationSectionInfos.getDouble("borderCenterLocationX");
            borderLocationCenterY = configurationSectionInfos.getDouble("borderCenterLocationY");
            borderLocationCenterZ = configurationSectionInfos.getDouble("borderCenterLocationZ");
        }

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

        logger.info(prefix + "Fermeture du plugin...");

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void setState(HungerGamesState state) {

        this.state = state;
    }


    public void loadBorders() {
        World world = Bukkit.getWorld("world");
        WorldBorder worldBorder = world.getWorldBorder();

        if (configurationSectionInfos.get("borderCenterLocationX") != null && configurationSectionInfos.get("borderCenterLocationY") != null && configurationSectionInfos.get("borderCenterLocationZ") != null) {
            locationToTeleport = new Location(world, borderLocationCenterX, borderLocationCenterY, borderLocationCenterZ);
            worldBorder.setCenter(locationToTeleport);
            worldBorder.setSize(800);

        }


    }


}

