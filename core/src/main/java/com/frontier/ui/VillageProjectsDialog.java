package com.frontier.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;
import com.frontier.model.*;
import java.util.function.Consumer;

final class VillageProjectsDialog extends Dialog {
    private final Skin skin;private final GameSession game;private final Consumer<String> onAction;
    private final Table cards=new Table();private final Label status;
    VillageProjectsDialog(Skin skin,GameSession game,Consumer<String> onAction){
        super("Red Creek - Wat laten we achter?",skin);this.skin=skin;this.game=game;this.onAction=onAction;setName("projects-window");
        getContentTable().pad(18);
        Label intro=new Label("Hout en steen kunnen maar één keer worden gebruikt. Kies wie je eerst helpt. Daarna kun je ook het andere project doen; de eerste keuze blijft in je dagboek staan.",skin,"muted");intro.setWrap(true);
        getContentTable().add(intro).width(760).padBottom(12).row();
        ScrollPane scroll=new ScrollPane(cards,skin);scroll.setFadeScrollBars(false);scroll.setScrollingDisabled(true,false);getContentTable().add(scroll).width(790).height(390).row();
        status=new Label("",skin,"accent");status.setWrap(true);getContentTable().add(status).width(760).height(70).padTop(10).row();
        button("Terug naar de Saloon");Ui.nameDialogButtons(this,"projects-close");refresh();
    }
    private void action(String result){status.setText(result);onAction.accept(result);refresh();}
    private void refresh(){cards.clearChildren();for(VillageProject p:VillageProject.values()){
        Table card=new Table(skin);card.setBackground("card");card.pad(12);
        String sketch=switch(p){case VERANDA -> "home";case WELL -> "well";case BRIDGE -> "bridge";};
        card.add(new Image(skin.get("sketch-"+sketch,Texture.class))).width(100).height(70).top().padRight(12);
        boolean active=game.state().journal().active()==p,done=game.state().journal().done(p);
        String reason=active?game.story().finishReason():game.story().selectReason(p);
        Label text=new Label(p.giver()+" - "+p.title()+"\n"+p.story()+"\n"+p.benefit()+"\nKosten: $"+p.money()+", hout "+game.state().inventory().count(Item.WOOD)+"/"+p.wood()+", steen "+game.state().inventory().count(Item.STONE)+"/"+p.stone()+". Bouw: 2 speluren."
            +(done?"\nVoltooid":reason.isEmpty()?"": "\n"+reason),skin);text.setWrap(true);card.add(text).width(450).left();
        TextButton button=Ui.button(skin,done?"Voltooid":active?"Samen bouwen":"Eerst helpen","project-"+p.name(),()->action(active?game.story().finish():game.story().select(p)));
        button.setDisabled(done||!reason.isEmpty());card.add(button).width(160).height(44).padLeft(12);cards.add(card).width(750).padBottom(12).row();
    }}
}
