/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.player

import op.air.airclient.event.BlockBBEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import net.minecraft.block.BlockCactus
import net.minecraft.util.AxisAlignedBB

object AntiCactus : Module("AntiCactus", Category.PLAYER, gameDetecting = false) {

    val onBlockBB = handler<BlockBBEvent> { event ->
        if (event.block is BlockCactus)
            event.boundingBox = AxisAlignedBB(
                event.x.toDouble(), event.y.toDouble(), event.z.toDouble(),
                event.x + 1.0, event.y + 1.0, event.z + 1.0
            )
    }
}
