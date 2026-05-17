/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

package op.air.airclient.features.command.special

import op.air.airclient.AirClient.commandManager
import op.air.airclient.chat.packet.packets.ServerRequestJWTPacket
import op.air.airclient.features.command.Command
import op.air.airclient.features.module.modules.misc.LiquidChat
import op.air.airclient.utils.io.MiscUtils
import op.air.airclient.utils.kotlin.StringUtils

object ChatTokenCommand : Command("chattoken") {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size <= 1) {
            chatSyntax("chattoken <set/copy/generate>")
            return
        }

        when (args[1].lowercase()) {
            "set" -> {
                if (args.size > 2) {
                    LiquidChat.jwtToken = StringUtils.toCompleteString(args, 2)
                    LiquidChat.jwt = true

                    if (LiquidChat.state) {
                        LiquidChat.state = false
                        LiquidChat.state = true
                    }
                } else {
                    chatSyntax("chattoken set <token>")
                }
            }

            "generate" -> {
                if (!LiquidChat.state) {
                    chat("§cError: §7LiquidChat is disabled!")
                    return
                }

                LiquidChat.client.sendPacket(ServerRequestJWTPacket())
            }

            "copy" -> {
                if (LiquidChat.jwtToken.isEmpty()) {
                    chat("§cError: §7No token set! Generate one first using '${commandManager.prefix}chattoken generate'.")
                    return
                }

                MiscUtils.copy(LiquidChat.jwtToken)
                chat("§aCopied to clipboard!")
            }
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty())
            return emptyList()

        return when (args.size) {
            1 -> {
                arrayOf("set", "generate", "copy")
                    .map { it.lowercase() }
                    .filter { it.startsWith(args[0], true) }
            }

            else -> emptyList()
        }
    }

}