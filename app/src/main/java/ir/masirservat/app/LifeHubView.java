package ir.masirservat.app;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class LifeHubView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private GameState s;

    public LifeHubView(Context c){ super(c); }

    public void setState(GameState state){ s=state; invalidate(); }

    private void rect(Canvas c,float l,float t,float r,float b,int color){
        p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRect(l,t,r,b,p);
    }
    private void rr(Canvas c,float l,float t,float r,float b,float rad,int color){
        p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRoundRect(l,t,r,b,rad,rad,p);
    }
    private void circle(Canvas c,float x,float y,float r,int color){
        p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawCircle(x,y,r,p);
    }
    private void line(Canvas c,float x1,float y1,float x2,float y2,float sw,int color){
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(sw);p.setStrokeCap(Paint.Cap.ROUND);p.setColor(color);c.drawLine(x1,y1,x2,y2,p);p.setStyle(Paint.Style.FILL);
    }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float w=getWidth(),h=getHeight();
        if(s==null)return;

        // room background
        int wall=s.roomLevel>=3?Color.rgb(224,235,239):Color.rgb(238,234,226);
        c.drawColor(wall);
        rect(c,0,h*.72f,w,h,Color.rgb(205,190,170));

        // city window
        rr(c,w*.06f,h*.07f,w*.39f,h*.46f,w*.02f,Color.rgb(246,249,251));
        rect(c,w*.08f,h*.09f,w*.37f,h*.44f,Color.rgb(157,203,228));
        for(int i=0;i<5;i++){
            float x=w*(.09f+i*.055f);
            float bh=h*(.08f+(i%3)*.045f);
            rect(c,x,h*.44f-bh,x+w*.04f,h*.44f,Color.rgb(91,119,139));
        }
        rect(c,w*.22f,h*.09f,w*.235f,h*.44f,Color.WHITE);

        // bed/sofa - improves with room level
        int sofa=s.roomLevel>=2?Color.rgb(56,96,126):Color.rgb(150,118,90);
        rr(c,w*.56f,h*.46f,w*.94f,h*.69f,w*.035f,sofa);
        rr(c,w*.54f,h*.41f,w*.96f,h*.57f,w*.035f,sofa);
        if(s.roomLevel>=2){
            rr(c,w*.59f,h*.43f,w*.72f,h*.53f,w*.02f,Color.rgb(224,195,129));
            rr(c,w*.78f,h*.43f,w*.91f,h*.53f,w*.02f,Color.rgb(225,229,232));
        }

        // desk
        rr(c,w*.06f,h*.57f,w*.46f,h*.63f,w*.015f,Color.rgb(112,82,61));
        rect(c,w*.10f,h*.63f,w*.13f,h*.80f,Color.rgb(79,61,50));
        rect(c,w*.39f,h*.63f,w*.42f,h*.80f,Color.rgb(79,61,50));

        // laptop upgrade
        if(s.laptopLevel>0){
            int screen=s.laptopLevel>=2?Color.rgb(75,166,187):Color.rgb(91,132,169);
            rr(c,w*.19f,h*.43f,w*.36f,h*.57f,w*.012f,Color.rgb(40,46,52));
            rect(c,w*.205f,h*.45f,w*.345f,h*.545f,screen);
            rr(c,w*.17f,h*.565f,w*.38f,h*.585f,w*.008f,Color.rgb(65,70,74));
        }

        // plant/decor upgrades
        if(s.roomLevel>=2){
            rect(c,w*.48f,h*.56f,w*.495f,h*.76f,Color.rgb(99,72,53));
            circle(c,w*.49f,h*.53f,w*.045f,Color.rgb(57,133,83));
            circle(c,w*.46f,h*.56f,w*.035f,Color.rgb(67,148,91));
        }
        if(s.roomLevel>=3){
            rr(c,w*.65f,h*.09f,w*.88f,h*.31f,w*.018f,Color.rgb(250,248,242));
            rect(c,w*.68f,h*.12f,w*.85f,h*.28f,Color.rgb(200,164,93));
            p.setColor(Color.WHITE);p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(w*.055f);
            c.drawText("Z",w*.765f,h*.225f,p);
        }

        // phone on desk
        rr(c,w*.105f,h*.51f,w*.145f,h*.575f,w*.008f,Color.rgb(26,30,35));
        if(s.phoneLevel>=2){
            circle(c,w*.125f,h*.535f,w*.009f,Color.rgb(200,164,93));
        }

        // avatar
        drawAvatar(c,w*.49f,h*.80f,Math.min(w,h)*.0034f,s.avatarStyle);

        // upgrade badges
        badge(c,w*.84f,h*.78f,"اتاق "+s.roomLevel);
        if(s.laptopLevel>0) badge(c,w*.24f,h*.72f,"لپ‌تاپ "+s.laptopLevel);
    }

    private void badge(Canvas c,float x,float y,String text){
        p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(getWidth()*.032f);p.setTextAlign(Paint.Align.CENTER);
        float tw=p.measureText(text);
        rr(c,x-tw*.65f,y-getHeight()*.035f,x+tw*.65f,y+getHeight()*.025f,getHeight()*.02f,Color.argb(195,7,27,50));
        p.setColor(Color.WHITE);c.drawText(text,x,y,p);
    }

    private void drawAvatar(Canvas c,float cx,float bottom,float sc,int style){
        int skin=style%3==0?Color.rgb(198,140,101):(style%2==0?Color.rgb(236,190,149):Color.rgb(218,164,124));
        int shirt=style%2==0?Color.rgb(200,164,93):Color.rgb(7,27,50);
        line(c,cx-11*sc,bottom-62*sc,cx-16*sc,bottom,10*sc,Color.rgb(44,49,55));
        line(c,cx+11*sc,bottom-62*sc,cx+17*sc,bottom,10*sc,Color.rgb(44,49,55));
        rr(c,cx-31*sc,bottom-142*sc,cx+31*sc,bottom-60*sc,18*sc,shirt);
        circle(c,cx,bottom-174*sc,27*sc,skin);
        p.setColor(Color.rgb(25,31,38));
        c.drawArc(cx-27*sc,bottom-205*sc,cx+27*sc,bottom-164*sc,185,170,true,p);
        circle(c,cx-9*sc,bottom-173*sc,2.2f*sc,Color.rgb(45,45,47));
        circle(c,cx+9*sc,bottom-173*sc,2.2f*sc,Color.rgb(45,45,47));
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2*sc);p.setColor(Color.rgb(120,65,55));
        c.drawArc(cx-9*sc,bottom-163*sc,cx+9*sc,bottom-151*sc,10,160,false,p);
        p.setStyle(Paint.Style.FILL);
    }
}
