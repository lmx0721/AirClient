/*
 * AirClient++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package op.air.airclient.ui.client.clickgui.newVer.extensions

import op.air.airclient.features.module.modules.client.NewGUI
import op.air.airclient.utils.render.LBPPAnimationUtils
import op.air.airclient.utils.render.RenderUtils

fun Float.animSmooth(target: Float, speed: Float) = if (NewGUI.fastRenderValue) target else LBPPAnimationUtils.animate(target, this, speed * RenderUtils.deltaTime * 0.025F)
fun Float.animLinear(speed: Float, min: Float, max: Float) = if (NewGUI.fastRenderValue) { if (speed < 0F) min else max } else (this + speed).coerceIn(min, max)