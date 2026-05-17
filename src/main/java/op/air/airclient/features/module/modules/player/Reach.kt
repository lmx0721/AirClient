/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.player

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import kotlin.math.max

object Reach : Module("Reach", Category.PLAYER) {

    val combatReach by float("CombatReach", 3.5f, 3f..7f)
    val buildReach by float("BuildReach", 5f, 4.5f..7f)

    val maxRange
        get() = max(combatReach, buildReach)
}
