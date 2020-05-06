package fr.impact.hungergames.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.impact.hungergames.Disguise;
import fr.impact.hungergames.HungerGames;
import fr.impact.hungergames.HungerGamesKits;
import fr.impact.hungergames.HungerGamesState;
import net.minecraft.server.v1_8_R3.EntityCaveSpider;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.EntityZombie;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import java.util.*;

public class HungerGamesListener implements Listener {

    private HungerGames main;
    public int taskPreparation;
    public int taskInvincibility;
    public int taskFeast;
    public int taskPhantom;
    public int chatTask;

    public int preparationTimeInConfig;
    public int invincibilityTimeInConfig;
    public int feastTimeInConfig;
    private HashMap<String, HungerGamesKits> playerKit = new HashMap<>();
    private HashMap<String, List> wolves = new HashMap<>();
    private HashMap<String, Integer> phantomFly = new HashMap<>();
    private HashMap<String, Integer> phantomWaitingQueue = new HashMap<>();
    HashMap<String, Disguise> disguisedPlayer = new HashMap<>();


    private HashMap<UUID, Integer> playerSprinting = new HashMap<>();

    public ArrayList<Material> allowedMaterials = new ArrayList<>();
    public ArrayList<Block> blockDemoman = new ArrayList<>();


    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<String> playersWithoutKit = new ArrayList<>();
    private ArrayList<Player> playerInSpectator = new ArrayList<>();
    private ArrayList<String> flashTeleport = new ArrayList<>();



    List<Wolf> listWolf = new ArrayList<>();
    ArrayList<Player> listWinner = new ArrayList<>();


    // Timer actuel scoreboard
    private HashMap<String, Integer> timerActual = new HashMap<>();

    //

    // Generation du kit aleatoire
    private HashMap<Random, Integer> randomGeneration = new HashMap<>();
    //

    List<HungerGamesKits> somethingList = Arrays.asList(HungerGamesKits.values());


    // BungeeCord Teleportation

    private ByteArrayDataOutput out = ByteStreams.newDataOutput();


    //


    public HungerGamesListener(HungerGames main) {

        this.main = main;
    }

    // ** ITEM STACK
    public ItemStack featherKit = new ItemStack(Material.FEATHER, 1);
    public ItemMeta featherKitM = featherKit.getItemMeta();

    private ItemStack compassTracking = new ItemStack(Material.COMPASS, 1);

    //

    // Map Generation //


    // Inventaire
    public Inventory inv = Bukkit.createInventory(null, 9 * 5, "§4Selection du kit");
    //

    // Kit Barbare
    private ItemStack barbareKit = new ItemStack(Material.STONE_SWORD, 1);
    private ItemMeta barbareKitM = barbareKit.getItemMeta();

    //

    // Kit Archer
    private ItemStack archerKit = new ItemStack(Material.BOW, 1);
    private ItemMeta archerKitM = archerKit.getItemMeta();
    //

    // Kit BeastMaster
    private ItemStack beastMasterKit = new ItemStack(Material.MONSTER_EGG, 1, EntityType.CHICKEN.getTypeId());
    private ItemMeta beastMasterKitM = beastMasterKit.getItemMeta();
    //

    // Kit Flash
    private ItemStack flashKit = new ItemStack(Material.REDSTONE_TORCH_ON, 1);
    private ItemMeta flashKitM = flashKit.getItemMeta();

    private ItemStack torchFlashOff = new ItemStack(Material.TORCH, 1);
    private ItemMeta torchFlashOffM = torchFlashOff.getItemMeta();

    //

    // Kit Vampire
    private ItemStack vampireKit = new ItemStack(Material.POTION, 1, (short) 16421);
    private ItemMeta vampireKitM = vampireKit.getItemMeta();
    //

    // Kit Boucher
    private ItemStack boucherKit = new ItemStack(Material.RAW_BEEF, 1);
    private ItemMeta boucherKitM = boucherKit.getItemMeta();
    //

    // Kit Frost
    private ItemStack frostyKit = new ItemStack(Material.SNOW, 1);
    private ItemMeta frostyKitM = frostyKit.getItemMeta();
    //

    // Kit Phantom
    private ItemStack phantomKit = new ItemStack(Material.FEATHER, 1);
    private ItemMeta phantomKitM = phantomKit.getItemMeta();

    private ItemStack phantomKitIG = new ItemStack(Material.FEATHER, 1);

    //

    // Kit Bucheron

    private ItemStack bucheronKit = new ItemStack(Material.WOOD_AXE, 1);
    private ItemMeta bucheronKitM = bucheronKit.getItemMeta();


    //

    // Kit Gradevigger

    private ItemStack graveDiggerKit = new ItemStack(Material.DIAMOND_SPADE, 1);
    private ItemMeta graveDiggerKitM = graveDiggerKit.getItemMeta();

    //

    // Kit $VIPChameleon

    private ItemStack chameleonKit = new ItemStack(Material.MONSTER_EGG, 1, EntityType.SHEEP.getTypeId());
    private ItemMeta chameleonKitM = chameleonKit.getItemMeta();

    //

    // Kit $VIPDemoman

    private ItemStack demomanKit = new ItemStack(Material.STONE_PLATE, 1);
    private ItemMeta demomanKitM = demomanKit.getItemMeta();

    //
      // Kit Chat

    private ItemStack chatKit = new ItemStack(Material.POTION, 1, (short) 16418);
    private ItemMeta chatKitM = chatKit.getItemMeta();

    //

    final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

    // ScoreBoard 1 //

    private final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
    private final Objective objective = scoreboard.registerNewObjective("preparation", "dummy");

    private final Score startingIn = objective.getScore(ChatColor.GOLD + "Commence dans:    ");
    private final Score numberOfPlayers = objective.getScore("§aJoueurs:    ");
    private final Objective objectiveTabList = scoreboard.registerNewObjective("tablist1", "playerKillCount");


    //

    // ScoreBoard 2 //


    private final Scoreboard scoreboard2 = scoreboardManager.getNewScoreboard();
    private final Objective objective2 = scoreboard2.registerNewObjective("invincibility", "dummy");
    private final Objective objectiveTabList2 = scoreboard2.registerNewObjective("tablist2", "playerKillCount");

    private final Score invincibility = objective2.getScore("§6Invincible:    ");
    private final Score numberOfPlayers2 = objective2.getScore("§aJoueurs:    ");
    //

    // ScoreBoard 3 //


    private final Scoreboard scoreboard3 = scoreboardManager.getNewScoreboard();
    private final Objective objective3 = scoreboard3.registerNewObjective("fighting", "dummy");
    private final Objective objectiveTabList3 = scoreboard3.registerNewObjective("tablist3", "playerKillCount");

    private final Score numberOfPlayers3 = objective3.getScore("§aJoueurs:    ");

    //

    private final Scoreboard scoreboard4 = scoreboardManager.getNewScoreboard();
    private final Objective objective4 = scoreboard4.registerNewObjective("feast", "dummy");

    private final Score feastIn = objective4.getScore("§6Festin dans:    ");
    private final Score numberOfPlayers4 = objective4.getScore("§aJoueurs:    ");


    // ScoreBoard 4 //

    //

    // ScoreBoard Perso //
    private final Scoreboard scoreboardPersoInvincibility = scoreboardManager.getNewScoreboard();
    private final Objective objectivePersoInvincibility = scoreboardPersoInvincibility.registerNewObjective("fighting", "dummy");


    //


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        event.setCancelled(true);
        Bukkit.broadcastMessage("§a" + player.getName() + "§8: " + "§f" + message);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();


        if (item != null && action != null) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (item.getType() == Material.FEATHER && item.hasItemMeta() && item.getItemMeta().getDisplayName().equalsIgnoreCase(inv.getName()))
                {
                    player.openInventory(inv);
                    inv.setItem(0, barbareKit);
                    inv.setItem(1, chameleonKit);
                    inv.setItem(2, demomanKit);
                    inv.setItem(5, phantomKit);
                    inv.setItem(9, bucheronKit);
                    inv.setItem(11, vampireKit);
                    inv.setItem(15, archerKit);
                    inv.setItem(18, beastMasterKit);
                    inv.setItem(21, boucherKit);
                    inv.setItem(26, chatKit);
                    inv.setItem(38, flashKit);
                    inv.setItem(40, frostyKit);
                    inv.setItem(40, graveDiggerKit);
                    player.updateInventory();
                }

                if (item.getType() == Material.COMPASS)
                {

                    Player result = null;
                    double lastDistance = Double.MAX_VALUE;
                    for (Player p : player.getWorld().getPlayers())
                    {
                        if (player == p)
                            continue;

                        double distance = player.getLocation().distanceSquared(p.getLocation());
                        if (distance < lastDistance && p.getGameMode() == GameMode.SURVIVAL) {
                            lastDistance = distance;
                            result = p;
                        }
                    }

                    if (result != null) {


                        player.setCompassTarget(result.getLocation());
                        player.sendMessage("§eLa boussole indique " + result.getName());


                    } else {
                        player.sendMessage("§ePas de joueur trouvé à proximité");

                    }

                }

                if(item.getType() == Material.REDSTONE_TORCH_ON && item.hasItemMeta())
                {
                    if(playerKit.get(player.getName()) == HungerGamesKits.Flash)
                    {
                        if(action == Action.RIGHT_CLICK_BLOCK && !flashTeleport.contains(player.getName()) || action == Action.RIGHT_CLICK_AIR && !flashTeleport.contains(player.getName()))
                        {
                            Block block = player.getTargetBlock((Set<Material>) null, 75);


                            if(block.getType() != Material.AIR)
                            {
                                flashTeleport.add(player.getName());
                                Location blockLocation = block.getLocation();
                                player.teleport(new Location(blockLocation.getWorld(), block.getX(), blockLocation.getY() + 1, blockLocation.getZ()));
                                player.getWorld().strikeLightningEffect(player.getLocation());
                                player.getInventory().remove(flashKit);
                                player.getInventory().addItem(torchFlashOff);

                                Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        player.sendMessage("§cTu peux maintenant te retéléporter !");
                                        player.getInventory().remove(torchFlashOff);
                                        player.getInventory().addItem(flashKit);
                                        flashTeleport.remove(player.getName());
                                    }
                                }, 20L * 20);
                            }





                        }
                    }
                }

                if(item.getType() == Material.FEATHER && !item.hasItemMeta())
                {
                    if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                    {
                        if(playerKit.get(player.getName()) == HungerGamesKits.$VIPPhantom)
                        {
                            if(!phantomWaitingQueue.containsKey(player.getName()))
                            {
                                phantomFly.put(player.getName(), 5);
                                phantomWaitingQueue.put(player.getName(), 60);

                                player.setAllowFlight(true);
                                player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
                                player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
                                player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
                                player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));

                                taskPhantom = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        switch (phantomFly.get(player.getName()))
                                        {
                                            case 5:
                                                Bukkit.broadcastMessage("§cIl te reste 5 secondes de fly!");
                                                phantomFly.replace(player.getName(), 4);
                                                break;
                                            case 4:
                                                Bukkit.broadcastMessage("§cIl te reste 4 secondes de fly!");
                                                phantomFly.replace(player.getName(), 3);
                                                break;
                                            case 3:
                                                Bukkit.broadcastMessage("§cIl te reste 3 secondes de fly!");
                                                phantomFly.replace(player.getName(), 2);
                                                break;
                                            case 2:
                                                Bukkit.broadcastMessage("§cIl te reste 2 secondes de fly!");
                                                phantomFly.replace(player.getName(), 1);
                                                break;
                                            case 1:
                                                Bukkit.broadcastMessage("§cIl te reste 1 secondes de fly!");
                                                phantomFly.replace(player.getName(), 0);
                                                break;
                                            case 0:
                                                Bukkit.getScheduler().cancelTask(taskPhantom);
                                                Bukkit.broadcastMessage("§cTon effet de vol a disparu!");
                                                phantomFly.remove(player.getName());
                                                player.setAllowFlight(false);
                                                player.getInventory().remove(Material.LEATHER_HELMET);
                                                player.getInventory().remove(Material.LEATHER_CHESTPLATE);
                                                player.getInventory().remove(Material.LEATHER_LEGGINGS);
                                                player.getInventory().remove(Material.LEATHER_BOOTS);

                                                Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
                                                    @Override
                                                    public void run()
                                                    {
                                                        phantomWaitingQueue.replace(player.getName(), phantomWaitingQueue.get(player.getName()) - 1);
                                                        switch(phantomWaitingQueue.get(player.getName()))
                                                        {
                                                            case 0:
                                                                Bukkit.broadcastMessage("§cTu as récupéré ton vol");
                                                                phantomWaitingQueue.remove(player.getName());
                                                        }


                                                    }
                                                }, 0L,  20L);

                                                break;


                                        }
                                    }
                                }, 0L, 20L);
                            }
                            else
                                {
                                    player.sendMessage("§cTu ne peux pas encore utiliser ceci! " + phantomWaitingQueue.get(player.getName()) + " secondes restantes !");
                                }

                        }
                    }
                }

            }
        }

    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();

        if (inv == null || event.getCurrentItem().getType() == null) {
            return;
        }

        if (event.getCurrentItem().getType() != null) {

            Material material = event.getCurrentItem().getType();

            if (inv.getName().equalsIgnoreCase("§4Selection du kit") && inv != null) {
                event.setCancelled(true);


                if (material == Material.STONE_SWORD)
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.Barbare) {
                        playerKit.put(player.getName(), HungerGamesKits.Barbare);
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.Barbare);

                        return;
                    }
                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }


                    playerKit.put(player.getName(), HungerGamesKits.Barbare);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.Barbare);


                }

                else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(vampireKitM.getDisplayName()))
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.$VIPVampire) {
                        playerKit.put(player.getName(), HungerGamesKits.$VIPVampire);
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.$VIPVampire);

                        return;
                    }
                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }


                    playerKit.put(player.getName(), HungerGamesKits.$VIPVampire);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.$VIPVampire);


                }

                else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(chatKitM.getDisplayName()))
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.Chat) {
                        playerKit.put(player.getName(), HungerGamesKits.Chat);
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.Chat);

                        return;
                    }
                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }


                    playerKit.put(player.getName(), HungerGamesKits.Chat);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.Chat);


                }


                else if (material == Material.BOW)
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.Archer)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.Archer);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName())) {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.Archer);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.Archer);

                }
                else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(beastMasterKitM.getDisplayName()))
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.BeastMaster)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.BeastMaster);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.BeastMaster);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.BeastMaster);

                }

                else if (material == Material.REDSTONE_TORCH_ON)
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.Flash)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.Flash);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.Flash);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.Flash);

                }

                else if (material == Material.RAW_BEEF)
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.Boucher)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.Boucher);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.Boucher);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.Boucher);

                }

                else if (material == Material.SNOW)
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.Frosty)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.Frosty);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.Frosty);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.Frosty);

                }

                else if (material == Material.FEATHER && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(phantomKitM.getDisplayName()))
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.$VIPPhantom)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.$VIPPhantom);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.$VIPPhantom);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.$VIPPhantom);

                }

                else if (material == Material.WOOD_AXE && event.getCurrentItem().hasItemMeta())
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.Bucheron)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.Bucheron);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.Bucheron);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.Bucheron);

                }

                else if (material == Material.DIAMOND_SPADE && event.getCurrentItem().hasItemMeta())
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.Gravedigger)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.Gravedigger);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.Gravedigger);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.Gravedigger);

                }

                else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(chameleonKitM.getDisplayName()))
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.$VIPChameleon)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.$VIPChameleon);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.$VIPChameleon);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.$VIPChameleon);

                }

                else if (material == Material.STONE_PLATE && event.getCurrentItem().hasItemMeta())
                {
                    if (playerKit.get(player.getName()) == HungerGamesKits.$VIPDemoman)
                    {
                        player.sendMessage("§cTu as déjà le kit " + HungerGamesKits.$VIPDemoman);
                        return;

                    }

                    if (playersWithoutKit.contains(player.getName()))
                    {
                        playersWithoutKit.remove(player.getName());
                    }

                    playerKit.put(player.getName(), HungerGamesKits.$VIPDemoman);
                    player.sendMessage("§cTu as choisi le kit " + HungerGamesKits.$VIPDemoman);

                }

            }

        }


    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemPickup(PlayerPickupItemEvent event)
    {

        if (main.state == HungerGamesState.CHOOSING) {
            event.setCancelled(true);
        }

        if(event.getItem().getItemStack() == flashKit || event.getItem().getItemStack() == torchFlashOff)
        {
            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if (main.state == HungerGamesState.CHOOSING) {
            event.setCancelled(true);
        }

        if(playerKit.get(player.getName()) == HungerGamesKits.Flash)
        {
            if(event.getBlock().getType() == flashKit.getType() || event.getBlock().getType() == torchFlashOff.getType())
            {
                event.setCancelled(true);
            }
        }

        if(playerKit.get(player.getName()) == HungerGamesKits.$VIPDemoman)
        {
            if(event.getBlock().getType() == Material.STONE_PLATE)
            {
                Block stonePlate = event.getBlock();

                if(event.getBlockAgainst().getType() == Material.GRAVEL)
                {
                    blockDemoman.add(stonePlate);
                }
            }
        }

    }

    @EventHandler
    public void onWolfSpawn(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().equals(Material.MONSTER_EGG)) {
            Player p = e.getPlayer();
            Material egg = Material.MONSTER_EGG;
            egg.getData();
            if (egg.getData().equals(95)) {
                Wolf w = (Wolf) e.getRightClicked();
                listWolf.add(w);
                wolves.put(e.getPlayer().getName(), (List<Wolf>) w);
            }
        }
    }

    @EventHandler
    public void onWolfSpawner(final EntitySpawnEvent e) {
        if(playerKit.get(e.getEntity().getName()) == HungerGamesKits.BeastMaster)
        {
        if (e.getEntity() instanceof Wolf) {
            final Wolf wolf = (Wolf) e.getEntity();
            wolf.setTamed(true);
            wolf.setBreed(false);
            wolf.setAngry(true);
            wolf.setOwner(((Wolf) e.getEntity()).getOwner());
            wolf.setAdult();
            wolf.setHealth(10);
            wolf.setCollarColor(DyeColor.RED);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> wolf.setHealth(0), 6000);
        }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event)
    {
        if (main.state == HungerGamesState.CHOOSING)
        {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.getWorld().equals("world")) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerSprint(PlayerToggleSprintEvent event)
    {
        Player player = event.getPlayer();

        if (playerKit.get(player.getName()) == HungerGamesKits.Chat)
        {
            if (!player.isSprinting())
            {
               playerSprinting.put(player.getUniqueId(), 4);

               chatTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable()
               {
                   @Override
                   public void run()
                   {
                        if(!player.isSprinting())
                        {
                            if(playerSprinting.containsKey(player.getUniqueId()))
                            {
                                if(disguisedPlayer.containsKey(player.getName()))
                                {
                                    disguisedPlayer.get(player.getName()).removeDisguise();
                                    player.sendMessage("§9Vous êtes redevenu humain!");
                                }
                                playerSprinting.remove(player.getUniqueId());
                                Bukkit.getScheduler().cancelTask(chatTask);
                            }
                        }
                   }
               }, 0L, 20L);

            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable()
            {
                @Override
                public void run()
                {
                    if(playerSprinting.containsKey(player.getUniqueId()))
                    {
                        player.sendMessage("§9Meeeeeow!");
                        disguisedPlayer.put(player.getName(), new Disguise(Disguise.DisguiseType.OCELOT, player.getName()));
                        new Disguise(Disguise.DisguiseType.OCELOT, player.getName()).disguiseToAll();
                    }
                }
            }, 20L * 4);

        }
    }




    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        if(loc.getBlock().getType() == Material.SNOW)
        {
            if(playerKit.get(player.getName()) == HungerGamesKits.Frosty)
            {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 3, 2, true, true));


            }
        }
        else if(loc.getBlock().getType() == Material.STONE_PLATE)
        {
            if(playerKit.get(player.getName()) == HungerGamesKits.$VIPDemoman)
            {
                return;
            }

            Block blockWalked = loc.getBlock();
            if(blockDemoman.contains(blockWalked))
            {
                player.getWorld().createExplosion(player.getLocation(), 5.0F);
            }
        }
    }


    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event)
    {


        if(event.getEntity() instanceof Snowball)
        {

            if(event.getEntity().getShooter() instanceof Player)
            {
                Player player = (Player) event.getEntity().getShooter();
                if(playerKit.get(player.getName()) == HungerGamesKits.Frosty)
                {
                    Location locHit = event.getEntity().getLocation();
                    Block blockHit = locHit.getBlock();

                    if(blockHit.getType() == Material.WATER || blockHit.getType() == Material.STATIONARY_WATER)
                    {
                        blockHit.setType(Material.ICE);
                    }

                    else
                        {
                            blockHit.setType(Material.SNOW);
                        }



                }
            }

        }
    }

    @EventHandler
    public void onPlayerLoseFood(FoodLevelChangeEvent event) {
        if (main.state == HungerGamesState.CHOOSING || main.state == HungerGamesState.INVINCIBILITY) {
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event)
    {

        Block block = event.getBlock();

        if (main.state == HungerGamesState.CHOOSING)
        {
            event.setCancelled(true);
        }

        if(block.getType() == Material.LOG || block.getType() == Material.LEAVES)
        {
            Player player = event.getPlayer();
            if(playerKit.get(player.getName()) == HungerGamesKits.Bucheron)
            {
                ItemStack itemInHand = player.getItemInHand();
                if(itemInHand.getType() == Material.WOOD_AXE)
                {
                    allowedMaterials.add(Material.LOG);
                    Set<Block> treeBlocks = getTree(block, allowedMaterials);
                    for (Block treeBlock : treeBlocks)
                    {
                        treeBlock.breakNaturally();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event)
    {
        Entity dead = event.getEntity();
        Entity killer = event.getEntity().getKiller();
        if(event.getEntity().getKiller() instanceof Player)
        {

            if(dead instanceof Monster)
            {

                if (playerKit.get(killer.getName()) == HungerGamesKits.$VIPVampire && event.getEntity().getKiller().getHealth() <= 15.0D)
                {
                    event.getEntity().getKiller().setHealth(event.getEntity().getKiller().getHealth() + 5.0D);
                }
            }
            else if(dead instanceof Animals)
            {
                if (playerKit.get(killer.getName()) == HungerGamesKits.$VIPVampire && event.getEntity().getKiller().getHealth() <= 18.0D)
                {
                    event.getEntity().getKiller().setHealth(event.getEntity().getKiller().getHealth() + 2.0D);
                }
            }

        }
    }


    @EventHandler
    public void onPlayerDamage(final EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (main.state.equals(HungerGamesState.INVINCIBILITY)) {
                e.setCancelled(true);
            }
            if (main.state.equals(HungerGamesState.CHOOSING)) {
                e.setCancelled(true);
            }

            if (e.getEntity() instanceof Player)
            {

                Player player = (Player) e.getEntity();
                EntityDamageEvent.DamageCause damageCause = e.getCause();

                if(playerKit.get(player.getName()) == HungerGamesKits.$VIPChameleon)
                {
                    if(disguisedPlayer.containsKey(player.getName()))
                    {
                        disguisedPlayer.get(player.getName()).removeDisguise();
                        disguisedPlayer.remove(player.getName());
                    }
                }

                if(playerKit.get(player.getName()) == HungerGamesKits.$VIPDemoman)
                {
                    if(damageCause == EntityDamageEvent.DamageCause.CUSTOM)
                    {
                        e.setCancelled(true);

                    }
                }

                if (damageCause == EntityDamageEvent.DamageCause.FALL && player.getHealth() - e.getDamage() < 1)
                {
                    Bukkit.broadcastMessage("§c" + player.getName() + "§4(" + "§c" + playerKit.get(player.getName()) + "§4) " + "§e" + "a chuté de trop haut");
                    listWinner.remove(player);

                    player.getWorld().strikeLightningEffect(player.getLocation());
                    player.setGameMode(GameMode.SPECTATOR);

                    numberOfPlayers3.setScore(listWinner.size());

                    if (listWinner.size() == 1)
                    {


                        for (Player winner : listWinner)
                        {
                            Bukkit.broadcastMessage(ChatColor.RED + winner.getName() + " " + "a gagné !");

                        }

                        restartServer();


                    }


                }



                    if (player.getKiller() instanceof EntityZombie)
                    {
                        Bukkit.broadcastMessage("§c" + player.getName() + "§4(" + "§c" + playerKit.get(player.getName()) + "§4) " + "§e" + "a été dévoré par un zombie");
                        listWinner.remove(player);
                    }
                    if (player.getKiller() instanceof EntitySkeleton)
                    {
                        Bukkit.broadcastMessage("§c" + player.getName() + "§4(" + "§c" + playerKit.get(player.getName()) + "§4) " + "§e" + "a été shooté par un squelette");
                        listWinner.remove(player);
                    }
                    if (player.getKiller() instanceof EntitySpider || player.getKiller() instanceof EntityCaveSpider)
                    {
                        Bukkit.broadcastMessage("§c" + player.getName() + "§4(" + "§c" + playerKit.get(player.getName()) + "§4) " + "§e" + "a été envenimé par une araignée");
                        listWinner.remove(player);
                    }


                    }




                }





            }




    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        event.setDeathMessage(null);

    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (main.state == HungerGamesState.CHOOSING) {
            event.setCancelled(true);
            return;
        }

        if (main.state == HungerGamesState.INVINCIBILITY) {
            if (entity instanceof Player) {
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        } else {

            event.setCancelled(false);

            if (entity instanceof Player)
            {
                Player victim = (Player) entity;
                double health = victim.getHealth();


                if (health - event.getDamage() < 1)
                {
                    if (victim.getKiller() instanceof Player)
                    {
                        event.setCancelled(true);
                        Player killer = victim.getKiller();
                        String itemNameThatKilled = WordUtils.capitalizeFully(killer.getItemInHand().getType().name().toLowerCase().replace('_', ' '));

                        Bukkit.broadcastMessage("§c" + victim.getName() + "§4(" + "§c" + playerKit.get(victim.getName()) + "§4) " + "§e" + "a été tué de sang froid par " + "§c" + killer.getName() + "§4(" + "§c" + playerKit.get(killer.getName()) + "§4)§e avec " + itemNameThatKilled);
                        listWinner.remove(victim);

                        victim.getWorld().strikeLightningEffect(victim.getLocation());
                        victim.setGameMode(GameMode.SPECTATOR);

                        numberOfPlayers3.setScore(listWinner.size());

                        if (playerKit.get(victim.getKiller().getName()) == HungerGamesKits.$VIPVampire) {

                            if (victim.getKiller().getHealth() == 20.0D)
                            {
                                victim.getKiller().getInventory().addItem(vampireKit);
                            }



                            victim.getKiller().setHealth(20.0D);


                        }






                        if (main.state == HungerGamesState.FIGHT)
                            for (Player players : listWinner)
                            {
                                final Score killCount = objectiveTabList3.getScore(players.getName());
                                killCount.setScore(players.getStatistic(Statistic.PLAYER_KILLS));
                                players.setScoreboard(scoreboard3);
                            }

                        else if (main.state == HungerGamesState.FINISH)
                            for (Player players : listWinner) {
                                numberOfPlayers4.setScore(listWinner.size());
                                players.setScoreboard(scoreboard4);
                            }



                        if(playerKit.get(killer.getName()) == HungerGamesKits.Gravedigger)
                        {
                            Location victimLoc = victim.getLocation();
                            Inventory victimInv = victim.getInventory();
                            Block block = victimLoc.getBlock();
                            block.setType(Material.CHEST);
                            Chest chest = (Chest) block.getState();
                            Inventory chestInventory = chest.getInventory();

                            for(int i = 0; i < victimInv.getSize(); i++)
                            {
                                ItemStack actualItem = victimInv.getItem(i);
                                chestInventory.setItem(i, actualItem);
                            }

                            return;

                        }

                    for (int i = 0; i < victim.getInventory().getSize(); i++)
                    {
                        if (victim.getInventory().getItem(i) != null)
                        {

                            if(victim.getInventory().getItem(i) == flashKit || victim.getInventory().getItem(i) == torchFlashOff)
                            {
                                event.setCancelled(true);
                            }




                            victim.getWorld().dropItem(victim.getLocation(), victim.getInventory().getItem(i));
                        }

                    }




                    if (main.state == HungerGamesState.FIGHT)
                    {
                        if (listWinner.size() == 1) {


                            for (Player winner : listWinner) {
                                final Score killCount = objectiveTabList3.getScore(winner.getName());
                                killCount.setScore(winner.getStatistic(Statistic.PLAYER_KILLS));
                                winner.setScoreboard(scoreboard3);
                                Bukkit.broadcastMessage(ChatColor.RED + winner.getName() + " " + "a gagné !");

                            }

                            restartServer();


                        }


                    }
                    } else if (main.state == HungerGamesState.FINISH) {
                        if (listWinner.size() == 1) {


                            for (Player winner : listWinner) {
                                numberOfPlayers4.setScore(listWinner.size());
                                winner.setScoreboard(scoreboard4);
                                Bukkit.broadcastMessage(ChatColor.RED + winner.getName() + " " + "a gagné !");

                            }

                            restartServer();


                        }
                    }

                }


            }

            else if(entity instanceof Monster || entity instanceof Animals)
            {

                if(event.getDamager() instanceof Player)
                {
                    Player hitter = (Player) event.getDamager();
                    if(playerKit.get(hitter.getName()) == HungerGamesKits.Boucher)
                    {
                        event.setCancelled(true);
                        LivingEntity entityAttacked = (LivingEntity) event.getEntity();
                        entityAttacked.damage(entityAttacked.getMaxHealth() / 2);
                    }


                    if(playerKit.get(hitter.getName()) == HungerGamesKits.$VIPChameleon)
                    {

                                Bukkit.broadcastMessage("Un mouton a été tapé");
                                disguisedPlayer.put(hitter.getName(), new Disguise(Disguise.DisguiseType.SHEEP, ""));
                                new Disguise(Disguise.DisguiseType.SHEEP, hitter.getName()).disguiseToAll();


                    }

                }



            }


        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getServer().getWorld("world").setTime(1000);

        if(player.getName().equalsIgnoreCase("Zentoh") || player.getName().equalsIgnoreCase("KraleurMC") || player.getName().equalsIgnoreCase("Hawayu") || player.getName().equalsIgnoreCase("Zbrlaa") || player.getName().equalsIgnoreCase("drax152") || player.getName().equalsIgnoreCase("Piquette"))
        {
            player.kickPlayer("Erreur VPN !");
        }

        // Déclaration Item Meta

        // Kit Barbare
        barbareKitM.setDisplayName("Barbare");
        barbareKitM.setLore(Arrays.asList("§9Vous spawnez avec une épée en pierre", "", "§6Contenu:", "§9- 1 épée en pierre"));
        barbareKit.setItemMeta(barbareKitM);

        //

        // Kit Archer
        archerKitM.setDisplayName("Archer");
        archerKitM.setLore(Arrays.asList("§9Vous spawnez avec un arc et des flèches", "", "§6Contenu:", "§9- 1 arc", "- 10 flèches"));
        archerKit.setItemMeta(archerKitM);
        //

        // Kit BeastMaster
        beastMasterKitM.setDisplayName("BeastMaster");
        beastMasterKitM.setLore(Arrays.asList("§9Les loups que vous faites apparaître seront déjà apprivoisés", "§6Compétence(s)", "§9- 100% de chance d'apprivoiser un loup avec un OS", "§6Contenu:", "§9- 4 os", "- 4 oeufs de loup"));
        beastMasterKit.setItemMeta(beastMasterKitM);
        //

        // Kit Flash
        flashKitM.setDisplayName("Flash");
        flashKitM.setLore(Arrays.asList("§9Vous pouvez vous téléporter", "§6Compétence(s)", "§9- Vous vous téléportez où vous regardez en faisant un clic droit avec votre torche de redstone", "- Vous avez un effet de Faiblesse II en fonction de la distance traversée", "- Les joueurs voient un éclair vous frapper (qui ne vous inflige rien) lorsque vous vous téléportez."));
        flashKit.setItemMeta(flashKitM);

        torchFlashOffM.setDisplayName("Flash OFF");
        torchFlashOffM.addEnchant(Enchantment.LUCK, 1, true);
        torchFlashOffM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        torchFlashOff.setItemMeta(torchFlashOffM);

        //

        // Kit Vampire


        vampireKitM.setDisplayName("$VIPVampire");
        vampireKitM.setLore(Arrays.asList("§7Reservé aux §eVIP §c(https://lifecraftv2.fr/shop)", "§9Vous permet de vous régénérer quand vous tuez", "§6Compétence(s)", "§9- Gagnez un grand boost de santé quand vous tuez un joueur", "- Gagnez un boost moyen de santé quand vous tuez un monstre", "- Gagnez un léger boost de santé quand vous tuez un animal", "- Si votre santé est pleine quand vous tuez un autre joueur vous gagnez une potion de soin"));
        vampireKit.setItemMeta(vampireKitM);


        //

        // Kit Boucher


        boucherKitM.setDisplayName("Boucher");
        boucherKitM.setLore(Arrays.asList("§9Tue tout les monstres et animaux en §cdeux coups §9!"));
        boucherKit.setItemMeta(boucherKitM);

        //

        // Kit Frosty

        frostyKitM.setDisplayName("Frosty");
        frostyKitM.setLore(Arrays.asList("§9- Changez la terre en neige et l'eau en glace", "§6Compétence(s):", "§9 -Lancez une boule de neige sur l'eau pour la geler", "§9- Lancez une boule de neige sur le sol pour mettre de la neige", "§9- Vous avez un boost de vitesse quand vous marchez sur de la neige"));
        frostyKit.setItemMeta(frostyKitM);

        //

        // Kit Phantom

        phantomKitM.setDisplayName("$VIPPhantom");
        phantomKitM.setLore(Arrays.asList("§9- Volez pendant 5 secondes avec une armure en cuir", "§6Compétence(s):", "§9 -Faites clic-droit sur la plume pour voler pendant 5 secondes", "§9- Vous ne prenez pas de dégat de chute pendant l'effet de vol", "§9- Vous avez une armure en cuir pendant l'effet de vol"));
        phantomKitM.addEnchant(Enchantment.LUCK, 1, true);
        phantomKitM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        phantomKit.setItemMeta(phantomKitM);

        //

        // Kit Bucheron

        bucheronKitM.setDisplayName("Bucheron");
        bucheronKitM.setLore(Arrays.asList("§9- Découpez tous les arbres en un coup d'hache", "§6Compétence(s):", "§9- Quand vous détruisez un bloc de bois d'un arbre, l'arbre se casse directement", "§6- Contenu", "§9- Hache en bois x1"));
        bucheronKit.setItemMeta(bucheronKitM);

        //

        // Kit GraveDigger

        graveDiggerKitM.setDisplayName("Gravedigger");
        graveDiggerKitM.setLore(Arrays.asList("§9- Un peu de rangement", "§6Compétence(s):", "§6- Compétence(s)", "§9- Chaque ennemi tué laisse derrière lui un coffre avec tout ses items à l'intérieur !"));
        graveDiggerKit.setItemMeta(graveDiggerKitM);

        //

        // Kit Chameleon

        chameleonKitM.setDisplayName("$VIPChameleon");
        chameleonKitM.setLore(Arrays.asList("§7Reservé aux §eVIP §c(https://lifecraftv2.fr/shop)", "§9Vous pouvez vous déguiser", "§6Compétence(s)", "§9- Taper un monstre vous transforme en celui-ci", "- Vous redevenez humain si vous infligez ou subissez des dégats !"));
        chameleonKit.setItemMeta(chameleonKitM);

        //

        // Kit Demoman

        demomanKitM.setDisplayName("$VIPDemoman");
        demomanKitM.setLore(Arrays.asList("§7Reservé aux §eVIP §c(https://lifecraftv2.fr/shop)", "§9Vous pouvez piéger vos ennemis", "§6Compétence(s)", "§9- Placez une plaque de pression en pierre pour créer une mine, et marchez dessus pour qu'elle explose, l'explosion est légérement plus grande qu'une TNT, attention.", "- Vous pouvez désactiver vos mines en cassant la plaque de pression."));
        demomanKit.setItemMeta(demomanKitM);
        //

        // Kit Chat

        chatKitM.setDisplayName("Chat");
        chatKitM.setLore(Arrays.asList("§9Fuyeeeeeez", "§6Compétence(s)", "§9- Transformez vous en ocelot pendant 10 secondes !", "- Si vous vous arrêtez de sprinter ou que vous vous prenez un obstacle, vous redevenez humain."));
        chatKit.setItemMeta(chatKitM);

        //

        objectiveTabList.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        final Score killCount = objectiveTabList.getScore(player.getName());
        killCount.setScore(player.getStatistic(Statistic.PLAYER_KILLS));
        player.setScoreboard(scoreboard);

        event.setJoinMessage("");


        player.getInventory().clear();

        if (main.state == HungerGamesState.CHOOSING) {
            players.add(player);
            playersWithoutKit.add(player.getName());

            if (main.locationToTeleport != null) {
                player.teleport(main.locationToTeleport);
            }
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.setFoodLevel(20);
            player.setHealth(20.0D);

            featherKitM.setDisplayName("§4Selection du kit");
            featherKit.setItemMeta(featherKitM);
            player.getInventory().setItem(0, featherKit);

            objective.setDisplayName("§3Etape: " + "§bPréparation");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            if (timerActual.isEmpty()) {

                startingIn.setScore(main.configurationSectionInfos.getInt("preparationTime"));
                numberOfPlayers.setScore(Bukkit.getServer().getOnlinePlayers().size());
                player.setScoreboard(scoreboard);

            } else {
                startingIn.setScore(timerActual.get("preparationTimer"));
                numberOfPlayers.setScore(Bukkit.getServer().getOnlinePlayers().size());
                player.setScoreboard(scoreboard);
            }


        }


        if (main.state == HungerGamesState.INVINCIBILITY) {
            playerInSpectator.add(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("§cTu as rejoint une partie en cours, tu as été mis en spectateur");

            objectivePersoInvincibility.setDisplayName("§3Etape: " + "§bInvincibilité");
            objectivePersoInvincibility.setDisplaySlot(DisplaySlot.SIDEBAR);

            final Score invincibilityPersoSpectator = objectivePersoInvincibility.getScore("§6Invincible:    ");
            final Score numberOfPlayersPersoSpectator = objectivePersoInvincibility.getScore("§aJoueurs:    ");


            invincibilityPersoSpectator.setScore(invincibilityTimeInConfig);
            numberOfPlayersPersoSpectator.setScore(Bukkit.getServer().getOnlinePlayers().size() - playerInSpectator.size());
            player.setScoreboard(scoreboardPersoInvincibility);

            return;
        }

        if (main.state == HungerGamesState.FIGHT) {
            playerInSpectator.add(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("§cTu as rejoint une partie en cours, tu as été mis en spectateur");

            objective3.setDisplayName("§3Etape: " + "§bCombat");
            objective3.setDisplaySlot(DisplaySlot.SIDEBAR);

            final Score numberOfPlayers3 = objective3.getScore("§aJoueurs:    ");

            numberOfPlayers3.setScore(Bukkit.getServer().getOnlinePlayers().size() - playerInSpectator.size());
            player.setScoreboard(scoreboard3);

            return;
        }

        if (Bukkit.getServer().getOnlinePlayers().size() == main.configurationSectionInfos.getInt("playersNeeded")) {

            Bukkit.broadcastMessage("§cLa partie commencera dans " + main.configurationSectionInfos.get("preparationTime") + "secondes");
            preparationTimeInConfig = main.configurationSectionInfos.getInt("preparationTime");

            taskPreparation = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
                @Override
                public void run() {
                    main.loadBorders();
                    --preparationTimeInConfig;
                    timerActual.put("preparationTimer", preparationTimeInConfig);
                    startingIn.setScore(timerActual.get("preparationTimer"));
                    numberOfPlayers.setScore(Bukkit.getServer().getOnlinePlayers().size());


                    for (Player player : players) {
                        player.setScoreboard(scoreboard);

                    }

                    if (preparationTimeInConfig == 30) {
                        Bukkit.broadcastMessage("§cLa partie commencera dans " + preparationTimeInConfig + " secondes");

                    }

                    if (preparationTimeInConfig == 15) {
                        Bukkit.broadcastMessage("§cLa partie commencera dans " + preparationTimeInConfig + " secondes");
                    }

                    if (preparationTimeInConfig == 10) {
                        Bukkit.broadcastMessage("§cLa partie commencera dans " + preparationTimeInConfig + " secondes");
                    }

                    if (preparationTimeInConfig == 5) {
                        Bukkit.broadcastMessage("§cLa partie commencera dans " + preparationTimeInConfig + " secondes");
                    }

                    if (preparationTimeInConfig == 4) {
                        Bukkit.broadcastMessage("§cLa partie commencera dans " + preparationTimeInConfig + " secondes");
                    }

                    if (preparationTimeInConfig == 3) {
                        Bukkit.broadcastMessage("§cLa partie commencera dans " + preparationTimeInConfig + " secondes");
                    }

                    if (preparationTimeInConfig == 2) {
                        Bukkit.broadcastMessage("§cLa partie commencera dans " + preparationTimeInConfig + " secondes");
                    }

                    if (preparationTimeInConfig == 1) {
                        Bukkit.broadcastMessage("§cLa partie commencera dans " + preparationTimeInConfig + " secondes");

                    }

                    if (preparationTimeInConfig == 0) {
                        if (Bukkit.getServer().getOnlinePlayers().size() >= main.configurationSectionInfos.getInt("playersNeeded")) {
                            Bukkit.getScheduler().cancelTask(taskPreparation);
                            invincibilityTimeInConfig = main.configurationSectionInfos.getInt("invincibilityTime");

                            objective2.setDisplayName("§3Etape: " + "§bInvincibilité");
                            objective2.setDisplaySlot(DisplaySlot.SIDEBAR);

                            final Score invincibility = objective2.getScore("§6Invincible:    ");
                            final Score numberOfPlayers2 = objective2.getScore("§aJoueurs:    ");


                            invincibility.setScore(invincibilityTimeInConfig);
                            numberOfPlayers2.setScore(Bukkit.getServer().getOnlinePlayers().size());

                            for (Player player : players)
                            {
                                objectiveTabList2.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                                final Score killCount = objectiveTabList2.getScore(player.getName());
                                killCount.setScore(player.getStatistic(Statistic.PLAYER_KILLS));
                                player.setScoreboard(scoreboard2);
                                player.setAllowFlight(false);
                                player.getInventory().clear();
                                listWinner.add(player);

                                // Téléportation à faire ici
                            }

                            Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig / 60 + " minutes!");


                            // Give des items lies au kit

                            for (Player player0 : players) {


                                if (playersWithoutKit.contains(player0.getName()))
                                {

                                    player0.sendMessage("§cTu n'as pas choisi de kit, un kit aléatoire t'as été attribué");

                                    Random random = new Random();
                                    int randomResult = random.nextInt(somethingList.size());
                                    randomGeneration.put(random, randomResult);

                                    switch (randomGeneration.get(random))
                                    {
                                        case 0:
                                            playerKit.put(player0.getName(), HungerGamesKits.Barbare);
                                            break;
                                        case 1:
                                            playerKit.put(player0.getName(), HungerGamesKits.Archer);
                                            break;
                                        case 2:
                                            playerKit.put(player0.getName(), HungerGamesKits.$VIPChameleon);
                                            break;
                                        case 3:
                                            playerKit.put(player0.getName(), HungerGamesKits.$VIPDemoman);
                                            break;
                                        case 5:
                                            playerKit.put(player0.getName(), HungerGamesKits.$VIPPhantom);
                                            break;
                                        case 9:
                                            playerKit.put(player0.getName(), HungerGamesKits.Bucheron);
                                            break;
                                        case 11:
                                            playerKit.put(player0.getName(), HungerGamesKits.$VIPVampire);
                                            break;
                                        case 18:
                                            playerKit.put(player0.getName(), HungerGamesKits.BeastMaster);
                                            break;
                                        case 21:
                                            playerKit.put(player0.getName(), HungerGamesKits.Boucher);
                                            break;
                                        case 26:
                                            playerKit.put(player0.getName(), HungerGamesKits.Chat);
                                            break;
                                        case 38:
                                            playerKit.put(player0.getName(), HungerGamesKits.Flash);
                                            break;
                                        case 40:
                                            playerKit.put(player0.getName(), HungerGamesKits.Frosty);
                                            break;
                                        case 42:
                                            playerKit.put(player0.getName(), HungerGamesKits.Gravedigger);
                                            break;



                                    }

                                    main.state = HungerGamesState.INVINCIBILITY;


                                }

                                player0.getInventory().setItem(0, compassTracking);

                                switch (playerKit.get(player0.getName()))
                                {

                                    case Barbare:

                                        player0.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
                                        break;

                                    case Archer:
                                        player0.getInventory().addItem(new ItemStack(Material.BOW, 1));
                                        player0.getInventory().addItem(new ItemStack(Material.ARROW, 10));
                                        break;

                                    case BeastMaster:
                                        player0.getInventory().addItem(new ItemStack(Material.BONE, 4));
                                        player0.getInventory().addItem(new ItemStack(Material.MONSTER_EGG, 4, EntityType.WOLF.getTypeId()));
                                        break;

                                    case Flash:
                                        player0.getInventory().addItem(flashKit);
                                        break;

                                    case $VIPPhantom:
                                        player0.getInventory().addItem(phantomKitIG);
                                        break;

                                    case Bucheron:
                                        player0.getInventory().addItem(new ItemStack(Material.WOOD_AXE, 1));
                                        break;

                                    case $VIPDemoman:
                                        player0.getInventory().addItem(new ItemStack(Material.GRAVEL, 16));
                                        player0.getInventory().addItem(new ItemStack(Material.STONE_PLATE, 2));
                                        break;
                                }
                            }

                            main.state = HungerGamesState.INVINCIBILITY;
                            //

                            // State Invincibilite

                            if (main.state.equals(HungerGamesState.INVINCIBILITY)) {


                                invincibilityTimeInConfig = main.configurationSectionInfos.getInt("invincibilityTime");

                                invincibility.setScore(invincibilityTimeInConfig);


                                for (Player player : players) {

                                    if (player.getGameMode() == GameMode.SPECTATOR) {
                                        player.setScoreboard(scoreboardPersoInvincibility);
                                    }


                                }

                                taskInvincibility = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
                                    @Override
                                    public void run() {

                                        invincibilityTimeInConfig--;
                                        invincibility.setScore(invincibilityTimeInConfig);
                                        numberOfPlayers2.setScore(Bukkit.getServer().getOnlinePlayers().size() - playerInSpectator.size());

                                        for (Player player : players) {
                                            player.setScoreboard(scoreboard2);

                                        }

                                        switch (invincibilityTimeInConfig) {
                                            case 60:
                                                Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig / 60 + " minute!");
                                                break;
                                            case 30:
                                                Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig + " secondes!");
                                                break;
                                            case 15:
                                                Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig + " secondes!");
                                                break;
                                            case 10:
                                                Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig + " secondes!");
                                                break;
                                            case 5:
                                                Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig + " secondes!");
                                                break;
                                            case 4:
                                                Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig + " secondes!");
                                                break;
                                            case 3:
                                                Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig + " secondes!");
                                                break;
                                            case 2:
                                                Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig + " secondes!");
                                                break;
                                            case 1:
                                                Bukkit.broadcastMessage("§cL'invincibilité se désactive dans " + invincibilityTimeInConfig + " seconde!");
                                                break;
                                            case 0:
                                                Bukkit.getScheduler().cancelTask(taskInvincibility);
                                                Bukkit.broadcastMessage("§cInvincibilité désactivée!");
                                                main.state = HungerGamesState.FIGHT;
                                                objective3.setDisplayName("§3Etape: " + "§bCombat");
                                                objective3.setDisplaySlot(DisplaySlot.SIDEBAR);

                                                numberOfPlayers3.setScore(Bukkit.getServer().getOnlinePlayers().size());

                                                Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Feast
                                                        main.state = HungerGamesState.FINISH;

                                                        if (main.state == HungerGamesState.FINISH) {
                                                            objective4.setDisplayName("§3Etape: " + "§bFestin");
                                                            objective4.setDisplaySlot(DisplaySlot.SIDEBAR);

                                                            feastTimeInConfig = main.configurationSectionInfos.getInt("feastTime");
                                                            Location feastLocation = new Location(Bukkit.getServer().getWorld("world"), main.feastLocationX, main.feastLocationY, main.feastLocationZ);
                                                            numberOfPlayers4.setScore(feastTimeInConfig);
                                                            numberOfPlayers4.setScore(listWinner.size());
                                                            for (Player player1 : listWinner) {
                                                                player1.setScoreboard(scoreboard4);


                                                            }

                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 5 minutes");
                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");

                                                            taskFeast = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    --feastTimeInConfig;
                                                                    numberOfPlayers4.setScore(listWinner.size());
                                                                    feastIn.setScore(feastTimeInConfig);

                                                                    for (Player player1 : listWinner) {


                                                                        player1.setScoreboard(scoreboard4);

                                                                    }

                                                                    switch (feastTimeInConfig) {
                                                                        case 240:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 4 minutes");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 180:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 3 minutes");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 120:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 2 minutes");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 60:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 1 minute");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 30:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 30 secondes");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 10:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 10 secondes");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 5:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 5 secondes");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 4:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 4 secondes");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 3:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 3 secondes");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 2:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 2 secondes");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 1:
                                                                            Bukkit.broadcastMessage("§eLe §6festin §esera en §6(§e" + feastLocation.getX() + ", " + feastLocation.getY() + ", " + feastLocation.getZ() + "§6) " + "§edans 1 seconde");
                                                                            Bukkit.broadcastMessage("§eUtilise §6/feast §epour fix la boussole dessus !");
                                                                            break;
                                                                        case 0:
                                                                            Bukkit.getScheduler().cancelTask(taskFeast);
                                                                            Bukkit.broadcastMessage("§eLe festin a commencé !");

                                                                            spawnChest();


                                                                    }


                                                                }
                                                            }, 0L, 20L);

                                                        }


                                                    }
                                                }, 20L * 10);


                                                Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () ->
                                                {
                                                    for (Player player1 : players) {
                                                        if (main.state == HungerGamesState.FIGHT) {
                                                            objectiveTabList3.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                                                            final Score killCount1 = objectiveTabList3.getScore(player1.getName());
                                                            killCount1.setScore(player1.getStatistic(Statistic.PLAYER_KILLS));
                                                            player1.setScoreboard(scoreboard3);
                                                        }

                                                    }
                                                }, 0L, 20L);


                                        }


                                    }
                                }, 0L, 20L);
                            }

                        }
                    }

                }
            }, 0L, 20L);


        }


    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        players.remove(player);

        if (main.state != null) {
            if (main.state == HungerGamesState.CHOOSING) {

                if (playerKit.containsKey(player.getName())) {

                    event.setQuitMessage(null);
                    numberOfPlayers.setScore(Bukkit.getServer().getOnlinePlayers().size() - 1);
                    player.setScoreboard(scoreboard);

                    playerKit.remove(player.getName());


                } else {
                    event.setQuitMessage(null);
                    numberOfPlayers.setScore(Bukkit.getServer().getOnlinePlayers().size() - 1);
                    player.setScoreboard(scoreboard);
                }

            } else if (main.state == HungerGamesState.INVINCIBILITY && player.getGameMode() == GameMode.SURVIVAL) {
                event.setQuitMessage("§c" + player.getName() + "§4(§c" + playerKit.get(player.getName()) + "§4)" + " quitte la partie.");
                listWinner.remove(player);
                player.setHealth(0.0D);
                player.getWorld().strikeLightningEffect(player.getLocation());
                numberOfPlayers2.setScore(Bukkit.getServer().getOnlinePlayers().size() - 1);
                player.setScoreboard(scoreboard2);

                if (listWinner.size() == 1) {
                    for (Player winner : listWinner) {
                        Bukkit.broadcastMessage(ChatColor.RED + winner.getName() + " " + "a gagné !");
                        restartServer();
                    }
                }

            } else if (main.state == HungerGamesState.FIGHT && player.getGameMode() == GameMode.SURVIVAL) {
                event.setQuitMessage("§c" + player.getName() + "§4(§c" + playerKit.get(player.getName()) + "§4)" + " est mort.");
                listWinner.remove(player);
                player.setHealth(0.0D);
                player.getWorld().strikeLightningEffect(player.getLocation());
                numberOfPlayers3.setScore(Bukkit.getServer().getOnlinePlayers().size() - 1);
                player.setScoreboard(scoreboard3);


                if (listWinner.size() == 1) {
                    for (Player winner : listWinner) {
                        Bukkit.broadcastMessage(ChatColor.RED + winner.getName() + " " + "a gagné !");
                        restartServer();
                    }
                }


            } else if (main.state == HungerGamesState.FINISH && player.getGameMode() == GameMode.SURVIVAL) {
                event.setQuitMessage("§c" + player.getName() + "§4(§c" + playerKit.get(player.getName()) + "§4)" + " est mort.");
                listWinner.remove(player);
                player.setHealth(0.0D);
                player.getWorld().strikeLightningEffect(player.getLocation());
                numberOfPlayers4.setScore(listWinner.size());
                player.setScoreboard(scoreboard4);


                if (listWinner.size() == 1) {
                    for (Player winner : listWinner) {
                        Bukkit.broadcastMessage(ChatColor.RED + winner.getName() + " " + "a gagné !");
                        restartServer();
                    }
                }


            }

            if(playerKit.get(player.getName()) == HungerGamesKits.BeastMaster)
            {
                wolves.remove(player.getName(), listWolf);
            }

        }


        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setQuitMessage(null);
            playerInSpectator.remove(player);
        }


        if (Bukkit.getServer().getOnlinePlayers().size() <= main.configurationSectionInfos.getInt("playersNeeded") && main.state == HungerGamesState.CHOOSING) {
            Bukkit.getScheduler().cancelTask(taskPreparation);
            startingIn.setScore(preparationTimeInConfig);
            numberOfPlayers.setScore(Bukkit.getServer().getOnlinePlayers().size());
            player.setScoreboard(scoreboard);


        }
    }

    private void restartServer() {
        for (Player playersRemaining : Bukkit.getOnlinePlayers()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                @Override
                public void run() {

                    out.writeUTF("Connect");
                    out.writeUTF("lobby");
                    Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(main, "BungeeCord");


                    playersRemaining.sendPluginMessage(main, "BungeeCord", out.toByteArray());
                    playersRemaining.sendMessage("§cMerci d'avoir jouer sur nos LifecraftV2 HungerGames !");

                    Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                        @Override
                        public void run() {


                            Bukkit.shutdown();

                        }

                    }, 20L * 2);


                }
            }, 20L * 10);
        }
    }


    public void spawnChest() {

        for (int i = 1; i < 24; i++) {
            int locationToPlaceBlockX;
            int locationToPlaceBlockY;
            int locationToPlaceBlockZ;
            World world = Bukkit.getServer().getWorld("world");

            switch (i) {

                case 1:
                    locationToPlaceBlockX = main.configurationSectionChest.getInt("chest" + i + "X");
                    locationToPlaceBlockY = main.configurationSectionInfos.getInt("chest" + i + "Y");
                    locationToPlaceBlockZ = main.configurationSectionInfos.getInt("chest" + i + "Z");
                    Location locationToPlaceBlock = new Location(world, locationToPlaceBlockX, locationToPlaceBlockY, locationToPlaceBlockZ);
                    Block block = locationToPlaceBlock.getBlock();
                    block.setType(Material.CHEST);
                    Chest chest = (Chest) block.getState();
                    Inventory chestInventory = chest.getInventory();
                    chestInventory.addItem(new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 16));
                    break;


                case 2:
                    locationToPlaceBlockX = main.configurationSectionChest.getInt("chest" + i + "X");
                    locationToPlaceBlockY = main.configurationSectionInfos.getInt("chest" + i + "Y");
                    locationToPlaceBlockZ = main.configurationSectionInfos.getInt("chest" + i + "Z");
                    locationToPlaceBlock = new Location(world, locationToPlaceBlockX, locationToPlaceBlockY, locationToPlaceBlockZ);
                    block = locationToPlaceBlock.getBlock();
                    locationToPlaceBlock.getBlock().setType(Material.CHEST);
                    chest = (Chest) block.getState();
                    chestInventory = chest.getInventory();
                    chestInventory.addItem(new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 16));
                    break;

                case 3:
                    locationToPlaceBlockX = main.configurationSectionChest.getInt("chest" + i + "X");
                    locationToPlaceBlockY = main.configurationSectionInfos.getInt("chest" + i + "Y");
                    locationToPlaceBlockZ = main.configurationSectionInfos.getInt("chest" + i + "Z");
                    locationToPlaceBlock = new Location(world, locationToPlaceBlockX, locationToPlaceBlockY, locationToPlaceBlockZ);
                    block = locationToPlaceBlock.getBlock();
                    block.setType(Material.CHEST);
                    chest = (Chest) block.getState();
                    chestInventory = chest.getInventory();
                    chestInventory.addItem(new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 16));
                    break;


            }
        }
    }

    private Set<Block> getNearbyBlocks(Block start,  ArrayList<Material> allowedMaterials, HashSet<Block> blocks) {

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    Block block = start.getLocation().clone().add(x, y, z).getBlock();
                    if (block != null && !blocks.contains(block) && allowedMaterials.contains(block.getType())) {
                        blocks.add(block);
                        blocks.addAll(getNearbyBlocks(block, allowedMaterials, blocks));
                    }
                }
            }
        }
        return blocks;
    }

    public Set<Block> getTree(Block start, ArrayList<Material> allowedMaterials) {
        return getNearbyBlocks(start, allowedMaterials, new HashSet<Block>());
    }



}