package com.frontier.ui;

import com.badlogic.gdx.graphics.*;

/** Kleine pentekeningen, geen externe assets nodig. */
final class JournalSketch {
    static Texture create(String kind){
        Pixmap p=new Pixmap(180,120,Pixmap.Format.RGBA8888);p.setColor(Color.valueOf("e6d4aa"));p.fill();p.setColor(Color.valueOf("756446"));
        switch(kind){
            case "home" -> {p.drawRectangle(36,48,106,50);p.drawLine(25,48,86,18);p.drawLine(86,18,153,48);p.drawRectangle(80,66,22,32);p.drawRectangle(46,59,20,19);p.drawLine(21,102,158,102);p.drawLine(28,83,151,83);}
            case "well" -> {p.drawCircle(90,83,33);p.drawCircle(90,82,27);p.drawLine(53,82,53,28);p.drawLine(128,82,128,28);p.drawLine(44,28,137,28);p.drawLine(90,28,90,73);p.drawRectangle(78,71,24,20);}
            case "bridge" -> {for(int y=82;y<106;y+=7)p.drawLine(12,y,166,y-6);p.drawLine(18,45,162,45);p.drawLine(18,69,162,69);for(int x=18;x<=162;x+=18){p.drawLine(x,35,x,69);p.drawLine(x,55,x+9,69);}}
            case "letter" -> {p.drawRectangle(28,26,124,72);p.drawLine(28,26,90,65);p.drawLine(90,65,152,26);p.drawLine(28,98,69,66);p.drawLine(152,98,111,66);}
            case "map" -> {p.drawRectangle(27,15,126,91);for(int x=41;x<153;x+=28)p.drawLine(x,15,x-4,106);p.drawLine(43,87,75,65);p.drawLine(75,65,68,45);p.drawLine(68,45,126,30);p.drawCircle(127,30,8);p.drawLine(46,23,46,40);p.drawLine(40,29,46,23);p.drawLine(52,29,46,23);}
            case "cellar" -> {p.drawRectangle(31,20,118,84);p.drawRectangle(56,43,68,61);for(int y=54;y<105;y+=10)p.drawLine(56,y,124,y);p.drawLine(31,20,56,43);p.drawLine(149,20,124,43);}
            default -> {p.drawRectangle(44,26,28,52);p.drawRectangle(97,23,29,53);p.drawRectangle(35,78,48,18);p.drawRectangle(89,76,49,18);}
        }
        p.drawLine(14,111,166,111);Texture texture=new Texture(p);p.dispose();return texture;
    }
    static void install(com.badlogic.gdx.scenes.scene2d.ui.Skin skin){for(String kind:new String[]{"home","well","bridge","letter","map","cellar","boots"})skin.add("sketch-"+kind,create(kind));}
}
