/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.world

import op.air.airclient.event.UpdateEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.utils.block.block
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks

object AutoBreak : Module("AutoBreak", Category.WORLD, subjective = true, gameDetecting = false) {

    val onUpdate = handler<UpdateEvent> {
        mc.theWorld ?: return@handler

        val target = mc.objectMouseOver?.blockPos ?: return@handler

        mc.gameSettings.keyBindAttack.pressed = target.block != Blocks.air
    }

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindAttack))
            mc.gameSettings.keyBindAttack.pressed = false
    }
}
