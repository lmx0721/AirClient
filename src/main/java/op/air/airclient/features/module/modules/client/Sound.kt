/*skid gold bounce
 * AirClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 * https://github.com/the-OmegaLabs/GoldBounce
 */
package op.air.airclient.features.module.modules.client

import op.air.airclient.event.EntityKilledEvent
import op.air.airclient.event.StartupEvent
import op.air.airclient.event.handler
import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.utils.getMP3S
import op.air.airclient.utils.getWAVS
import op.air.airclient.utils.playMP3
import op.air.airclient.utils.playWavSound

object Sound : Module("Sound", Category.CLIENT, canBeEnabled = false) {

    val enableSounds by choices(
        "Enable",
        getMP3S("assets/minecraft/airclient/sounds/Enable").toTypedArray().takeIf { it.isNotEmpty() } 
            ?: arrayOf("None"),
        getMP3S("assets/minecraft/airclient/sounds/Enable").firstOrNull() ?: "None"
    )

    val disableSounds by choices(
        "Disable",
        getMP3S("assets/minecraft/airclient/sounds/Disable").toTypedArray().takeIf { it.isNotEmpty() }
            ?: arrayOf("None"),
        getMP3S("assets/minecraft/airclient/sounds/Disable").firstOrNull() ?: "None"
    )

    val startupSounds by choices(
        "Startup",
        getMP3S("assets/minecraft/airclient/sounds/Startup").toTypedArray().takeIf { it.isNotEmpty() }
            ?: arrayOf("None"),
        getMP3S("assets/minecraft/airclient/sounds/Startup").firstOrNull() ?: "Air"
    )

    val killSoundEnabled by boolean("KillSound", true)
    
    val killCooldown by int("KillCooldown", 1, 0..60) { killSoundEnabled }
    
    private var lastKillSoundTime = 0L
    
    val killSounds by choices(
        "Kill",
        (getWAVS("assets/minecraft/airclient/sounds/Kill") + getMP3S("assets/minecraft/airclient/sounds/Kill")).toTypedArray().takeIf { it.isNotEmpty() }
            ?: arrayOf("None"),
        (getWAVS("assets/minecraft/airclient/sounds/Kill") + getMP3S("assets/minecraft/airclient/sounds/Kill")).firstOrNull() ?: "None"
    ) { killSoundEnabled }

    fun playEnableSound() {
        if (enableSounds == "None") return
        playMP3("airclient/sounds/Enable/${enableSounds}.mp3")
    }

    fun playDisableSound() {
        if (disableSounds == "None") return
        playMP3("airclient/sounds/Disable/${disableSounds}.mp3")
    }

    fun playToggleSound(enabled: Boolean) {
        if (enabled) {
            playEnableSound()
        } else {
            playDisableSound()
        }
    }

    fun playStartupSound() {
        if (startupSounds == "None") return
        playMP3("airclient/sounds/Startup/${startupSounds}.mp3")
    }

    fun playKillSound() {
        if (!killSoundEnabled) return
        if (killSounds == "None") return
        
        val currentTime = System.currentTimeMillis()
        if (killCooldown > 0 && currentTime - lastKillSoundTime < killCooldown * 1000L) {
            return
        }
        lastKillSoundTime = currentTime
        
        val wavPath = "airclient/sounds/Kill/${killSounds}.wav"
        val mp3Path = "airclient/sounds/Kill/${killSounds}.mp3"
        
        val wavExists = javaClass.getResourceAsStream("/assets/minecraft/$wavPath") != null
        val mp3Exists = javaClass.getResourceAsStream("/assets/minecraft/$mp3Path") != null
        
        when {
            wavExists -> playWavSound(wavPath)
            mp3Exists -> playMP3(mp3Path)
        }
    }

    val onStartup = handler<StartupEvent>(always = true) {
        playStartupSound()
    }

    val onKilled = handler<EntityKilledEvent>(always = true) {
        playKillSound()
    }
}
