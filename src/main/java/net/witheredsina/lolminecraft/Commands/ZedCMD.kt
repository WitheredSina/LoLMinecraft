package net.witheredsina.lolminecraft.Commands

import net.witheredsina.lolminecraft.Listeners.Zed.DeathMark
import net.witheredsina.lolminecraft.Listeners.Zed.ShadowSlash
import net.witheredsina.lolminecraft.Listeners.Zed.ShadowStep
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ZedCMD : CommandExecutor {
    val commandsList: MutableList<String> = mutableListOf()
    init {
        commandsList.add("Zed")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p: Player = sender as? Player ?: return false

        when (args[0].lowercase()) {
            "zed" -> {
                val items: MutableList<ItemStack> = mutableListOf()

                items.add(ShadowSlash().getItem())
                items.add(ShadowStep().getItem())
                items.add(DeathMark().getItem())

                for (item in items) p.inventory.addItem(item)
                return true
            }
        }


        return false
    }
}
