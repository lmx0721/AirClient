/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.command.commands

import op.air.airclient.features.command.Command
import op.air.airclient.features.module.modules.misc.PacketDebugger.packetType
import op.air.airclient.features.module.modules.misc.PacketDebugger.selectedPackets
import op.air.airclient.utils.client.ClientUtils

object PacketDebuggerCommand : Command("packetdebugger", "debug") {

    private lateinit var packetList: Set<String>

    init {
        runCatching {
            javaClass.getResourceAsStream("/assets/minecraft/airclient/packets.txt")!!.bufferedReader().use {
                packetList = it.readLines().toSet()
            }
        }.onFailure {
            ClientUtils.LOGGER.error("Failed to load packet list", it)
        }
    }

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (packetType != "Custom") {
            chat("§cPlease select 'Custom' mode in PacketDebugger first.")
            return
        }

        val usedAlias = args[0].lowercase()

        if (args.size < 2) {
            chatSyntax("$usedAlias <add/remove/list>")
            return
        }

        when (args[1].lowercase()) {
            "add" -> {
                if (args.size < 3) {
                    chatSyntax("$usedAlias add <packet>")
                    return
                }

                val packetName = args[2]
                if (selectedPackets.contains(packetName)) {
                    chat("§cPacket §b$packetName §cis already in the debug list.")
                } else {
                    selectedPackets.add(packetName)
                    chat("§b$packetName §ahas been added to the list.")
                }
            }

            "remove" -> {
                if (args.size < 3) {
                    chatSyntax("$usedAlias remove <packet>")
                    return
                }

                val packetName = args[2]
                if (selectedPackets.contains(packetName)) {
                    selectedPackets.remove(packetName)
                    chat("§b$packetName §6has been removed from the list.")
                } else {
                    chat("§b$packetName §cis not in the list.")
                }
            }

            "list" -> {
                val packets = selectedPackets
                chat("Packets List:")
                packets.forEach { chat("§b$it") }
            }

            else -> chatSyntax("$usedAlias <add/remove/list>")
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) {
            return emptyList()
        }

        return when (args.size) {
            1 -> listOf("add", "remove", "list").filter { it.startsWith(args[0], true) }
            2 -> {
                when (args[0].lowercase()) {
                    "add" -> {
                        val input = args[1].lowercase()
                        packetList.filter { it.lowercase().startsWith(input) }
                    }

                    "remove" -> {
                        val input = args[1].lowercase()
                        selectedPackets.filter { it.lowercase().startsWith(input) }
                    }

                    else -> emptyList()
                }
            }

            else -> emptyList()
        }
    }

}