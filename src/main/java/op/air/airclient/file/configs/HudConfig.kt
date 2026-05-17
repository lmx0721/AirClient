/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.file.configs

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import op.air.airclient.config.FontValue
import op.air.airclient.config.Value
import op.air.airclient.file.FileConfig
import op.air.airclient.ui.client.hud.HUD
import op.air.airclient.ui.client.hud.element.Side
import op.air.airclient.utils.client.ClientUtils
import op.air.airclient.utils.io.json
import op.air.airclient.utils.io.jsonArray
import op.air.airclient.utils.io.readJson
import op.air.airclient.utils.io.writeJson
import java.io.File
import java.io.IOException

class HudConfig(file: File) : FileConfig(file) {

    override fun loadDefault() = HUD.setDefault()

    /**
     * Load config from file
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun loadConfig() {
        val jsonArray = file.readJson() as? JsonArray ?: return

        HUD.clearElements()

        try {
            for (jsonObject in jsonArray) {
                if (jsonObject !is JsonObject)
                    continue

                if (!jsonObject.has("Type"))
                    continue

                val type = jsonObject["Type"].asString

                try {
                    val elementClass = HUD.ELEMENTS.toList().find { it.second?.name == type }?.first

                    if (elementClass == null) {
                        ClientUtils.LOGGER.warn("Unrecognized HUD element: '$type'")
                        continue
                    }

                    val element = elementClass.getDeclaredConstructor().newInstance()

                    element.x = jsonObject["X"].asDouble
                    element.y = jsonObject["Y"].asDouble
                    element.scale = jsonObject["Scale"].asFloat
                    element.side = Side(
                        Side.Horizontal.getByName(jsonObject["HorizontalFacing"].asString) ?: Side.Horizontal.RIGHT,
                        Side.Vertical.getByName(jsonObject["VerticalFacing"].asString) ?: Side.Vertical.UP
                    )

                    for (value in element.values) {
                        if (jsonObject.has(value.name))
                            (value as Value<*>).fromJson(jsonObject[value.name])
                    }

                    if (jsonObject.has("font"))
                        (element.values.find { it is FontValue } as? Value<*>)?.fromJson(jsonObject["font"])

                    HUD.addElement(element)
                } catch (e: Exception) {
                    ClientUtils.LOGGER.error("Error while loading custom HUD element '$type' from config.", e)
                }
            }

            // Add forced elements when missing
            for ((elementClass, info) in HUD.ELEMENTS.toList()) {
                if (info?.force == true && HUD.elements.none { it.javaClass == elementClass }) {
                    HUD.addElement(elementClass.getDeclaredConstructor().newInstance())
                }
            }
        } catch (e: Exception) {
            ClientUtils.LOGGER.error("Error while loading custom hud config.", e)
            HUD.setDefault()
        }
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun saveConfig() {
        val jsonArray = jsonArray {
            for (element in HUD.elements) {
                +json {
                    "Type" to element.name
                    "X" to element.x
                    "Y" to element.y
                    "Scale" to element.scale
                    "HorizontalFacing" to element.side.horizontal.sideName
                    "VerticalFacing" to element.side.vertical.sideName

                    element.values.forEach {
                        it.name to it.toJson()
                    }
                }
            }
        }

        file.writeJson(jsonArray)
    }
}