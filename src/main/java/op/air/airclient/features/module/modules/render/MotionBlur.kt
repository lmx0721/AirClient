/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.render

import op.air.airclient.event.GameTickEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import net.minecraft.util.ResourceLocation

object MotionBlur : Module("MotionBlur", Category.RENDER) {
    private val mode by choices("Mode", arrayOf("Simple", "Quality", "Phosphor"), "Simple")
    private val amount by int("Amount", 6, 1..10)

    override val tag: String
        get() = mode

    override fun onDisable() {
        if (mc.entityRenderer.isShaderActive) {
            mc.entityRenderer.stopUseShader()
        }
    }

    val onTick = handler<GameTickEvent> {
        if (mc.thePlayer != null) {
            val shaderLocation = when (mode) {
                "Simple" -> "shaders/post/motion_blur_simple.json"
                "Quality" -> "shaders/post/motion_blur_quality.json"
                "Phosphor" -> "shaders/post/motion_blur_phosphor.json"
                else -> "shaders/post/motion_blur_simple.json"
            }
            
            if (mc.entityRenderer.shaderGroup == null) {
                mc.entityRenderer.loadShader(ResourceLocation(shaderLocation))
            }
            
            val strength = (amount / 10f).coerceIn(0.1f, 0.95f)
            
            if (mc.entityRenderer.shaderGroup != null) {
                mc.entityRenderer.shaderGroup.listShaders[0].shaderManager.getShaderUniform("Phosphor")
                    .set(strength, 0f, 0f)
            }
        }
    }
}
