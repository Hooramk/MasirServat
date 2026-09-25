package ir.masirservat.app;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import java.util.LinkedHashMap;
import java.util.Map;

public class CityMapView extends View {
    public interface Listener { void onLocation(String id); }
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Map<String,RectF> spots=new LinkedHashMap<>();
    private Listener listener;
    private GameState state;

    public CityMapView(Context c){ super(c); setClickable(true); }
    public void setListener(Listener l){ listener=l; }
    public void setState(GameState s){ state=s; invalidate(); }

    private void fill(Canvas c,int color){ c.drawColor(color); }
    private void rect(Canvas c,float l,float t,float r,float b,int color){
        p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRect(l,t,r,b,p);
    }
    private void rr(Canvas c,float l,float t,float r,float b,float rad,int color){
        p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRoundRect(l,t,r,b,rad,rad,p);
    }
    private void line(Canvas c,float x1,float y1,float x2,float y2,float sw,int color){
        p.setStyle(Paint.Style.STROKE);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeWidth(sw);p.setColor(color);c.drawLine(x1,y1,x2,y2,p);p.setStyle(Paint.Style.FILL);
    }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float w=getWidth(),h=getHeight();
        if(w<=0||h<=0)return;
        spots.clear();

        LinearGradient sky=new LinearGradient(0,0,0,h,Color.rgb(183,220,238),Color.rgb(232,227,204),Shader.TileMode.CLAMP);
        p.setShader(sky);c.drawRect(0,0,w,h,p);p.setShader(null);

        // distant Tehran-like skyline
        int skyline=Color.rgb(111,132,145);
        for(int i=0;i<12;i++){
            float bw=w*(.045f+(i%3)*.012f);
            float x=w*(.01f+i*.084f);
            float top=h*(.17f+(i%4)*.035f);
            rect(c,x,top,x+bw,h*.38f,skyline);
            for(int r=0;r<3;r++) for(int col=0;col<2;col++){
                rect(c,x+bw*(.18f+col*.42f),top+h*(.035f+r*.045f),x+bw*(.33f+col*.42f),top+h*(.052f+r*.045f),Color.rgb(215,224,224));
            }
        }

        // park/ground
        rect(c,0,h*.36f,w,h,Color.rgb(170,196,148));
        // roads
        line(c,w*.05f,h*.58f,w*.95f,h*.58f,h*.105f,Color.rgb(90,96,100));
        line(c,w*.48f,h*.37f,w*.48f,h*.95f,h*.105f,Color.rgb(90,96,100));
        line(c,w*.08f,h*.84f,w*.90f,h*.43f,h*.075f,Color.rgb(100,105,108));
        // lane marks
        line(c,w*.05f,h*.58f,w*.95f,h*.58f,h*.008f,Color.rgb(236,220,156));
        line(c,w*.48f,h*.37f,w*.48f,h*.95f,h*.008f,Color.rgb(236,220,156));

        // trees
        for(int i=0;i<9;i++){
            float x=w*(.07f+(i*.113f)%0.86f), y=h*(.41f+(i%3)*.19f);
            p.setColor(Color.rgb(63,132,78));c.drawCircle(x,y,w*.022f,p);
            rect(c,x-w*.004f,y,x+w*.004f,y+h*.045f,Color.rgb(96,72,52));
        }

        building(c,"home",w*.08f,h*.42f,w*.28f,h*.56f,Color.rgb(226,194,147),"خانه",state!=null?state.roomLevel:1);
        building(c,"university",w*.64f,h*.39f,w*.91f,h*.53f,Color.rgb(220,209,179),"دانشگاه",0);
        building(c,"cafe",w*.07f,h*.66f,w*.27f,h*.79f,Color.rgb(133,83,62),"کافه",0);
        building(c,"work",w*.67f,h*.65f,w*.91f,h*.79f,Color.rgb(105,137,160),"محل کار",state!=null?state.careerLevel:1);
        building(c,"gym",w*.08f,h*.84f,w*.27f,h*.96f,Color.rgb(74,91,106),"باشگاه",0);
        building(c,"shop",w*.70f,h*.83f,w*.92f,h*.96f,Color.rgb(191,116,104),"فروشگاه",0);
        building(c,"bank",w*.36f,h*.70f,w*.58f,h*.82f,Color.rgb(81,133,126),"بانک",0);
        int biz=state!=null?state.businessLevel:0;
        building(c,"business",w*.37f,h*.42f,w*.59f,h*.53f,biz>0?Color.rgb(200,164,93):Color.rgb(151,155,158),biz>0?"کسب‌وکار":"ملک خالی",biz);

        // player marker
        float px=w*.48f, py=h*.61f;
        p.setColor(Color.rgb(7,27,50));c.drawCircle(px,py,w*.026f,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(w*.008f);p.setColor(Color.WHITE);c.drawCircle(px,py,w*.026f,p);p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(w*.038f);p.setColor(Color.WHITE);
        c.drawText("تو",px,py+w*.013f,p);

        // vehicle
        if(state!=null && state.transportLevel>0){
            float vx=w*.53f,vy=h*.60f;
            rr(c,vx-w*.035f,vy-h*.018f,vx+w*.035f,vy+h*.018f,w*.01f,state.transportLevel>=2?Color.rgb(200,164,93):Color.rgb(45,105,150));
            p.setColor(Color.rgb(35,40,44));c.drawCircle(vx-w*.022f,vy+h*.02f,w*.009f,p);c.drawCircle(vx+w*.022f,vy+h*.02f,w*.009f,p);
        }
    }

    private void building(Canvas c,String id,float l,float t,float r,float b,int color,String label,int level){
        float w=getWidth(),h=getHeight();
        spots.put(id,new RectF(l,t,r,b));
        // shadow
        rr(c,l+w*.008f,t+h*.008f,r+w*.008f,b+h*.008f,w*.018f,Color.argb(45,0,0,0));
        rr(c,l,t,r,b,w*.018f,color);
        rect(c,l+w*.04f,t+h*.035f,r-w*.04f,b-h*.03f,Color.argb(32,255,255,255));
        // windows
        for(int i=0;i<2;i++)for(int j=0;j<2;j++){
            float x=l+(r-l)*(.18f+i*.46f),y=t+(b-t)*(.18f+j*.30f);
            rr(c,x,y,x+(r-l)*.18f,y+(b-t)*.16f,w*.006f,Color.rgb(205,226,232));
        }
        // label
        float cy=b+h*.022f;
        p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(w*.030f);p.setColor(Color.rgb(7,27,50));
        c.drawText(label,(l+r)/2f,cy,p);
        if(level>0){
            String lv="Lv."+level;
            p.setTextSize(w*.023f);
            float tw=p.measureText(lv);
            rr(c,r-tw-w*.018f,t-h*.005f,r+w*.006f,t+h*.038f,w*.01f,Color.rgb(7,27,50));
            p.setColor(Color.WHITE);c.drawText(lv,r-tw*.48f-w*.006f,t+h*.025f,p);
        }
    }

    @Override public boolean onTouchEvent(MotionEvent e){
        if(e.getAction()!=MotionEvent.ACTION_UP)return true;
        float x=e.getX(),y=e.getY();
        for(Map.Entry<String,RectF> it:spots.entrySet()){
            if(it.getValue().contains(x,y)){
                performClick();
                if(listener!=null)listener.onLocation(it.getKey());
                return true;
            }
        }
        return true;
    }

    @Override public boolean performClick(){ super.performClick(); return true; }
}
