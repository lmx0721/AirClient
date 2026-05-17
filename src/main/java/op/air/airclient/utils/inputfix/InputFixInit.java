package op.air.airclient.utils.inputfix;

import op.air.airclient.utils.inputfix.impl.GuiScreenFixOthers;
import op.air.airclient.utils.inputfix.impl.GuiScreenFixWindows;

public class InputFixInit
{
    public static IGuiScreenFix impl;

    public static void init()
    {
        OSDetector.OS OS = OSDetector.detectOS();
        switch (OS)
        {
            case Windows:
                impl = new GuiScreenFixWindows();
                break;
            case Linux:
            case Mac:
                try
                {
                    impl = new GuiScreenFixOthers();
                }
                catch (Throwable t)
                {
                    impl = new GuiScreenFixWindows();
                }
                break;
            case Unknown:
            default:
                break;
        }
        
        if (impl != null)
        {
            System.out.println("[InputFix] Initialized for " + OS);
        }
    }
}
