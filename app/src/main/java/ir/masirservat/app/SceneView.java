package ir.masirservat.app;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.View;

public class SceneView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private String location="خانه";
    private String mood="neutral";
    private int speakerStyle=2;
    private int playerStyle=1;
    private int progress=1;

    public SceneView(Context c){ super(c); setLayerType(View.LAYER_TYPE_SOFTWARE,null); }

    public void setScene(String location,String mood,int speakerStyle,int playerStyle,int progress){
        this.location=location==null?"خانه":location;
        this.mood=mood==null?"neutral":mood;
        this.speakerStyle=Math.max(1,Math.min(6,speakerStyle));
        this.playerStyle=Math.max(1,Math.min(6,playerStyle));
        this.progress=Math.max(1,progress);
        invalidate();
    }

    private void fill(Canvas c,int color){ c.drawColor(color); }
    private void rect(Canvas c,float l,float t,float r,float b,int color){ p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRect(l,t,r,b,p); }
    private void rr(Canvas c,float l,float t,float r,float b,float rad,int color){ p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRoundRect(l,t,r,b,rad,rad,p); }
    private void circle(Canvas c,float x,float y,float r,int color){ p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawCircle(x,y,r,p); }
    private void line(Canvas c,float x1,float y1,float x2,float y2,float sw,int color){ p.setShader(null);p.setStyle(Paint.Style.STROKE);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeWidth(sw);p.setColor(color);c.drawLine(x1,y1,x2,y2,p);p.setStyle(Paint.Style.FILL); }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float w=getWidth(), h=getHeight();
        if(w<=0||h<=0)return;
        drawBackground(c,w,h);
        // ground shadow
        p.setShader(null);
        p.setColor(Color.argb(55,0,0,0));
        c.drawOval(w*.08f,h*.84f,w*.42f,h*.92f,p);
        c.drawOval(w*.58f,h*.82f,w*.92f,h*.92f,p);

        drawHuman(c,w*.24f,h*.88f,Math.min(w,h)*.00365f,playerStyle,Color.rgb(7,27,50),"neutral",true,false);
        int npcAccent = speakerStyle==1?Color.rgb(72,117,177):
                speakerStyle==2?Color.rgb(47,146,113):
                speakerStyle==3?Color.rgb(205,126,52):
                speakerStyle==4?Color.rgb(97,104,126):
                speakerStyle==5?Color.rgb(152,86,123):Color.rgb(200,164,93);
        drawHuman(c,w*.72f,h*.87f,Math.min(w,h)*.00435f,speakerStyle,npcAccent,mood,false,true);

        // depth vignette
        LinearGradient shade=new LinearGradient(0,0,0,h,new int[]{Color.argb(10,0,0,0),Color.TRANSPARENT,Color.argb(45,0,0,0)},null,Shader.TileMode.CLAMP);
        p.setShader(shade); c.drawRect(0,0,w,h,p); p.setShader(null);
    }

    private void drawBackground(Canvas c,float w,float h){
        if("کافه".equals(location)) drawCafe(c,w,h);
        else if("دانشگاه".equals(location)||"آموزشگاه".equals(location)) drawUniversity(c,w,h);
        else if("محل کار".equals(location)) drawWork(c,w,h);
        else if("فروشگاه".equals(location)||"مرکز خرید".equals(location)) drawShop(c,w,h);
        else if("بانک".equals(location)) drawBank(c,w,h);
        else if("باشگاه".equals(location)) drawGym(c,w,h);
        else if("آنلاین".equals(location)) drawOnline(c,w,h);
        else if("سفر".equals(location)) drawTravel(c,w,h);
        else drawHome(c,w,h);
    }

    private void gradient(Canvas c,float w,float h,int top,int bottom){
        LinearGradient g=new LinearGradient(0,0,0,h,top,bottom,Shader.TileMode.CLAMP);
        p.setShader(g); c.drawRect(0,0,w,h,p); p.setShader(null);
    }

    private void drawHome(Canvas c,float w,float h){
        gradient(c,w,h,Color.rgb(225,235,244),Color.rgb(246,239,225));
        // window + skyline
        rr(c,w*.08f,h*.08f,w*.45f,h*.50f,w*.02f,Color.rgb(245,249,252));
        rect(c,w*.10f,h*.10f,w*.43f,h*.48f,Color.rgb(173,210,232));
        rect(c,w*.23f,h*.10f,w*.25f,h*.48f,Color.WHITE);
        rect(c,w*.10f,h*.28f,w*.43f,h*.30f,Color.WHITE);
        for(int i=0;i<5;i++){
            float x=w*(.105f+i*.063f);
            float bh=h*(.11f+(i%3)*.04f);
            rect(c,x,h*.48f-bh,x+w*.045f,h*.48f,Color.rgb(102,130,150));
        }
        // sofa
        rr(c,w*.50f,h*.53f,w*.94f,h*.76f,w*.035f,Color.rgb(204,178,147));
        rr(c,w*.48f,h*.48f,w*.96f,h*.65f,w*.035f,Color.rgb(221,197,166));
        rr(c,w*.53f,h*.50f,w*.71f,h*.61f,w*.025f,Color.rgb(238,219,194));
        rr(c,w*.74f,h*.50f,w*.91f,h*.61f,w*.025f,Color.rgb(196,159,121));
        // plant
        rect(c,w*.045f,h*.60f,w*.065f,h*.79f,Color.rgb(110,80,58));
        circle(c,w*.06f,h*.55f,w*.045f,Color.rgb(67,135,91));
        circle(c,w*.095f,h*.58f,w*.042f,Color.rgb(78,150,100));
        circle(c,w*.035f,h*.59f,w*.035f,Color.rgb(85,160,105));
        rect(c,0,h*.79f,w,h,Color.rgb(225,211,191));
    }

    private void drawCafe(Canvas c,float w,float h){
        gradient(c,w,h,Color.rgb(80,62,55),Color.rgb(226,184,132));
        // wall panels
        for(int i=0;i<6;i++) rect(c,i*w/6f,0,(i+1)*w/6f,h*.55f,(i%2==0)?Color.rgb(117,82,69):Color.rgb(103,73,63));
        // warm lights
        for(int i=0;i<3;i++){ float x=w*(.2f+i*.3f); line(c,x,0,x,h*.15f,w*.006f,Color.rgb(60,45,40)); circle(c,x,h*.18f,w*.035f,Color.rgb(255,218,132)); }
        // counter
        rect(c,w*.56f,h*.42f,w,h*.69f,Color.rgb(63,46,42));
        rect(c,w*.54f,h*.39f,w,h*.44f,Color.rgb(215,175,123));
        // tables
        rr(c,w*.06f,h*.61f,w*.39f,h*.68f,w*.02f,Color.rgb(121,78,50));
        rect(c,w*.20f,h*.68f,w*.24f,h*.86f,Color.rgb(72,55,49));
        rr(c,w*.62f,h*.66f,w*.90f,h*.72f,w*.02f,Color.rgb(121,78,50));
        rect(c,w*.75f,h*.72f,w*.79f,h*.88f,Color.rgb(72,55,49));
        rect(c,0,h*.82f,w,h,Color.rgb(92,68,58));
    }

    private void drawUniversity(Canvas c,float w,float h){
        gradient(c,w,h,Color.rgb(180,214,235),Color.rgb(239,236,220));
        rect(c,0,h*.68f,w,h,Color.rgb(180,190,162));
        rect(c,w*.08f,h*.18f,w*.92f,h*.70f,Color.rgb(221,210,183));
        // columns
        for(int i=0;i<6;i++){ float x=w*(.13f+i*.145f); rect(c,x,h*.25f,x+w*.045f,h*.68f,Color.rgb(238,231,211));}
        rr(c,w*.36f,h*.36f,w*.64f,h*.70f,w*.02f,Color.rgb(80,107,120));
        // trees
        circle(c,w*.05f,h*.54f,w*.07f,Color.rgb(71,137,85));
        circle(c,w*.95f,h*.52f,w*.08f,Color.rgb(63,128,80));
    }

    private void drawWork(Canvas c,float w,float h){
        gradient(c,w,h,Color.rgb(214,226,238),Color.rgb(235,240,244));
        rect(c,0,h*.77f,w,h,Color.rgb(188,197,205));
        // large office windows
        for(int i=0;i<4;i++){
            float x=w*(.05f+i*.235f);
            rect(c,x,h*.08f,x+w*.19f,h*.48f,Color.rgb(157,197,219));
            rect(c,x+w*.09f,h*.08f,x+w*.105f,h*.48f,Color.rgb(228,236,240));
        }
        // desks and monitors
        rr(c,w*.05f,h*.57f,w*.42f,h*.64f,w*.015f,Color.rgb(126,92,70));
        rect(c,w*.12f,h*.40f,w*.32f,h*.57f,Color.rgb(48,59,69));
        rect(c,w*.15f,h*.43f,w*.29f,h*.53f,Color.rgb(91,153,181));
        rr(c,w*.62f,h*.59f,w*.94f,h*.66f,w*.015f,Color.rgb(126,92,70));
        rect(c,w*.69f,h*.43f,w*.87f,h*.59f,Color.rgb(48,59,69));
    }

    private void drawShop(Canvas c,float w,float h){
        gradient(c,w,h,Color.rgb(245,239,232),Color.rgb(220,224,232));
        rect(c,0,h*.78f,w,h,Color.rgb(197,198,203));
        // shelves
        for(int r=0;r<3;r++){
            float y=h*(.18f+r*.17f);
            rect(c,w*.05f,y,w*.95f,y+h*.025f,Color.rgb(119,96,79));
            for(int i=0;i<8;i++){
                float x=w*(.08f+i*.105f);
                int col=(i+r)%3==0?Color.rgb(66,120,166):(i+r)%3==1?Color.rgb(200,164,93):Color.rgb(180,91,99);
                rr(c,x,y-h*.09f,x+w*.055f,y,w*.008f,col);
            }
        }
        // checkout
        rr(c,w*.58f,h*.60f,w*.94f,h*.75f,w*.02f,Color.rgb(75,84,93));
    }

    private void drawBank(Canvas c,float w,float h){
        gradient(c,w,h,Color.rgb(220,232,238),Color.rgb(242,244,244));
        rect(c,0,h*.79f,w,h,Color.rgb(201,207,210));
        // teller wall
        rect(c,w*.06f,h*.13f,w*.94f,h*.58f,Color.rgb(244,246,247));
        for(int i=0;i<3;i++){
            float x=w*(.12f+i*.29f);
            rr(c,x,h*.22f,x+w*.20f,h*.50f,w*.018f,Color.rgb(186,209,217));
            circle(c,x+w*.10f,h*.34f,w*.04f,Color.rgb(64,120,134));
        }
        rect(c,w*.05f,h*.58f,w*.95f,h*.66f,Color.rgb(70,103,112));
    }

    private void drawGym(Canvas c,float w,float h){
        gradient(c,w,h,Color.rgb(58,65,73),Color.rgb(116,124,130));
        rect(c,0,h*.80f,w,h,Color.rgb(44,48,52));
        // mirrors
        rect(c,w*.04f,h*.08f,w*.96f,h*.48f,Color.rgb(134,159,172));
        for(int i=1;i<5;i++) rect(c,w*i*.2f,h*.08f,w*i*.2f+w*.008f,h*.48f,Color.rgb(220,225,228));
        // weight rack
        line(c,w*.08f,h*.62f,w*.35f,h*.62f,w*.018f,Color.rgb(30,32,35));
        for(int i=0;i<4;i++){
            circle(c,w*(.12f+i*.065f),h*.62f,w*.025f,Color.rgb(20,22,24));
        }
        // bench
        rr(c,w*.60f,h*.62f,w*.88f,h*.69f,w*.015f,Color.rgb(30,32,35));
        line(c,w*.65f,h*.69f,w*.62f,h*.84f,w*.015f,Color.rgb(30,32,35));
        line(c,w*.83f,h*.69f,w*.86f,h*.84f,w*.015f,Color.rgb(30,32,35));
    }

    private void drawOnline(Canvas c,float w,float h){
        gradient(c,w,h,Color.rgb(19,31,52),Color.rgb(64,87,120));
        // giant phone
        rr(c,w*.16f,h*.07f,w*.84f,h*.76f,w*.06f,Color.rgb(18,22,30));
        rr(c,w*.19f,h*.11f,w*.81f,h*.71f,w*.045f,Color.rgb(235,241,247));
        rr(c,w*.27f,h*.18f,w*.70f,h*.27f,w*.03f,Color.rgb(211,226,237));
        rr(c,w*.34f,h*.32f,w*.75f,h*.42f,w*.03f,Color.rgb(214,239,225));
        rr(c,w*.25f,h*.47f,w*.65f,h*.57f,w*.03f,Color.rgb(231,222,244));
        circle(c,w*.50f,h*.75f,w*.035f,Color.rgb(90,100,110));
        // floating reactions
        circle(c,w*.08f,h*.30f,w*.035f,Color.rgb(236,85,112));
        circle(c,w*.92f,h*.22f,w*.03f,Color.rgb(200,164,93));
        circle(c,w*.09f,h*.58f,w*.028f,Color.rgb(68,165,122));
        rect(c,0,h*.82f,w,h,Color.rgb(22,30,43));
    }

    private void drawTravel(Canvas c,float w,float h){
        gradient(c,w,h,Color.rgb(121,192,227),Color.rgb(244,207,142));
        circle(c,w*.82f,h*.17f,w*.075f,Color.rgb(255,224,125));
        // mountains
        Path m1=new Path();m1.moveTo(0,h*.70f);m1.lineTo(w*.34f,h*.30f);m1.lineTo(w*.60f,h*.70f);m1.close();p.setColor(Color.rgb(111,139,128));c.drawPath(m1,p);
        Path m2=new Path();m2.moveTo(w*.30f,h*.70f);m2.lineTo(w*.68f,h*.36f);m2.lineTo(w,h*.70f);m2.close();p.setColor(Color.rgb(88,120,110));c.drawPath(m2,p);
        rect(c,0,h*.69f,w,h,Color.rgb(105,156,91));
        // road
        Path road=new Path();road.moveTo(w*.42f,h);road.lineTo(w*.49f,h*.69f);road.lineTo(w*.56f,h*.69f);road.lineTo(w*.72f,h);road.close();p.setColor(Color.rgb(85,88,89));c.drawPath(road,p);
    }

    private void drawHuman(Canvas c,float cx,float bottom,float s,int style,int accent,String mood,boolean faceRight,boolean foreground){
        float headR=28*s, headY=bottom-175*s;
        int skin=style%3==0?Color.rgb(198,140,101):(style%2==0?Color.rgb(236,190,149):Color.rgb(218,164,124));
        int hair=style==5?Color.rgb(74,43,35):Color.rgb(24,31,39);

        // legs
        line(c,cx-12*s,bottom-65*s,cx-17*s,bottom,10*s,Color.rgb(43,48,55));
        line(c,cx+12*s,bottom-65*s,cx+19*s,bottom,10*s,Color.rgb(43,48,55));
        // shoes
        line(c,cx-18*s,bottom,cx-31*s,bottom+2*s,8*s,Color.rgb(24,27,31));
        line(c,cx+19*s,bottom,cx+32*s,bottom+2*s,8*s,Color.rgb(24,27,31));
        // torso
        Path torso=new Path();
        torso.moveTo(cx-33*s,bottom-145*s);torso.lineTo(cx+33*s,bottom-145*s);torso.lineTo(cx+25*s,bottom-65*s);torso.lineTo(cx-25*s,bottom-65*s);torso.close();
        p.setColor(accent);c.drawPath(torso,p);
        // arms
        float handDir=faceRight?1:-1;
        line(c,cx-30*s,bottom-135*s,cx-46*s,bottom-85*s,11*s,accent);
        line(c,cx+30*s,bottom-135*s,cx+42*s,bottom-95*s,11*s,accent);
        circle(c,cx-47*s,bottom-80*s,7*s,skin);
        circle(c,cx+43*s,bottom-90*s,7*s,skin);
        // neck
        rr(c,cx-8*s,bottom-158*s,cx+8*s,bottom-136*s,5*s,skin);
        // head
        circle(c,cx,headY,headR,skin);
        // ears
        circle(c,cx-headR*.92f,headY+2*s,5*s,skin);
        circle(c,cx+headR*.92f,headY+2*s,5*s,skin);
        // hair
        if(style==2||style==5){
            p.setColor(hair);
            c.drawArc(cx-headR,headY-headR,cx+headR,headY+headR*.35f,185,170,true,p);
            rr(c,cx-headR,headY-8*s,cx-headR+6*s,headY+18*s,3*s,hair);
        }else{
            Path hp=new Path();hp.moveTo(cx-headR,headY-5*s);
            hp.cubicTo(cx-headR*.75f,headY-headR*1.05f,cx+headR*.85f,headY-headR*.95f,cx+headR,headY-3*s);
            hp.cubicTo(cx+headR*.3f,headY-headR*.45f,cx-headR*.3f,headY-headR*.40f,cx-headR,headY-5*s);
            hp.close();p.setColor(hair);c.drawPath(hp,p);
        }
        // eyes
        float dir=faceRight?1f:-1f;
        circle(c,cx-9*s+dir*2*s,headY+2*s,2.2f*s,Color.rgb(42,44,46));
        circle(c,cx+9*s+dir*2*s,headY+2*s,2.2f*s,Color.rgb(42,44,46));
        // eyebrows
        line(c,cx-14*s,headY-6*s,cx-5*s,headY-7*s,1.7f*s,Color.rgb(55,45,42));
        line(c,cx+5*s,headY-7*s,cx+14*s,headY-6*s,1.7f*s,Color.rgb(55,45,42));
        // mouth by mood
        p.setStyle(Paint.Style.STROKE);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeWidth(2.2f*s);p.setColor(Color.rgb(120,65,55));
        if("excited".equals(mood)||"hopeful".equals(mood)){
            c.drawArc(cx-11*s,headY+8*s,cx+11*s,headY+22*s,10,160,false,p);
        }else if("tense".equals(mood)){
            c.drawArc(cx-10*s,headY+14*s,cx+10*s,headY+24*s,195,150,false,p);
        }else{
            c.drawLine(cx-8*s,headY+16*s,cx+8*s,headY+16*s,p);
        }
        p.setStyle(Paint.Style.FILL);
        // subtle highlight for active speaker
        if(foreground){
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2*s);p.setColor(Color.argb(110,255,255,255));
            c.drawOval(cx-55*s,bottom-225*s,cx+55*s,bottom+8*s,p);p.setStyle(Paint.Style.FILL);
        }
    }
}
