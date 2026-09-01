package com.onlytanner.industrialmetallurgy.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Minimal inline-markdown-to-Component translation for the guide book's hand-written prose --
// **bold**, *italic*, and `code`. Not a general markdown parser, just enough for GUIDE.md's own
// vocabulary (bolded machine/item names, occasional italic cross-references, backtick-quoted
// registry IDs).
final class MarkdownText {

    private static final Pattern TOKEN = Pattern.compile("\\*\\*(.+?)\\*\\*|\\*(.+?)\\*|`(.+?)`");

    private MarkdownText() {
    }

    static MutableComponent toComponent(String text) {
        MutableComponent result = Component.empty();
        Matcher matcher = TOKEN.matcher(text);
        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                result.append(Component.literal(text.substring(lastEnd, matcher.start())));
            }
            if (matcher.group(1) != null) {
                result.append(Component.literal(matcher.group(1)).withStyle(ChatFormatting.BOLD));
            } else if (matcher.group(2) != null) {
                result.append(Component.literal(matcher.group(2)).withStyle(ChatFormatting.ITALIC));
            } else {
                result.append(Component.literal(matcher.group(3)).withStyle(ChatFormatting.DARK_GREEN));
            }
            lastEnd = matcher.end();
        }
        if (lastEnd < text.length()) {
            result.append(Component.literal(text.substring(lastEnd)));
        }
        return result;
    }

}
