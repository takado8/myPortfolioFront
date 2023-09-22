package com.takado.myportfoliofront.config;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public final static float TRADES_GRID_HEIGHT_MINIMIZED = 30;
    public final static float MAIN_VIEW_GRID_MAX_HEIGHT = 100;
    public final static float TRADES_GRID_WIDTH_MAXIMIZED = 20;
    public final static float TRADES_GRID_MAX_WIDTH_MAXIMIZED = 75;
    public final static float NEW_ASSET_FORM_MAX_WIDTH = 42;
    public final static float NEW_ASSET_FORM_MIN_WIDTH = 41;
    public final static float NEW_ASSET_FORM_MIN_WIDTH_SHORT = 25;

    public final static String ADD_BUTTON_TEXT_SHORT = "Add";
    public final static String SUBTRACT_BUTTON_TEXT_SHORT = "Subtract";
    public final static String DELETE_BUTTON_TEXT_SHORT = "Delete";

    public final static String REGEX_VALIDATION_PATTERN = "(?!0\\d)[0-9]*(?<=\\d+)\\.?[0-9]*";

    public final static int DEFAULT_TRADE_POSITIONS_PER_PAGE = 9;
    public final static int DEFAULT_NB_OF_RECENT_TRADES_DISPLAYED = 3;
    private final static Map<Integer, Integer[]> SCREEN_SIZE_NB_OF_TRADES_DISPLAYED = new HashMap<>();
    public static Map<Integer, Integer[]> getScreenSizeNbOfTradesDisplayed() {
        if (SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.size() == 0) {
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(600, new Integer[]{7, 2});
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(720, new Integer[]{9, 3});
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(768, new Integer[]{10, 3});
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(800, new Integer[]{11, 3});
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(864, new Integer[]{13, 4});
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(900, new Integer[]{14, 4});
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(960, new Integer[]{16, 5});
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(1024, new Integer[]{17, 5});
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(1050, new Integer[]{18, 5});
            SCREEN_SIZE_NB_OF_TRADES_DISPLAYED.put(1080, new Integer[]{19, 6});
        }
        return SCREEN_SIZE_NB_OF_TRADES_DISPLAYED;
    }
}
