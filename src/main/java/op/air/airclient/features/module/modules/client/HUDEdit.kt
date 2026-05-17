package op.air.airclient.features.module.modules.client

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module
import op.air.airclient.ui.client.hud.designer.GuiHudDesigner

object HUDEdit : Module("HUDEdit", Category.CLIENT, canBeEnabled = false) {

    override fun onEnable() {
        super.onEnable()
        mc.displayGuiScreen(GuiHudDesigner())
    }
}
