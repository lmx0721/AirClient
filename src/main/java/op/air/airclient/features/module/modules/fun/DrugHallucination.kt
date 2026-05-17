/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.`fun`

import op.air.airclient.event.GameTickEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import net.minecraft.util.ResourceLocation

object DrugHallucination : Module("DrugHallucination", Category.FUN) {
    private val amount by int("Amount", 6, 1..10)

    override fun onDisable() {
        if (mc.entityRenderer.isShaderActive) {
            mc.entityRenderer.stopUseShader()
        }
    }

    val onTick = handler<GameTickEvent> {
        if (mc.thePlayer != null) {
            if (mc.entityRenderer.shaderGroup == null) {
                mc.entityRenderer.loadShader(ResourceLocation("shaders/post/drug_hallucination.json"))
            }
            
            val strength = (amount / 10f).coerceIn(0.1f, 0.95f)
            
            if (mc.entityRenderer.shaderGroup != null) {
                mc.entityRenderer.shaderGroup.listShaders[0].shaderManager.getShaderUniform("Phosphor")
                    .set(strength, 0f, 0f)
            }
        }
    }
}
