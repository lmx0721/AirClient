/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

package op.air.airclient.features.module.modules.movement.nowebmodes

import op.air.airclient.utils.client.MinecraftInstance

open class NoWebMode(val modeName: String) : MinecraftInstance {
    open fun onUpdate() {}
}
