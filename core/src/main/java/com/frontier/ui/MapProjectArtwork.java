package com.frontier.ui;

import com.badlogic.gdx.graphics.*;

final class MapProjectArtwork {
    static Texture create(boolean bridge){
        Pixmap p=new Pixmap(240,140,Pixmap.Format.RGBA8888);
        p.setColor(Color.CLEAR);p.fill();
        if(bridge){
            p.setColor(Color.valueOf("94734d"));p.fillRectangle(12,53,216,33);
            p.setColor(Color.valueOf("e0c38d"));for(int x=16;x<225;x+=10)p.fillRectangle(x,56,6,26);
            p.setColor(Color.valueOf("664a32"));p.fillRectangle(10,48,220,5);p.fillRectangle(10,86,220,5);
            for(int x=12;x<230;x+=30){p.fillRectangle(x,39,4,16);p.fillRectangle(x,84,4,15);}
        }else{
            p.setColor(Color.valueOf("75985b"));p.fillRectangle(102,58,130,64);
            p.setColor(Color.valueOf("a0be72"));for(int x=108;x<230;x+=13)for(int y=65;y<120;y+=13)p.fillCircle(x,y,3);
            p.setColor(Color.valueOf("b7ac86"));p.fillCircle(56,93,25);p.setColor(Color.valueOf("56635e"));p.fillCircle(56,91,16);
            p.setColor(Color.valueOf("715337"));p.fillRectangle(25,42,5,51);p.fillRectangle(82,42,5,51);p.fillRectangle(22,40,67,7);
            p.setColor(Color.valueOf("c3a36e"));p.fillTriangle(15,39,55,17,96,39);p.setColor(Color.valueOf("d0c69c"));p.drawLine(55,42,55,87);
        }
        Texture t=new Texture(p);p.dispose();return t;
    }
}
