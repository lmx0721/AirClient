package op.air.airclient.features.module.modules.music

import op.air.airclient.features.module.Category
import op.air.airclient.features.module.Module

object NextTrack : Module("下一首", Category.MUSIC, canBeEnabled = false) {

    override fun onEnable() {
        super.onEnable()
        MusicPlayer.playNext()
    }
}
