/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.misc

import op.air.airclient.event.PacketEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.utils.client.ClientUtils.LOGGER
import op.air.airclient.utils.client.PacketUtils.sendPacket
import op.air.airclient.utils.client.PacketUtils.sendPackets
import net.minecraft.network.play.client.C19PacketResourcePackStatus
import net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.*
import net.minecraft.network.play.server.S48PacketResourcePackSend
import java.net.URI
import java.net.URISyntaxException

object ResourcePackSpoof : Module("ResourcePackSpoof", Category.MISC, gameDetecting = false) {

    val onPacket = handler<PacketEvent> { event ->
        val packet = event.packet as? S48PacketResourcePackSend ?: return@handler

        val url = packet.url
        val hash = packet.hash

        try {
            val scheme = URI(url).scheme
            val isLevelProtocol = "level" == scheme

            if ("http" != scheme && "https" != scheme && !isLevelProtocol)
                throw URISyntaxException(url, "Wrong protocol")

            if (isLevelProtocol && (".." in url || !url.endsWith("/resources.zip")))
                throw URISyntaxException(url, "Invalid levelstorage resourcepack path")

            sendPackets(
                C19PacketResourcePackStatus(packet.hash, ACCEPTED),
                C19PacketResourcePackStatus(packet.hash, SUCCESSFULLY_LOADED)
            )
        } catch (e: URISyntaxException) {
            LOGGER.error("Failed to handle resource pack", e)
            sendPacket(C19PacketResourcePackStatus(hash, FAILED_DOWNLOAD))
        }
    }

}