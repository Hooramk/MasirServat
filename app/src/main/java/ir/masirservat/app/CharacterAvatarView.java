package ir.masirservat.app;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class CharacterAvatarView extends View {
    private int style = 1;
    private String role = "player";
    private int accent = Color.rgb(200,164,93);
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CharacterAvatarView(Context c) {
        super(c);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setCharacter(int style, String role) {
        this.style = Math.max(1, Math.min(6, style));
        this.role = role == null ? "player" : role;
        if (this.role.contains("سارا")) accent = Color.rgb(63,137,111);
        else if (this.role.contains("امیر")) accent = Color.rgb(66,104,160);
        else if (this.role.contains("رضا")) accent = Color.rgb(184,117,58);
        else if (this.role.contains("مدیر")) accent = Color.rgb(84,92,112);
        else accent = Color.rgb(200,164,93);
        invalidate();
    }

    private void circle(Canvas c, float x, float y, float r, int color) {
        p.setColor(color); p.setStyle(Paint.Style.FILL); c.drawCircle(x,y,r,p);
    }

    private void roundRect(Canvas c, float l,float t,float r,float b,float radius,int color) {
        p.setColor(color); p.setStyle(Paint.Style.FILL); c.drawRoundRect(l,t,r,b,radius,radius,p);
    }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        float w=getWidth(), h=getHeight();
        float cx=w/2f;
        int navy=Color.rgb(7,27,50);
        int skin = style % 3 == 0 ? Color.rgb(201,145,108) : (style % 2 == 0 ? Color.rgb(235,190,151) : Color.rgb(218,164,124));

        // soft background medallion
        circle(c,cx,h*0.48f,Math.min(w,h)*0.42f,Color.rgb(244,246,249));
        circle(c,cx,h*0.48f,Math.min(w,h)*0.35f,Color.WHITE);

        // torso
        float torsoTop=h*0.60f;
        roundRect(c,w*0.22f,torsoTop,w*0.78f,h*0.96f,w*0.14f,accent);
        // shirt
        Path shirt=new Path();
        shirt.moveTo(cx,torsoTop+h*0.02f);
        shirt.lineTo(cx-w*0.10f,torsoTop);
        shirt.lineTo(cx,torsoTop+h*0.17f);
        shirt.lineTo(cx+w*0.10f,torsoTop);
        shirt.close();
        p.setColor(Color.WHITE); c.drawPath(shirt,p);

        // neck
        roundRect(c,cx-w*0.07f,h*0.52f,cx+w*0.07f,h*0.68f,w*0.04f,skin);

        // head
        circle(c,cx,h*0.39f,w*0.19f,skin);

        // ears
        circle(c,cx-w*0.19f,h*0.40f,w*0.035f,skin);
        circle(c,cx+w*0.19f,h*0.40f,w*0.035f,skin);

        // hair variations
        p.setColor(navy);
        if (style==1 || style==4) {
            Path hair=new Path();
            hair.moveTo(cx-w*0.19f,h*0.36f);
            hair.cubicTo(cx-w*0.12f,h*0.18f,cx+w*0.20f,h*0.19f,cx+w*0.18f,h*0.36f);
            hair.cubicTo(cx+w*0.08f,h*0.30f,cx-w*0.08f,h*0.30f,cx-w*0.19f,h*0.36f);
            c.drawPath(hair,p);
        } else if (style==2 || style==5) {
            c.drawArc(cx-w*0.20f,h*0.19f,cx+w*0.20f,h*0.41f,185,170,true,p);
            roundRect(c,cx-w*0.20f,h*0.28f,cx-w*0.15f,h*0.46f,w*0.02f,navy);
        } else {
            circle(c,cx,h*0.27f,w*0.17f,navy);
        }

        // eyes
        int eye=Color.rgb(45,46,48);
        circle(c,cx-w*0.07f,h*0.40f,w*0.014f,eye);
        circle(c,cx+w*0.07f,h*0.40f,w*0.014f,eye);

        // brows
        p.setColor(eye); p.setStrokeWidth(Math.max(2,w*0.012f)); p.setStrokeCap(Paint.Cap.ROUND);
        c.drawLine(cx-w*0.10f,h*0.36f,cx-w*0.04f,h*0.35f,p);
        c.drawLine(cx+w*0.04f,h*0.35f,cx+w*0.10f,h*0.36f,p);

        // smile
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(Math.max(2,w*0.010f)); p.setColor(Color.rgb(120,65,55));
        c.drawArc(cx-w*0.06f,h*0.43f,cx+w*0.06f,h*0.50f,10,160,false,p);
        p.setStyle(Paint.Style.FILL);

        // optional glasses for analyst-like avatar
        if(style==4){
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(Math.max(2,w*0.009f)); p.setColor(navy);
            c.drawCircle(cx-w*0.07f,h*0.40f,w*0.045f,p);
            c.drawCircle(cx+w*0.07f,h*0.40f,w*0.045f,p);
            c.drawLine(cx-w*0.025f,h*0.40f,cx+w*0.025f,h*0.40f,p);
            p.setStyle(Paint.Style.FILL);
        }

        // badge
        circle(c,w*0.78f,h*0.78f,w*0.095f,Color.WHITE);
        circle(c,w*0.78f,h*0.78f,w*0.075f,accent);
        p.setColor(Color.WHITE); p.setTextAlign(Paint.Align.CENTER); p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextSize(w*0.075f);
        c.drawText(role.startsWith("player") ? "★" : "●",w*0.78f,h*0.805f,p);
    }
}
