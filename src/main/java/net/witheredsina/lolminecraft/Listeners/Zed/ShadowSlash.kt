package net.witheredsina.lolminecraft.Listeners.Zed

import org.bukkit.*
import org.bukkit.ChatColor.*
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.cos
import kotlin.math.sin


class ShadowSlash: Listener {
    private val name = "${RED}Shadow Slash [E]"
    private val cooldown = 3 // Seconds
    private val item: ItemStack = ItemStack(Material.NETHERITE_SWORD, 1).apply {
        val meta = itemMeta?.apply {
            setDisplayName(name)
            lore = listOf(
                "${RED}Shadow Slash ${GOLD}${BOLD}[Right Click]: ",
                "${GRAY}Slash around you and deal ${AQUA}3 Damage ${GRAY}to nearby Mobs.",
                "${DARK_GRAY}Cooldown: ${AQUA}3 Seconds",
                "${DARK_GRAY}Range: ${AQUA}3 Blocks"
            )
            isUnbreakable = true
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        }
        this.itemMeta = meta
    }

    private val cooldowns = HashMap<UUID, Long>()

    @EventHandler
    fun onShadowSlash(e: PlayerInteractEvent) {
        if(!(e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR)) return

        val hand: ItemStack = e.player.inventory.itemInMainHand
        if (hand != item) return
        val p = e.player

        if (getCooldown(p)) return

        val world = p.world
        val playerLocation = p.location
        val startRadius = 1
        val endRadius = 3.0
        val particleCount = 100
        val circleCount = 20

        for (j in 0 until circleCount) {
            val currentRadius: Double =
                startRadius + (endRadius - startRadius) * j / (circleCount - 1)
            for (i in 0 until particleCount) {
                val angle = 2 * Math.PI * i / particleCount

                val x = playerLocation.x + currentRadius * cos(angle)
                val z = playerLocation.z + currentRadius * sin(angle)
                val particleLocation = Location(world, x, playerLocation.y + 1, z)

                world.spawnParticle(Particle.REDSTONE, particleLocation, 1, DustOptions(Color.BLACK, 1.0f))
            }
        }
        p.playSound(p, Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1f)

        var nearbyEntities = p.getNearbyEntities(4.0, 4.0, 4.0)
        for (entity in nearbyEntities) {
            if (entity is LivingEntity) {
                entity.damage(6.0, p)
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
