package ir.masirservat.app;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.view.View;

public class TimingGameView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean running=false;
    private long startTime=0L;
    private float marker=.1f;
    private float targetStart=.42f;
    private float targetEnd=.58f;
    private float speed=.72f;

    public TimingGameView(Context c){ super(c); }

    public void startRound(int seed){
        float center=.22f + ((seed*37)%55)/100f;
        targetStart=Math.max(.08f,center-.09f);
        targetEnd=Math.min(.92f,center+.09f);
        speed=.62f+((seed*13)%35)/100f;
        startTime=SystemClock.uptimeMillis();
        running=true;
        invalidate();
    }

    public int stopRound(){
        updateMarker();
        running=false;
        float center=(targetStart+targetEnd)/2f;
        float half=(targetEnd-targetStart)/2f;
        float dist=Math.abs(marker-center);
        if(dist<=half) return Math.max(82,100-Math.round((dist/Math.max(.001f,half))*18));
        float outside=dist-half;
        return Math.max(18,82-Math.round(outside*150f));
    }

    public boolean isRunning(){ return running; }

    private void updateMarker(){
        if(!running)return;
        float elapsed=(SystemClock.uptimeMillis()-startTime)/1000f;
        float x=(elapsed*speed)%2f;
        marker=x<=1f?x:2f-x;
    }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float w=getWidth(),h=getHeight();
        if(running) updateMarker();

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(222,227,232));
        c.drawRoundRect(w*.06f,h*.38f,w*.94f,h*.63f,h*.12f,h*.12f,p);

        p.setColor(Color.rgb(200,164,93));
        c.drawRoundRect(w*(.06f+.88f*targetStart),h*.38f,w*(.06f+.88f*targetEnd),h*.63f,h*.12f,h*.12f,p);

        float mx=w*(.06f+.88f*marker);
        p.setColor(Color.rgb(7,27,50));
        c.drawRoundRect(mx-w*.012f,h*.24f,mx+w*.012f,h*.77f,w*.01f,w*.01f,p);

        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextSize(h*.16f);
        p.setColor(Color.rgb(75,86,98));
        c.drawText("منطقه طلایی",w*.5f,h*.93f,p);

        if(running) postInvalidateOnAnimation();
    }
}
