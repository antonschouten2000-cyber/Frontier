package com.frontier;

import com.badlogic.gdx.Game;
import com.frontier.logic.GameSession;
import com.frontier.save.SaveManager;
import com.frontier.ui.GameScreen;

public final class FrontierGame extends Game {
    @Override public void create() { setScreen(new GameScreen(new GameSession(), SaveManager.local())); }
    @Override public void dispose() { if (getScreen() != null) getScreen().dispose(); }
}
