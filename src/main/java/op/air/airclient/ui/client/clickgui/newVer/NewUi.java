/*
 * AirClient++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package op.air.airclient.ui.client.clickgui.newVer;

import op.air.airclient.features.module.Category;
import op.air.airclient.features.module.modules.client.NewGUI;
import op.air.airclient.ui.client.clickgui.newVer.element.CategoryElement;
import op.air.airclient.ui.client.clickgui.newVer.element.SearchElement;
import op.air.airclient.ui.font.Fonts;
import op.air.airclient.utils.render.ColorUtils;
import op.air.airclient.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NewUi extends GuiScreen {
    private static NewUi instance;
    public static NewUi getInstance() {
        return instance == null ? instance = new NewUi() : instance;
    }

    private static final ResourceLocation background = new ResourceLocation("airclient/background.png");

    public final List<CategoryElement> categoryElements = new ArrayList<>();

    private final SearchElement searchElement = new SearchElement();

    public int scrollAmt = 0;

    public NewUi() {
        for (Category c : Category.values()) {
            if (c.shouldShow()) {
                CategoryElement e = new CategoryElement(c);
                if (categoryElements.isEmpty()) e.setFocused(true);
                categoryElements.add(e);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final Color accentColor = NewGUI.INSTANCE.getAccentColor();

        final ScaledResolution sr = new ScaledResolution(mc);
        int scrollWheel = Mouse.getDWheel();

        if (scrollWheel != 0) {
            if (scrollWheel < 0) scrollAmt += 5;
            else scrollAmt -= 5;
            scrollAmt = Math.max(0, Math.min(scrollAmt, getScrollHeight(sr)));
        }

        final float panelWidth = sr.getScaledWidth() - 40;
        final float panelHeight = sr.getScaledHeight() - 40;
        final float panelX = 30;
        final float panelY = 30;

        int bgColor = ColorManager.INSTANCE.getBackground().getRGB();
        int bgAlpha = (bgColor >> 24) & 0xFF;
        int bgRed = (bgColor >> 16) & 0xFF;
        int bgGreen = (bgColor >> 8) & 0xFF;
        int bgBlue = bgColor & 0xFF;
        Color bgWithAlpha = new Color(bgRed, bgGreen, bgBlue, 200);

        RenderUtils.INSTANCE.originalRoundedRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 10, bgWithAlpha.getRGB());

        // left sidebar
        RenderUtils.INSTANCE.drawRoundedRect(panelX + 5, panelY + 5, panelX + 195, panelY + panelHeight - 5, ColorManager.INSTANCE.getDropDown().getRGB(), 5f, RenderUtils.RoundedCorners.ALL);
        float categoryY = panelY + 50 + scrollAmt;

        for (CategoryElement c : categoryElements) {
            c.drawLabel(mouseX, mouseY, panelX, categoryY, 200, 35);
            categoryY += 37;
        }

        // Search box
        float searchX = panelX + 205;
        float searchY = panelY + 10;
        float searchW = panelWidth - 210;
        float searchH = 30;

        boolean usingSearch = searchElement.drawBox(mouseX, mouseY, searchX, searchY, searchW, searchH, accentColor);

        if (usingSearch) {
            searchElement.drawPanel(mouseX, mouseY, panelX + 205, panelY + 45, panelWidth - 210, panelHeight - 50, scrollWheel, categoryElements, accentColor);
        } else {
            CategoryElement ce = categoryElements.stream().filter(CategoryElement::getFocused).findFirst().orElse(categoryElements.get(0));
            ce.drawPanel(mouseX, mouseY, panelX + 205, panelY + 45, panelWidth - 210, panelHeight - 50, scrollWheel, accentColor);
        }

        // title bar
        Fonts.INSTANCE.getFontSF35().drawString("AirClient", panelX + 20, panelY + 10, Color.WHITE.getRGB());

        // right sidebar top
        Fonts.INSTANCE.getFont35().drawString("ClickGUI", panelX + 210, panelY + 15, Color.WHITE.getRGB());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final float panelWidth = sr.getScaledWidth() - 40;
        final float panelHeight = sr.getScaledHeight() - 40;
        final float panelX = 30;
        final float panelY = 30;
        float categoryY = panelY + 50 + scrollAmt;

        for (CategoryElement c : categoryElements) {
            if (mouseX >= panelX && mouseX <= panelX + 200 && mouseY >= categoryY && mouseY <= categoryY + 35) {
                categoryElements.forEach(x -> x.setFocused(false));
                c.setFocused(true);
            }
            categoryY += 37;
        }

        float searchX = panelX + 205;
        float searchY = panelY + 10;
        float searchW = panelWidth - 210;
        float searchH = 30;
        
        if (mouseX >= searchX && mouseX <= searchX + searchW && mouseY >= searchY && mouseY <= searchY + searchH) {
            searchElement.handleMouseClick(mouseX, mouseY, mouseButton, searchX, searchY, searchW, searchH, categoryElements);
            return;
        }
        
        searchElement.clearFocus();

        boolean hasSearchContent = searchElement.hasSearchContent();
        if (hasSearchContent) {
            searchElement.handleMouseClick(mouseX, mouseY, mouseButton, searchX, panelY + 45, searchW, panelHeight - 50, categoryElements);
        } else {
            CategoryElement ce = categoryElements.stream().filter(CategoryElement::getFocused).findFirst().orElse(categoryElements.get(0));
            ce.handleMouseClick(mouseX, mouseY, mouseButton, searchX, panelY + 45, searchW, panelHeight - 50);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final float panelWidth = sr.getScaledWidth() - 40;
        final float panelHeight = sr.getScaledHeight() - 40;
        final float panelX = 30;
        final float panelY = 30;

        float searchX = panelX + 205;
        float searchY = panelY + 10;
        float searchW = panelWidth - 210;
        float searchH = 30;
        
        boolean hasSearchContent = searchElement.hasSearchContent();
        if (hasSearchContent) {
            searchElement.handleMouseRelease(mouseX, mouseY, state, searchX, panelY + 45, searchW, panelHeight - 50, categoryElements);
        } else {
            CategoryElement ce = categoryElements.stream().filter(CategoryElement::getFocused).findFirst().orElse(categoryElements.get(0));
            ce.handleMouseRelease(mouseX, mouseY, state, searchX, panelY + 45, searchW, panelHeight - 50);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final float panelWidth = sr.getScaledWidth() - 40;
        final float panelHeight = sr.getScaledHeight() - 40;
        final float panelX = 30;
        final float panelY = 30;

        float searchX = panelX + 205;
        float searchW = panelWidth - 210;
        
        boolean hasSearchContent = searchElement.hasSearchContent();
        if (hasSearchContent) {
            searchElement.handleMouseDrag(mouseX, mouseY, clickedMouseButton, searchX, panelY + 45, searchW, panelHeight - 50, categoryElements);
        } else {
            CategoryElement ce = categoryElements.stream().filter(CategoryElement::getFocused).findFirst().orElse(categoryElements.get(0));
            ce.handleMouseDrag(mouseX, mouseY, clickedMouseButton, searchX, panelY + 45, searchW, panelHeight - 50);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        CategoryElement ce = categoryElements.stream().filter(CategoryElement::getFocused).findFirst().orElse(categoryElements.get(0));
        
        boolean hasKeybindListening = ce.isAnyKeybindListening() || searchElement.isAnyKeybindListening(categoryElements);
        
        if (keyCode == 1 && !hasKeybindListening) {
            mc.displayGuiScreen(null);
            return;
        }
        
        final ScaledResolution sr = new ScaledResolution(mc);
        final float panelWidth = sr.getScaledWidth() - 40;
        final float panelHeight = sr.getScaledHeight() - 40;
        final float panelX = 30;
        final float panelY = 30;
        float searchX = panelX + 205;
        float searchW = panelWidth - 210;
        float searchH = panelHeight - 50;
        
        boolean hasSearchContent = searchElement.hasSearchContent();
        boolean isSearchTyping = searchElement.isTyping();
        boolean isSearchModuleTyping = searchElement.isAnyModuleTyping(categoryElements);
        boolean isSearchKeybindListening = searchElement.isAnyKeybindListening(categoryElements);
        boolean isCategoryKeybindListening = ce.isAnyKeybindListening();
        
        if (hasSearchContent || isSearchTyping || isSearchModuleTyping) {
            searchElement.handleTyping(typedChar, keyCode, searchX, panelY + 45, searchW, searchH, categoryElements);
        } else if (isCategoryKeybindListening) {
            ce.handleKeyTyped(typedChar, keyCode);
        } else if (isSearchKeybindListening) {
            searchElement.handleTyping(typedChar, keyCode, searchX, panelY + 45, searchW, searchH, categoryElements);
        } else {
            ce.handleKeyTyped(typedChar, keyCode);
        }
        
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (Exception ignored) {
        }
    }

    private int getScrollHeight(ScaledResolution sr) {
        int baseHeight = sr.getScaledHeight() - 40 - 30;
        int categoryHeight = 37 * categoryElements.size() + 50;
        return Math.max(0, categoryHeight - baseHeight);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}