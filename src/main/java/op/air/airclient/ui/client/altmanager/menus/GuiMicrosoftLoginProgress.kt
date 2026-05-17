/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.ui.client.altmanager.menus

import me.liuli.elixir.account.MicrosoftAccount
import me.liuli.elixir.compat.OAuthServer
import op.air.airclient.file.FileManager.accountsConfig
import op.air.airclient.file.FileManager.saveConfig
import op.air.airclient.lang.translationButton
import op.air.airclient.lang.translationText
import op.air.airclient.ui.font.AWTFontRenderer.Companion.assumeNonVolatile
import op.air.airclient.ui.font.Fonts
import op.air.airclient.utils.client.ClientUtils.LOGGER
import op.air.airclient.utils.io.MiscUtils
import op.air.airclient.utils.render.RenderUtils.drawLoadingCircle
import op.air.airclient.utils.ui.AbstractScreen
import net.minecraft.client.gui.GuiButton
import java.net.BindException

class GuiMicrosoftLoginProgress(val updateStatus: (String) -> Unit, val done: () -> Unit) : AbstractScreen() {

    private var oAuthServer: OAuthServer? = null
    private var loginUrl: String? = null

    private var serverStopAlreadyCalled = false

    override fun initGui() {
        // This will start a login server and open the browser.
        try {
            oAuthServer = MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {

                /**
                 * Called when the user has cancelled the authentication process or the thread has been interrupted
                 */
                override fun authError(error: String) {
                    serverStopAlreadyCalled = true
                    errorAndDone(error)
                    loginUrl = null
                }

                /**
                 * Called when the user has completed authentication
                 */
                override fun authResult(account: MicrosoftAccount) {
                    serverStopAlreadyCalled = true

                    loginUrl = null
                    if (accountsConfig.accountExists(account)) {
                        errorAndDone("The account has already been added.")
                        return
                    }

                    accountsConfig.addAccount(account)
                    saveConfig(accountsConfig)
                    successAndDone()
                }

                /**
                 * Called when the server has prepared the user for authentication
                 */
                override fun openUrl(url: String) {
                    loginUrl = url
                    MiscUtils.showURL(url)
                }

            })
        } catch (bindException: BindException) {
            errorAndDone("Failed to start login server. (Port already in use)")
            LOGGER.error("Failed to start login server.", bindException)
        } catch (e: Exception) {
            errorAndDone("Failed to start login server.")
            LOGGER.error("Failed to start login server.", e)
        }

        +GuiButton(0, width / 2 - 100, height / 2 + 60, translationButton("openURL"))
        +GuiButton(1, width / 2 - 100, height / 2 + 90, translationButton("cancel"))

        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        assumeNonVolatile {
            drawDefaultBackground()
            drawLoadingCircle(width / 2f, height / 4f + 70)
            Fonts.fontSemibold40.drawCenteredString(
                translationText(
                "Loggingintoaccount"), width / 2f, height / 2 - 60f, 0xffffff)

        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        // Not enabled buttons should be ignored
        if (!button.enabled) {
            return
        }

        when (button.id) {
            0 -> loginUrl?.let { MiscUtils.showURL(it) }

            1 -> errorAndDone("Login cancelled.")
        }


        super.actionPerformed(button)
    }

    override fun onGuiClosed() {
        if (!serverStopAlreadyCalled) {
            oAuthServer?.stop(isInterrupt = false)
        }

        super.onGuiClosed()
    }

    private fun successAndDone() {
        updateStatus("§aSuccessfully logged in.")
        done()
    }

    private fun errorAndDone(error: String) {
        updateStatus("§c$error")
        done()
    }

}