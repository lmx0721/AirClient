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

object NoFOV : Module("NoFOV", Category.RENDER, gameDetecting = false) {
    init {
        state = true
    }
    val fov by float("FOV", 1f, 0f..1.5f)
}
