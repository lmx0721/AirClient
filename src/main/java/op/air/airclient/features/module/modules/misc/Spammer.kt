/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.misc

import kotlinx.coroutines.delay
import op.air.airclient.AirClient.CLIENT_NAME
import op.air.airclient.event.async.loopSequence
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.utils.kotlin.RandomUtils.nextFloat
import op.air.airclient.utils.kotlin.RandomUtils.nextInt
import op.air.airclient.utils.kotlin.RandomUtils.randomString

object Spammer : Module("Spammer", Category.MISC, subjective = true) {

    private val delay by intRange("Delay", 500..1000, 0..5000)

    private val message by text("Message", "$CLIENT_NAME Client | liquidbounce(.net) | CCBlueX on yt")

    private val custom by boolean("Custom", false)

    val onUpdate = loopSequence {
        mc.thePlayer?.sendChatMessage(
            if (custom) replace(message)
            else message + " >" + randomString(nextInt(5, 11)) + "<"
        )

        delay(delay.random().toLong())
    }

    private fun replace(text: String): String {
        var replacedStr = text

        replaceMap.forEach { (key, valueFunc) ->
            replacedStr = replacedStr.replace(key, valueFunc)
        }

        return replacedStr
    }

    private inline fun String.replace(oldValue: String, newValueProvider: () -> Any): String {
        var index = 0
        val newString = StringBuilder(this)
        while (true) {
            index = newString.indexOf(oldValue, startIndex = index)
            if (index == -1) {
                break
            }

            // You have to replace them one by one, otherwise all parameters like %s would be set to the same random string.
            val newValue = newValueProvider().toString()
            newString.replace(index, index + oldValue.length, newValue)

            index += newValue.length
        }
        return newString.toString()
    }

    private fun randomPlayer() =
        mc.netHandler.playerInfoMap
            .map { playerInfo -> playerInfo.gameProfile.name }
            .filter { name -> name != mc.thePlayer.name }
            .randomOrNull() ?: "none"

    private val replaceMap = mapOf(
        "%f" to { nextFloat().toString() },
        "%i" to { nextInt(0, 10000).toString() },
        "%ss" to { randomString(nextInt(1, 6)) },
        "%s" to { randomString(nextInt(1, 10)) },
        "%ls" to { randomString(nextInt(1, 17)) },
        "%p" to { randomPlayer() }
    )
}