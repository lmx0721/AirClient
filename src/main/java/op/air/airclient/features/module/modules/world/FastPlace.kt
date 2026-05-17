/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.world

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module

object FastPlace : Module("FastPlace", Category.WORLD) {
    val speed by int("Speed", 0, 0..4)
    val onlyBlocks by boolean("OnlyBlocks", true)
    val facingBlocks by boolean("OnlyWhenFacingBlocks", true)
}
