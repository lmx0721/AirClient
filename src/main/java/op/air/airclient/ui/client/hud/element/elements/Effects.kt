/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.ui.client.hud.element.elements

import op.air.airclient.ui.client.hud.element.Border
import op.air.airclient.ui.client.hud.element.Element
import op.air.airclient.ui.client.hud.element.ElementInfo
import op.air.airclient.ui.client.hud.element.Side
import op.air.airclient.ui.font.AWTFontRenderer.Companion.assumeNonVolatile
import op.air.airclient.ui.font.Fonts
import op.air.airclient.ui.font.GameFontRenderer
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Potion

/**
 * CustomHUD effects element
 *
 * Shows a list of active potion effects
 */
@ElementInfo(name = "Effects")
class Effects(
    x: Double = 2.0, y: Double = 10.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element("Effects", x, y, scale, side) {

    private val font by font("Font", Fonts.fontSemibold35)
    private val shadow by boolean("Shadow", true)

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        var y = 0F
        var width = 0F

        val height = ((font as? GameFontRenderer)?.height ?: font.FONT_HEIGHT).toFloat()

        assumeNonVolatile {
            for (effect in mc.thePlayer.activePotionEffects) {
                val potion = Potion.potionTypes[effect.potionID]

                val number = when {
                    effect.amplifier == 1 -> "II"
                    effect.amplifier == 2 -> "III"
                    effect.amplifier == 3 -> "IV"
                    effect.amplifier == 4 -> "V"
                    effect.amplifier == 5 -> "VI"
                    effect.amplifier == 6 -> "VII"
                    effect.amplifier == 7 -> "VIII"
                    effect.amplifier == 8 -> "IX"
                    effect.amplifier == 9 -> "X"
                    effect.amplifier > 10 -> "X+"
                    else -> "I"
                }

                val name = "${I18n.format(potion.name)} $number§f: §7${Potion.getDurationString(effect)}"
                val stringWidth = font.getStringWidth(name).toFloat()

                if (width < stringWidth)
                    width = stringWidth

                font.drawString(name, -stringWidth, y, potion.liquidColor, shadow)
                y -= height
            }
        }

        if (width == 0F)
            width = 40F

        if (y == 0F)
            y = -10F

        return Border(2F, height, -width - 2F, y + height - 2F)
    }
}