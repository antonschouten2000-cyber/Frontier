package com.frontier.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.model.*;

final class TravelJournalDialog extends Dialog {
    TravelJournalDialog(Skin skin,TravelJournal journal){
        super("Reisdagboek - Mijn spoor in Red Creek",skin);setName("journal-window");getContentTable().pad(18);
        Label heading=new Label("Geen grootse held. Wel iemand die bleef.",skin,"accent");getContentTable().add(heading).left().padBottom(12).row();
        Table pages=new Table();
        var entries=journal.pages();
        for(int i=entries.size()-1;i>=0;i--){TravelJournal.Page page=entries.get(i);
            Table sheet=new Table(skin);sheet.setBackground("card");sheet.pad(14);
            sheet.add(new Image(skin.get("sketch-"+page.sketch(),Texture.class))).width(144).height(96).top().padRight(14);
            Label text=new Label(new GameTime(page.date()).display()+"\n"+page.title()+"\n\n"+page.text(),skin);text.setWrap(true);
            sheet.add(text).width(480).left();pages.add(sheet).width(670).padBottom(14).row();
        }
        ScrollPane scroll=new ScrollPane(pages,skin);scroll.setFadeScrollBars(false);scroll.setScrollingDisabled(true,false);
        getContentTable().add(scroll).width(700).height(470).row();button("Sluiten");Ui.nameDialogButtons(this,"journal-close");getButtonTable().pad(12);
    }
}
