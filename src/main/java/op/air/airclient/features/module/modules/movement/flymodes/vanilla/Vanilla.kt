/*
 * AirClient Hacked Client
 *  A free open source mixin-based injection hacked client built on Liquidbounce legacy codebase.
 * https://github.com/lmx0721/AirClient
 */

/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.features.module.modules.movement.flymodes.vanilla

import op.air.airclient.event.MoveEvent
import op.air.airclient.features.module.modules.movement.Fly.handleVanillaKickBypass
import op.air.airclient.features.module.modules.movement.Fly.vanillaSpeed
import op.air.airclient.features.module.modules.movement.flymodes.FlyMode
import op.air.airclient.utils.movement.MovementUtils.strafe

object Vanilla : FlyMode("Vanilla") {
    override fun onMove(event: MoveEvent) {
        val thePlayer = mc.thePlayer ?: return

        strafe(vanillaSpeed, true, event)

        thePlayer.onGround = false
        thePlayer.isInWeb = false

        thePlayer.capabilities.isFlying = false

        var ySpeed = 0.0

        if (mc.gameSettings.keyBindJump.isKeyDown)
            ySpeed += vanillaSpeed

        if (mc.gameSettings.keyBindSneak.isKeyDown)
            ySpeed -= vanillaSpeed

        thePlayer.motionY = ySpeed
        event.y = ySpeed

        handleVanillaKickBypass()
    }
}
