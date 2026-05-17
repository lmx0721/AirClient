/*
 * AirClient++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package op.air.airclient.ui.client.clickgui.newVer.element.module.value

import op.air.airclient.config.Value
import op.air.airclient.utils.MinecraftInstance

import java.awt.Color

abstract class ValueElement<T>(val value: Value<T>) : MinecraftInstance() {

    var valueHeight = 20F

    abstract fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float
    abstract fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float): Boolean
    open fun onRelease(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {}
    open fun onDrag(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {}
    open fun onKeyPress(typed: Char, keyCode: Int): Boolean = false
    open fun isTyping(): Boolean = false

    fun isDisplayable(): Boolean = value.shouldRender()
}