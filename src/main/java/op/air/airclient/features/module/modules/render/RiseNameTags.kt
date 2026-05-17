/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * skid neko bounce 
 * https://github.com/RouQingNeko1024/NekoBounce
 */
package op.air.airclient.features.module.modules.render

import op.air.airclient.event.Render3DEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.features.module.modules.misc.AntiBot.isBot
import op.air.airclient.ui.font.Fonts
import op.air.airclient.utils.attack.EntityUtils.getHealth
import op.air.airclient.utils.attack.EntityUtils.isLookingOnEntities
import op.air.airclient.utils.attack.EntityUtils.isSelected
import op.air.airclient.utils.client.EntityLookup
import op.air.airclient.utils.render.RenderUtils
import op.air.airclient.utils.render.RenderUtils.disableGlCap
import op.air.airclient.utils.render.RenderUtils.enableGlCap
import op.air.airclient.utils.render.RenderUtils.resetCaps
import op.air.airclient.utils.GlowUtils
import op.air.airclient.utils.extensions.*
import op.air.airclient.utils.rotation.RotationUtils.isEntityHeightVisible
import net.minecraft.client.entity.EntityPlayerSP
import org.lwjgl.opengl.GL11.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.pow

object RiseNameTags : Module("RiseNameTags", Category.RENDER) {
    private val renderSelf by boolean("RenderSelf", false)
    private val bot by boolean("Bots", true)

    private val maxRenderDistance by int("MaxRenderDistance", 50, 1..200).onChanged { value ->
        maxRenderDistanceSq = value.toDouble().pow(2)
    }

    private val onLook by boolean("OnLook", false)
    private val maxAngleDifference by float("MaxAngleDifference", 90f, 5.0f..90f) { onLook }

    private val thruBlocks by boolean("ThruBlocks", true)

    private val shadowcheck by boolean("ShadowCheck", true)
    private val shadowStrength by int("ShadowStrength", 1, 1..2)

    private var maxRenderDistanceSq = 0.0
        set(value) {
            field = if (value <= 0.0) maxRenderDistance.toDouble().pow(2.0) else value
        }
    private val inventoryBackground = ResourceLocation("textures/gui/container/inventory.png")
    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val entities by EntityLookup<EntityLivingBase>()
        .filter { bot || !isBot(it) }
        .filter { !onLook || isLookingOnEntities(it, maxAngleDifference.toDouble()) }
        .filter { thruBlocks || isEntityHeightVisible(it) }
        
    val onRender3D = handler<Render3DEvent> {
        if (mc.theWorld == null || mc.thePlayer == null) return@handler

        glPushAttrib(GL_ENABLE_BIT)
        glPushMatrix()

        glDisable(GL_LIGHTING)
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_LINE_SMOOTH)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        for (entity in entities) {
            val isRenderingSelf =
                entity is EntityPlayerSP && (mc.gameSettings.thirdPersonView != 0 || FreeCam.handleEvents())

            if (!isRenderingSelf || !renderSelf) {
                if (!isSelected(entity, false)) continue
            }

            val distanceSquared = mc.thePlayer.getDistanceSqToEntity(entity)

            if (isRenderingSelf) {
                FreeCam.restoreOriginalPosition()
            }

            if (distanceSquared <= maxRenderDistanceSq) {
                renderNameTag(entity, isRenderingSelf)
            }

            if (isRenderingSelf) {
                FreeCam.useModifiedPosition()
            }
        }

        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)

        glPopMatrix()
        glPopAttrib()

        glColor4f(1F, 1F, 1F, 1F)
    }
    
    private fun renderNameTag(entity: EntityLivingBase, isRenderingSelf: Boolean) {
        val thePlayer = mc.thePlayer ?: return
        val fontRenderer = Fonts.fontRegular35

        glPushMatrix()

        disableGlCap(GL_LIGHTING, GL_DEPTH_TEST)

        enableGlCap(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        val name = entity.displayName.unformattedText
        val health = getHealth(entity)
        val healthText = health.toInt().toString()

        val renderManager = mc.renderManager
        val rotateX = if (mc.gameSettings.thirdPersonView == 2) -1.0f else 1.0f

        val (x, y, z) = entity.interpolatedPosition(entity.lastTickPos) - renderManager.renderPos

        glTranslated(x, y + entity.eyeHeight.toDouble() + 0.55, z)

        glRotatef(-renderManager.playerViewY, 0F, 1F, 0F)
        glRotatef(renderManager.playerViewX * rotateX, 1F, 0F, 0F)

        val distance = thePlayer.getDistanceToEntity(entity)
        val scale = ((distance / 4F).coerceAtLeast(1F) / 150F) * 2F
        glScalef(-scale, -scale, scale)

        val nameWidth = fontRenderer.getStringWidth(name)
        val healthWidth = fontRenderer.getStringWidth(healthText)
        val maxWidth = maxOf(nameWidth, healthWidth) + 10
        val height = (fontRenderer.FONT_HEIGHT * 2) + 6

        glDisable(GL_TEXTURE_2D)

        glColor4f(0f, 0f, 0f, 0.7f)

        if (shadowcheck) {
            GlowUtils.drawGlow(
                -maxWidth / 2f, -height / 2f,
                maxWidth.toFloat(), height.toFloat(), 
                (shadowStrength * 13f).toInt(),
                Color(0, 0, 0, 140)
            )
        }

        RenderUtils.drawRoundedRect(
            -maxWidth / 2f, -height / 2f,
            maxWidth / 2f, height / 2f,
            Color(0, 0, 0, 178).rgb,
            5f
        )

        glEnable(GL_TEXTURE_2D)

        fontRenderer.drawString(
            name,
            -nameWidth / 2f,
            -height / 2f + 2f,
            Color(103, 216, 230).rgb,
            false
        )

        fontRenderer.drawString(
            healthText,
            -healthWidth / 2f,
            -height / 2f + fontRenderer.FONT_HEIGHT + 4f,
            Color.WHITE.rgb,
            false
        )

        resetCaps()
        glColor4f(1f, 1f, 1f, 1f)

        glPopMatrix()
    }
}
