/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan

import net.ccbluex.liquidbounce.config.BoolValue
import net.ccbluex.liquidbounce.config.IntValue
import net.ccbluex.liquidbounce.config.ListValue
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.extensions.isMoving
import net.ccbluex.liquidbounce.utils.movement.MovementUtils
import net.minecraft.client.settings.GameSettings
import kotlin.math.abs

object VulcanFDP : SpeedMode("VulcanFDP") {
    private val vulcanMode = ListValue("VulcanFDP-Mode", arrayOf("LowHop", "Hop", "OldGround", "YPort", "LowHop2"), "LowHop")
    private val boostDelay = IntValue("VulcanFDP-BoostDelay", 8, 2..15)
    private val boostSpeed = BoolValue("VulcanFDP-GroundBoost", true)

    private var wasTimer = false
    private var ticks = 0
    private var jumped = false
    private var jumpCount = 0
    private var launchY = 0.0

    override fun onEnable() {
        ticks = 0
        wasTimer = true
        mc.timer.timerSpeed = 1.0f
        jumpCount = 0
        launchY = mc.thePlayer?.posY ?: 0.0
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
        ticks = 0
        jumpCount = 0
        mc.thePlayer?.jumpMovementFactor = 0.02f
    }

    override fun onMove(event: MoveEvent) {
        if (vulcanMode.get() == "OldGround") {
            if (jumpCount >= boostDelay.get() && boostSpeed.get()) {
                event.x *= 1.7181145141919810
                event.z *= 1.7181145141919810
                jumpCount = 0
            } else if (!boostSpeed.get()) {
                jumpCount = 4
            }
        }
    }

    override fun onUpdate() {
        val player = mc.thePlayer ?: return
        val settings = mc.gameSettings

        when (vulcanMode.get()) {
            "LowHop" -> {
                ticks++
                player.jumpMovementFactor = 0.0245f
                if (player.onGround && player.isMoving) {
                    player.jump()
                    ticks = 0
                    MovementUtils.strafe()
                    if (MovementUtils.speed < 0.5f) MovementUtils.strafe(0.484f)
                    launchY = player.posY
                } else if (player.posY > launchY && ticks <= 1) {
                    player.setPosition(player.posX, launchY, player.posZ)
                } else if (ticks == 5) {
                    player.motionY = -0.17
                }
                if (MovementUtils.speed < 0.215f) MovementUtils.strafe(0.215f)
            }

            "Hop" -> {
                if (wasTimer) {
                    mc.timer.timerSpeed = 1.0f
                    wasTimer = false
                }
                player.jumpMovementFactor = if (abs(player.movementInput.moveStrafe) < 0.1f) 0.026499f else 0.0244f
                settings.keyBindJump.pressed = GameSettings.isKeyDown(settings.keyBindJump)
                if (MovementUtils.speed < 0.215f && !player.onGround) MovementUtils.strafe(0.215f)
                if (player.onGround && player.isMoving) {
                    settings.keyBindJump.pressed = false
                    player.jump()
                    if (!player.isAirBorne) return
                    mc.timer.timerSpeed = 1.25f
                    wasTimer = true
                    MovementUtils.strafe()
                    if (MovementUtils.speed < 0.5f) MovementUtils.strafe(0.4849f)
                } else if (!player.isMoving) {
                    mc.timer.timerSpeed = 1.0f
                    player.motionX = 0.0
                    player.motionZ = 0.0
                }
            }

            "OldGround" -> {
                if (jumped) {
                    player.motionY = -0.1
                    player.onGround = false
                    jumped = false
                }
                player.jumpMovementFactor = 0.025f
                if (player.onGround && player.isMoving) {
                    if (player.isCollidedHorizontally || settings.keyBindJump.pressed) {
                        if (!settings.keyBindJump.pressed) player.jump()
                        return
                    }
                    player.jump()
                    player.motionY = 0.0
                    MovementUtils.strafe(0.48f + jumpCount * 0.001f)
                    jumpCount++
                    jumped = true
                } else if (player.isMoving) {
                    MovementUtils.strafe(0.27f + jumpCount * 0.0018f)
                }
            }

            "YPort" -> {
                ticks++
                if (wasTimer) {
                    mc.timer.timerSpeed = 1.0f
                    wasTimer = false
                }
                player.jumpMovementFactor = 0.0245f
                if (!player.onGround && ticks > 3 && player.motionY > 0) player.motionY = -0.27
                settings.keyBindJump.pressed = GameSettings.isKeyDown(settings.keyBindJump)
                if (MovementUtils.speed < 0.215f && !player.onGround) MovementUtils.strafe(0.215f)
                if (player.onGround && player.isMoving) {
                    ticks = 0
                    settings.keyBindJump.pressed = false
                    player.jump()
                    if (!player.isAirBorne) return
                    mc.timer.timerSpeed = 1.2f
                    wasTimer = true
                    if (MovementUtils.speed < 0.48f) MovementUtils.strafe(0.48f) else MovementUtils.strafe(MovementUtils.speed * 0.985f)
                } else if (!player.isMoving) {
                    mc.timer.timerSpeed = 1.0f
                    player.motionX = 0.0
                    player.motionZ = 0.0
                }
            }

            "LowHop2" -> {
                if (player.isMoving) {
                    if (!player.onGround && player.fallDistance > 1.1) {
                        mc.timer.timerSpeed = 1f
                        player.motionY = -0.25
                        return
                    }
                    if (player.onGround) {
                        player.jump()
                        MovementUtils.strafe(0.4815f)
                        mc.timer.timerSpeed = 1.263f
                    } else if (player.ticksExisted % 4 == 0) {
                        player.motionY = if (player.ticksExisted % 3 == 0) -0.01 / player.motionY else -player.motionY / player.posY
                        mc.timer.timerSpeed = 0.8985f
                    }
                }
            }
        }
    }
}
