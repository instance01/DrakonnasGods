package com.comze_instancelabs.gods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
//import org.bukkit.material.Sign;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


/**
 * 
 * @author instancelabs
 *
 */

public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("gods")){
			if(sender.hasPermission("gods.create")){
				if(args.length > 0){
					String action = args[0];
					if(action.equalsIgnoreCase("create")){
						if(args.length > 3){
							String effect = args[3];
							if(effect.equals("BLINDNESS") || effect.equals("CONFUSION") || effect.equals("DAMAGE_RESISTANCE") || effect.equals("FAST_DIGGING") || effect.equals("FIRE_RESISTANCE") || effect.equals("HARM") || effect.equals("HEAL") || effect.equals("INCREASE_DAMAGE") || effect.equals("INVISIBILITY") || effect.equals("JUMP") || effect.equals("NIGHT_VISION") || effect.equals("POISON") || effect.equals("REGENERATION") || effect.equals("SLOW") || effect.equals("SLOW_DIGGING") || effect.equals("SPEED") || effect.equals("WATER_BREATHING") || effect.equals("WEAKNESS") || effect.equals("WITHER")){
								String godname = args[1];
								getConfig().set(godname + ".xp", Integer.parseInt(args[2]));
								getConfig().set(godname + ".potion", args[3]);
								this.saveConfig();
								sender.sendMessage("§f[§3DrakonnasGods§f] §2A new God has been created!");	
							}else{
								sender.sendMessage("§4There's no such potion effect. Use §3/gods potioneffects §4to get a list.");
							}
						}else{
							sender.sendMessage("§2/gods create [name] [points] [potion] [duration]");
						}
					}else if(action.equalsIgnoreCase("remove")){
						if(args.length > 1){
							String godname = args[1];
							getConfig().set(godname, null);
							this.saveConfig();
							sender.sendMessage("§f[§3DrakonnasGods§f] §2You removed " + godname + ".");
						}
					}else if(action.equalsIgnoreCase("potioneffects")){
						sender.sendMessage("§2SPEED, SLOW, FAST_DIGGING, SLOW_DIGGING, INCREASE_DAMAGE, HEAL, HARM, JUMP, CONFUSION, REGENERATION, DAMAGE_RESISTANCE, FIRE_RESISTANCE, WATER_BREATHING, INVISIBILITY, BLINDNESS, NIGHT_VISION, WEAKNESS, POISON, WITHER");
					}else if(action.equalsIgnoreCase("reload")){
						this.reloadConfig();
						sender.sendMessage("§f[§3DrakonnasGods§f] §2Successfully reloaded Gods configuration.");
					}else if(action.equalsIgnoreCase("resetcooldown")){
						if(args.length > 1){
							String player = args[1];
							getConfig().set(player, null);
							this.saveConfig();
							sender.sendMessage("§f[§3DrakonnasGods§f] §2Successfully reset " + player + "'s cooldown from the config.");
						}
					}else if(action.equalsIgnoreCase("list")){
						this.reloadConfig();
						sender.sendMessage("§f[§3DrakonnasGods§f] §2Too many..");
					}else if(action.equalsIgnoreCase("info")){
						sender.sendMessage("§3DrakonnasGods §2was developed by instancelabs with fucking great help of hades700. ");
					}
				}else{
					sender.sendMessage("§3DrakonnasGods help:");
					sender.sendMessage("§2/gods create [name] [points] [potion] [duration]");
					sender.sendMessage("§2/gods remove [name]");
					sender.sendMessage("§2/gods reload");
					sender.sendMessage("§2/gods resetcooldown [player]");
					sender.sendMessage("§2/gods list");
					sender.sendMessage("§2/gods info");
					sender.sendMessage("§2/gods potioneffects");
				}
			}else{
				sender.sendMessage("§4You don't have permission!");
			}
			return true;
		}
		return false;
	}
	
	
	// if player places sign and diamond block there -> new temple
	
	// Sign:
	// [god]
	// name
	
   @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player p = event.getPlayer();
        if(event.getLine(0).toLowerCase().equalsIgnoreCase("[god]") && p.hasPermission("gods.create")){
    		String godname = event.getLine(1);
    		if(getConfig().isSet(godname)){
    			if(isValidTemple(event.getBlock())){
    				event.setLine(0, "§f[§bDR Gods§f]");
    				event.setLine(2, Integer.toString(getConfig().getInt(godname + ".xp")) + " DR Points");
    				p.sendMessage("§f[§3DrakonnasGods§f] §2You successfully created a temple!");
    			}else{
    				event.setLine(0, "§f[§4DR Gods§f]");
    				p.sendMessage("§f[§3DrakonnasGods§f] §cA temple sign needs two diamond blocks to be attached to!");
    			}
    		}else{
    			p.sendMessage("§f[§3DrakonnasGods§f] §cThere's no god by that name!");
    			event.getBlock().breakNaturally();
    		}
        }
    }
	
	
	/*
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		
	}*/
	
	
	// if player uses sign and one hour away -> give xp
	@EventHandler
	public void onSignUse(PlayerInteractEvent event)
	{
		if (event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
		    if (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN)
		    {
		        final Sign s = (Sign) event.getClickedBlock().getState();
		        if(s.getLine(0).equalsIgnoreCase("§f[§bdr gods§f]")){
		        	String godname = s.getLine(1);
		        	if(isValidTemple(event.getClickedBlock())){
		        		if(!getConfig().isSet(event.getPlayer().getName() + ".hoursleft")){
			        		SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			        		StringBuilder test = new StringBuilder(sdfToDate.format(new Date()));
			        		getConfig().set(event.getPlayer().getName() + ".hoursleft", test.toString());
			        		this.saveConfig();
			        		//event.getPlayer().giveExp(getConfig().getInt(godname + ".xp"));
			        		getServer().dispatchCommand(getServer().getConsoleSender(), "enjin addpoints " + event.getPlayer().getName() + " " + Integer.toString(getConfig().getInt(godname + ".xp")));
			        		//PotionEffect speed = PotionEffectType.SPEED.createEffect(99999999, 7);
			        		PotionEffect speed = PotionEffectType.getByName(getConfig().getString(godname + ".potion")).createEffect(12000, 1);
				            event.getPlayer().addPotionEffect(speed, true);
				            event.getPlayer().playEffect(event.getPlayer().getLocation(), Effect.POTION_BREAK, 5);
				            event.getPlayer().sendMessage("§f[§3DrakonnasGods§f] §cYou got " + getConfig().getInt(godname + ".xp") + " DR Points!");
		        		}else{
			        		if(checkHours(event.getPlayer())){
			        			//event.getPlayer().giveExp(getConfig().getInt(godname + ".xp"));
			        			getServer().dispatchCommand(getServer().getConsoleSender(), "enjin addpoints " + event.getPlayer().getName() + " " + Integer.toString(getConfig().getInt(godname + ".xp")));
			        			SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			        			StringBuilder test = new StringBuilder(sdfToDate.format(new Date()));
				        		getConfig().set(event.getPlayer().getName() + ".hoursleft", test.toString());
				        		this.saveConfig();
				        		PotionEffect speed = PotionEffectType.getByName(getConfig().getString(godname + ".potion")).createEffect(12000, 1);
					            event.getPlayer().addPotionEffect(speed, true);
					            event.getPlayer().playEffect(event.getPlayer().getLocation(), Effect.POTION_BREAK, 5);
				        		event.getPlayer().sendMessage("§f[§3DrakonnasGods§f] §cYou got " + getConfig().getInt(godname + ".xp") + " DR Points!");
			        		}else{
			        			event.getPlayer().sendMessage("§f[§3DrakonnasGods§f] §cYou need to wait 24 hours between praying.. :/");
			        		}	
		        		}
		        		
		        	}
		        }
		    }
		}
	}
	
	
	/***
	 * Returns if sign is attached to two diamond blocks and thus is a valid temple
	 * @param b the sign
	 * @return returns true if valid, false if not
	 */
	public boolean isValidTemple(Block b){
		org.bukkit.material.Sign s = (org.bukkit.material.Sign) b.getState().getData();
		Block attachedBlock = b.getRelative(s.getAttachedFace());
		if(attachedBlock.getType() == Material.DIAMOND_BLOCK && attachedBlock.getRelative(BlockFace.DOWN).getType() == Material.DIAMOND_BLOCK){
			// we have tow diamond blocks!
			return true;
		}else{
			return false;
		}
	}
	
	
	/***
	 * Checks if player is able to use the action again
	 * @param p Player to check
	 * @return returns true if last use 1 hour ago, false if not
	 */
	public boolean checkHours(Player p){
		SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date datecurrent = new Date();
		String daysdate = getConfig().getString(p.getName() + ".hoursleft");
		//p.sendMessage(daysdate);
		Date date1 = null;
		try {
			date1 = sdfToDate.parse(daysdate);
			System.out.println(date1);
		} catch (ParseException ex2){
			ex2.printStackTrace();
		}
		Integer between = this.hoursBetween(datecurrent, date1);
		getLogger().info(Integer.toString(between));
		if(between > 23 || between < -23){
			return true;
		}else{
			return false;
		}
	}
	
		
	public int hoursBetween(Date d1, Date d2){
	    long differenceMilliSeconds = d2.getTime() - d1.getTime();
	    long hours = differenceMilliSeconds / 1000 / 60 / 60;
	    return (int) hours;
	}
	
}
