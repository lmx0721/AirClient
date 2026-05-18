/*
 * AirClient Hacked Client
 * A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */
package op.air.airclient

import com.formdev.flatlaf.themes.FlatMacLightLaf
import kotlinx.coroutines.launch
import op.air.airclient.api.loadSettings
import op.air.airclient.cape.CapeService
import op.air.airclient.event.ClientShutdownEvent
import op.air.airclient.event.EventManager
import op.air.airclient.event.StartupEvent
import op.air.airclient.features.command.CommandManager
import op.air.airclient.features.command.CommandManager.registerCommands
import op.air.airclient.features.module.ModuleManager
import op.air.airclient.features.module.ModuleManager.registerModules
import op.air.airclient.features.special.BungeeCordSpoof
import op.air.airclient.features.special.ClientFixes
import op.air.airclient.features.special.ClientRichPresence
import op.air.airclient.features.special.ClientRichPresence.showRPCValue
import op.air.airclient.file.FileManager
import op.air.airclient.file.FileManager.loadAllConfigs
import op.air.airclient.file.FileManager.saveAllConfigs
import op.air.airclient.file.configs.models.ClientConfiguration
import op.air.airclient.file.configs.models.ClientConfiguration.updateClientWindow
import op.air.airclient.lang.LanguageManager.loadLanguages
import op.air.airclient.script.ScriptManager
import op.air.airclient.script.ScriptManager.enableScripts
import op.air.airclient.script.ScriptManager.loadScripts
import op.air.airclient.script.remapper.Remapper
import op.air.airclient.script.remapper.Remapper.loadSrg
import op.air.airclient.tabs.BlocksTab
import op.air.airclient.tabs.ExploitsTab
import op.air.airclient.tabs.HeadsTab
import op.air.airclient.ui.client.altmanager.GuiAltManager.Companion.loadActiveGenerators
import op.air.airclient.ui.client.clickgui.ClickGui
import op.air.airclient.ui.client.hud.HUD
import op.air.airclient.ui.font.Fonts
import op.air.airclient.utils.client.BlinkUtils
import op.air.airclient.utils.client.ClassUtils.hasForge
import op.air.airclient.utils.client.ClientUtils.LOGGER
import op.air.airclient.utils.client.ClientUtils.disableFastRender
import op.air.airclient.utils.client.PacketUtils
import op.air.airclient.utils.inventory.InventoryManager
import op.air.airclient.utils.inventory.InventoryUtils
import op.air.airclient.utils.inventory.SilentHotbar
import op.air.airclient.utils.io.MiscUtils
import op.air.airclient.utils.io.MiscUtils.showErrorPopup
import op.air.airclient.utils.kotlin.SharedScopes
import op.air.airclient.utils.movement.BPSUtils
import op.air.airclient.utils.movement.MovementUtils
import op.air.airclient.utils.movement.TimerBalanceUtils
import op.air.airclient.utils.render.MiniMapRegister
import op.air.airclient.utils.render.shader.Background
import op.air.airclient.utils.render.shader.BuiltinShaderBackground
import op.air.airclient.utils.rotation.RotationUtils
import op.air.airclient.utils.timing.TickedActions
import op.air.airclient.utils.timing.WaitTickUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import javax.swing.UIManager

object AirClient {

    /**
     * Client Information
     *
     * This has all the basic information.
     */
    const val CLIENT_NAME = "AirClient"
    const val CLIENT_AUTHOR = "CCBlueX,LMX"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"
    const val CLIENT_GITHUB = "https://github.com/lmx0721/AirClient"

    const val MINECRAFT_VERSION = "1.8.9"
    
    val clientVersionText = "1.0"
    val clientVersionNumber = 100
    val clientCommit = "unknown"
    val clientBranch = "unknown"

    /**
     * Defines if the client is in development mode.
     * This will enable update checking on commit time instead of regular legacy versioning.
     */
    const val IN_DEV = false

    val clientTitle = CLIENT_NAME + " " + clientVersionText + " " + MINECRAFT_VERSION + if (IN_DEV) " | DEVELOPMENT BUILD" else ""

    var isStarting = true

    // Managers
    val moduleManager = ModuleManager
    val commandManager = CommandManager
    val eventManager = EventManager
    val fileManager = FileManager
    val scriptManager = ScriptManager

    // HUD & ClickGUI
    val hud = HUD

    val clickGui = ClickGui

    // Menu Background
    var background: Background? = null
    var defaultMenuBackground: BuiltinShaderBackground? = null
    var customMenuBackground: BuiltinShaderBackground? = null

    fun getCurrentBackground(): Background? {
        val customBgFile = FileManager.backgroundImageFile
        val customShaderFile = FileManager.backgroundShaderFile
        
        if (customBgFile.exists() || customShaderFile.exists()) {
            return background
        }
        
        return if (ClientConfiguration.mainMenuStyle == "Custom") {
            customMenuBackground ?: Background.fromBuiltin(ClientConfiguration.customMenuBackgroundIndex).also { customMenuBackground = it }
        } else {
            defaultMenuBackground ?: Background.fromBuiltin(ClientConfiguration.defaultMenuBackgroundIndex).also { defaultMenuBackground = it }
        }
    }

    // Discord RPC
    val clientRichPresence = ClientRichPresence

    /**
     * Start IO tasks
     */
    fun preload(): Future<*> {

        op.air.airclient.utils.client.javaVersion

        // Change theme of Swing
        UIManager.setLookAndFeel(FlatMacLightLaf())

        val future = CompletableFuture<Unit>()

        SharedScopes.IO.launch {
            try {
                LOGGER.info("Starting preload tasks of $CLIENT_NAME")

                // Load languages
                loadLanguages()

                // Load alt generators
                loadActiveGenerators()

                // Load SRG file
                loadSrg()

                LOGGER.info("Preload tasks of $CLIENT_NAME are completed!")

                future.complete(Unit)
            } catch (e: Exception) {
                future.completeExceptionally(e)
            }
        }

        return future
    }

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        LOGGER.info("Starting $CLIENT_NAME $clientVersionText $clientCommit, by $CLIENT_AUTHOR")

        try {
            // Load client fonts
            Fonts.loadFonts()

            // Register listeners
            RotationUtils
            ClientFixes
            BungeeCordSpoof
            CapeService
            InventoryUtils
            InventoryManager
            MiniMapRegister
            TickedActions
            MovementUtils
            PacketUtils
            TimerBalanceUtils
            BPSUtils
            WaitTickUtils
            SilentHotbar
            BlinkUtils

            // Load settings
            loadSettings(false) {
                LOGGER.info("Successfully loaded ${it.size} settings.")
            }

            // Register commands
            registerCommands()

            // Setup module manager and register modules
            registerModules()

            runCatching {
                // Remapper
                loadSrg()

                if (!Remapper.mappingsLoaded) {
                    error("Failed to load SRG mappings.")
                }

                // ScriptManager
                loadScripts()
                enableScripts()
            }.onFailure {
                LOGGER.error("Failed to load scripts.", it)
            }

            // Load configs
            loadAllConfigs()
            
            // Sync theme settings from ThemeManager to ClientThemesUtils
            op.air.airclient.features.module.modules.client.ThemeManager.syncToClientThemesUtils()

            // Update client window
            updateClientWindow()

            // Tabs (Only for Forge!)
            if (hasForge()) {
                BlocksTab()
                ExploitsTab()
                HeadsTab()
            }

            // Disable Optifine FastRender
            disableFastRender()

            // Initialize InputFix
            op.air.airclient.utils.inputfix.InputFixInit.init()

            // Setup Discord RPC
            if (showRPCValue) {
                SharedScopes.IO.launch {
                    try {
                        clientRichPresence.setup()
                    } catch (throwable: Throwable) {
                        LOGGER.error("Failed to setup Discord RPC.", throwable)
                    }
                }
            }

            // Login into known token if not empty
            if (CapeService.knownToken.isNotBlank()) {
                SharedScopes.IO.launch {
                    runCatching {
                        CapeService.login(CapeService.knownToken)
                    }.onFailure {
                        LOGGER.error("Failed to login into known cape token.", it)
                    }.onSuccess {
                        LOGGER.info("Successfully logged in into known cape token.")
                    }
                }
            }

            // Refresh cape service
            CapeService.refreshCapeCarriers {
                LOGGER.info("Successfully loaded ${it.size} cape carriers.")
            }

            // Load background
            FileManager.loadBackground()
        } catch (e: Exception) {
            LOGGER.error("Failed to start client: ${e.message}")
            e.showErrorPopup()
        } finally {
            // Set is starting status
            isStarting = false

            if (!FileManager.firstStart && FileManager.backedup) {
                SharedScopes.IO.launch {
                    MiscUtils.showMessageDialog("Warning: backup triggered", "Client update detected! Please check the config folder.")
                }
            }

            EventManager.call(StartupEvent)
            LOGGER.info("Successfully started client")
        }
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        EventManager.call(ClientShutdownEvent)

        // Stop all CoroutineScopes
        SharedScopes.stop()

        // Save all available configs
        saveAllConfigs()
    }

}
