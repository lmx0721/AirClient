/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.player

import op.air.airclient.event.PacketEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import net.minecraft.network.play.client.C03PacketPlayer

object PotionSaver : Module("PotionSaver", Category.PLAYER) {

    val onPacket = handler<PacketEvent> {
        val packet = it.packet

        if (packet is C03PacketPlayer && mc.thePlayer?.isUsingItem == false && !packet.rotating &&
            (!packet.isMoving || (packet.x == mc.thePlayer.lastTickPosX && packet.y == mc.thePlayer.lastTickPosY && packet.z == mc.thePlayer.lastTickPosZ))
        )
            it.cancelEvent()
    }

}