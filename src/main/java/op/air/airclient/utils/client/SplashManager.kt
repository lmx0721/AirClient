/*
 * Air Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 */
package op.air.airclient.utils.client

import net.minecraft.util.ResourceLocation

object SplashManager : MinecraftInstance {
    
    var shouldShowSplash = true
    var splashStartTime = 0L
    var splashAlpha = 0f
    
    private const val FADE_IN_DURATION = 500L
    private const val FADE_OUT_DURATION = 500L
    private const val SHOW_DURATION = 2000L
    private const val TOTAL_DURATION = FADE_IN_DURATION + SHOW_DURATION + FADE_OUT_DURATION
    
    val splashResource = ResourceLocation("airclient/splash.png")
    
    private var hasStarted = false
    
    fun update(): Boolean {
        if (!shouldShowSplash) return false
        
        if (!hasStarted) {
            hasStarted = true
            splashStartTime = System.currentTimeMillis()
        }
        
        if (splashStartTime == 0L) {
            splashStartTime = System.currentTimeMillis()
        }
        
        val elapsed = System.currentTimeMillis() - splashStartTime
        
        when {
            elapsed < FADE_IN_DURATION -> {
                splashAlpha = elapsed.toFloat() / FADE_IN_DURATION
            }
            elapsed < FADE_IN_DURATION + SHOW_DURATION -> {
                splashAlpha = 1f
            }
            elapsed < TOTAL_DURATION -> {
                val fadeOutElapsed = elapsed - FADE_IN_DURATION - SHOW_DURATION
                splashAlpha = 1f - (fadeOutElapsed.toFloat() / FADE_OUT_DURATION)
            }
            else -> {
                shouldShowSplash = false
                splashAlpha = 0f
                return false
            }
        }
        
        return true
    }
    
    fun reset() {
        shouldShowSplash = true
        splashStartTime = 0L
        splashAlpha = 0f
        hasStarted = false
    }
}
