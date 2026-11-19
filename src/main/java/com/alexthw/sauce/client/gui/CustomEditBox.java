package com.alexthw.sauce.client.gui;


import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class CustomEditBox extends AbstractWidget implements Renderable {
    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/text_field"), ResourceLocation.withDefaultNamespace("widget/text_field_highlighted")
    );
    public static final int BACKWARDS = -1;
    public static final int FORWARDS = 1;
    private static final int CURSOR_INSERT_WIDTH = 1;
    private static final int CURSOR_INSERT_COLOR = -3092272;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    public static final int DEFAULT_TEXT_COLOR = 14737632;
    private static final int CURSOR_BLINK_INTERVAL_MS = 300;
    private final Font font;
    /**
     * Has the current text being edited on the textbox.
     */
    private String value = "";
    private int maxLength = 32;
    private boolean bordered = true;
    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    private boolean canLoseFocus = true;
    /**
     * If this value is true along with isFocused, keyTyped will process the keys.
     */
    private boolean isEditable = true;
    /**
     * The current character index that should be used as start of the rendered text.
     */
    private int displayPos;
    private int cursorPos;
    /**
     * other selection position, maybe the same as the cursor
     */
    private int highlightPos;
    private int textColor = 14737632;
    private int textColorUneditable = 7368816;
    @Nullable
    private String suggestion;
    @Nullable
    private Consumer<String> responder;
    /**
     * Called to check if the text is valid
     */
    private Predicate<String> filter = Objects::nonNull;
    private BiFunction<String, Integer, FormattedCharSequence> formatter = (p_94147_, p_94148_) -> FormattedCharSequence.forward(p_94147_, Style.EMPTY);
    @Nullable
    private Component hint;
    private long focusedTime = Util.getMillis();
    private boolean textShadow = false; // Neo: Allow modders to disable the default shadow for the text.

    public CustomEditBox(Font font, int width, int height, Component message) {
        this(font, 0, 0, width, height, message);
    }

    public CustomEditBox(Font font, int x, int y, int width, int height, Component message) {
        this(font, x, y, width, height, null, message);
    }

    public CustomEditBox(Font font, int x, int y, int width, int height, @Nullable net.minecraft.client.gui.components.EditBox editBox, Component message) {
        super(x, y, width, height, message);
        this.font = font;
        if (editBox != null) {
            this.setValue(editBox.getValue());
        }
    }

    public void setResponder(@org.jetbrains.annotations.Nullable Consumer<String> responder) {
        this.responder = responder;
    }

    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> textFormatter) {
        this.formatter = textFormatter;
    }

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        Component component = this.getMessage();
        return Component.translatable("gui.narrate.editBox", component, this.value);
    }

    /**
     * Sets the text of the textbox, and moves the cursor to the end.
     */
    public void setValue(String text) {
        if (this.filter.test(text)) {
            if (text.length() > this.maxLength) {
                this.value = text.substring(0, this.maxLength);
            } else {
                this.value = text;
            }

            this.moveCursorToEnd(false);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(text);
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(i, j);
    }

    public void setFilter(Predicate<String> validator) {
        this.filter = validator;
    }

    /**
     * Adds the given text after the cursor, or replaces the currently selected text if there is a selection.
     */
    public void insertText(String textToWrite) {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        int k = this.maxLength - this.value.length() - (i - j);
        if (k > 0) {
            String s = StringUtil.filterText(textToWrite);
            int l = s.length();
            if (k < l) {
                if (Character.isHighSurrogate(s.charAt(k - 1))) {
                    k--;
                }

                s = s.substring(0, k);
                l = k;
            }

            String s1 = new StringBuilder(this.value).replace(i, j, s).toString();
            if (this.filter.test(s1)) {
                this.value = s1;
                this.setCursorPosition(i + l);
                this.setHighlightPos(this.cursorPos);
                this.onValueChange(this.value);
            }
        }
    }

    private void onValueChange(String newText) {
        if (this.responder != null) {
            this.responder.accept(newText);
        }
    }

    private void deleteText(int count) {
        if (Screen.hasControlDown()) {
            this.deleteWords(count);
        } else {
            this.deleteChars(count);
        }
    }

    /**
     * Deletes the given number of words from the current cursor's position, unless there is currently a selection, in which case the selection is deleted instead.
     */
    public void deleteWords(int num) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteCharsToPos(this.getWordPosition(num));
            }
        }
    }

    /**
     * Deletes the given number of characters from the current cursor's position, unless there is currently a selection, in which case the selection is deleted instead.
     */
    public void deleteChars(int num) {
        this.deleteCharsToPos(this.getCursorPos(num));
    }

    public void deleteCharsToPos(int num) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int i = Math.min(num, this.cursorPos);
                int j = Math.max(num, this.cursorPos);
                if (i != j) {
                    String s = new StringBuilder(this.value).delete(i, j).toString();
                    if (this.filter.test(s)) {
                        this.value = s;
                        this.moveCursorTo(i, false);
                    }
                }
            }
        }
    }

    /**
     * Gets the starting index of the word at the specified number of words away from the cursor position.
     */
    public int getWordPosition(int numWords) {
        return this.getWordPosition(numWords, this.getCursorPosition());
    }

    /**
     * Gets the starting index of the word at a distance of the specified number of words away from the given position.
     */
    private int getWordPosition(int numWords, int pos) {
        return this.getWordPosition(numWords, pos, true);
    }

    /**
     * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
     */
    private int getWordPosition(int numWords, int pos, boolean skipConsecutiveSpaces) {
        int i = pos;
        boolean flag = numWords < 0;
        int j = Math.abs(numWords);

        for (int k = 0; k < j; k++) {
            if (!flag) {
                int l = this.value.length();
                i = this.value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while (skipConsecutiveSpaces && i < l && this.value.charAt(i) == ' ') {
                        i++;
                    }
                }
            } else {
                while (skipConsecutiveSpaces && i > 0 && this.value.charAt(i - 1) == ' ') {
                    i--;
                }

                while (i > 0 && this.value.charAt(i - 1) != ' ') {
                    i--;
                }
            }
        }

        return i;
    }

    public void moveCursor(int delta, boolean select) {
        this.moveCursorTo(this.getCursorPos(delta), select);
    }

    private int getCursorPos(int delta) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, delta);
    }

    public void moveCursorTo(int delta, boolean select) {
        this.setCursorPosition(delta);
        if (!select) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    public void setCursorPosition(int pos) {
        this.cursorPos = Mth.clamp(pos, 0, this.value.length());
        this.scrollTo(this.cursorPos);
    }

    public void moveCursorToStart(boolean select) {
        this.moveCursorTo(0, select);
    }

    public void moveCursorToEnd(boolean select) {
        this.moveCursorTo(this.value.length(), select);
    }

    /**
     * Called when a keyboard key is pressed within the GUI element.
     * <p>
     *
     * @param keyCode   the key code of the pressed key.
     * @param scanCode  the scan code of the pressed key.
     * @param modifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isActive() && this.isFocused()) {
            switch (keyCode) {
                case 259:
                    if (this.isEditable) {
                        this.deleteText(-1);
                    }

                    return true;
                case 260:
                case 264:
                case 265:
                case 266:
                case 267:
                default:
                    if (Screen.isSelectAll(keyCode)) {
                        this.moveCursorToEnd(false);
                        this.setHighlightPos(0);
                        return true;
                    } else if (Screen.isCopy(keyCode)) {
                        Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                        return true;
                    } else if (Screen.isPaste(keyCode)) {
                        if (this.isEditable()) {
                            this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                        }

                        return true;
                    } else {
                        if (Screen.isCut(keyCode)) {
                            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                            if (this.isEditable()) {
                                this.insertText("");
                            }

                            return true;
                        }

                        return false;
                    }
                case 261:
                    if (this.isEditable) {
                        this.deleteText(1);
                    }

                    return true;
                case 262:
                    if (Screen.hasControlDown()) {
                        this.moveCursorTo(this.getWordPosition(1), Screen.hasShiftDown());
                    } else {
                        this.moveCursor(1, Screen.hasShiftDown());
                    }

                    return true;
                case 263:
                    if (Screen.hasControlDown()) {
                        this.moveCursorTo(this.getWordPosition(-1), Screen.hasShiftDown());
                    } else {
                        this.moveCursor(-1, Screen.hasShiftDown());
                    }

                    return true;
                case 268:
                    this.moveCursorToStart(Screen.hasShiftDown());
                    return true;
                case 269:
                    this.moveCursorToEnd(Screen.hasShiftDown());
                    return true;
            }
        } else {
            return false;
        }
    }

    public boolean canConsumeInput() {
        return this.isActive() && this.isFocused() && this.isEditable();
    }

    /**
     * Called when a character is typed within the GUI element.
     * <p>
     *
     * @param codePoint the code point of the typed character.
     * @param modifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (StringUtil.isAllowedChatCharacter(codePoint)) {
            if (this.isEditable) {
                this.insertText(Character.toString(codePoint));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int i = Mth.floor(mouseX) - this.getX();
        if (this.bordered) {
            i -= 4;
        }

        String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos, Screen.hasShiftDown());
    }

    @Override
    public void playDownSound(@NotNull SoundManager handler) {
    }

    @Override
    public void renderWidget(
            @NotNull GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        setTextColor(0xFF927F68);

        if (!isVisible()) {
            return;
        }

        // Custom background
        graphics.fill(
                getX(),
                getY(),
                getX() + getWidth(),
                getY() + getHeight(),
                0xFFE5D4B5
        );

        // Custom border
        graphics.renderOutline(
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                isFocused() ? 0xFF80613D : 0xFFB49A75
        );

        // Vanilla text rendering
        int textColor = isEditable() ? this.textColor : this.textColorUneditable;

        int cursorOffset = this.cursorPos - this.displayPos;

        String visibleText = this.font.plainSubstrByWidth(
                this.value.substring(this.displayPos),
                getInnerWidth()
        );

        boolean cursorVisible = cursorOffset >= 0 && cursorOffset <= visibleText.length();

        boolean drawCursor = isFocused()
                && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L
                && cursorVisible;

        int textX = getX() + 4;
        int textY = getY() + (this.height - 8) / 2;

        int textEndX = textX;

        int highlightOffset = Mth.clamp(
                this.highlightPos - this.displayPos,
                0,
                visibleText.length()
        );

        if (!visibleText.isEmpty()) {
            String beforeCursor = cursorVisible
                    ? visibleText.substring(0, cursorOffset)
                    : visibleText;

            textEndX = graphics.drawString(
                    this.font,
                    this.formatter.apply(beforeCursor, this.displayPos),
                    textX,
                    textY,
                    textColor,
                    this.textShadow
            );
        }

        boolean cursorAtEnd = this.cursorPos < this.value.length()
                || this.value.length() >= this.getMaxLength();

        int cursorX = textEndX;

        if (!cursorVisible) {
            cursorX = cursorOffset > 0 ? textX + this.width : textX;
        } else if (cursorAtEnd) {
            cursorX = textEndX - 1;
            textEndX--;
        }

        if (!visibleText.isEmpty() && cursorVisible && cursorOffset < visibleText.length()) {
            graphics.drawString(
                    this.font,
                    this.formatter.apply(
                            visibleText.substring(cursorOffset),
                            this.cursorPos
                    ),
                    textEndX,
                    textY,
                    textColor,
                    this.textShadow
            );
        }

        if (this.hint != null && visibleText.isEmpty() && !isFocused()) {
            graphics.drawString(
                    this.font,
                    this.hint,
                    textX,
                    textY,
                    0xFF927F68,
                    this.textShadow
            );
        }

        if (!cursorAtEnd && this.suggestion != null) {
            graphics.drawString(
                    this.font,
                    this.suggestion,
                    cursorX - 1,
                    textY,
                    -8355712,
                    this.textShadow
            );
        }

        if (drawCursor) {
            if (cursorAtEnd) {
                graphics.fill(
                        RenderType.guiOverlay(),
                        cursorX,
                        textY - 1,
                        cursorX + 1,
                        textY + 1 + 9,
                        -3092272
                );
            } else {
                graphics.drawString(
                        this.font,
                        "_",
                        cursorX,
                        textY,
                        textColor,
                        this.textShadow
                );
            }
        }

        if (highlightOffset != cursorOffset) {
            int highlightX = textX + this.font.width(
                    visibleText.substring(0, highlightOffset)
            );

            this.renderHighlight(
                    graphics,
                    cursorX,
                    textY - 1,
                    highlightX - 1,
                    textY + 1 + 9
            );
        }
    }

    private void renderHighlight(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY) {
        if (minX < maxX) {
            int i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {
            int j = minY;
            minY = maxY;
            maxY = j;
        }

        if (maxX > this.getX() + this.width) {
            maxX = this.getX() + this.width;
        }

        if (minX > this.getX() + this.width) {
            minX = this.getX() + this.width;
        }

        guiGraphics.fill(RenderType.guiTextHighlight(), minX, minY, maxX, maxY, -16776961);
    }

    /**
     * Sets the maximum length for the text in this text box. If the current text is longer than this length, the current text will be trimmed.
     */
    public void setMaxLength(int length) {
        this.maxLength = length;
        if (this.value.length() > length) {
            this.value = this.value.substring(0, length);
            this.onValueChange(this.value);
        }
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    public boolean isBordered() {
        return this.bordered;
    }

    /**
     * Sets whether the background and outline of this text box should be drawn.
     */
    public void setBordered(boolean enableBackgroundDrawing) {
        this.bordered = enableBackgroundDrawing;
    }

    /**
     * Sets the color to use when drawing this text box's text. A different color is used if this text box is disabled.
     */
    public void setTextColor(int color) {
        this.textColor = color;
    }

    /**
     * Sets the color to use for text in this text box when this text box is disabled.
     */
    public void setTextColorUneditable(int color) {
        this.textColorUneditable = color;
    }

    /**
     * Sets the focus state of the GUI element.
     *
     * @param focused {@code true} to apply focus, {@code false} to remove focus
     */
    @Override
    public void setFocused(boolean focused) {
        if (this.canLoseFocus || focused) {
            super.setFocused(focused);
            if (focused) {
                this.focusedTime = Util.getMillis();
            }
        }
    }

    private boolean isEditable() {
        return this.isEditable;
    }

    /**
     * Sets whether this text box is enabled. Disabled text boxes cannot be typed in.
     */
    public void setEditable(boolean enabled) {
        this.isEditable = enabled;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    /**
     * Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the selection). If the anchor is set beyond the bounds of the current text, it will be put back inside.
     */
    public void setHighlightPos(int position) {
        this.highlightPos = Mth.clamp(position, 0, this.value.length());
        this.scrollTo(this.highlightPos);
    }

    private void scrollTo(int position) {
        if (this.font != null) {
            this.displayPos = Math.min(this.displayPos, this.value.length());
            int i = this.getInnerWidth();
            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), i);
            int j = s.length() + this.displayPos;
            if (position == this.displayPos) {
                this.displayPos = this.displayPos - this.font.plainSubstrByWidth(this.value, i, true).length();
            }

            if (position > j) {
                this.displayPos += position - j;
            } else if (position <= this.displayPos) {
                this.displayPos = this.displayPos - (this.displayPos - position);
            }

            this.displayPos = Mth.clamp(this.displayPos, 0, this.value.length());
        }
    }

    /**
     * Sets whether this text box loses focus when something other than it is clicked.
     */
    public void setCanLoseFocus(boolean canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
    }

    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets whether this textbox is visible.
     */
    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

    public void setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion;
    }

    public int getScreenX(int charNum) {
        return charNum > this.value.length() ? this.getX() : this.getX() + this.font.width(this.value.substring(0, charNum));
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }

    public void setHint(@org.jetbrains.annotations.Nullable Component hint) {
        this.hint = hint;
    }

    public void setTextShadow(boolean textShadow) {
        this.textShadow = textShadow;
    }

    public boolean getTextShadow() {
        return this.textShadow;
    }
}
