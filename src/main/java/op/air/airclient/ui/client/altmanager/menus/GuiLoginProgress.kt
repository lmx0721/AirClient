/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.ui.client.altmanager.menus

import me.liuli.elixir.account.MinecraftAccount
import op.air.airclient.lang.translationText
import op.air.airclient.ui.client.altmanager.GuiAltManager.Companion.login
import op.air.airclient.ui.font.AWTFontRenderer.Companion.assumeNonVolatile
import op.air.airclient.utils.render.RenderUtils.drawLoadingCircle
import op.air.airclient.utils.ui.AbstractScreen

class GuiLoginProgress(
    minecraftAccount: MinecraftAccount,
    success: () -> Unit,
    error: (Exception) -> Unit,
    done: () -> Unit
) : AbstractScreen() {

    init {
        login(minecraftAccount, success, error, done)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        assumeNonVolatile {
            drawDefaultBackground()
            drawLoadingCircle(width / 2f, height / 4f + 70)
            drawCenteredString(fontRendererObj, translationText(
                "Loggingintoaccount"), width / 2, height / 2 - 60, 16777215)
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}