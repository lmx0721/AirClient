/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.player.nofallmodes.other

import op.air.airclient.event.PacketEvent
import op.air.airclient.event.Render3DEvent
import op.air.airclient.features.module.modules.player.NoFall.autoOff
import op.air.airclient.features.module.modules.player.NoFall.checkFallDist
import op.air.airclient.features.module.modules.player.NoFall.fakePlayer
import op.air.airclient.features.module.modules.player.NoFall.fallDist
import op.air.airclient.features.module.modules.player.NoFall.simulateDebug
import op.air.airclient.features.module.modules.player.NoFall.state
import op.air.airclient.features.module.modules.player.nofallmodes.NoFallMode
import op.air.airclient.injection.implementations.IMixinEntity
import op.air.airclient.utils.client.BlinkUtils
import op.air.airclient.utils.client.chat
import op.air.airclient.utils.extensions.*
import op.air.airclient.utils.movement.FallingPlayer
import op.air.airclient.utils.render.RenderUtils.drawBacktrackBox
import op.air.airclient.utils.simulation.SimulatedPlayer
import op.air.airclient.utils.timing.TickTimer
import net.minecraft.network.play.client.C03PacketPlayer
import java.awt.Color

object Blink : NoFallMode("Blink") {
    private var blinked = false

    private val tick = TickTimer()

    override fun onDisable() {
        BlinkUtils.unblink()
        blinked = false
        tick.reset()
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isDead)
            return

        val simPlayer = SimulatedPlayer.fromClientPlayer(thePlayer.movementInput)

        simPlayer.tick()

        if (simPlayer.onGround && blinked) {
            if (thePlayer.onGround) {
                tick.update()

                if (tick.hasTimePassed(100)) {
                    BlinkUtils.unblink()
                    blinked = false
                    chat("Unblink")

                    if (autoOff) {
                        state = false
                    }
                    tick.reset()
                }
            }
        }

        if (event.packet is C03PacketPlayer) {
            if (blinked && thePlayer.fallDistance > fallDist.start) {
                if (thePlayer.fallDistance < fallDist.endInclusive) {
                    if (blinked) {
                        event.packet.onGround = thePlayer.ticksExisted % 2 == 0
                    }
                } else {
                    chat("rewriting ground")
                    BlinkUtils.unblink()
                    blinked = false
                    event.packet.onGround = false
                }
            }
        }

        // Re-check #1
        repeat(2) {
            simPlayer.tick()
        }

        if (simPlayer.isOnLadder() || simPlayer.inWater || simPlayer.isInLava() || simPlayer.isInWeb || simPlayer.isCollided)
            return

        if (thePlayer.motionY > 0 && blinked)
            return

        if (simPlayer.onGround)
            return

        // Re-check #2
        if (checkFallDist) {
            repeat(6) {
                simPlayer.tick()
            }
        }

        val fallingPlayer = FallingPlayer(thePlayer)

        if ((checkFallDist && simPlayer.fallDistance > fallDist.start) ||
            !checkFallDist && fallingPlayer.findCollision(60) != null && simPlayer.motionY < 0
        ) {
            if (thePlayer.onGround && !blinked) {
                blinked = true

                if (fakePlayer)
                    BlinkUtils.addFakePlayer()

                chat("Blinked")
                BlinkUtils.blink(packet, event)
            }
        }
    }

    override fun onRender3D(event: Render3DEvent) {
        if (!simulateDebug) return

        val thePlayer = mc.thePlayer ?: return

        val simPlayer = SimulatedPlayer.fromClientPlayer(thePlayer.movementInput)

        repeat(4) {
            simPlayer.tick()
        }

        thePlayer.run {
            val targetEntity = thePlayer as IMixinEntity

            if (targetEntity.truePos) {
                val pos = simPlayer.pos - mc.renderManager.renderPos

                val axisAlignedBB = entityBoundingBox.offset(-currPos + pos)

                drawBacktrackBox(axisAlignedBB, Color.BLUE)
            }
        }
    }
}