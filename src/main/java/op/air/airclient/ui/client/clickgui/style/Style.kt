/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.ui.client.clickgui.style

import op.air.airclient.config.ColorValue
import op.air.airclient.config.Value
import op.air.airclient.file.FileManager.saveConfig
import op.air.airclient.file.FileManager.valuesConfig
import op.air.airclient.ui.client.clickgui.Panel
import op.air.airclient.ui.client.clickgui.elements.ButtonElement
import op.air.airclient.ui.client.clickgui.elements.ModuleElement
import op.air.airclient.utils.client.MinecraftInstance
import op.air.airclient.utils.client.asResourceLocation
import op.air.airclient.utils.client.playSound
import op.air.airclient.utils.extensions.decimalPlaces
import op.air.airclient.utils.timing.WaitTickUtils
import op.air.airclient.utils.ui.EditableText
import org.lwjgl.input.Mouse
import java.awt.Color
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max

abstract class Style : MinecraftInstance {
    val rgbaLabels = listOf("R:", "G:", "B:", "A:")

    protected var sliderValueHeld: Value<*>? = null
        get() {
            if (!Mouse.isButtonDown(0)) field = null
            return field
        }
        set(value) {
            if (chosenText?.value != value) {
                chosenText = null
            }

            field = value
        }

    var chosenText: EditableText? = null

    abstract fun drawPanel(mouseX: Int, mouseY: Int, panel: Panel)
    abstract fun drawHoverText(mouseX: Int, mouseY: Int, text: String)
    abstract fun drawButtonElement(mouseX: Int, mouseY: Int, buttonElement: ButtonElement)
    abstract fun drawModuleElementAndClick(
        mouseX: Int,
        mouseY: Int,
        moduleElement: ModuleElement,
        mouseButton: Int?
    ): Boolean

    fun clickSound() {
        mc.playSound("gui.button.press".asResourceLocation())
    }

    fun showSettingsSound() {
        mc.playSound("random.bow".asResourceLocation())
    }

    protected fun round(v: Float): Float {
        var bigDecimal = BigDecimal(v.toString())
        bigDecimal = bigDecimal.setScale(if (decimalPlaces(v) == 3) 3 else 2, RoundingMode.HALF_UP)
        return bigDecimal.toFloat()
    }

    protected fun getHoverColor(color: Color, hover: Int, inactiveModule: Boolean = false): Int {
        val r = color.red - hover * 2
        val g = color.green - hover * 2
        val b = color.blue - hover * 2
        val alpha = if (inactiveModule) color.alpha.coerceAtMost(128) else color.alpha

        return Color(max(r, 0), max(g, 0), max(b, 0), alpha).rgb
    }

    fun <T> Value<T>.setAndSaveValueOnButtonRelease(new: T) {
        if (this is ColorValue) {
            changeValue(new)
        } else {
            set(new, false)
        }

        with(WaitTickUtils) {
            if (!hasScheduled(this)) {
                conditionalSchedule(this, 10) {
                    (sliderValueHeld == null).also { if (it) saveConfig(valuesConfig) }
                }
            }
        }
    }

    fun withDelayedSave(f: () -> Unit) {
        f()

        with(WaitTickUtils) {
            if (!hasScheduled(this)) {
                conditionalSchedule(this, 10) {
                    (sliderValueHeld == null).also { if (it) saveConfig(valuesConfig) }
                }
            }
        }
    }

    fun resetChosenText(value: Value<*>) {
        if (chosenText?.value == value) {
            chosenText = null
        }
    }

    fun moveRGBAIndexBy(delta: Int) {
        val chosenText = chosenText ?: return

        if (chosenText.value !is ColorValue) {
            return
        }

        this.chosenText = EditableText.forRGBA(chosenText.value, (chosenText.value.rgbaIndex + delta).mod(4))
    }
}
