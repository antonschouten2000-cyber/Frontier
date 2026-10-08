package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.model.*;
import com.frontier.logic.GameSession;
import java.util.function.Consumer;

final class SaloonDialog extends Dialog {
    private final GameSession game;
    private final Skin skin;
    private final Consumer<String> onAction;
    private final Table cards = new Table();
    private final Label status;
    SaloonDialog(Skin skin, GameSession game, Consumer<String> onAction) {
        super("Saloon - Opdrachtgevers",skin); this.skin=skin; this.game=game; this.onAction=onAction;
        setName("saloon-window"); getContentTable().pad(18);
        Label welcome=new Label("Aan de tafels van de Saloon zoeken inwoners hulp. Neem een opdracht aan en kom terug voor je beloning.",skin,"muted");
        welcome.setWrap(true); getContentTable().add(welcome).width(760).padBottom(12).row();
        getContentTable().add(Ui.button(skin,"Dorpsprojecten - Martha, Elias en Clara","village-projects",()->new VillageProjectsDialog(skin,game,onAction).show(getStage()))).width(760).height(44).padBottom(12).row();
        getContentTable().add(cards).width(760).row();
        status=new Label("",skin,"accent"); status.setWrap(true); getContentTable().add(status).width(760).height(60).padTop(10).row();
        button("Sluiten"); Ui.nameDialogButtons(this,"saloon-close"); refresh();
    }
    private void action(String message){status.setText(message); onAction.accept(message); refresh();}
    private void refresh(){
        cards.clearChildren();
        for(Mission mission:Mission.values()){
            MissionJournal.Entry entry=game.state().missions().entry(mission);
            Table card=new Table(skin); card.setBackground("card"); card.pad(12);
            Label text=new Label(mission.giver()+" - "+mission.title()+"\n"+mission.description()+"\nBeloning: $"+mission.money()+", "+mission.xp()+" ervaring en "+mission.rewardItem().displayName()
                +(entry==null ? "" : "\n"+(entry.claimed()?"Voltooid": "Voortgang: "+game.missions().progress(mission)+"/"+mission.target())),skin);
            text.setWrap(true); card.add(text).width(555).left();
            TextButton action=Ui.button(skin,entry==null?"Aannemen":entry.claimed()?"Voltooid":"Inleveren","mission-"+mission.name(),
                ()->action(entry==null?game.missions().accept(mission):game.missions().claim(mission)));
            action.setDisabled(entry!=null && (entry.claimed()||game.missions().progress(mission)<mission.target()) || !game.missions().blockReason().isEmpty());
            card.add(action).width(155).height(44).padLeft(12); cards.add(card).growX().padBottom(10).row();
        }
    }
}
