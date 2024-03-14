package net.witheredsina.lolminecraft.Listeners.Zed

import net.witheredsina.lolminecraft.LoLMinecraft
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

class DeathMark: Listener {
    private val marks = mutableMapOf<Entity, Double>()
    private val cooldowns = mutableMapOf<UUID, Long>()
    private val cooldown = 30
    private val name = "${ChatColor.RED}Death Mark"
    private val item: ItemStack = ItemStack(Material.STONE_SWORD, 1).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(name)
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
            addItemFlags(ItemFlag.HIDE_DYE)
            addItemFlags(ItemFlag.HIDE_PLACED_ON)
            addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
            lore = listOf(
                "$name ${ChatColor.GOLD}${ChatColor.BOLD}[Right Click]: ",
                "${ChatColor.GRAY}Mark an Enemy.",
                "after ${ChatColor.AQUA}3 Seconds${ChatColor.GRAY}, the mark Detonates and repeats ${ChatColor.AQUA}50%" +
                        " ${ChatColor.GRAY}of damage dealt to the target.",
                "${ChatColor.DARK_GRAY}Cooldown: ${ChatColor.AQUA}$cooldown Seconds",
            )
            isUnbreakable = true
        }
    }

    @EventHandler
    fun onRightClickEntity(e: PlayerInteractAtEntityEvent){
        val p = e.player
        val target = e.rightClicked
        if(p.inventory.itemInMainHand != item) return
        if(target !is LivingEntity) return
        if(target.isDead) return
        if(getCooldown(p)) return
        if(marks[target] != null) {
            p.sendMessage("${ChatColor.RED}This entity is already Marked!")
            cooldowns.remove(p.uniqueId)
            return
        }
        marks[target] = 0.0
        p.playSound(target.location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 1.0f)

        val plugin = Bukkit.getPluginManager().getPlugin("LoLMinecraft")
        if(plugin !is LoLMinecraft && plugin == null) throw ClassCastException("The plugin is not an instance of ZedMinecraft")
        val main = plugin as LoLMinecraft

        Bukkit.getScheduler().runTaskLater(main, Runnable {
            if (target.isDead) return@Runnable
            p.sendMessage("${ChatColor.GREEN}Marked ${target.name} with ${ChatColor.RED}${marks[target]}${ChatColor.GREEN} damage!")
            target.damage(marks[target]!! * 0.35)
            target.world.strikeLightning(target.location)
            marks.remove(target)
        }, 3 * 20L)
    }

    @EventHandler
    fun onMarkedEntityDamage(e: EntityDamageEvent){
        if (!marks.containsKey(e.entity)) return
        val damage = e.finalDamage
        val markDamage = marks[e.entity] ?: 0.0
        marks[e.entity] = markDamage + damage
    }

    private fun getCooldown(p: Player): Boolean {
        if (cooldowns.containsKey(p.uniqueId)) {
            if (cooldowns[p.uniqueId]!! > System.currentTimeMillis()) {
                val remaining = (cooldowns[p.uniqueId]!! - System.currentTimeMillis()) / 1000
                p.sendMessage("$name ${ChatColor.YELLOW}will be ready in ${ChatColor.AQUA}${remaining}${ChatColor.YELLOW} Second(s)")
                return true
            }
        }
        cooldowns[p.uniqueId] = System.currentTimeMillis() + (15 * 1000)
        return false
    }
    fun getItem(): ItemStack {
        return item
    }

}
