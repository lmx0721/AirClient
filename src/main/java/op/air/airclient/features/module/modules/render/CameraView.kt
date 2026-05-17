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

import op.air.airclient.event.CameraPositionEvent
import op.air.airclient.event.EventState
import op.air.airclient.event.MotionEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.features.module.modules.world.scaffolds.Scaffold

object CameraView : Module("CameraView", Category.RENDER, gameDetecting = false) {

    private val customY by float("CustomY", 0f, -10f..10f)
    private val saveLastGroundY by boolean("SaveLastGroundY", true)
    private val onScaffold by boolean("OnScaffold", true)
    private val onF5 by boolean("OnF5", true)

    private var launchY: Double? = null

    override fun onEnable() {
        mc.thePlayer?.run {
            launchY = posY
        }
    }

    val onMotion = handler<MotionEvent> { event ->
        if (event.eventState != EventState.POST) return@handler

        mc.thePlayer?.run {
            if (!saveLastGroundY || (onGround || ticksExisted == 1)) {
                launchY = posY
            }
        }
    }

    val onCameraUpdate = handler<CameraPositionEvent> { event ->
        mc.thePlayer?.run {
            val currentLaunchY = launchY ?: return@handler
            if (onScaffold && !Scaffold.handleEvents()) return@handler
            if (onF5 && mc.gameSettings.thirdPersonView == 0) return@handler

            event.withY(currentLaunchY + customY)
        }
    }
}