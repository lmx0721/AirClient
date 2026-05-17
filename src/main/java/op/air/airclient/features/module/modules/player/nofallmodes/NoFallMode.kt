/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.player.nofallmodes

import op.air.airclient.event.*
import op.air.airclient.utils.client.MinecraftInstance

open class NoFallMode(val modeName: String) : MinecraftInstance {
    open fun onMove(event: MoveEvent) {}
    open fun onPacket(event: PacketEvent) {}
    open fun onRender3D(event: Render3DEvent) {}
    open fun onBB(event: BlockBBEvent) {}
    open fun onJump(event: JumpEvent) {}
    open fun onStep(event: StepEvent) {}
    open fun onMotion(event: MotionEvent) {}
    open fun onUpdate() {}
    open fun onTick() {}
    open fun onRotationUpdate() {}

    open fun onEnable() {}
    open fun onDisable() {}
}