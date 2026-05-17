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

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import op.air.airclient.AirClient
import op.air.airclient.api.ClientApi
import op.air.airclient.api.Status
import op.air.airclient.api.autoSettingsList
import op.air.airclient.api.loadSettings
import op.air.airclient.config.SettingsUtils
import op.air.airclient.features.command.Command
import op.air.airclient.ui.client.hud.HUD.addNotification
import op.air.airclient.ui.client.hud.element.elements.Notification
import op.air.airclient.utils.client.ClientUtils.LOGGER
import op.air.airclient.utils.io.MiscUtils
import op.air.airclient.utils.kotlin.SharedScopes
import op.air.airclient.utils.kotlin.StringUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object SettingsCommand : Command("autosettings", "autosetting", "settings", "setting", "config") {

    private val mutex = Mutex()

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        val usedAlias = args[0].lowercase()

        if (args.size <= 1) {
            chatSyntax("$usedAlias <load/list/upload/report>")
            return
        }

        if (mutex.isLocked) {
            chat("§cPrevious task is not finished!")
            return
        }

        SharedScopes.IO.launch {
            mutex.withLock {
                when (args[1].lowercase()) {
                    "load" -> loadSettings(args)
                    "report" -> reportSettings(args)
                    "upload" -> uploadSettings(args)
                    "list" -> listSettings()
                    else -> chatSyntax("$usedAlias <load/list/upload/report>")
                }
            }
        }
    }

    // Load subcommand
    private fun loadSettings(args: Array<String>) {
        if (args.size < 3) {
            chatSyntax("${args[0].lowercase()} load <name/url>")
            return
        }

        try {
            val settings = SettingsUtils.loadFromUrl(args[2])

            chat("Applying settings...")
            SettingsUtils.applyScript(settings)
            chat("§6Settings applied successfully")
            addNotification(Notification("Settings Command", "Successfully updated settings!"))
            playEdit()
        } catch (e: Exception) {
            LOGGER.error("Failed to load settings", e)
            chat("Failed to load settings: ${e.message}")
        }
    }

    // Report subcommand
    private fun reportSettings(args: Array<String>) {
        if (args.size < 3) {
            chatSyntax("${args[0].lowercase()} report <name>")
            return
        }

        try {
            val response = runBlocking { ClientApi.reportSettings(settingId = args[2]) }
            when (response.status) {
                Status.SUCCESS -> chat("§6${response.message}")
                Status.ERROR -> chat("§c${response.message}")
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to report settings", e)
            chat("Failed to report settings: ${e.message}")
        }
    }

    // Upload subcommand
    private fun uploadSettings(args: Array<String>) {
        val option = if (args.size > 3) StringUtils.toCompleteString(args, 3).lowercase() else "all"
        val all = "all" in option
        val values = all || "values" in option
        val binds = all || "binds" in option
        val states = all || "states" in option

        if (!values && !binds && !states) {
            chatSyntax("${args[0].lowercase()} upload [all/values/binds/states]...")
            return
        }

        try {
            chat("§9Creating settings...")
            val settingsScript = SettingsUtils.generateScript(values, binds, states)
            chat("§9Uploading settings...")

            val serverData = mc.currentServerData ?: error("You need to be on a server to upload settings.")

            val name = "${AirClient.clientCommit}-${serverData.serverIP.replace(".", "_")}"
            val response = runBlocking {
                ClientApi.uploadSettings(
                    name = name.toRequestBody(),
                    contributors = mc.session.username.toRequestBody(),
                    settingsFile = MultipartBody.Part.createFormData(
                        "settings_file",
                        "settings_file",
                        settingsScript.toByteArray().toRequestBody("application/octet-stream".toMediaTypeOrNull())
                    )
                )
            }

            when (response.status) {
                Status.SUCCESS -> {
                    chat("§6${response.message}")
                    chat("§9Token: §6${response.token}")

                    // Store token in clipboard
                    MiscUtils.copy(response.token)
                }

                Status.ERROR -> chat("§c${response.message}")
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to upload settings", e)
            chat("Failed to upload settings: ${e.message}")
        }
    }

    // List subcommand
    private fun listSettings() {
        chat("Loading settings...")
        loadSettings(false) {
            for (setting in it) {
                chat("> ${setting.settingId} (Last updated: ${setting.date}, Status: ${setting.statusType.displayName})")
            }
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) {
            return emptyList()
        }

        return when (args.size) {
            1 -> listOf("list", "load", "upload", "report").filter { it.startsWith(args[0], true) }
            2 -> {
                when (args[0].lowercase()) {
                    "load", "report" -> {
                        if (autoSettingsList == null) {
                            loadSettings(true, 500)
                        }

                        return autoSettingsList?.filter { it.settingId.startsWith(args[1], true) }?.map { it.settingId }
                            ?: emptyList()
                    }

                    "upload" -> {
                        return listOf("all", "values", "binds", "states").filter { it.startsWith(args[1], true) }
                    }

                    else -> emptyList()
                }
            }

            else -> emptyList()
        }
    }
}
