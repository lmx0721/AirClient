/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.combat

import op.air.airclient.event.*
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.utils.client.BlinkUtils
import op.air.airclient.utils.client.chat
import op.air.airclient.utils.extensions.getDistanceToEntityBox
import op.air.airclient.utils.render.RenderUtils.glColor
import op.air.airclient.utils.timing.MSTimer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.opengl.GL11.*

object SmartBlink : Module("SmartBlink", Category.COMBAT) {

    private val startDistance by float("StartDistance", 6f, 1f..20f)
    private val stopDistance by float("StopDistance", 3f, 1f..20f)
    
    private val pulse by boolean("Pulse", false)
    private val pulseDelay by int("PulseDelay", 1000, 100..5000) { pulse }
    
    private val markPosition by boolean("MarkPosition", true)
    
    private val chatNotify by boolean("ChatNotify", true)
    
    private val flagPause by boolean("FlagPause", true)
    private val flagPauseTime by int("FlagPauseTime", 10, 1..60) { flagPause }
    
    private var isBlinking = false
    private var startPosition: AxisAlignedBB? = null
    
    private val pulseTimer = MSTimer()
    private val flagTimer = MSTimer()
    
    private var target: EntityLivingBase? = null
    private var isPausedByFlag = false

    override fun onDisable() {
        if (isBlinking) {
            BlinkUtils.unblink()
            isBlinking = false
            if (chatNotify) {
                chat("§7[§cSmartBlink§7] §fStopped blinking")
            }
        }
        startPosition = null
        target = null
        isPausedByFlag = false
    }

    val onUpdate = handler<UpdateEvent> {
        val player = mc.thePlayer ?: return@handler
        
        if (isPausedByFlag) {
            if (!flagTimer.hasTimePassed(flagPauseTime * 1000L)) {
                return@handler
            }
            isPausedByFlag = false
            if (chatNotify) {
                chat("§7[§cSmartBlink§7] §fResumed after flag cooldown")
            }
        }
        
        target = mc.theWorld?.loadedEntityList
            ?.filterIsInstance<EntityPlayer>()
            ?.filter { it != player && !it.isDead }
            ?.minByOrNull { player.getDistanceToEntityBox(it) }
        
        val currentTarget = target
        
        if (currentTarget == null) {
            if (isBlinking) {
                BlinkUtils.unblink()
                isBlinking = false
                startPosition = null
                if (chatNotify) {
                    chat("§7[§cSmartBlink§7] §fStopped blinking (no target)")
                }
            }
            return@handler
        }
        
        val distance = player.getDistanceToEntityBox(currentTarget)
        
        if (!isBlinking && distance <= startDistance && !isPausedByFlag) {
            isBlinking = true
            startPosition = AxisAlignedBB(
                player.posX - 0.5, player.posY, player.posZ - 0.5,
                player.posX + 0.5, player.posY + 1.8, player.posZ + 0.5
            )
            pulseTimer.reset()
            if (chatNotify) {
                chat("§7[§cSmartBlink§7] §aStarted blinking")
            }
        }
        
        if (isBlinking && distance <= stopDistance) {
            BlinkUtils.unblink()
            isBlinking = false
            startPosition = null
            if (chatNotify) {
                chat("§7[§cSmartBlink§7] §fStopped blinking (reached target)")
            }
        }
        
        if (pulse && isBlinking && pulseTimer.hasTimePassed(pulseDelay)) {
            BlinkUtils.unblink()
            isBlinking = false
            startPosition = null
            if (chatNotify) {
                chat("§7[§cSmartBlink§7] §fPulse release")
            }
        }
    }

    val onPacket = handler<PacketEvent> { event ->
        if (!isBlinking) return@handler
        
        val packet = event.packet
        val player = mc.thePlayer ?: return@handler
        
        if (player.isDead || player.ticksExisted <= 10) {
            BlinkUtils.unblink()
            isBlinking = false
            startPosition = null
            if (chatNotify) {
                chat("§7[§cSmartBlink§7] §fStopped blinking (player dead/respawn)")
            }
            return@handler
        }
        
        BlinkUtils.blink(packet, event, sent = true, receive = false)
    }
    
    val onReceivePacket = handler<PacketEvent> { event ->
        if (!flagPause) return@handler
        
        val packet = event.packet
        if (packet is S08PacketPlayerPosLook) {
            if (isBlinking) {
                BlinkUtils.unblink()
                isBlinking = false
                startPosition = null
                isPausedByFlag = true
                flagTimer.reset()
                if (chatNotify) {
                    chat("§7[§cSmartBlink§7] §cFlag detected! Pausing for ${flagPauseTime}s")
                }
            }
        }
    }

    val onMotion = handler<MotionEvent> { event ->
        if (event.eventState == EventState.POST && isBlinking) {
            BlinkUtils.syncSent()
        }
    }

    val onRender3D = handler<Render3DEvent> {
        if (!markPosition || !isBlinking) return@handler
        
        val box = startPosition ?: return@handler
        val renderManager = mc.renderManager ?: return@handler
        val renderPosX = renderManager.viewerPosX
        val renderPosY = renderManager.viewerPosY
        val renderPosZ = renderManager.viewerPosZ
        
        glPushMatrix()
        glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glEnable(GL_BLEND)
        glDisable(GL_DEPTH_TEST)
        try {
            mc.entityRenderer?.disableLightmap()
        } catch (e: Exception) {
        }
        
        glColor4f(1f, 0f, 0f, 0.5f)
        glLineWidth(2f)
        glBegin(GL_LINE_STRIP)
        
        glVertex3d(box.minX - renderPosX, box.minY - renderPosY, box.minZ - renderPosZ)
        glVertex3d(box.maxX - renderPosX, box.minY - renderPosY, box.minZ - renderPosZ)
        glVertex3d(box.maxX - renderPosX, box.minY - renderPosY, box.maxZ - renderPosZ)
        glVertex3d(box.minX - renderPosX, box.minY - renderPosY, box.maxZ - renderPosZ)
        glVertex3d(box.minX - renderPosX, box.minY - renderPosY, box.minZ - renderPosZ)
        
        glVertex3d(box.minX - renderPosX, box.maxY - renderPosY, box.minZ - renderPosZ)
        glVertex3d(box.maxX - renderPosX, box.maxY - renderPosY, box.minZ - renderPosZ)
        glVertex3d(box.maxX - renderPosX, box.maxY - renderPosY, box.maxZ - renderPosZ)
        glVertex3d(box.minX - renderPosX, box.maxY - renderPosY, box.maxZ - renderPosZ)
        glVertex3d(box.minX - renderPosX, box.maxY - renderPosY, box.minZ - renderPosZ)
        
        glEnd()
        
        glBegin(GL_LINES)
        glVertex3d(box.minX - renderPosX, box.minY - renderPosY, box.minZ - renderPosZ)
        glVertex3d(box.minX - renderPosX, box.maxY - renderPosY, box.minZ - renderPosZ)
        glVertex3d(box.maxX - renderPosX, box.minY - renderPosY, box.minZ - renderPosZ)
        glVertex3d(box.maxX - renderPosX, box.maxY - renderPosY, box.minZ - renderPosZ)
        glVertex3d(box.maxX - renderPosX, box.minY - renderPosY, box.maxZ - renderPosZ)
        glVertex3d(box.maxX - renderPosX, box.maxY - renderPosY, box.maxZ - renderPosZ)
        glVertex3d(box.minX - renderPosX, box.minY - renderPosY, box.maxZ - renderPosZ)
        glVertex3d(box.minX - renderPosX, box.maxY - renderPosY, box.maxZ - renderPosZ)
        glEnd()
        
        glEnable(GL_DEPTH_TEST)
        glDisable(GL_LINE_SMOOTH)
        glDisable(GL_BLEND)
        glEnable(GL_TEXTURE_2D)
        glPopMatrix()
    }

    override val tag: String
        get() = when {
            isPausedByFlag -> "Paused(${flagPauseTime}s)"
            isBlinking -> BlinkUtils.packets.size.toString()
            else -> "Waiting"
        }
}
