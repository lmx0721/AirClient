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
import op.air.airclient.utils.extensions.*
import op.air.airclient.utils.render.ColorUtils
import op.air.airclient.utils.render.ColorUtils.withAlpha
import op.air.airclient.utils.render.RenderUtils.disableGlCap
import op.air.airclient.utils.render.RenderUtils.drawTexturedModalRect
import op.air.airclient.utils.render.RenderUtils.enableGlCap
import op.air.airclient.utils.render.RenderUtils.quickDrawBorderedRect
import op.air.airclient.utils.render.RenderUtils.quickDrawRect
import op.air.airclient.utils.render.RenderUtils.resetCaps
import op.air.airclient.utils.render.RenderUtils
import op.air.airclient.utils.GlowUtils
import op.air.airclient.utils.rotation.RotationUtils.isEntityHeightVisible
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

private fun Entity.getLerpedPos(partialTicks: Float): Vec3 {
    return Vec3(
        this.lastTickPosX + (this.posX - this.lastTickPosX) * partialTicks,
        this.lastTickPosY + (this.posY - this.lastTickPosY) * partialTicks,
        this.lastTickPosZ + (this.posZ - this.lastTickPosZ) * partialTicks
    )
}

object NameTags2 : Module("NameTags2", Category.RENDER) {
    private val MordenBar by boolean("MordenBar", true)
    private val DTBL by boolean("动态比例", false) { MordenBar }
    private val openLoveEmoji by boolean("HealthIcon", false)
    private val shadowValue = boolean("Shadow2", false)
    private val roundingValue = float("Rounding", 5f, 0f..10f)
    private val shadowRadius = float("ShadowRadius", 6f, 0f..20f)
    private val bgAlpha = float("BackgroundAlpha", 0.8f, 0f..1f)
    private val scaleValue = float("Scale", 2f, 1f..5f)
    private val fontValue = font("Font", Fonts.fontRegular45)
    private val renderSelf by boolean("RenderSelf", false)
    private val health by boolean("Health", true)
    private val healthFromScoreboard by boolean("HealthFromScoreboard", false) { health }
    private val absorption by boolean("Absorption", false) { health || healthBar }
    private val roundedHealth by boolean("RoundedHealth", true) { health }

    private val healthPrefix by boolean("HealthPrefix", false) { health }
    private val healthPrefixText by text("HealthPrefixText", "") { health && healthPrefix }

    private val healthSuffix by boolean("HealthSuffix", true) { health }
    private val healthSuffixText by text("HealthSuffixText", " HP") { health && healthSuffix }

    private val ping by boolean("Ping", false)
    private val healthBar by boolean("Bar", true)
    private val distance by boolean("Distance", false)
    private val armor by boolean("Armor", true)
    private val bot by boolean("Bots", true)
    private val potion by boolean("Potions", true)
    private val clearNames by boolean("ClearNames", false)
    private val font by font("Font", Fonts.fontRegular40)
    private val scale by float("Scale", 1F, 1F..4F)
    private val fontShadow by boolean("Shadow", true)

    private val background by boolean("Background", true)
    private val backgroundColor by color("BackgroundColor", Color.BLACK.withAlpha(70)) { background }

    private val border by boolean("Border", true)
    private val borderColor by color("BorderColor", Color.BLACK.withAlpha(100)) { border }

    private val maxRenderDistance by int("MaxRenderDistance", 50, 1..200).onChanged { value ->
        maxRenderDistanceSq = value.toDouble().pow(2)
    }

    private val onLook by boolean("OnLook", false)
    private val maxAngleDifference by float("MaxAngleDifference", 90f, 5.0f..90f) { onLook }

    private val thruBlocks by boolean("ThruBlocks", true)

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

            val name = entity.displayName.unformattedText ?: continue

            val distanceSquared = mc.thePlayer.getDistanceSqToEntity(entity)

            if (isRenderingSelf) {
                FreeCam.restoreOriginalPosition()
            }

            if (distanceSquared <= maxRenderDistanceSq) {
                if (!MordenBar) renderNameTag(entity, isRenderingSelf, if (clearNames) ColorUtils.stripColor(name) else name)
                else renderModernTag(entity,
                    ColorUtils.stripColor(entity.displayName.unformattedText) ?: entity.displayName.unformattedText,
                    entity.health
                )
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

    private fun renderNameTag(entity: EntityLivingBase, isRenderingSelf: Boolean, name: String) {
        val thePlayer = mc.thePlayer ?: return

        val fontRenderer = font

        glPushMatrix()

        val renderManager = mc.renderManager
        val rotateX = if (mc.gameSettings.thirdPersonView == 2) -1.0f else 1.0f

        val (x, y, z) = entity.interpolatedPosition(entity.lastTickPos) - renderManager.renderPos

        glTranslated(x, y + entity.eyeHeight.toDouble() + 0.55, z)

        glRotatef(-renderManager.playerViewY, 0F, 1F, 0F)
        glRotatef(renderManager.playerViewX * rotateX, 1F, 0F, 0F)

        disableGlCap(GL_LIGHTING, GL_DEPTH_TEST)

        enableGlCap(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        val bot = isBot(entity)
        val nameColor = if (bot) "§3" else if (entity.isInvisible) "§6" else if (entity.isSneaking) "§4" else "§7"
        val playerPing = if (entity is EntityPlayer) entity.getPing() else 0
        val playerDistance = thePlayer.getDistanceToEntity(entity)

        val distanceText = if (distance && !isRenderingSelf) "§7${playerDistance.roundToInt()} m " else ""
        val pingText =
            if (ping && entity is EntityPlayer) "§7[" + (if (playerPing > 200) "§c" else if (playerPing > 100) "§e" else "§a") + playerPing + "ms§7] " else ""
        val healthText = if (health) " " + getHealthString(entity) else ""
        val botText = if (bot) " §c§lBot" else ""

        val text = "$distanceText$pingText$nameColor$name$healthText$botText"

        val healthColor = when {
            entity.health <= 0 -> Color(255, 0, 0)
            else -> {
                val healthRatio = (getHealth(entity, healthFromScoreboard) / entity.maxHealth).coerceIn(0.0F, 1.0F)
                val red = (255 * (1 - healthRatio)).toInt()
                val green = (255 * healthRatio).toInt()
                Color(red, green, 0)
            }
        }

        val scale = ((playerDistance / 4F).coerceAtLeast(1F) / 150F) * scale

        glScalef(-scale, -scale, scale)

        val width = fontRenderer.getStringWidth(text) * 0.5f
        fontRenderer.drawString(
            text, 1F + -width, if (fontRenderer == Fonts.minecraftFont) 1F else 1.5F, 0xFFFFFF, fontShadow
        )

        val dist = width + 4F - (-width - 2F)

        glDisable(GL_TEXTURE_2D)
        glEnable(GL_BLEND)

        val bgColor = if (background) {
            backgroundColor
        } else {
            Color(0, 0, 0, 0)
        }

        if (border) quickDrawBorderedRect(
            -width - 2F,
            -2F,
            width + 4F,
            fontRenderer.FONT_HEIGHT + 2F + if (healthBar) 2F else 0F,
            2F,
            borderColor.rgb,
            bgColor.rgb
        )
        else quickDrawRect(
            -width - 2F, -2F, width + 4F, fontRenderer.FONT_HEIGHT + 2F + if (healthBar) 2F else 0F, bgColor.rgb
        )

        if (healthBar) {
            quickDrawRect(
                -width - 2F,
                fontRenderer.FONT_HEIGHT + 3F,
                -width - 2F + dist,
                fontRenderer.FONT_HEIGHT + 4F,
                Color(50, 50, 50).rgb
            )
            quickDrawRect(
                -width - 2F,
                fontRenderer.FONT_HEIGHT + 3F,
                -width - 2F + (dist * (getHealth(entity, healthFromScoreboard) / entity.maxHealth).coerceIn(0F, 1F)),
                fontRenderer.FONT_HEIGHT + 4F,
                healthColor.rgb
            )
        }

        glEnable(GL_TEXTURE_2D)

        fontRenderer.drawString(
            text, 1F + -width, if (fontRenderer == Fonts.minecraftFont) 1F else 1.5F, Color.white.rgb, fontShadow
        )

        var foundPotion = false

        if (potion && entity is EntityPlayer) {
            val potions =
                entity.activePotionEffects.map { Potion.potionTypes[it.potionID] }.filter { it.hasStatusIcon() }
            if (potions.isNotEmpty()) {
                foundPotion = true

                color(1.0F, 1.0F, 1.0F, 1.0F)
                disableLighting()
                enableTexture2D()

                val minX = (potions.size * -20) / 2

                glPushMatrix()
                enableRescaleNormal()
                for ((index, potion) in potions.withIndex()) {
                    color(1.0F, 1.0F, 1.0F, 1.0F)
                    mc.textureManager.bindTexture(inventoryBackground)
                    val i1 = potion.statusIconIndex
                    drawTexturedModalRect(minX + index * 20, -22, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18, 0F)
                }
                disableRescaleNormal()
                glPopMatrix()

                enableAlpha()
                disableBlend()
                enableTexture2D()
            }
        }

        if (armor && entity is EntityPlayer) {
            RenderHelper.enableGUIStandardItemLighting()
            for (index in 0..4) {
                val itemStack = entity.getEquipmentInSlot(index) ?: continue

                mc.renderItem.zLevel = -147F
                mc.renderItem.renderItemAndEffectIntoGUI(
                    itemStack, -50 + index * 20, if (potion && foundPotion) -42 else -22
                )
            }
            RenderHelper.disableStandardItemLighting()

            enableAlpha()
            disableBlend()
            enableTexture2D()
        }

        resetCaps()

        resetColor()
        glColor4f(1F, 1F, 1F, 1F)

        glPopMatrix()
    }

    private fun getHealthString(entity: EntityLivingBase): String {
        val prefix = if (healthPrefix) healthPrefixText else ""
        val suffix = if (healthSuffix) healthSuffixText else ""

        val result = getHealth(entity, healthFromScoreboard, absorption)

        val healthPercentage = (getHealth(entity, healthFromScoreboard) / entity.maxHealth).coerceIn(0.0F, 1.0F)
        val healthColor = when {
            entity.health <= 0 -> "§4"
            healthPercentage >= 0.75 -> "§a"
            healthPercentage >= 0.5 -> "§e"
            healthPercentage >= 0.25 -> "§6"
            else -> "§c"
        }

        return "$healthColor$prefix${if (roundedHealth) result.roundToInt() else decimalFormat.format(result)}$suffix"
    }


    fun shouldRenderNameTags(entity: Entity) =
        handleEvents() && entity is EntityLivingBase && (ESP.handleEvents() && ESP.renderNameTags || isSelected(
            entity,
            false
        ) && (bot || !isBot(entity)))

    private fun renderModernTag(entity: EntityLivingBase, name: String, health: Float) {
        pushMatrix()
        enableBlend()
        disableLighting()
        val timer = mc.timer
        val renderManager = mc.renderManager
        val interpolatedPos = entity.getLerpedPos(timer.renderPartialTicks)
        translate(
            interpolatedPos.xCoord - renderManager.viewerPosX,
            interpolatedPos.yCoord + entity.eyeHeight + 0.6 - renderManager.viewerPosY,
            interpolatedPos.zCoord - renderManager.viewerPosZ
        )
        rotate(-renderManager.playerViewY, 0f, 1f, 0f)
        rotate(renderManager.playerViewX, 1f, 0f, 0f)
        val distance = mc.thePlayer.getDistanceToEntity(entity)
        val minDistance = 5f
        val scale:Float
        if (DTBL){
            val baseScale = 0.01f
            val adjustedDistance = distance.coerceAtLeast(minDistance).coerceAtMost(6F)
            scale = (baseScale * scaleValue.get()) / (adjustedDistance * 0.3f)
        }else{
            scale = 0.02f * scaleValue.get()
        }
        scale(-scale, -scale, scale)

        val font = fontValue.get()
        val distanceText = "${distance.roundToInt()}m"
        var healthText = "%.1f".format(health)
        if (openLoveEmoji) healthText = "❤${healthText}"
        val nameWidth = font.getStringWidth(name)
        val healthWidth = font.getStringWidth(healthText)
        val distanceWidth = font.getStringWidth(distanceText)
        val padding = 4f
        val elementSpacing = 2f
        val leftWidth = healthWidth + padding * 2
        val middleWidth = nameWidth + padding * 2
        val rightWidth = distanceWidth + padding * 2
        val totalWidth = leftWidth + middleWidth + rightWidth + elementSpacing * 2
        val height = font.FONT_HEIGHT + padding * 2

        if (shadowValue.get()) {
            GlowUtils.drawGlow(
                -totalWidth / 2 - 4f, -4f,
                totalWidth + 8f, height + 8f,
                shadowRadius.get().toInt(),
                Color(0, 0, 0, 100))
        }

        val bgColor = Color(30, 30, 30, (200 * bgAlpha.get()).toInt())
        RenderUtils.drawRoundedRect(
            -totalWidth / 2f, 0f,
            -totalWidth / 2f + leftWidth, height,
            bgColor.rgb, roundingValue.get()
        )
        RenderUtils.drawRoundedRect(
            -totalWidth / 2f + leftWidth + elementSpacing, 0f,
            -totalWidth / 2f + leftWidth + elementSpacing + middleWidth, height,
            bgColor.rgb, roundingValue.get()
        )
        RenderUtils.drawRoundedRect(
            -totalWidth / 2f + leftWidth + elementSpacing * 2 + middleWidth, 0f,
            -totalWidth / 2f + leftWidth + elementSpacing * 2 + middleWidth + rightWidth, height,
            bgColor.rgb, roundingValue.get()
        )

        val kiwiColor = Color(0x7F, 0xCF, 0x00)
        val grayColor = Color(0xAA, 0xAA, 0xAA)
        font.drawString(
            healthText,
            -totalWidth / 2f + padding,
            padding,
            kiwiColor.rgb,
            false
        )
        font.drawString(
            name,
            -totalWidth / 2f + leftWidth + elementSpacing + padding,
            padding,
            Color.WHITE.rgb,
            false
        )
        font.drawString(
            distanceText,
            -totalWidth / 2f + leftWidth + elementSpacing * 2 + middleWidth + padding,
            padding,
            grayColor.rgb,
            false
        )

        enableLighting()
        popMatrix()
    }
}
