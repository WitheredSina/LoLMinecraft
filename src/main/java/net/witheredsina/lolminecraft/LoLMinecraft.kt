package net.witheredsina.lolminecraft

import net.witheredsina.lolminecraft.Commands.ZedCMD
import net.witheredsina.lolminecraft.Listeners.Zed.DeathMark
import net.witheredsina.lolminecraft.Listeners.Zed.ShadowSlash
import net.witheredsina.lolminecraft.Listeners.Zed.ShadowStep
import org.bukkit.plugin.java.JavaPlugin

class LoLMinecraft : JavaPlugin() {
    override fun onEnable() {
        // Zed
        server.pluginManager.registerEvents(ShadowSlash(), this)
        server.pluginManager.registerEvents(ShadowStep(), this)
        server.pluginManager.registerEvents(DeathMark(), this)
//        server.pluginManager.registerEvents(RazorShuriken(), this)



        getCommand("lol")?.setExecutor(ZedCMD())
        getCommand("lol")?.tabCompleter = TabComplete()
    }
}
