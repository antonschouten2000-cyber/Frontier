package com.frontier.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.*;
import com.frontier.FrontierGame;

public final class DesktopLauncher {
    private DesktopLauncher() {}
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Frontier");
        config.setWindowedMode(1200, 800);
        config.setResizable(true);
        config.setWindowSizeLimits(960, 640, -1, -1);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.disableAudio(true);
        new Lwjgl3Application(new FrontierGame(), config);
    }
}
