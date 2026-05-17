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

import op.air.airclient.AirClient.moduleManager
import op.air.airclient.features.command.Command
import org.lwjgl.input.Keyboard

object BindsCommand : Command("binds") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            if (args[1].equals("clear", true)) {
                for (module in moduleManager)
                    module.keyBind = Keyboard.KEY_NONE

                chat("Removed all binds.")
                return
            }
        }

        chat("§c§lBinds")
        moduleManager.forEach {
            if (it.keyBind != Keyboard.KEY_NONE)
                chat("§6> §c${it.getName()}: §a§l${Keyboard.getKeyName(it.keyBind)}")
        }

        chatSyntax("binds clear")
    }
}