/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.render

import op.air.airclient.event.Render3DEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.utils.extensions.interpolatedPosition
import op.air.airclient.utils.extensions.prevPos
import op.air.airclient.utils.render.ColorSettingsInteger
import op.air.airclient.utils.render.RenderUtils.drawDome
import op.air.airclient.utils.render.RenderUtils.drawEntityBox
import net.minecraft.entity.item.EntityTNTPrimed
import org.lwjgl.opengl.GL11.*
import java.awt.Color

object TNTESP : Module("TNTESP", Category.RENDER, spacedName = "TNT ESP") {

    private val dangerZoneDome by boolean("DangerZoneDome", false)
    private val mode by choices("Mode", arrayOf("Lines", "Triangles", "Filled"), "Lines") { dangerZoneDome }
    private val lineWidth by float("LineWidth", 1F, 0.5F..5F) { mode == "Lines" }
    private val colors = ColorSettingsInteger(this, "Dome") { dangerZoneDome }

    private val renderModes = mapOf("Lines" to GL_LINES, "Triangles" to GL_TRIANGLES, "Filled" to GL_QUADS)

    val onRender3D = handler<Render3DEvent> {
        val renderMode = renderModes[mode] ?: return@handler
        val color = colors.color()

        val width = lineWidth.takeIf { mode == "Lines" }

        mc.theWorld.loadedEntityList.forEach {
            if (it !is EntityTNTPrimed) return@forEach

            if (dangerZoneDome) {
                drawDome(it.interpolatedPosition(it.prevPos), 8.0, 8.0, width, color, renderMode)
            }

            drawEntityBox(it, Color.RED, false)
        }
    }
}