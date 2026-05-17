/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.file.configs

import com.google.gson.JsonArray
import op.air.airclient.features.module.modules.render.XRay
import op.air.airclient.file.FileConfig
import op.air.airclient.utils.block.blockById
import op.air.airclient.utils.block.id
import op.air.airclient.utils.io.readJson
import op.air.airclient.utils.io.writeJson
import net.minecraft.init.Blocks
import java.io.*

class XRayConfig(file: File) : FileConfig(file) {

    /**
     * Load config from file
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun loadConfig() {
        val json = file.readJson() as? JsonArray ?: return

        XRay.xrayBlocks.clear()

        json.mapNotNullTo(XRay.xrayBlocks) {
            it.asInt.blockById.takeIf { b -> b != Blocks.air }
        }
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun saveConfig() {
        file.writeJson(XRay.xrayBlocks.map { it.id }.sorted())
    }
}