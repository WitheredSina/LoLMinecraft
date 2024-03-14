package net.witheredsina.lolminecraft.Listeners.Zed

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID

class ShadowStep: Listener {
    private val cooldowns = HashMap<UUID, Long>()
    private val dashingPlayers: MutableList<UUID> = mutableListOf()

    private val name = "${RED}Shadow's Step [W]"
    private val cooldown: Long = 10 // Seconds

    private val item = ItemStack(Material.DIAMOND_SWORD).apply {
        itemMeta = itemMeta?.apply {
            setDisplayName(name)
            addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            isUnbreakable = true
            lore = listOf(
                "${RED}Shadow Step ${GOLD}${BOLD}[Right Click]: ",
                "${GRAY}Lunge forward, Gaining Invisibility and Speed for ${AQUA}3 Seconds${GRAY} and Dealing${AQUA}5 Damage ${GRAY}to nearby Mobs on landing.",
                "${DARK_GRAY}Cooldown: ${AQUA}$cooldown Seconds",
            )
        }
    }

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        if(e.action != Action.RIGHT_CLICK_AIR) return
        val hand = e.player.inventory.itemInMainHand
        if (hand != item) return
        val p = e.player
        if (getCooldown(p)) return

        p.velocity = p.location.direction.multiply(1.5)
        p.playSound(p, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 0.9f)
        dashingPlayers.add(p.uniqueId)
    }

    @EventHandler
    fun onFallDamage(e: EntityDamageEvent){
        if (!dashingPlayers.contains(e.entity.uniqueId)) return
        if (e.cause != EntityDamageEvent.DamageCause.FALL) return
        e.isCancelled = true
        val p = e.entity as Player
        dashingPlayers.remove(p.uniqueId)
        p.addPotionEffects(mutableListOf<PotionEffect>().apply {
            add(PotionEffect(PotionEffectType.INVISIBILITY, 20 * 3, 0))
            add(PotionEffect(PotionEffectType.SPEED, 20 * 3, 0))
        })
        p.getNearbyEntities(2.0, 2.0, 2.0).forEach {
            if (it is LivingEntity) {
                it.damage(5.0)
                it.location.world?.spawnParticle(Particle.LANDING_LAVA, it.location, 10, 0.0, 0.0, 0.0)
                it.world.playSound(it.location, Sound.ENTITY_GENERIC_EXPLODE, .5f, 1.0f)
            }
        }
    }


    private fun getCooldown(p: Player): Boolean {
        if (cooldowns.containsKey(p.uniqueId)) {
            if (cooldowns[p.uniqueId]!! > System.currentTimeMillis()) {
                val remaining = (cooldowns[p.uniqueId]!! - System.currentTimeMillis()) / 1000
                p.sendMessage("$name ${YELLOW}will be ready in $AQUA${remaining}$YELLOW Second(s)")
                return true
            }
        }
        cooldowns[p.uniqueId] = System.currentTimeMillis() + (3 * 1000)
        return false
    }

    fun getItem(): ItemStack {
        return item
    }
}