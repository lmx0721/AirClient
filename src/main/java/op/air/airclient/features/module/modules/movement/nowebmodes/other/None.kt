/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.nowebmodes.other

import op.air.airclient.features.module.modules.movement.nowebmodes.NoWebMode

object None : NoWebMode("None") {
    override fun onUpdate() {
        mc.thePlayer.isInWeb = false
    }
}
