/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement

import op.air.airclient.event.EventState
import op.air.airclient.event.MotionEvent
import op.air.airclient.event.WorldEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.utils.client.PacketUtils.sendPacket
import op.air.airclient.utils.client.PacketUtils.sendPackets
import op.air.airclient.utils.extensions.isMoving
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SNEAKING
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SNEAKING

object Sneak : Module("Sneak", Category.MOVEMENT) {

    val mode by choices("Mode", arrayOf("Legit", "Vanilla", "Switch", "MineSecure"), "MineSecure")
    val stopMove by boolean("StopMove", false)

    private var sneaking = false

    val onMotion = handler<MotionEvent> { event ->
        if (stopMove && mc.thePlayer.isMoving) {
            if (sneaking)
                onDisable()
            return@handler
        }

        when (mode.lowercase()) {
            "legit" -> mc.gameSettings.keyBindSneak.pressed = true
            "vanilla" -> {
                if (sneaking)
                    return@handler

                sendPacket(C0BPacketEntityAction(mc.thePlayer, START_SNEAKING))
            }

            "switch" -> {
                when (event.eventState) {
                    EventState.PRE -> {
                        sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, START_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, STOP_SNEAKING)
                        )
                    }

                    EventState.POST -> {
                        sendPackets(
                            C0BPacketEntityAction(mc.thePlayer, STOP_SNEAKING),
                            C0BPacketEntityAction(mc.thePlayer, START_SNEAKING)
                        )
                    }

                    else -> {}
                }
            }

            "minesecure" -> {
                if (event.eventState == EventState.PRE)
                    return@handler

                sendPacket(C0BPacketEntityAction(mc.thePlayer, START_SNEAKING))
            }
        }
    }

    val onWorld = handler<WorldEvent> {
        sneaking = false
    }

    override fun onDisable() {
        val player = mc.thePlayer ?: return

        when (mode.lowercase()) {
            "legit" -> {
                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.gameSettings.keyBindSneak.pressed = false
                }
            }

            "vanilla", "switch", "minesecure" -> sendPacket(C0BPacketEntityAction(player, STOP_SNEAKING))
        }
        sneaking = false
    }
}