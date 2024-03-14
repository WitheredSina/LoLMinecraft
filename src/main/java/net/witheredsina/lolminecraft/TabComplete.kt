package net.witheredsina.lolminecraft

import net.witheredsina.lolminecraft.Commands.ZedCMD
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabComplete: TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return ZedCMD().commandsList
    }
}
