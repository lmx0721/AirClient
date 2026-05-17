/*skid gold bounce
 *https://github.com/bzym2/GoldBounce/
 */
package op.air.airclient.features.module.modules.client

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.utils.client.chat
import op.air.airclient.utils.client.SysUtils
import op.air.airclient.utils.skid.fpsmaster.RawInputMod

object RawInput : Module("RawInput", Category.CLIENT) {
    override fun onEnable(){
        if(SysUtils().isAndroid()){
            chat("警告: RawInput模块在安卓上无法使用，可能会导致视角无法转动!")
            RawInputMod().stop()
            return
        } else if (SysUtils().isLinux()){
            chat("警告: RawInput模块在Linux上无法使用，可能会导致视角无法转�?")
            RawInputMod().stop()
            return
        }
        RawInputMod().start()
    }
    override fun onDisable(){
        RawInputMod().stop()
    }
}
