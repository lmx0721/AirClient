/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.command.commands

import op.air.airclient.features.command.Command
import op.air.airclient.file.FileManager.dir
import op.air.airclient.file.FileManager.hudConfig
import op.air.airclient.file.FileManager.loadConfig
import op.air.airclient.file.FileManager.themesDir
import op.air.airclient.ui.client.hud.HUD.addNotification
import op.air.airclient.ui.client.hud.element.elements.Notification
import op.air.airclient.utils.client.ClientUtils.LOGGER
import java.awt.Desktop
import java.io.File
import java.io.FileFilter
import java.io.IOException

object LocalThemesCommand : Command("localthemes", "localtheme") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        val usedAlias = args[0].lowercase()

        if (args.size <= 1) {
            chatSyntax("$usedAlias <load/save/list/delete/folder>")
            return
        }

        when (args[1].lowercase()) {
            "load" -> {
                if (args.size <= 2) {
                    chatSyntax("$usedAlias load <name>")
                    return
                }

                val themeFile = File(themesDir, args[2] + ".json")
                val hudFile = File(dir, "hud.json")

                if (!themeFile.exists()) {
                    chat("§cTheme file does not exist!")
                    return
                }

                try {
                    chat("§9Loading theme...")
                    themeFile.copyTo(hudFile, true)
                    loadConfig(hudConfig)
                    chat("§6Theme applied successfully.")
                    addNotification(Notification("Local Themes Command", "Updated Theme"))
                    playEdit()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            "save" -> {
                if (args.size <= 2) {
                    chatSyntax("$usedAlias save <name>...")
                    return
                }

                val themeFile = File(themesDir, args[2] + ".json")

                try {
                    if (themeFile.exists())
                        themeFile.delete()

                    chat("§9Creating theme...")
                    themeFile.createNewFile()

                    chat("§9Saving theme...")
                    File(dir, "hud.json").copyTo(themeFile, true)
                    loadConfig(hudConfig)

                    chat("§6Theme saved successfully.")
                } catch (throwable: Throwable) {
                    chat("§cFailed to create local theme: §3${throwable.message}")
                    LOGGER.error("Failed to create local theme.", throwable)
                }
            }

            "delete" -> {
                if (args.size <= 2) {
                    chatSyntax("$usedAlias delete <name>")
                    return
                }

                val themeFile = File(themesDir, args[2] + ".json")

                if (!themeFile.exists()) {
                    chat("§cTheme file does not exist!")
                    return
                }

                themeFile.delete()
                chat("§6Theme file deleted successfully.")
            }

            "list" -> {
                chat("§cThemes:")

                val themes = getLocalThemes() ?: return

                for (file in themes) {
                    val fileName = file.name.removeSuffix(".json")

                    chat("> $fileName")
                }
            }

            "folder" -> {
                Desktop.getDesktop().open(themesDir)
            }
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("delete", "list", "load", "save", "folder").filter { it.startsWith(args[0], true) }

            2 ->
                when (args[0].lowercase()) {
                    "delete", "load", "save" -> {
                        val themes = getLocalThemes() ?: return emptyList()

                        themes
                            .map { it.name.replace(".json", "") }
                            .filter { it.startsWith(args[1], true) }
                    }

                    else -> emptyList()
                }

            else -> emptyList()
        }
    }

    private fun getLocalThemes() = themesDir.listFiles(FileFilter { it.extension == "json" })
}
