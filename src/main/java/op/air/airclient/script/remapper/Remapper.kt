/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.script.remapper

import kotlinx.coroutines.runBlocking
import op.air.airclient.AirClient.CLIENT_CLOUD
import op.air.airclient.file.FileManager.dir
import op.air.airclient.utils.client.ClientUtils.LOGGER
import op.air.airclient.utils.io.Downloader
import op.air.airclient.utils.io.isEmpty
import op.air.airclient.utils.io.sha256
import java.io.File

/**
 * A srg remapper
 *
 * @author CCBlueX
 */
object Remapper {

    private const val SRG_NAME = "stable_22"
    private val srgFile = File(dir, "mcp-$SRG_NAME.srg")

    var mappingsLoaded = false
        private set

    private val fields = hashMapOf<String, HashMap<String, String>>()
    private val methods = hashMapOf<String, HashMap<String, String>>()

    /**
     * Load srg
     */
    fun loadSrg() {
        if (mappingsLoaded) return

        synchronized(this) {
            if (mappingsLoaded) return

            mappingsLoaded = false

            // Download sha256 file
            val sha256File = File(dir, "mcp-$SRG_NAME.srg.sha256")
            if (!sha256File.exists() || !sha256File.isFile || sha256File.isEmpty) {
                sha256File.createNewFile()

                Downloader.downloadWholeFile("$CLIENT_CLOUD/srgs/mcp-$SRG_NAME.srg.sha256", sha256File)
                LOGGER.info("[Remapper] Downloaded $SRG_NAME sha256.")
            }

            // Check if srg file is already downloaded
            if (!srgFile.exists() || !hashMatches(srgFile, sha256File)) {
                // Download srg file
                srgFile.createNewFile()

                runBlocking {
                    Downloader.download("$CLIENT_CLOUD/srgs/mcp-$SRG_NAME.srg", srgFile)
                }
                LOGGER.info("[Remapper] Downloaded $SRG_NAME.")
            }

            // Load srg
            parseSrg()

            mappingsLoaded = true

            LOGGER.info("[Remapper] Successfully loaded SRG mappings.")
        }
    }

    private fun hashMatches(srgFile: File, sha256File: File): Boolean {
        if (!sha256File.exists()) {
            LOGGER.warn("[Remapper] No sha256 file found.")
            return false
        }

        // Generate SHA-256 hash of file content
        val hash = srgFile.sha256()

        // sha265sum mcp-stable_22.srg
        // -> a8486671a5e85153773eaac313f8babd1913b41524b45e92d42e6cf019e658eb  mcp-stable_22.srg
        val sha256 = sha256File.readText().substringBefore(' ')

        return sha256 == hash
    }

    private fun parseSrg() {
        srgFile.forEachLine {
            val args = it.split(' ')

            when {
                it.startsWith("FD:") -> {
                    val name = args[1]
                    val srg = args[2]

                    val className = name.substringBeforeLast('/').replace('/', '.')
                    val fieldName = name.substringAfterLast('/')
                    val fieldSrg = srg.substringAfterLast('/')

                    fields.getOrPut(className, ::HashMap)[fieldSrg] = fieldName
                }

                it.startsWith("MD:") -> {
                    val name = args[1]
                    val desc = args[2]
                    val srg = args[3]

                    val className = name.substringBeforeLast('/').replace('/', '.')
                    val methodName = name.substringAfterLast('/')
                    val methodSrg = srg.substringAfterLast('/')

                    methods.getOrPut(className, ::HashMap)[methodSrg + desc] = methodName
                }
            }
        }
    }

    /**
     * Remap field
     */
    fun remapField(clazz : Class<*>, name : String) =
        fields[clazz.name]?.get(name) ?: name

    /**
     * Remap method
     */
    fun remapMethod(clazz : Class<*>, name : String, desc : String) =
        methods[clazz.name]?.get(name + desc) ?: name
}
