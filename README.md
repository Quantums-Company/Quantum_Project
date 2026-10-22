# UI polish patch (on top of part 2)

    cd <project root>
    unzip -o /path/to/LogiRoute-ui-polish.zip
    bash scripts/3-ui-polish.sh
    ./gradlew test detekt

What changed
- The "2K" glitch: the spinner no longer uses the "erase line" escape code (IDE consoles print it as text).
- "number" -> "index" in every prompt, menu hint and table header (Index).
- Every table is coloured (dim frame, bold cyan header, cyan ids, yellow index, coloured priority/zone)
  and long lists show the first 10 rows, then "... and 81 more".
- The shortest-route and classification screens now use the same shared tables.
- ConsoleTerminal() no longer takes a style parameter (it was only used for the old erase code).
