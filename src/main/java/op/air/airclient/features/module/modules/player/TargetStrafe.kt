/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.player

import op.air.airclient.event.MoveEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.features.module.modules.combat.KillAura
import op.air.airclient.features.module.modules.movement.Fly
import op.air.airclient.features.module.modules.movement.LongJump
import op.air.airclient.features.module.modules.movement.Speed
import op.air.airclient.utils.extensions.isMoving
import op.air.airclient.utils.rotation.RotationUtils
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object TargetStrafe : Module("TargetStrafe", Category.PLAYER) {
    private val keyMode by choices("KeyMode", arrayOf("Jump", "None"), "Jump")
    private val safeWalk by boolean("SafeWalk", true)
    private val behind by boolean("Behind", false)
    private val thirdPerson by boolean("ThirdPerson", false)

    private var direction = 1
    private var lastView = 0
    private var hasChangedThirdPerson = true

    override val tag: String
        get() = if (behind) "Behind" else "Adaptive"

    override fun onEnable() {
        hasChangedThirdPerson = true
        lastView = mc.gameSettings.thirdPersonView
    }

    override fun onDisable() {
        if (!hasChangedThirdPerson && thirdPerson) {
            mc.gameSettings.thirdPersonView = lastView
            hasChangedThirdPerson = true
        }
    }

    private val canStrafe: Boolean
        get() {
            val isSpeedEnabled = Speed.handleEvents()
            val isFlyEnabled = Fly.handleEvents()
            val isLongJumpEnabled = LongJump.handleEvents()
            val isKillAuraEnabled = KillAura.handleEvents()
            val hasTarget = KillAura.target != null

            val keyModeCheck = when (keyMode.lowercase(Locale.getDefault())) {
                "jump" -> mc.thePlayer.isMoving && mc.gameSettings.keyBindJump.isKeyDown
                "none" -> mc.thePlayer.isMoving
                else -> false
            }

            return state && (isSpeedEnabled || isFlyEnabled || isLongJumpEnabled) && 
                   isKillAuraEnabled && hasTarget && 
                   !mc.thePlayer.isSneaking && keyModeCheck &&
                   mc.gameSettings.keyBindForward.isKeyDown &&
                   !mc.gameSettings.keyBindRight.isKeyDown &&
                   !mc.gameSettings.keyBindLeft.isKeyDown &&
                   !mc.gameSettings.keyBindBack.isKeyDown
        }

    val onMove = handler<MoveEvent> { event ->
        if (canStrafe) {
            strafe(event, getSpeed(event.x, event.z))

            if (safeWalk && checkVoid() && !Fly.handleEvents()) {
                event.isSafeWalk = true
            }
        }

        if (mc.thePlayer.isCollidedHorizontally || (safeWalk && checkVoid() && !Fly.handleEvents())) {
            if (canStrafe) {
                direction = -direction
            }
        }
    }

    private fun strafe(event: MoveEvent, moveSpeed: Double) {
        val target = KillAura.target ?: return

        val rotYaw = RotationUtils.toRotation(target.positionVector, false).yaw

        if (mc.thePlayer.getDistanceToEntity(target) <= 1.5) {
            setSpeed(event, moveSpeed, rotYaw, direction.toDouble(), 0.0)
        } else {
            setSpeed(event, moveSpeed, rotYaw, direction.toDouble(), 1.0)
        }

        if (behind) {
            val xPos = target.posX + -sin(Math.toRadians(target.rotationYaw.toDouble())) * -2
            val zPos = target.posZ + cos(Math.toRadians(target.rotationYaw.toDouble())) * -2
            val behindYaw = getRotations(xPos, target.posY, zPos)
            event.x = moveSpeed * -MathHelper.sin(Math.toRadians(behindYaw).toFloat())
            event.z = moveSpeed * MathHelper.cos(Math.toRadians(behindYaw).toFloat())
        } else {
            if (mc.thePlayer.getDistanceToEntity(target) <= 2.5f) {
                setSpeed(event, moveSpeed, rotYaw, direction.toDouble(), 0.0)
            } else {
                setSpeed(event, moveSpeed, rotYaw, direction.toDouble(), 1.0)
            }
        }
    }

    private fun setSpeed(event: MoveEvent, speed: Double, yaw: Float, direction: Double, forward: Double) {
        var yaw = yaw
        var forward = forward

        if (forward != 0.0) {
            if (direction > 0) {
                yaw += if (forward > 0) -45 else 45
            } else {
                yaw += if (forward > 0) 45 else -45
            }
            forward = 1.0
        }

        val yawRad = Math.toRadians(yaw.toDouble() + 90.0)
        event.x = -sin(yawRad) * speed * forward * if (direction > 0) 1.0 else -1.0
        event.z = cos(yawRad) * speed * forward * if (direction > 0) 1.0 else -1.0
    }

    private fun getSpeed(x: Double, z: Double): Double {
        return sqrt(x * x + z * z)
    }

    private fun getRotations(x: Double, y: Double, z: Double): Double {
        val diffX = x - mc.thePlayer.posX
        val diffZ = z - mc.thePlayer.posZ
        return Math.toDegrees(atan2(diffZ, diffX)) - 90.0
    }

    private fun checkVoid(): Boolean {
        for (x in -1..0) {
            for (z in -1..0) {
                if (isVoid(x, z)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isVoid(X: Int, Z: Int): Boolean {
        if (mc.thePlayer.posY < 0.0) {
            return true
        }
        var off = 0
        while (off < mc.thePlayer.posY.toInt() + 2) {
            val bb = mc.thePlayer.entityBoundingBox.offset(X.toDouble(), (-off).toDouble(), Z.toDouble())
            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer as Entity, bb).isEmpty()) {
                off += 2
                continue
            }
            return false
        }
        return true
    }
}
