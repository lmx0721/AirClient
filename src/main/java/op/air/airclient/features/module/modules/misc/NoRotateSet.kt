/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.misc

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.utils.extensions.rotation
import op.air.airclient.utils.rotation.AlwaysRotationSettings
import op.air.airclient.utils.rotation.Rotation
import op.air.airclient.utils.rotation.RotationUtils.currentRotation
import op.air.airclient.utils.rotation.RotationUtils.setTargetRotation
import op.air.airclient.utils.timing.WaitTickUtils
import net.minecraft.entity.player.EntityPlayer

object NoRotateSet : Module("NoRotateSet", Category.MISC, gameDetecting = false) {
    var savedRotation = Rotation.ZERO

    private val ignoreOnSpawn by boolean("IgnoreOnSpawn", false)
    val affectRotation by boolean("AffectRotation", true)

    private val ticksUntilStart = intRange("TicksUntilStart", 0..0, 0..20) { affectRotation }

    private val options = AlwaysRotationSettings(this) { affectRotation }.apply {
        withoutKeepRotation()
        applyServerSideValue.hideWithState(true)
        resetTicksValue.excludeWithState(1)
    }

    fun shouldModify(player: EntityPlayer) = handleEvents() && (!ignoreOnSpawn || player.ticksExisted != 0)

    fun rotateBackToPlayerRotation() {
        val player = mc.thePlayer ?: return

        currentRotation = player.rotation

        WaitTickUtils.schedule(ticksUntilStart.random, this)

        setTargetRotation(savedRotation, options = options)
    }
}