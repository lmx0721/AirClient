/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.script.api

import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.ScriptUtils
import op.air.airclient.features.command.Command
import op.air.airclient.utils.client.ClientUtils.LOGGER

class ScriptCommand(private val commandObject: JSObject) : Command(commandObject.getMember("name") as String,
        *ScriptUtils.convert(commandObject.getMember("aliases"), Array<String>::class.java) as Array<out String>) {

    private val events = hashMapOf<String, JSObject>()

    /**
     * Called from inside the script to register a new event handler.
     * @param eventName Name of the event.
     * @param handler JavaScript function used to handle the event.
     */
    fun on(eventName: String, handler: JSObject) {
        events[eventName] = handler
    }

    override fun execute(args: Array<String>) {
        try {
            events["execute"]?.call(commandObject, args)
        } catch (throwable: Throwable) {
            LOGGER.error("[ScriptAPI] Exception in command '$command'!", throwable)
        }
    }
}