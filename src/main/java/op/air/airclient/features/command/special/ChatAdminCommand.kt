/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

package op.air.airclient.features.command.special

import op.air.airclient.features.command.Command
import op.air.airclient.features.module.modules.misc.LiquidChat

object ChatAdminCommand : Command("chatadmin") {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (!LiquidChat.state) {
            chat("§cError: §7LiquidChat is disabled!")
            return
        }

        if (args.size <= 1) {
            chatSyntax("chatadmin <ban/unban>")
            return
        }

        when (args[1].lowercase()) {
            "ban" -> {
                if (args.size > 2) {
                    LiquidChat.client.banUser(args[2])
                } else {
                    chatSyntax("chatadmin ban <username>")
                }
            }

            "unban" -> {
                if (args.size > 2) {
                    LiquidChat.client.unbanUser(args[2])
                } else {
                    chatSyntax("chatadmin unban <username>")
                }
            }
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty())
            return emptyList()

        return when (args.size) {
            1 -> {
                arrayOf("ban", "unban")
                    .map { it.lowercase() }
                    .filter { it.startsWith(args[0], true) }
            }

            else -> emptyList()
        }
    }
}