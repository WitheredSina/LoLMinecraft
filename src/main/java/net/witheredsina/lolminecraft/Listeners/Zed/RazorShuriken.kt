package net.witheredsina.lolminecraft.Listeners.Zed

import net.witheredsina.lolminecraft.LoLMinecraft
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class RazorShuriken: Listener {
    private val shurikenRange = 10
    private val shurikenVelocity = 1.5

    /*@EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player: Player = event.player
        if (player.inventory.itemInMainHand.type == Material.WOODEN_SWORD) {
            val shuriken = player.world.spawn(Location(player.world, player.eyeLocation.x, player.eyeLocation.y, player.eyeLocation.z), ArmorStand::class.java)
            shuriken.isVisible = true
            shuriken.customName = "RazorShuriken" // Set a custom name to prevent despawning
            shuriken.isCustomNameVisible = false
            shuriken.isMarker = true
            shuriken.setGravity(false)
            shuriken.setVelocity(player.location.direction.normalize().multiply(shurikenVelocity)) // Set initial velocity
            shuriken.isSmall = true

            val plugin = Bukkit.getPluginManager().getPlugin("ZedMinecraft")
            if(plugin !is ZedMinecraft && plugin == null) throw ClassCastException("The plugin is not an instance of ZedMinecraft")
            val main = plugin as ZedMinecraft

            var isShurikenActive = true // Flag to track the status of the shuriken

            val task = main.server.scheduler.runTaskTimer(main, Runnable {
                if (isShurikenActive) { // Check if the shuriken is still active
                    if (shuriken.isDead || shuriken.velocity.length() < 0.1 || shuriken.location.distance(player.location) > shurikenRange) {
                        shuriken.remove()
                        isShurikenActive = false // Set the flag to false to stop further execution
                    } else {
                        // Apply velocity to the shuriken using setVelocity method
                        shuriken.setVelocity(player.location.direction.normalize().multiply(shurikenVelocity))

                        checkShurikenRange(shuriken, player)
                    }
                }
            }, 0L, 1L)
        }
    }*/

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player: Player = event.player
        if (player.inventory.itemInMainHand.type == Material.WOODEN_SWORD) {
            val shuriken = player.world.spawn(Location(player.world, player.eyeLocation.x, player.eyeLocation.y, player.eyeLocation.z), ArmorStand::class.java)
            shuriken.isVisible = true
            shuriken.customName = "RazorShuriken" // Set a custom name to prevent despawning
            shuriken.isCustomNameVisible = false
            shuriken.isMarker = true
            shuriken.setGravity(false)
            shuriken.velocity = player.location.direction.normalize().multiply(shurikenVelocity) // Set initial velocity
            shuriken.isSmall = true

            val plugin = Bukkit.getPluginManager().getPlugin("ZedMinecraft")
            if(plugin !is LoLMinecraft && plugin == null) throw ClassCastException("The plugin is not an instance of ZedMinecraft")
            val main = plugin as LoLMinecraft

            var isShurikenActive = true
            val task = main.server.scheduler.runTaskTimer(main, Runnable {
                if (isShurikenActive) {
                    if (shuriken.isDead || shuriken.location.distance(player.location) > shurikenRange) {
                        shuriken.remove()
                        isShurikenActive = false
                    } else {
                        val direction = player.location.toVector().subtract(shuriken.location.toVector()).normalize()
                        shuriken.velocity = direction.multiply(shurikenVelocity) // Update velocity
                        shuriken.teleport(shuriken.location.add(shuriken.velocity)) // Update position based on velocity

                        checkShurikenRange(shuriken, player)
                    }
                }
            }, 0L, 1L)
        }
    }

    private fun checkShurikenRange(shuriken: ArmorStand, player: Player) {
        val entitiesHit = shuriken.getNearbyEntities(0.5, 0.5, 0.5)
            .filterIsInstance<LivingEntity>()

        entitiesHit.forEach { entity ->
            // Apply slowness to the entity
            if(entity != player) {
                entity.damage(10.0)
                entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 60, 1))
            }
        }
    }
}
