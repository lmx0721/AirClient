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

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module

object ItemPhysics : Module("ItemPhysics", Category.RENDER) {

    val realistic by boolean("Realistic", false)
    val weight by float("Weight", 0.5F, 0.1F..3F)
    val rotationSpeed by float("RotationSpeed", 1.0F, 0.01F..3F)

}