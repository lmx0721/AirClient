/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.world

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module

object NoSlowBreak : Module("NoSlowBreak", Category.WORLD, gameDetecting = false) {
    val air by boolean("Air", true)
    val water by boolean("Water", false)
}
