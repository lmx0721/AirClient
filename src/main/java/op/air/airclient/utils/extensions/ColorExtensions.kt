/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.utils.extensions

import java.awt.Color

fun Color.withAlpha(a: Int) = Color(red, green, blue, a)
