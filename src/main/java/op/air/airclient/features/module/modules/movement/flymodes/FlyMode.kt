/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

package op.air.airclient.features.module.modules.movement.flymodes

import op.air.airclient.event.*
import op.air.airclient.utils.client.MinecraftInstance

open class FlyMode(val modeName: String) : MinecraftInstance {
    open fun onMove(event: MoveEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onRender3D(event: Render3DEvent) {}
    open fun onBB(event: BlockBBEvent) {}
    open fun onJump(event: JumpEvent) {}
    open fun onStep(event: StepEvent) {}
    open fun onMotion(event: MotionEvent) {}
    open fun onUpdate() {}
    open fun onTick() {}

    open fun onEnable() {}
    open fun onDisable() {}
}