package com.frontier.ui;

import com.badlogic.gdx.graphics.*;
import java.util.Random;

/** Papiervezels en leerstiksels; hout blijft in de straatillustratie. */
final class FieldTexture {
    static Texture create(int width,int height,String base,String border,boolean leather){
        Pixmap p=new Pixmap(width,height,Pixmap.Format.RGBA8888);Color c=Color.valueOf(base);Random random=new Random(1880);
        for(int y=0;y<height;y++)for(int x=0;x<width;x++){
            float noise=(random.nextFloat()-.5f)*(leather?.05f:.035f);
            float edge=Math.min(1f,Math.min(Math.min(x,width-1-x),Math.min(y,height-1-y))/12f);
            float shade=noise-(1-edge)*.055f;
            p.setColor(Math.max(0,c.r+shade),Math.max(0,c.g+shade),Math.max(0,c.b+shade),1);p.drawPixel(x,y);
        }
        if(border!=null){p.setColor(Color.valueOf(border));p.drawRectangle(2,2,width-4,height-4);
            if(leather)for(int x=8;x<width-8;x+=8){p.drawLine(x,6,x+3,6);p.drawLine(x,height-7,x+3,height-7);}
        }
        Texture t=new Texture(p);p.dispose();return t;
    }
}
