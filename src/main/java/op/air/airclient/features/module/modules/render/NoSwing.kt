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

object NoSwing : Module("NoSwing", Category.RENDER) {
    val serverSide by boolean("ServerSide", true).hide()
    val clientSide by boolean("ClientSide", true).hide()
}
