package op.air.airclient.features.module.modules.`fun`

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module

object FullDisabler : Module("FullDisabler", Category.FUN) {
    override fun onEnable() {
        toggle()
        mc.shutdown()
    }
}
