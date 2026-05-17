package op.air.airclient.utils.inputfix.impl;

import op.air.airclient.utils.inputfix.IGuiScreen;
import op.air.airclient.utils.inputfix.IGuiScreenFix;
import org.lwjgl.input.Keyboard;

public class GuiScreenFixWindows implements IGuiScreenFix
{
    @Override
    public void handleKeyboardInput(IGuiScreen gui)
    {
        char c = Keyboard.getEventCharacter();
        int k = Keyboard.getEventKey();
        if (Keyboard.getEventKeyState() || (k == 0 && Character.isDefined(c)))
        {
            gui.keyTyped(c, k);
        }
    }
}
