/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.client

import op.air.airclient.AirClient.clickGui
import op.air.airclient.event.PacketEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.ui.client.clickgui.ClickGui
import op.air.airclient.ui.client.clickgui.style.styles.BlackStyle
import op.air.airclient.ui.client.clickgui.style.styles.LiquidBounceStyle
import op.air.airclient.ui.client.clickgui.style.styles.NullStyle
import op.air.airclient.ui.client.clickgui.style.styles.SlowlyStyle
import net.minecraft.network.play.server.S2EPacketCloseWindow
import org.lwjgl.input.Keyboard
import java.awt.Color

object ClickGUI : Module("ClickGUI", Category.CLIENT, Keyboard.KEY_RSHIFT, canBeEnabled = false) {
    private val style by choices(
        "Style",
        arrayOf("AirClient", "Null", "Slowly", "Black"),
        "AirClient"
    ).onChanged {
        updateStyle()
    }
    var scale by float("Scale", 0.8f, 0.5f..1.5f)
    val maxElements by int("MaxElements", 15, 1..30)
    val fadeSpeed by float("FadeSpeed", 1f, 0.5f..4f)
    val scrolls by boolean("Scrolls", true)
    val spacedModules by boolean("SpacedModules", false)
    val panelsForcedInBoundaries by boolean("PanelsForcedInBoundaries", false)

    private val color by color("Color", Color(0, 160, 255)) { style !in arrayOf("Slowly", "Black") }

    val guiColor
        get() = color.rgb

    override fun onEnable() {
        updateStyle()
        mc.displayGuiScreen(clickGui)
        Keyboard.enableRepeatEvents(true)
    }

    private fun updateStyle() {
        clickGui.style = when (style) {
            "AirClient" -> LiquidBounceStyle
            "Null" -> NullStyle
            "Slowly" -> SlowlyStyle
            "Black" -> BlackStyle
            else -> return
        }
    }

    val onPacket = handler<PacketEvent>(always = true) { event ->
        if (event.packet is S2EPacketCloseWindow && mc.currentScreen is ClickGui) {
            event.cancelEvent()
        }
    }
}
