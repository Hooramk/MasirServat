package ir.masirservat.app;

import android.app.*;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.text.NumberFormat;
import java.util.Locale;

public class PlayActivity extends Activity {
    private static final int NAVY=Color.rgb(7,27,50);
    private static final int NAVY2=Color.rgb(20,52,82);
    private static final int GOLD=Color.rgb(200,164,93);
    private static final int BG=Color.rgb(239,242,246);
    private static final int TEXT=Color.rgb(29,39,50);
    private static final int MUTED=Color.rgb(102,111,122);
    private static final int GREEN=Color.rgb(28,130,83);
    private static final int RED=Color.rgb(176,57,57);

    private SharedPreferences prefs;
    private GameState state;
    private final NumberFormat nf=NumberFormat.getInstance(new Locale("fa","IR"));
    private boolean inHub=true;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        prefs=getSharedPreferences("game",MODE_PRIVATE);
        state=GameState.load(prefs);
        migrateIfNeeded();
        showHub();
    }

    @Override protected void onPause(){
        super.onPause();
        if(state!=null)state.save(prefs);
    }

    @Override public void onBackPressed(){
        if(inHub){ state.save(prefs); finish(); }
        else showHub();
    }

    private void migrateIfNeeded(){
        if(state.loopVersion>=6)return;
        String old=state.profession;
        if(old==null)old="";
        String mapped;
        if(old.contains("فریلنسر")||old.contains("کسب"))mapped="فریلنسر تازه‌کار";
        else if(old.contains("کارمند")||old.contains("کارآموز"))mapped="کارآموز";
        else mapped="دانشجو";
        state.applyProfile(mapped);
        if(!state.personalityChosen){
            CharacterSystem.applyPersonality(state,"دانشجو".equals(mapped)?"محافظ":("کارآموز".equals(mapped)?"استراتژیست":"فرصت‌جو"));
            state.personalityChosen=true;
        }
        state.save(prefs);
    }

    private int dp(int x){return Math.round(x*getResources().getDisplayMetrics().density);}
    private String money(long v){return nf.format(v)+" تومان";}
    private String shortMoney(long v){
        if(Math.abs(v)>=1_000_000_000L)return String.format(new Locale("fa","IR"),"%.1f میلیارد",v/1_000_000_000.0);
        if(Math.abs(v)>=1_000_000L)return String.format(new Locale("fa","IR"),"%.1f م",v/1_000_000.0);
        return nf.format(v);
    }

    private GradientDrawable bg(int color,int radius){
        GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;
    }
    private GradientDrawable border(int color,int radius,int stroke){
        GradientDrawable d=bg(color,radius);d.setStroke(dp(1),stroke);return d;
    }
    private TextView tv(String t,int size,int color,boolean bold){
        TextView v=new TextView(this);v.setText(t);v.setTextSize(size);v.setTextColor(color);
        v.setGravity(Gravity.RIGHT);v.setTextDirection(View.TEXT_DIRECTION_RTL);v.setLineSpacing(0,1.12f);
        if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;
    }
    private TextView pill(String t,int fg,int bgc){
        TextView v=tv(t,12,fg,true);v.setGravity(Gravity.CENTER);v.setPadding(dp(10),dp(5),dp(10),dp(5));v.setBackground(bg(bgc,16));return v;
    }
    private Button btn(String t,int bgc,int fg){
        Button b=new Button(this);b.setText(t);b.setTextSize(15);b.setTextColor(fg);b.setAllCaps(false);
        b.setGravity(Gravity.CENTER);b.setTextDirection(View.TEXT_DIRECTION_RTL);b.setBackground(bg(bgc,15));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(54));p.setMargins(0,dp(5),0,dp(5));b.setLayoutParams(p);return b;
    }
    private Button outline(String t){
        Button b=btn(t,Color.WHITE,NAVY);b.setBackground(border(Color.WHITE,15,Color.rgb(210,217,224)));return b;
    }
    private LinearLayout card(){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        c.setPadding(dp(15),dp(14),dp(15),dp(14));c.setBackground(bg(Color.WHITE,18));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(dp(12),dp(6),dp(12),dp(6));c.setLayoutParams(p);return c;
    }
    private ScrollView page(String title){
        inHub=false;
        ScrollView sc=new ScrollView(this);sc.setFillViewport(true);
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        box.setPadding(dp(12),dp(16),dp(12),dp(24));box.setBackgroundColor(BG);sc.addView(box);
        LinearLayout head=new LinearLayout(this);head.setOrientation(LinearLayout.HORIZONTAL);head.setGravity(Gravity.CENTER_VERTICAL);head.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView t=tv(title,25,NAVY,true);head.addView(t,new LinearLayout.LayoutParams(0,-2,1));
        Button back=outline("بازگشت");LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(dp(95),dp(46));back.setLayoutParams(bp);back.setOnClickListener(v->showHub());head.addView(back);
        box.addView(head);
        return sc;
    }
    private LinearLayout contentOf(ScrollView sc){return (LinearLayout)sc.getChildAt(0);}

    private void showHub(){
        inHub=true;
        if(state.week>24){showSeasonEnd();return;}

        ScrollView sc=new ScrollView(this);sc.setFillViewport(true);
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        box.setBackgroundColor(BG);box.setPadding(0,0,0,dp(20));sc.addView(box);

        LinearLayout top=new LinearLayout(this);top.setOrientation(LinearLayout.VERTICAL);top.setPadding(dp(16),dp(14),dp(16),dp(12));
        GradientDrawable topBg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{NAVY,NAVY2});top.setBackground(topBg);
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setGravity(Gravity.CENTER_VERTICAL);row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView who=tv((state.playerName==null||state.playerName.isEmpty()?"بازیکن":state.playerName)+" · "+state.profession,18,Color.WHITE,true);
        row.addView(who,new LinearLayout.LayoutParams(0,-2,1));
        row.addView(pill("هفته "+nf.format(state.week)+" / ۲۴",NAVY,GOLD));
        top.addView(row);

        LinearLayout hud=new LinearLayout(this);hud.setOrientation(LinearLayout.HORIZONTAL);hud.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);hud.setPadding(0,dp(10),0,0);
        hud.addView(hudBox("💰","نقد",shortMoney(state.cash)),new LinearLayout.LayoutParams(0,dp(62),1));
        hud.addView(hudBox("⚡","انرژی",nf.format(state.energy)+"/"+nf.format(state.maxEnergy)),new LinearLayout.LayoutParams(0,dp(62),1));
        hud.addView(hudBox("🧠","مهارت",nf.format(state.skill)),new LinearLayout.LayoutParams(0,dp(62),1));
        hud.addView(hudBox("🔥","فالوئر",nf.format(state.followers)),new LinearLayout.LayoutParams(0,dp(62),1));
        top.addView(hud);
        box.addView(top);

        LinearLayout mission=card();
        mission.addView(tv("🎯 هدف فعلی",13,GOLD,true));
        TextView mt=tv(state.loopMission(),16,TEXT,true);mt.setPadding(0,dp(4),0,0);mission.addView(mt);box.addView(mission);

        LifeHubView hub=new LifeHubView(this);hub.setState(state);
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,dp(285));hp.setMargins(dp(12),dp(4),dp(12),dp(6));hub.setLayoutParams(hp);
        hub.setBackground(bg(Color.WHITE,18));box.addView(hub);

        LinearLayout progress=card();
        LinearLayout pr=new LinearLayout(this);pr.setOrientation(LinearLayout.HORIZONTAL);pr.setGravity(Gravity.CENTER_VERTICAL);pr.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        pr.addView(tv("Level "+nf.format(state.careerLevel)+" · XP شغلی "+nf.format(state.workXp)+"/۱۰۰",14,TEXT,true),new LinearLayout.LayoutParams(0,-2,1));
        pr.addView(pill(GenZSystem.vibe(state),NAVY,Color.rgb(239,230,207)));
        progress.addView(pr);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(100);bar.setProgress(state.workXp);
        bar.setProgressTintList(ColorStateList.valueOf(GOLD));bar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(232,235,238)));
        LinearLayout.LayoutParams barp=new LinearLayout.LayoutParams(-1,dp(7));barp.setMargins(0,dp(8),0,0);bar.setLayoutParams(barp);progress.addView(bar);
        box.addView(progress);

        TextView act=tv("امروز چی کار می‌کنی؟",18,NAVY,true);act.setPadding(dp(16),dp(8),dp(16),dp(3));box.addView(act);
        LinearLayout grid=new LinearLayout(this);grid.setOrientation(LinearLayout.VERTICAL);grid.setPadding(dp(10),0,dp(10),0);
        grid.addView(actionRow(new String[]{"💼 کار","🧠 مهارت","🗺 بیرون"},new View.OnClickListener[]{
                v->showJobs(),v->showStudy(),v->showMap()
        }));
        grid.addView(actionRow(new String[]{"📱 آنلاین","🛍 ارتقا","🏦 بانک"},new View.OnClickListener[]{
                v->showOnline(),v->showShop(),v->showBank()
        }));
        box.addView(grid);

        LinearLayout footer=card();
        if(!state.restedThisWeek){
            Button rest=outline("😴 یک استراحت کوتاه  ·  +۲ انرژی");
            rest.setOnClickListener(v->rest());
            footer.addView(rest);
        }
        Button end=btn(state.energy==0?"🌙 انرژی تمام شد — پایان هفته":"🌙 پایان هفته",GOLD,NAVY);
        end.setOnClickListener(v->endWeek());
        footer.addView(end);
        box.addView(footer);

        setContentView(sc);
    }

    private TextView hudBox(String icon,String label,String value){
        TextView t=tv(icon+"  "+label+"\n"+value,12,Color.WHITE,true);t.setGravity(Gravity.CENTER);
        t.setBackground(bg(Color.argb(70,255,255,255),12));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(62),1);p.setMargins(dp(2),0,dp(2),0);t.setLayoutParams(p);return t;
    }

    private LinearLayout actionRow(String[] labels,View.OnClickListener[] listeners){
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        for(int i=0;i<labels.length;i++){
            Button b=new Button(this);b.setText(labels[i]);b.setTextSize(14);b.setTextColor(NAVY);b.setAllCaps(false);b.setGravity(Gravity.CENTER);
            b.setBackground(bg(Color.WHITE,17));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(76),1);p.setMargins(dp(4),dp(4),dp(4),dp(4));b.setLayoutParams(p);
            b.setOnClickListener(listeners[i]);row.addView(b);
        }
        return row;
    }

    private void showJobs(){
        ScrollView sc=page("کار و درآمد");
        LinearLayout box=contentOf(sc);
        TextView sub=tv("اینجا واقعاً کار می‌کنی. در مینی‌گیم، نشانگر را داخل منطقه طلایی متوقف کن؛ عملکرد بهتر = درآمد بیشتر.",15,MUTED,false);
        sub.setPadding(dp(3),dp(8),dp(3),dp(8));box.addView(sub);

        jobCard(box,"☕","شیفت کافه","همیشه باز","کسب درآمد سریع؛ مناسب شروع بازی","کافه",true);
        jobCard(box,"🛵","ارسال سفارش",state.transportLevel>=1?"باز شده":"نیاز به موتور/وسیله رفت‌وآمد","درآمد بیشتر، با ارتقای وسیله بهتر می‌شود","ارسال",state.transportLevel>=1);
        boolean freelance=state.skill>=45 && state.laptopLevel>=1;
        jobCard(box,"💻","پروژه فریلنس",freelance?"باز شده":"نیاز: مهارت ۴۵ + لپ‌تاپ","درآمد وابسته به مهارت و سطح لپ‌تاپ","فریلنس",freelance);

        setContentView(sc);
    }

    private void jobCard(LinearLayout box,String icon,String title,String unlock,String desc,String job,boolean enabled){
        LinearLayout c=card();
        LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);r.setGravity(Gravity.CENTER_VERTICAL);r.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView ic=tv(icon,30,NAVY,true);ic.setGravity(Gravity.CENTER);r.addView(ic,new LinearLayout.LayoutParams(dp(62),dp(62)));
        LinearLayout txt=new LinearLayout(this);txt.setOrientation(LinearLayout.VERTICAL);
        txt.addView(tv(title,18,NAVY,true));txt.addView(tv(unlock,13,enabled?GREEN:RED,true));txt.addView(tv(desc,13,MUTED,false));
        r.addView(txt,new LinearLayout.LayoutParams(0,-2,1));c.addView(r);
        Button b=btn(enabled?"شروع شیفت  ⚡۱":"قفل است",enabled?NAVY:Color.rgb(180,185,190),Color.WHITE);
        b.setEnabled(enabled);b.setOnClickListener(v->startShift(job,title));c.addView(b);box.addView(c);
    }

    private void startShift(String job,String title){
        if(state.energy<1){noEnergy();return;}
        GameLoopEngine.spendEnergy(state,1);
        state.save(prefs);

        ScrollView sc=page(title);
        LinearLayout box=contentOf(sc);
        TextView help=tv("سه راند داری. نشانگر را تا جای ممکن داخل قسمت طلایی متوقف کن.",15,MUTED,false);
        help.setPadding(0,dp(8),0,dp(10));box.addView(help);

        TextView roundText=tv("راند ۱ از ۳",18,NAVY,true);roundText.setGravity(Gravity.CENTER);box.addView(roundText);
        TimingGameView game=new TimingGameView(this);
        LinearLayout.LayoutParams gp=new LinearLayout.LayoutParams(-1,dp(150));gp.setMargins(0,dp(6),0,dp(6));game.setLayoutParams(gp);game.setBackground(bg(Color.WHITE,18));box.addView(game);
        TextView scoreText=tv("روی منطقه طلایی بزن.",15,GOLD,true);scoreText.setGravity(Gravity.CENTER);scoreText.setPadding(0,dp(4),0,dp(6));box.addView(scoreText);
        Button stop=btn("شروع راند",GOLD,NAVY);box.addView(stop);

        final int[] round={1};final int[] total={0};final boolean[] active={false};
        stop.setOnClickListener(v->{
            if(!active[0]){
                game.startRound(state.week*7+round[0]*11+state.skill);
                active[0]=true;stop.setText("توقف!");scoreText.setText("الان!");
            }else{
                int score=game.stopRound();total[0]+=score;active[0]=false;
                scoreText.setText("امتیاز راند: "+nf.format(score));
                if(round[0]>=3){
                    int avg=total[0]/3;
                    long reward=GameLoopEngine.workReward(state,job,avg);
                    state.save(prefs);
                    stop.setEnabled(false);stop.setText("شیفت تمام شد");
                    showShiftResult(title,avg,reward);
                }else{
                    round[0]++;
                    roundText.setText("راند "+nf.format(round[0])+" از ۳");
                    stop.setText("شروع راند بعد");
                }
            }
        });

        setContentView(sc);
    }

    private void showShiftResult(String title,int score,long reward){
        new AlertDialog.Builder(this)
                .setTitle("شیفت تمام شد")
                .setMessage(title+"\n\nعملکرد: "+nf.format(score)+"/۱۰۰\nدرآمد: "+money(reward)+"\nXP شغلی: "+nf.format(state.workXp)+"/۱۰۰")
                .setPositiveButton("برگشت خانه",(d,w)->showHub())
                .setCancelable(false).show();
    }

    private void showStudy(){
        ScrollView sc=page("مهارت و رشد");
        LinearLayout box=contentOf(sc);
        TextView sub=tv("مهارت، شغل‌های بهتر و درآمد بالاتر را باز می‌کند. هر تمرین ۱ انرژی مصرف می‌کند.",15,MUTED,false);
        sub.setPadding(0,dp(7),0,dp(8));box.addView(sub);
        studyCard(box,"🇬🇧 زبان و ارتباط",700_000L,3,"برای پروژه‌های بهتر و فرصت‌های بین‌المللی");
        studyCard(box,"🤖 ابزارهای AI",1_100_000L,5,"افزایش سریع مهارت دیجیتال");
        studyCard(box,"🎬 تدوین و محتوا",900_000L,4,"کمک به فریلنس و رشد پیج");
        studyCard(box,"💬 فروش و مذاکره",850_000L,3,"مهارت + اعتبار اجتماعی");
        setContentView(sc);
    }

    private void studyCard(LinearLayout box,String title,long cost,int gain,String desc){
        LinearLayout c=card();c.addView(tv(title,18,NAVY,true));c.addView(tv(desc,13,MUTED,false));
        TextView meta=tv("هزینه "+money(cost)+"  ·  مهارت +"+nf.format(gain),13,GOLD,true);meta.setPadding(0,dp(6),0,dp(3));c.addView(meta);
        Button b=btn("تمرین  ⚡۱",NAVY,Color.WHITE);
        b.setOnClickListener(v->{
            int before=state.skill;
            int got=GameLoopEngine.study(state,title,cost,gain);
            if(got==0){
                Toast.makeText(this,state.energy<1?"انرژی کافی نداری":"پول کافی نداری",Toast.LENGTH_SHORT).show();
            }else{
                if(title.contains("فروش"))state.social=Math.min(100,state.social+2);
                state.save(prefs);
                Toast.makeText(this,"مهارت +"+nf.format(state.skill-before),Toast.LENGTH_SHORT).show();
                showStudy();
            }
        });
        c.addView(b);box.addView(c);
    }

    private void showOnline(){
        ScrollView sc=page("زندگی آنلاین");
        LinearLayout box=contentOf(sc);
        LinearLayout stat=card();
        stat.addView(tv("📱 پیج تو",18,NAVY,true));
        stat.addView(tv(nf.format(state.followers)+" فالوئر  ·  گوشی Level "+nf.format(state.phoneLevel),16,TEXT,true));
        stat.addView(tv("از ۱۰۰۰ فالوئر به بعد، پیج هر هفته درآمد کوچک ایجاد می‌کند.",13,MUTED,false));
        box.addView(stat);

        LinearLayout content=card();
        content.addView(tv("🎥 ساخت محتوا",18,NAVY,true));
        content.addView(tv("یک انرژی مصرف می‌کند. کیفیت رشد به گوشی، مهارت و اعتبارت بستگی دارد.",13,MUTED,false));
        Button make=btn("یک محتوا بساز  ⚡۱",GOLD,NAVY);
        make.setOnClickListener(v->{
            if(!GameLoopEngine.spendEnergy(state,1)){noEnergy();return;}
            int gain=GameLoopEngine.createContent(state);state.save(prefs);
            Toast.makeText(this,"+"+nf.format(gain)+" فالوئر",Toast.LENGTH_SHORT).show();showOnline();
        });
        content.addView(make);box.addView(content);

        boolean free=state.skill>=45&&state.laptopLevel>=1;
        LinearLayout fl=card();fl.addView(tv("💻 پروژه آنلاین",18,NAVY,true));
        fl.addView(tv(free?"یک پروژه فریلنس واقعی بگیر.":"برای باز شدن: مهارت ۴۵ + لپ‌تاپ",13,free?GREEN:RED,true));
        Button fb=btn(free?"شروع پروژه":"قفل است",free?NAVY:Color.rgb(185,189,193),Color.WHITE);fb.setEnabled(free);
        fb.setOnClickListener(v->startShift("فریلنس","پروژه آنلاین"));fl.addView(fb);box.addView(fl);

        setContentView(sc);
    }

    private void showShop(){
        ScrollView sc=page("فروشگاه و ارتقا");
        LinearLayout box=contentOf(sc);
        TextView sub=tv("اینجا خرج کردن فقط ظاهر نیست؛ هر ارتقا یک قابلیت واقعی در بازی باز می‌کند.",15,MUTED,false);
        sub.setPadding(0,dp(7),0,dp(8));box.addView(sub);

        upgradeCard(box,"💻 لپ‌تاپ","فریلنس و درآمد پروژه را بهتر می‌کند",state.laptopLevel,2,state.laptopLevel==0?25_000_000L:45_000_000L,"laptop");
        upgradeCard(box,"📱 گوشی","رشد فالوئر و اعتبار آنلاین را بیشتر می‌کند",state.phoneLevel,3,state.phoneLevel==1?18_000_000L:32_000_000L,"phone");
        upgradeCard(box,"🏠 اتاق","ظاهر خانه و حال‌وهوای کاراکتر را بهتر می‌کند",state.roomLevel,3,state.roomLevel==1?12_000_000L:25_000_000L,"room");
        upgradeCard(box,"🛵 رفت‌وآمد","شغل ارسال را باز می‌کند و انرژی هفتگی را بالا می‌برد",state.transportLevel,2,state.transportLevel==0?35_000_000L:70_000_000L,"transport");
        setContentView(sc);
    }

    private void upgradeCard(LinearLayout box,String title,String desc,int level,int max,long cost,String type){
        LinearLayout c=card();c.addView(tv(title+"  ·  Level "+nf.format(level)+"/"+nf.format(max),18,NAVY,true));c.addView(tv(desc,13,MUTED,false));
        if(level>=max){TextView done=pill("MAX",Color.WHITE,GREEN);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,-2);p.gravity=Gravity.RIGHT;done.setLayoutParams(p);c.addView(done);}
        else{
            Button b=btn("ارتقا  ·  "+money(cost),GOLD,NAVY);b.setOnClickListener(v->buyUpgrade(type,cost));c.addView(b);
        }
        box.addView(c);
    }

    private void buyUpgrade(String type,long cost){
        if(state.cash<cost){Toast.makeText(this,"پول کافی نداری",Toast.LENGTH_SHORT).show();return;}
        state.cash-=cost;
        if("laptop".equals(type)){state.laptopLevel++;state.skill=Math.min(100,state.skill+2);}
        else if("phone".equals(type)){state.phoneLevel++;state.social=Math.min(100,state.social+2);}
        else if("room".equals(type)){state.roomLevel++;state.mood=Math.min(100,state.mood+8);}
        else if("transport".equals(type)){state.transportLevel++;state.maxEnergy=Math.min(7,state.maxEnergy+1);state.energy=Math.min(state.maxEnergy,state.energy+1);}
        state.xp+=20;state.save(prefs);
        Toast.makeText(this,"ارتقا انجام شد ✨",Toast.LENGTH_SHORT).show();showShop();
    }

    private void showBank(){
        ScrollView sc=page("بانک و دارایی");
        LinearLayout box=contentOf(sc);
        LinearLayout balance=card();
        balance.addView(tv("موجودی",18,NAVY,true));
        balance.addView(tv("نقد: "+money(state.cash)+"\nصندوق امن: "+money(state.savings)+"\nطلا: "+money(state.gold)+"\nصندوق سرمایه‌گذاری: "+money(state.funds)+"\nبدهی: "+money(state.debt),15,TEXT,false));
        box.addView(balance);

        LinearLayout save=card();save.addView(tv("🛡 صندوق امن",18,NAVY,true));save.addView(tv("پولی که برای خرج روزمره لمسش نمی‌کنی.",13,MUTED,false));
        Button s1=outline("۱ میلیون کنار بگذار");s1.setOnClickListener(v->moveToSavings(1_000_000L));save.addView(s1);
        Button s5=outline("۵ میلیون کنار بگذار");s5.setOnClickListener(v->moveToSavings(5_000_000L));save.addView(s5);
        if(state.savings>0){Button wd=outline("۱ میلیون برداشت");wd.setOnClickListener(v->withdrawSavings(1_000_000L));save.addView(wd);}
        box.addView(save);

        LinearLayout invest=card();invest.addView(tv("📈 سرمایه‌گذاری",18,NAVY,true));invest.addView(tv("بازده تضمینی نیست؛ در بازی هم دارایی‌ها با زمان تغییر می‌کنند.",13,MUTED,false));
        Button gold=outline("۵ میلیون طلا");gold.setOnClickListener(v->invest("gold",5_000_000L));invest.addView(gold);
        Button fund=outline("۵ میلیون صندوق");fund.setOnClickListener(v->invest("fund",5_000_000L));invest.addView(fund);
        box.addView(invest);

        setContentView(sc);
    }

    private void moveToSavings(long x){
        if(state.cash<x){Toast.makeText(this,"نقدینگی کافی نیست",Toast.LENGTH_SHORT).show();return;}
        state.cash-=x;state.savings+=x;state.freedom=Math.min(100,state.freedom+1);state.save(prefs);showBank();
    }
    private void withdrawSavings(long x){
        long a=Math.min(x,state.savings);state.savings-=a;state.cash+=a;state.save(prefs);showBank();
    }
    private void invest(String type,long x){
        if(state.cash<x){Toast.makeText(this,"نقدینگی کافی نیست",Toast.LENGTH_SHORT).show();return;}
        state.cash-=x;if("gold".equals(type))state.gold+=x;else state.funds+=x;state.xp+=5;state.save(prefs);showBank();
    }

    private void showMap(){
        ScrollView sc=page("نقشه شهر");
        LinearLayout box=contentOf(sc);
        TextView sub=tv("لوکیشن را انتخاب کن؛ هر جا فعالیت خودش را دارد.",15,MUTED,false);sub.setPadding(0,dp(7),0,dp(8));box.addView(sub);
        box.addView(mapRow(new String[]{"🏠 خانه","🎓 دانشگاه"},new View.OnClickListener[]{v->showHub(),v->showStudy()}));
        box.addView(mapRow(new String[]{"💼 محل کار","☕ کافه"},new View.OnClickListener[]{v->showJobs(),v->showCafe()}));
        box.addView(mapRow(new String[]{"🏋 باشگاه","🛍 فروشگاه"},new View.OnClickListener[]{v->showGym(),v->showShop()}));
        box.addView(mapRow(new String[]{"🏦 بانک","📱 آنلاین"},new View.OnClickListener[]{v->showBank(),v->showOnline()}));
        setContentView(sc);
    }

    private LinearLayout mapRow(String[] names,View.OnClickListener[] ls){
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        for(int i=0;i<2;i++){
            Button b=new Button(this);b.setText(names[i]);b.setTextSize(17);b.setTextColor(NAVY);b.setAllCaps(false);b.setGravity(Gravity.CENTER);
            b.setBackground(bg(Color.WHITE,20));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(105),1);p.setMargins(dp(5),dp(5),dp(5),dp(5));b.setLayoutParams(p);b.setOnClickListener(ls[i]);row.addView(b);
        }
        return row;
    }

    private void showCafe(){
        ScrollView sc=page("کافه");
        LinearLayout box=contentOf(sc);
        LinearLayout scene=card();scene.addView(tv("☕ یه میز کنار پنجره",22,NAVY,true));scene.addView(tv("دوست‌ها اینجایند؛ می‌توانی وقت بگذرانی یا یک شیفت کار کنی.",15,MUTED,false));box.addView(scene);
        Button hang=btn("نشستن با دوست‌ها  ·  ۶۵۰هزار  ·  ⚡۱",GOLD,NAVY);
        hang.setOnClickListener(v->{
            if(state.cash<650_000L){Toast.makeText(this,"پول کافی نیست",Toast.LENGTH_SHORT).show();return;}
            if(!GameLoopEngine.spendEnergy(state,1)){noEnergy();return;}
            state.cash-=650_000L;state.social=Math.min(100,state.social+5);state.mood=Math.min(100,state.mood+8);
            state.relAmir=Math.min(100,state.relAmir+2);state.save(prefs);Toast.makeText(this,"حال خوب +۸ · اعتبار +۵",Toast.LENGTH_SHORT).show();showCafe();
        });
        box.addView(hang);
        Button work=outline("یک شیفت کافه کار کن");work.setOnClickListener(v->startShift("کافه","شیفت کافه"));box.addView(work);
        setContentView(sc);
    }

    private void showGym(){
        ScrollView sc=page("باشگاه");
        LinearLayout box=contentOf(sc);
        LinearLayout c=card();c.addView(tv("🏋 تمرین",22,NAVY,true));c.addView(tv("هزینه: ۳۵۰هزار · انرژی: ۱\nتمرکز و حال کاراکتر را بالا می‌برد.",14,MUTED,false));
        Button b=btn("تمرین کن",NAVY,Color.WHITE);
        b.setOnClickListener(v->{
            if(state.cash<350_000L){Toast.makeText(this,"پول کافی نیست",Toast.LENGTH_SHORT).show();return;}
            if(!GameLoopEngine.spendEnergy(state,1)){noEnergy();return;}
            state.cash-=350_000L;state.mood=Math.min(100,state.mood+8);state.focus=Math.min(100,state.focus+5);state.social=Math.min(100,state.social+1);state.save(prefs);
            Toast.makeText(this,"تمرکز +۵ · حال +۸",Toast.LENGTH_SHORT).show();showGym();
        });
        c.addView(b);box.addView(c);setContentView(sc);
    }

    private void rest(){
        if(state.restedThisWeek){Toast.makeText(this,"این هفته استراحتت را انجام دادی",Toast.LENGTH_SHORT).show();return;}
        state.restedThisWeek=true;state.energy=Math.min(state.maxEnergy,state.energy+2);state.mood=Math.min(100,state.mood+10);state.focus=Math.min(100,state.focus+8);state.save(prefs);showHub();
    }

    private void endWeek(){
        String recap=GameLoopEngine.endWeek(state);state.save(prefs);
        if(state.week>24){showSeasonEnd();return;}
        new AlertDialog.Builder(this)
                .setTitle("هفته تمام شد 🌙")
                .setMessage(recap+"\n\nهفته "+nf.format(state.week)+" شروع شد. انرژی دوباره پر شده.")
                .setPositiveButton("شروع هفته",(d,w)->showHub())
                .setCancelable(false).show();
    }

    private void noEnergy(){
        Toast.makeText(this,"انرژی نداری؛ استراحت کن یا هفته را تمام کن.",Toast.LENGTH_LONG).show();
    }

    private void showSeasonEnd(){
        inHub=false;
        ScrollView sc=new ScrollView(this);
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);box.setPadding(dp(12),dp(18),dp(12),dp(28));box.setBackgroundColor(BG);sc.addView(box);

        int score=Math.max(0,Math.min(100,(int)Math.round(
                Math.min(100,(state.cash+state.savings+state.gold+state.funds-state.debt)/1_000_000.0)*.25+
                state.skill*.25+state.freedom*.20+state.social*.10+
                Math.min(100,state.followers/20.0)*.10+Math.min(100,state.careerLevel*22)*.10)));
        String ending;
        if(state.debt>30_000_000L)ending="درگیر تعهدات";
        else if(state.skill>=70&&state.followers>=2000)ending="سازنده دیجیتال";
        else if(state.savings>=15_000_000L&&state.freedom>=65)ending="مستقل و آماده رشد";
        else if(state.careerLevel>=3)ending="حرفه‌ای رو به بالا";
        else ending="شروع خوب، مسیر باز";

        LinearLayout hero=card();hero.setBackground(bg(NAVY,20));
        hero.addView(tv("پایان فصل اول · ۲۴ هفته",14,GOLD,true));
        TextView e=tv(ending,30,Color.WHITE,true);e.setPadding(0,dp(8),0,dp(4));hero.addView(e);
        hero.addView(tv("امتیاز مسیر: "+nf.format(score)+" / ۱۰۰",18,Color.rgb(225,231,238),true));box.addView(hero);

        LinearLayout summary=card();summary.addView(tv("چیزی که واقعاً ساختی",19,NAVY,true));
        summary.addView(tv(
                "💰 نقد و پس‌انداز: "+money(state.cash+state.savings)+
                "\n🧠 مهارت: "+nf.format(state.skill)+"/۱۰۰"+
                "\n💼 Level شغلی: "+nf.format(state.careerLevel)+
                "\n📱 فالوئر: "+nf.format(state.followers)+
                "\n🏠 Level اتاق: "+nf.format(state.roomLevel)+
                "\n💻 Level لپ‌تاپ: "+nf.format(state.laptopLevel)+
                "\n🛵 Level رفت‌وآمد: "+nf.format(state.transportLevel)+
                "\n📈 دارایی سرمایه‌ای: "+money(state.gold+state.funds)+
                "\n⚠ بدهی: "+money(state.debt),
                16,TEXT,false));box.addView(summary);

        LinearLayout next=card();next.addView(tv("فصل بعدی اگر ادامه بدهی",19,GOLD,true));
        String n=state.skill<55?"مهارتت را جدی‌تر کن تا کارهای پردرآمد باز شوند.":
                state.savings<10_000_000L?"اول امنیت مالی بساز؛ بعد سراغ ارتقای سبک زندگی برو.":
                        state.followers<1000?"یک دارایی دیجیتال بساز؛ پیجت هنوز ظرفیت رشد دارد.":
                                "وقت ساختن درآمدی است که فقط به ساعت کاری تو وابسته نباشد.";
        next.addView(tv(n,16,TEXT,false));box.addView(next);

        Button replay=btn("🔁 یک زندگی تازه شروع کن",GOLD,NAVY);
        replay.setOnClickListener(v->{
            prefs.edit().clear().apply();
            Intent i=new Intent(this,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });box.addView(replay);
        Button exit=outline("بازگشت به صفحه اول");exit.setOnClickListener(v->finish());box.addView(exit);

        setContentView(sc);
    }
}
