/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.command.commands

import op.air.airclient.AirClient.isStarting
import op.air.airclient.AirClient.moduleManager
import op.air.airclient.features.command.Command
import op.air.airclient.features.command.CommandManager
import op.air.airclient.file.FileManager.accountsConfig
import op.air.airclient.file.FileManager.clickGuiConfig
import op.air.airclient.file.FileManager.friendsConfig
import op.air.airclient.file.FileManager.hudConfig
import op.air.airclient.file.FileManager.loadConfig
import op.air.airclient.file.FileManager.modulesConfig
import op.air.airclient.file.FileManager.valuesConfig
import op.air.airclient.file.FileManager.xrayConfig
import op.air.airclient.script.ScriptManager.disableScripts
import op.air.airclient.script.ScriptManager.reloadScripts
import op.air.airclient.script.ScriptManager.unloadScripts
import op.air.airclient.ui.font.Fonts

object ReloadCommand : Command("reload", "configreload") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Reloading...")
        isStarting = true

        chat("§c§lReloading commands...")
        CommandManager.registerCommands()

        disableScripts()
        unloadScripts()

        for (module in moduleManager)
            moduleManager.generateCommand(module)

        chat("§c§lReloading scripts...")
        reloadScripts()

        chat("§c§lReloading fonts...")
        Fonts.loadFonts()

        chat("§c§lReloading modules...")
        loadConfig(modulesConfig)


        chat("§c§lReloading values...")
        loadConfig(valuesConfig)

        chat("§c§lReloading accounts...")
        loadConfig(accountsConfig)

        chat("§c§lReloading friends...")
        loadConfig(friendsConfig)

        chat("§c§lReloading xray...")
        loadConfig(xrayConfig)

        chat("§c§lReloading HUD...")
        loadConfig(hudConfig)

        chat("§c§lReloading ClickGUI...")
        loadConfig(clickGuiConfig)

        isStarting = false
        chat("Reloaded.")
    }
}
