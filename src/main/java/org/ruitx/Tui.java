package org.ruitx;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;

public final class Tui {

    private static final String ALIVE_STRING = "•";
    private static final Terminal.Color ALIVE_COLOR = Terminal.Color.GREEN;
    private static final String DEAD_STRING = " ";

    private static int width;
    private static int height;
    private static Screen screen;
    private static ScreenWriter screenWriter;


    private Tui() {
    }

    public static void init(int width, int height) {
        screen = TerminalFacade.createScreen();

        Tui.width = width;
        Tui.height = height;
        screen.getTerminal().getTerminalSize().setColumns(width);
        screen.getTerminal().getTerminalSize().setRows(height);

        screenWriter = new ScreenWriter(screen);
        screen.setCursorPosition(null);
        screen.startScreen();

        screen.refresh();
    }

    public static void close() {
        screen.stopScreen();
    }

    static void drawGrid(Conways.GameGrid gameGrid) {
        for (int y = 0; y < gameGrid.lengthY(); y++) {
            for (int x = 0; x < gameGrid.lengthX(); x++) {
                if (gameGrid.checkCoordinates(y, x)) {
                    screen.putString(x, y, ALIVE_STRING, ALIVE_COLOR, null);
                } else {
                    screen.putString(x, y, DEAD_STRING, null, null);
                }
            }
        }
        screen.refresh();
    }
}
