/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * skid FDP Client
 * https://github.com/SkidderMC/FDPClient
 */
package op.air.airclient.ui.client.hud.element.elements

import op.air.airclient.ui.client.hud.element.Border
import op.air.airclient.ui.client.hud.element.Element
import op.air.airclient.ui.client.hud.element.ElementInfo
import op.air.airclient.ui.client.hud.element.Side
import op.air.airclient.ui.font.AWTFontRenderer.Companion.assumeNonVolatile
import op.air.airclient.ui.font.Fonts
import op.air.airclient.utils.extensions.getPing
import op.air.airclient.utils.render.BlurUtils
import op.air.airclient.utils.render.RenderUtils
import op.air.airclient.utils.render.RoundedUtil
import op.air.airclient.utils.client.ClientThemesUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*

@ElementInfo(name = "Watermark", single = true)
class Watermark(
    x: Double = 0.0, y: Double = 0.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP),
) : Element("Watermark", x, y, scale, side) {

    private val mode by choices("Mode", arrayOf("Blur", "ZAVZ", "AirClient"), "AirClient")
    private val colorMode by choices("Color", arrayOf("Custom", "Theme"), "Theme")
    private val customColor by color("CustomColor", Color(24, 114, 165))

    private val dateFormat = SimpleDateFormat("HH:mm")

    override fun drawElement(): Border {
        assumeNonVolatile {
            val name = "AirClient"
            
            when (mode) {
                "Blur" -> drawBlurStyle(name)
                "ZAVZ" -> drawZavzStyle(name)
                "AirClient" -> drawAirClientStyle(name)
            }
        }
        
        return calculateBorder()
    }

    private fun calculateBorder(): Border {
        val name = "AirClient"
        val text = EnumChatFormatting.DARK_GRAY.toString() + "   |  " + EnumChatFormatting.WHITE + mc.thePlayer.name + EnumChatFormatting.DARK_GRAY + "  |  " + EnumChatFormatting.WHITE + mc.thePlayer.getPing() +
                "ms" + EnumChatFormatting.DARK_GRAY + "  |  " + EnumChatFormatting.WHITE + dateFormat.format(Date()) + EnumChatFormatting.DARK_GRAY + "  |  " + EnumChatFormatting.WHITE

        val width = 5f + Fonts.fontSF35.getStringWidth(text) + Fonts.fontSF35.getStringWidth(name)
        val height = 20f
        
        return Border(0f, 0f, width, height)
    }

    private fun drawBlurStyle(name: String) {
        val text =
            EnumChatFormatting.DARK_GRAY.toString() + "   |  " + EnumChatFormatting.WHITE + mc.thePlayer.name + EnumChatFormatting.DARK_GRAY + "  |  " + EnumChatFormatting.WHITE + mc.thePlayer.getPing() +
                    "ms" + EnumChatFormatting.DARK_GRAY + "  |  " + EnumChatFormatting.WHITE + dateFormat.format(
                Date()
            ) + EnumChatFormatting.DARK_GRAY + "  |  " + EnumChatFormatting.WHITE

        val width = 5f + Fonts.fontSF35.getStringWidth(text) + Fonts.fontSF35.getStringWidth(name)
        val height = 20f

        glPushMatrix()
        glTranslated(-renderX, -renderY, 0.0)
        glScalef(1F / scale, 1F / scale, 1F)
        BlurUtils.blurAreaRounded(
            renderX.toFloat(), renderY.toFloat(),
            renderX.toFloat() + width, renderY.toFloat() + height,
            5.4f, 10f
        )
        glPopMatrix()

        RenderUtils.drawRoundedRect(0f, 0f, width, height, Color(0, 0, 0, 100).rgb, 5.4f)
        
        Fonts.fontSF35.drawString(" $name", 4f, 5f, getColor().rgb)
        Fonts.fontSF35.drawString(text, 3 + Fonts.fontSF35.getStringWidth(name), 6, Color.WHITE.rgb)
    }

    private fun drawZavzStyle(name: String) {
        val username = mc.thePlayer.name
        val servername = if (mc.isSingleplayer) "Singleplayer" else mc.currentServerData?.serverIP ?: "Unknown"
        val times = dateFormat.format(Date())
        
        val width = (Fonts.fontSF35.getStringWidth(name) + Fonts.font35.getStringWidth(
            " | $username | $servername | $times"
        ) + 3 + 5).toFloat()
        val height = 12f
        
        RoundedUtil.drawRound(0f, 0f, width, height, 1f, Color(0, 0, 0, 100))
        Fonts.fontSF35.drawString(name, 3f, 2f, getColor().rgb)
        Fonts.fontSF35.drawString(name, 2f, 2f, -1)
        Fonts.font35.drawString(
            " | $username | $servername | $times",
            Fonts.fontSF35.getStringWidth(name) + 5f,
            3f,
            -1
        )
    }

    private fun drawAirClientStyle(name: String) {
        val text =
            EnumChatFormatting.DARK_GRAY.toString() + "   |  " + EnumChatFormatting.WHITE + mc.thePlayer.name + EnumChatFormatting.DARK_GRAY + "  |  " + EnumChatFormatting.WHITE + mc.thePlayer.getPing() +
                    "ms" + EnumChatFormatting.DARK_GRAY + "  |  " + EnumChatFormatting.WHITE + dateFormat.format(
                Date()
            ) + EnumChatFormatting.DARK_GRAY + "  |  " + EnumChatFormatting.WHITE

        val width = 5f + Fonts.fontSF35.getStringWidth(text) + Fonts.fontSF35.getStringWidth(name)
        val height = 20f

        glPushMatrix()
        glTranslated(-renderX, -renderY, 0.0)
        glScalef(1F / scale, 1F / scale, 1F)
        BlurUtils.blurAreaRounded(
            renderX.toFloat(), renderY.toFloat(),
            renderX.toFloat() + width, renderY.toFloat() + height,
            5.4f, 10f
        )
        glPopMatrix()

        RenderUtils.drawRoundedRect(0f, 0f, width, height, Color(0, 0, 0, 100).rgb, 5.4f)

        Fonts.fontSF35.drawStringWithShadow(" $name", 2f, 6f, getColor().rgb)
        Fonts.fontSF35.drawString(text, 1 + Fonts.fontSF35.getStringWidth(name), 6, Color.WHITE.rgb)
    }

    private fun getColor(): Color {
        return when (colorMode) {
            "Theme" -> ClientThemesUtils.getColor(1)
            else -> customColor
        }
    }
}
