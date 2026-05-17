/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.flymodes.other

import op.air.airclient.event.BlockBBEvent
import op.air.airclient.features.module.modules.movement.Fly.jumpY
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode
import op.air.airclient.utils.extensions.tryJump
import net.minecraft.block.BlockLadder
import net.minecraft.block.material.Material
import net.minecraft.util.AxisAlignedBB

object Jump : FlyMode("Jump") {

    override fun onUpdate() {
        if (mc.thePlayer == null)
            return
        if (mc.thePlayer.onGround && !mc.thePlayer.isJumping)
            mc.thePlayer.tryJump()
        if ((mc.gameSettings.keyBindJump.isKeyDown && !mc.gameSettings.keyBindSneak.isKeyDown) || mc.thePlayer.onGround)
            jumpY = mc.thePlayer.posY
    }

    override fun onBB(event: BlockBBEvent) {
        val jumpYCondition =
            if (!mc.gameSettings.keyBindJump.isKeyDown && mc.gameSettings.keyBindSneak.isKeyDown) event.y.toDouble() < jumpY else event.y.toDouble() <= jumpY
        if ((!event.block.material.blocksMovement() && event.block.material != Material.carpet && event.block.material != Material.vine && event.block.material != Material.snow && event.block !is BlockLadder) && jumpYCondition) {
            event.boundingBox = AxisAlignedBB.fromBounds(
                event.x.toDouble(),
                event.y.toDouble(),
                event.z.toDouble(),
                event.x.toDouble() + 1,
                1.0,
                event.z.toDouble() + 1
            )
        }
    }
}
