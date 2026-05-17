/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement

import op.air.airclient.event.UpdateEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.features.module.modules.movement.nowebmodes.aac.AAC
import op.air.airclient.features.module.modules.movement.nowebmodes.aac.LAAC
import op.air.airclient.features.module.modules.movement.nowebmodes.intave.IntaveNew
import op.air.airclient.features.module.modules.movement.nowebmodes.intave.IntaveOld
import op.air.airclient.features.module.modules.movement.nowebmodes.other.None
import op.air.airclient.features.module.modules.movement.nowebmodes.other.OldGrim
import op.air.airclient.features.module.modules.movement.nowebmodes.other.Rewi

object NoWeb : Module("NoWeb", Category.MOVEMENT) {

    private val noWebModes = arrayOf(
        // Vanilla
        None,

        // AAC
        AAC, LAAC,

        // Intave
        IntaveOld,
        IntaveNew,

        // Other
        Rewi,
        OldGrim
    )

    private val modes = noWebModes.map { it.modeName }.toTypedArray()

    val mode by choices(
        "Mode", modes, "None"
    )

    val onUpdate = handler<UpdateEvent> {
        modeModule.onUpdate()
    }

    override val tag
        get() = mode

    private val modeModule
        get() = noWebModes.find { it.modeName == mode }!!
}
