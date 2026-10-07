package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.frontier.model.*;

/** Losse westernstatuskaarten met voortgang binnen het huidige niveau. */
final class ActionBar extends Table {
    private final Label player, money, level, experience, energy, time;
    private final ProgressBar xpBar, energyBar;
    ActionBar(Skin skin) {
        player = new Label("", skin, "accent"); money = new Label("", skin, "accent"); level = new Label("", skin, "accent");
        experience = new Label("", skin); energy = new Label("", skin); time = new Label("", skin, "muted");
        player.setEllipsis(true); player.setName("player-hud"); time.setName("time-hud"); time.setAlignment(Align.right);
        xpBar = new ProgressBar(0, 1, .001f, false, skin, "experience-horizontal"); xpBar.setName("xp-bar");
        energyBar = new ProgressBar(0, 100, 1, false, skin); energyBar.setName("energy-bar");
        defaults().padRight(8).height(86);
        Table identity = card(skin); identity.add(player).left().row(); identity.add(level).left().padTop(8);
        add(identity).width(150);
        Table wallet = card(skin); wallet.add(new Label("GELD", skin, "muted")).left().row(); wallet.add(money).left().padTop(8);
        add(wallet).width(115);
        add(meter(skin, "ERVARING", xpBar, experience)).width(258);
        add(meter(skin, "ENERGIE", energyBar, energy)).width(218);
        Table clock = card(skin); clock.add(time).expandX().right(); add(clock).expandX().fillX().padRight(0);
    }
    private static Table card(Skin skin) { Table t = new Table(); t.setBackground(skin.getDrawable("card")); t.pad(12); return t; }
    private static Table meter(Skin skin, String title, ProgressBar bar, Label text) {
        Table t = card(skin); t.add(new Label(title, skin, "muted")).left().padBottom(7).row();
        Stack stack = new Stack(); stack.add(bar); Table overlay = new Table(); overlay.add(text).center(); stack.add(overlay);
        t.add(stack).growX().height(25); return t;
    }
    void refresh(GameState state) {
        Player p = state.player(); player.setText(p.name()); level.setText("Niveau " + p.level()); money.setText("$ " + p.money());
        long lower = (long) p.level() * (p.level() - 1) * 25;
        long current = p.xp() - lower, required = p.nextLevelXp() - lower;
        xpBar.setValue(current / (float) required); experience.setText(current + " / " + required);
        energyBar.setValue(p.stamina()); energy.setText(p.stamina() + " / 100");
        time.setText(state.time().display() + "\n" + state.location().displayName());
    }
}
