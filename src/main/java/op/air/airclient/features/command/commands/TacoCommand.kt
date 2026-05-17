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

import op.air.airclient.event.Listenable
import op.air.airclient.event.Render2DEvent
import op.air.airclient.event.UpdateEvent
import op.air.airclient.event.handler
import op.air.airclient.features.command.Command
import op.air.airclient.utils.extensions.component1
import op.air.airclient.utils.extensions.component2
import op.air.airclient.utils.render.RenderUtils.deltaTime
import op.air.airclient.utils.render.RenderUtils.drawImage
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation

object TacoCommand : Command("taco"), Listenable {
    var tacoToggle = false
    private var image = 0
    private var running = 0f
    private val tacoTextures = arrayOf(
        ResourceLocation("airclient/taco/1.jpg"),
        ResourceLocation("airclient/taco/2.jpg"),
        ResourceLocation("airclient/taco/3.jpg"),
        ResourceLocation("airclient/taco/4.jpg")
    )

    override fun execute(args: Array<String>) {
        tacoToggle = !tacoToggle
        chat(if (tacoToggle) "§aTACO TACO TACO. :)" else "§cYou made the little taco sad! :(")
    }

    val onRender2D = handler<Render2DEvent> {
        if (!tacoToggle)
            return@handler

        running += 0.15f * deltaTime
        val (width, height) = ScaledResolution(mc)
        drawImage(tacoTextures[image], running.toInt(), height - 60, 64, 32)
        if (width <= running)
            running = -64f
    }

    val onUpdate = handler<UpdateEvent> {
        if (!tacoToggle) {
            image = 0
            return@handler
        }

        image++
        if (image >= tacoTextures.size) image = 0
    }

    override fun tabComplete(args: Array<String>) = listOf("TACO")
}
