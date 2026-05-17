package op.air.airclient.file.configs.models

import op.air.airclient.AirClient
import op.air.airclient.config.Configurable
import op.air.airclient.utils.client.MinecraftInstance
import op.air.airclient.utils.render.IconUtils
import org.lwjgl.opengl.Display

object ClientConfiguration : Configurable("ClientConfiguration"), MinecraftInstance {
    var clientTitle by boolean("ClientTitle", true)
    var customBackground by boolean("CustomBackground", false)
    var particles by boolean("Particles", false)
    var mainMenuStyle by text("MainMenuStyle", "Default")
    var defaultMenuBackgroundIndex by int("DefaultMenuBackgroundIndex", 6, 0..11)
    var customMenuBackgroundIndex by int("CustomMenuBackgroundIndex", 6, 0..11)
    var stylisedAlts by boolean("StylisedAlts", true)
    var unformattedAlts by boolean("CleanAlts", true)
    var altsLength by int("AltsLength", 16, 4..20)
    var altsPrefix by text("AltsPrefix", "")
    var overrideLanguage by text("OverrideLanguage","")

    fun updateClientWindow() {
        if (clientTitle) {
            // Set AirClient title
            Display.setTitle(AirClient.clientTitle)
            // Update favicon
            IconUtils.favicon?.let { icons ->
                if (icons.all { it != null }) {
                    Display.setIcon(icons)
                }
            }
        } else {
            // Set original title
            Display.setTitle("Minecraft 1.8.9")
            // Update favicon
            mc.setWindowIcon()
        }
    }

}