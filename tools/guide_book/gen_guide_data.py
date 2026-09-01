#!/usr/bin/env python3
"""Regenerates GuideBookData.java from GUIDE.md.

GUIDE.md is the single source of truth for the in-game guide book's content. Run this after
editing GUIDE.md to regenerate the Java data it compiles into:

    python3 tools/guide_book/gen_guide_data.py

Format: `##` headings are categories, `###` headings are entries within the current category,
and `<!-- page -->` on its own line is a manual in-book page break. Everything else is treated
as page body text (blank lines are just paragraph breaks within a page, not page breaks).

This script only emits the raw category/entry/page *data* (GuideBookData.java). The page-layout
logic (table of contents, page-link wiring, per-page character budget) lives in
GuideBookContent.java and is hand-written there, not generated -- it doesn't change when the
content does.
"""
import re
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
GUIDE_MD = REPO_ROOT / "GUIDE.md"
OUT_JAVA = (
    REPO_ROOT
    / "src/main/java/com/onlytanner/industrialmetallurgy/items/guide/GuideBookData.java"
)

# Stay comfortably under the vanilla book UI's real ~256-character-per-page cap (14 lines) --
# GuideBookContent.java also adds a title and/or a "back to contents" link on top of this.
SAFE_PAGE_CHARS = 260


def parse(markdown_lines):
    categories = []
    current_category = None
    current_entry = None
    current_page_lines = []
    started = False

    def flush_page():
        nonlocal current_page_lines
        if current_entry is not None:
            text = " ".join(l.strip() for l in current_page_lines if l.strip())
            text = re.sub(r"\s+", " ", text).strip()
            if text:
                current_entry["pages"].append(text)
        current_page_lines = []

    for raw in markdown_lines:
        line = raw.rstrip("\n")
        stripped = line.strip()

        if stripped.startswith("## "):
            flush_page()
            started = True
            current_category = {"title": stripped[3:].strip(), "entries": []}
            categories.append(current_category)
            current_entry = None
            continue

        if stripped.startswith("### "):
            flush_page()
            current_entry = {"title": stripped[4:].strip(), "pages": []}
            current_category["entries"].append(current_entry)
            continue

        if stripped == "<!-- page -->":
            flush_page()
            continue

        if stripped == "---" or not started:
            continue

        current_page_lines.append(line)

    flush_page()
    return categories


def java_string_literal(text):
    escaped = (
        text.replace("\\", "\\\\")
        .replace('"', '\\"')
    )
    return f'"{escaped}"'


def emit_java(categories):
    lines = []
    lines.append("package com.onlytanner.industrialmetallurgy.items.guide;")
    lines.append("")
    lines.append("import java.util.List;")
    lines.append("")
    lines.append("// Generated from GUIDE.md by tools/guide_book/gen_guide_data.py -- edit GUIDE.md and")
    lines.append("// re-run that script rather than hand-editing this file; changes made here directly")
    lines.append("// will be overwritten the next time it runs.")
    lines.append("public final class GuideBookData {")
    lines.append("")
    lines.append("    public static final List<GuideCategory> CATEGORIES = List.of(")

    category_blocks = []
    for cat in categories:
        entry_blocks = []
        for entry in cat["entries"]:
            pages = ",\n".join(f"                    {java_string_literal(p)}" for p in entry["pages"])
            entry_blocks.append(
                f'            new GuideEntry({java_string_literal(entry["title"])}, List.of(\n{pages}\n            ))'
            )
        entries_joined = ",\n".join(entry_blocks)
        category_blocks.append(
            f'        new GuideCategory({java_string_literal(cat["title"])}, List.of(\n{entries_joined}\n        ))'
        )

    lines.append(",\n".join(category_blocks))
    lines.append("    );")
    lines.append("")
    lines.append("    private GuideBookData() {}")
    lines.append("")
    lines.append("}")
    lines.append("")
    return "\n".join(lines)


def main():
    markdown_lines = GUIDE_MD.read_text(encoding="utf-8").splitlines(keepends=True)
    categories = parse(markdown_lines)

    total_pages = 0
    problems = []
    for cat in categories:
        for entry in cat["entries"]:
            for i, page in enumerate(entry["pages"]):
                total_pages += 1
                if len(page) > SAFE_PAGE_CHARS:
                    problems.append(
                        f'{cat["title"]} / {entry["title"]} page {i + 1}: {len(page)} chars'
                    )

    print(
        f"Parsed {len(categories)} categories, "
        f"{sum(len(c['entries']) for c in categories)} entries, {total_pages} entry pages."
    )
    if problems:
        print(f"ERROR: {len(problems)} page(s) exceed {SAFE_PAGE_CHARS} characters:", file=sys.stderr)
        for p in problems:
            print(f"  {p}", file=sys.stderr)
        sys.exit(1)

    OUT_JAVA.parent.mkdir(parents=True, exist_ok=True)
    OUT_JAVA.write_text(emit_java(categories), encoding="utf-8")
    print(f"Wrote {OUT_JAVA.relative_to(REPO_ROOT)}")


if __name__ == "__main__":
    main()
