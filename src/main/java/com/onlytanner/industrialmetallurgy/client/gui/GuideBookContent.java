package com.onlytanner.industrialmetallurgy.client.gui;

import com.onlytanner.industrialmetallurgy.items.guide.GuideBookData;
import com.onlytanner.industrialmetallurgy.items.guide.GuideCategory;
import com.onlytanner.industrialmetallurgy.items.guide.GuideEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Assembles GuideBookData's content into one flat BookViewScreen.BookAccess -- a single virtual
// book, reusing vanilla's own book texture, page-turn buttons, and click-to-jump page links
// (ClickEvent.ChangePage) entirely for free. This is the one piece of the guide book that isn't
// generated from GUIDE.md: the content is data, but the table-of-contents/page-numbering logic
// below is fixed regardless of what the content says, so it's hand-written once here.
public final class GuideBookContent {

    private static final int COVER_PAGE = 0;

    private GuideBookContent() {
    }

    public static void open() {
        Minecraft.getInstance().setScreenAndShow(new BookViewScreen(build()));
    }

    private static BookViewScreen.BookAccess build() {
        List<GuideCategory> categories = GuideBookData.CATEGORIES;

        // Pass 1: lay out every page's 0-based index before building any content, so links to
        // "later" pages can be written while assembling "earlier" ones.
        int[] categoryIndexPage = new int[categories.size()];
        int[][] entryStartPage = new int[categories.size()][];
        int cursor = COVER_PAGE + 1;
        for (int c = 0; c < categories.size(); c++) {
            List<GuideEntry> entries = categories.get(c).entries();
            categoryIndexPage[c] = cursor++;
            entryStartPage[c] = new int[entries.size()];
            for (int e = 0; e < entries.size(); e++) {
                entryStartPage[c][e] = cursor;
                cursor += entries.get(e).pages().size();
            }
        }

        List<Component> pages = new ArrayList<>(Collections.nCopies(cursor, Component.empty()));
        pages.set(COVER_PAGE, buildCoverPage(categories, categoryIndexPage));

        for (int c = 0; c < categories.size(); c++) {
            GuideCategory category = categories.get(c);
            List<GuideEntry> entries = category.entries();
            pages.set(categoryIndexPage[c], buildCategoryIndexPage(category, entryStartPage[c]));

            for (int e = 0; e < entries.size(); e++) {
                GuideEntry entry = entries.get(e);
                List<String> entryPages = entry.pages();
                for (int p = 0; p < entryPages.size(); p++) {
                    boolean isFirst = p == 0;
                    boolean isLast = p == entryPages.size() - 1;
                    pages.set(entryStartPage[c][e] + p, buildEntryPage(entry, entryPages.get(p), isFirst, isLast));
                }
            }
        }

        return new BookViewScreen.BookAccess(pages);
    }

    private static Component buildCoverPage(List<GuideCategory> categories, int[] categoryIndexPage) {
        MutableComponent page = title("The Metallurgist's Companion")
                .append(Component.literal("\n\nA field guide to Industrial Metallurgy.\n\n"));
        for (int c = 0; c < categories.size(); c++) {
            if (c > 0) {
                page.append(Component.literal("\n"));
            }
            page.append(pageLink(categories.get(c).title(), categoryIndexPage[c]));
        }
        return page;
    }

    private static Component buildCategoryIndexPage(GuideCategory category, int[] entryStartPage) {
        List<GuideEntry> entries = category.entries();
        MutableComponent page = title(category.title()).append(Component.literal("\n\n"));
        for (int e = 0; e < entries.size(); e++) {
            if (e > 0) {
                page.append(Component.literal("\n"));
            }
            page.append(pageLink(entries.get(e).title(), entryStartPage[e]));
        }
        return page.append(Component.literal("\n\n")).append(backToContentsLink());
    }

    private static Component buildEntryPage(GuideEntry entry, String pageText, boolean isFirst, boolean isLast) {
        MutableComponent page = Component.empty();
        if (isFirst) {
            page.append(title(entry.title())).append(Component.literal("\n\n"));
        }
        page.append(MarkdownText.toComponent(pageText));
        if (isLast) {
            page.append(Component.literal("\n\n")).append(backToContentsLink());
        }
        return page;
    }

    private static MutableComponent title(String text) {
        return Component.literal(text).withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_BLUE);
    }

    private static MutableComponent pageLink(String label, int pageIndex0Based) {
        return Component.literal("» " + label)
                .withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withUnderlined(true)
                        .withClickEvent(new ClickEvent.ChangePage(pageIndex0Based + 1)));
    }

    private static MutableComponent backToContentsLink() {
        return Component.literal("« Table of Contents")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withUnderlined(true)
                        .withClickEvent(new ClickEvent.ChangePage(COVER_PAGE + 1)));
    }

}
