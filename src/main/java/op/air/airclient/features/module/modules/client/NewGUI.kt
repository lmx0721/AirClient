/*
 * skid gold bounce
 * https://github.com/bzym2/GoldBounce/
 * AirClient++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package op.air.airclient.features.module.modules.client

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.ui.client.clickgui.newVer.NewUi
import op.air.airclient.utils.render.ColorUtils
import op.air.airclient.utils.render.ColorUtils.fade
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.util.*

object NewGUI : Module("NewGUI", Category.CLIENT, Keyboard.KEY_RSHIFT, canBeEnabled = false) {

    override fun onEnable() {
        mc.displayGuiScreen(NewUi.getInstance())
    }

    val fastRenderValue by boolean("FastRender", false)

    val fontMode by choices("Font", arrayOf("Minecraft", "HarmonyOS"), "HarmonyOS")

    val colorModeValue by choices("Color", arrayOf("Custom", "Fade"), "Custom")

    val colorRedValue by int("Red", 0, 0..255)

    val colorGreenValue by int("Green", 140, 0..255)

    val colorBlueValue by int("Blue", 255, 0..255)

    val accentColor: Color?
        get() {
            var c: Color? = Color(255, 255, 255, 255)
            when (colorModeValue.lowercase(Locale.getDefault())) {
                "custom" -> c = Color(colorRedValue, colorGreenValue, colorBlueValue)
                "fade" -> c = fade(Color(colorRedValue, colorGreenValue, colorBlueValue), 0, 100)
            }
            return c
        }
}
