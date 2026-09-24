package ir.masirservat.app;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import java.text.NumberFormat;
import java.util.*;

public class MainActivity extends Activity {
    private static final int NAVY=Color.rgb(8,27,49), GOLD=Color.rgb(205,168,91), BG=Color.rgb(246,247,249);
    private static final int WHITE=Color.WHITE, TEXT=Color.rgb(29,40,53), MUTED=Color.rgb(100,111,124), RED=Color.rgb(177,55,55), GREEN=Color.rgb(30,132,83), SOFT=Color.rgb(236,240,244);
    private GameState state; private List<EventData> events; private SharedPreferences prefs;
    private LinearLayout root,choicesBox,resultCard;
    private TextView monthText,cashText,flowText,realWorthText,powerText,eventTitle,eventDesc,lessonText,chapterText,missionText,healthText,resultText,mentorText;
    private ProgressBar chapterProgress,healthProgress;
    private Button nextButton;
    private final NumberFormat nf=NumberFormat.getInstance(new Locale("fa","IR"));

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        prefs=getSharedPreferences("game",MODE_PRIVATE);
        state=GameState.load(prefs);
        events=EventRepository.load(this);
        showWelcome();
    }

    private int dp(int x){return Math.round(x*getResources().getDisplayMetrics().density);}
    private String money(long v){return nf.format(v)+" تومان";}
    private GradientDrawable bg(int color,float radius){
        GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp((int)radius)); return g;
    }
    private TextView tv(String t,int s,int c,boolean bold){
        TextView v=new TextView(this); v.setText(t); v.setTextSize(s); v.setTextColor(c); v.setGravity(Gravity.RIGHT);
        v.setTextDirection(View.TEXT_DIRECTION_RTL); v.setPadding(dp(14),dp(7),dp(14),dp(7));
        if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return v;
    }
    private Button btn(String t,int color,int textColor){
        Button b=new Button(this); b.setText(t); b.setTextSize(16); b.setTextColor(textColor); b.setAllCaps(false);
        b.setGravity(Gravity.CENTER); b.setTextDirection(View.TEXT_DIRECTION_RTL); b.setBackground(bg(color,18));
        b.setPadding(dp(12),dp(13),dp(12),dp(13));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(6),0,dp(6)); b.setLayoutParams(p); return b;
    }
    private LinearLayout card(int color){
        LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(dp(14),dp(13),dp(14),dp(13)); c.setBackground(bg(color,20)); c.setElevation(dp(2));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(7),0,dp(7)); c.setLayoutParams(p); return c;
    }
    private void animateIn(View v){ AlphaAnimation a=new AlphaAnimation(0f,1f); a.setDuration(350); v.startAnimation(a); }

    private void showWelcome(){
        ScrollView sc=new ScrollView(this); LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(20),dp(44),dp(20),dp(30)); box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); box.setBackgroundColor(BG); sc.addView(box);

        TextView mini=tv("یک داستان مالی ایرانی",14,GOLD,true); mini.setGravity(Gravity.CENTER); box.addView(mini);
        TextView logo=tv("مسیر ثروت",36,NAVY,true); logo.setGravity(Gravity.CENTER); box.addView(logo);
        TextView sub=tv("تصمیم بگیر · نتیجه‌اش را زندگی کن",17,MUTED,false); sub.setGravity(Gravity.CENTER); box.addView(sub);

        LinearLayout story=card(NAVY);
        TextView year=tv("تهران · ۱۴۰۵",15,GOLD,true); story.addView(year);
        TextView intro=tv("۲۵ سالته. ۶۰ میلیون تومان پول نقد داری، یک شغل معمولی و یک سؤال بزرگ: آیا می‌توانی قبل از ۴۰ سالگی به جایی برسی که پول برایت تصمیم نگیرد؟\n\nهر ماه یک اتفاق واقعی جلویت قرار می‌گیرد. انتخاب‌هایت روی آینده داستان اثر می‌گذارند.",18,WHITE,false);
        intro.setLineSpacing(dp(4),1f); story.addView(intro); box.addView(story);

        if(!prefs.contains("career")){
            Button start=btn("شروع داستان  ←",GOLD,NAVY); start.setOnClickListener(v->showCareerSelection()); box.addView(start);
        } else {
            TextView profile=tv("مسیر فعلی: "+prefs.getString("career",""),15,NAVY,true); profile.setGravity(Gravity.CENTER); box.addView(profile);
            Button start=btn("ادامه داستان",GOLD,NAVY); start.setOnClickListener(v->showGame()); box.addView(start);
            Button newGame=btn("شروع دوباره",SOFT,TEXT); newGame.setOnClickListener(v->confirmReset()); box.addView(newGame);
        }
        setContentView(sc); animateIn(box);
    }

    private void showCareerSelection(){
        ScrollView sc=new ScrollView(this); LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(18),dp(34),dp(18),dp(25)); box.setBackgroundColor(BG); box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); sc.addView(box);
        TextView h=tv("داستانت از کجا شروع می‌شود؟",27,NAVY,true); box.addView(h);
        TextView p=tv("یکی را انتخاب کن. مسیرها سختی و فرصت‌های متفاوتی دارند.",16,MUTED,false); box.addView(p);
        addCareer(box,"کارمند شرکت","درآمد ثابت‌تر · رشد آهسته‌تر","💼",35_000_000L,0L,60_000_000L);
        addCareer(box,"فریلنسر","درآمد متغیر · آزادی بیشتر","💻",24_000_000L,12_000_000L,55_000_000L);
        addCareer(box,"کارآفرین تازه‌کار","ریسک بیشتر · شانس رشد بالاتر","🚀",20_000_000L,10_000_000L,75_000_000L);
        Button back=btn("بازگشت",SOFT,TEXT); back.setOnClickListener(v->showWelcome()); box.addView(back);
        setContentView(sc); animateIn(box);
    }

    private void addCareer(LinearLayout parent,String title,String sub,String icon,long salary,long side,long cash){
        LinearLayout c=card(WHITE); c.setClickable(true);
        TextView t=tv(icon+"  "+title,20,NAVY,true); c.addView(t); TextView s=tv(sub,15,MUTED,false); c.addView(s);
        c.setOnClickListener(v->{ prefs.edit().clear().apply(); state=new GameState(); state.salary=salary; state.sideIncome=side; state.cash=cash; prefs.edit().putString("career",title).apply(); state.save(prefs); showGame(); });
        parent.addView(c);
    }

    private void confirmReset(){
        new AlertDialog.Builder(this).setTitle("شروع دوباره؟").setMessage("تمام تصمیم‌ها و پیشرفت فعلی پاک می‌شود.")
                .setNegativeButton("نه",null).setPositiveButton("شروع دوباره",(d,w)->{prefs.edit().clear().apply();state=new GameState();showCareerSelection();}).show();
    }

    private void showGame(){
        ScrollView sc=new ScrollView(this); root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(14),dp(16),dp(14),dp(28)); root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); root.setBackgroundColor(BG); sc.addView(root);

        LinearLayout top=new LinearLayout(this); top.setOrientation(LinearLayout.HORIZONTAL); top.setGravity(Gravity.CENTER_VERTICAL); top.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView title=tv("مسیر ثروت",24,NAVY,true); top.addView(title,new LinearLayout.LayoutParams(0,-2,1));
        monthText=tv("",15,GOLD,true); top.addView(monthText); root.addView(top);

        LinearLayout chapter=card(NAVY); chapterText=tv("",17,GOLD,true); missionText=tv("",14,WHITE,false); chapterProgress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal); chapterProgress.setMax(6); chapterProgress.setProgressTintList(android.content.res.ColorStateList.valueOf(GOLD));
        chapter.addView(chapterText); chapter.addView(missionText); chapter.addView(chapterProgress,new LinearLayout.LayoutParams(-1,dp(8))); root.addView(chapter);

        LinearLayout stats=card(WHITE);
        cashText=tv("",20,NAVY,true); flowText=tv("",16,TEXT,true); realWorthText=tv("",14,MUTED,false); powerText=tv("",14,MUTED,false);
        healthText=tv("",15,NAVY,true); healthProgress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal); healthProgress.setMax(100); healthProgress.setProgressTintList(android.content.res.ColorStateList.valueOf(GREEN));
        stats.addView(cashText); stats.addView(flowText); stats.addView(realWorthText); stats.addView(powerText); stats.addView(healthText); stats.addView(healthProgress,new LinearLayout.LayoutParams(-1,dp(8))); root.addView(stats);

        mentorText=tv("",15,TEXT,false); mentorText.setBackground(bg(Color.rgb(239,233,216),16)); mentorText.setPadding(dp(14),dp(11),dp(14),dp(11)); LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,-2); mp.setMargins(0,dp(6),0,dp(8)); mentorText.setLayoutParams(mp); root.addView(mentorText);

        LinearLayout ev=card(WHITE);
        TextView label=tv("اتفاق این ماه",13,GOLD,true); ev.addView(label);
        eventTitle=tv("",23,NAVY,true); ev.addView(eventTitle);
        eventDesc=tv("",17,TEXT,false); eventDesc.setLineSpacing(dp(5),1f); ev.addView(eventDesc);
        choicesBox=new LinearLayout(this); choicesBox.setOrientation(LinearLayout.VERTICAL); choicesBox.setPadding(0,dp(8),0,0); ev.addView(choicesBox);
        lessonText=tv("",15,GREEN,true); lessonText.setVisibility(View.GONE); ev.addView(lessonText);
        root.addView(ev);

        resultCard=card(Color.rgb(235,247,240)); resultCard.setVisibility(View.GONE); resultText=tv("",15,TEXT,false); resultText.setLineSpacing(dp(3),1f); resultCard.addView(tv("نتیجه انتخابت",17,GREEN,true)); resultCard.addView(resultText); root.addView(resultCard);

        nextButton=btn("برو ماه بعد  ←",GOLD,NAVY); nextButton.setOnClickListener(v->{state.settleMonth();state.save(prefs);render();sc.smoothScrollTo(0,0);}); root.addView(nextButton);
        Button details=btn("جزئیات مالی",SOFT,TEXT); details.setOnClickListener(v->showBalanceSheet()); root.addView(details);
        Button home=btn("صفحه اول",Color.rgb(226,229,233),TEXT); home.setOnClickListener(v->{state.save(prefs);showWelcome();}); root.addView(home);
        setContentView(sc); render(); animateIn(root);
    }

    private int financialHealth(){
        int score=50;
        if(state.cashFlow()>0) score+=15; else score-=15;
        if(state.debt==0) score+=10; else if(state.debt>state.monthlyIncome()*4) score-=15;
        double months=state.essentialExpenses()==0?0:(double)state.emergencyFund/state.essentialExpenses();
        score+=(int)Math.min(20,months*4);
        if(state.realNetWorth()>90_000_000L) score+=5;
        return Math.max(0,Math.min(100,score));
    }

    private void render(){
        monthText.setText("ماه "+nf.format(state.month));
        chapterText.setText(StoryRepository.chapterName(state.month));
        missionText.setText(StoryRepository.chapterMission(state.month));
        chapterProgress.setProgress(((state.month-1)%6)+1);
        cashText.setText("💰  "+money(state.cash));
        flowText.setText("جریان نقدی ماهانه: "+money(state.cashFlow())); flowText.setTextColor(state.cashFlow()>=0?GREEN:RED);
        realWorthText.setText("ثروت واقعی: "+money(state.realNetWorth()));
        powerText.setText("قدرت خرید: "+String.format(new Locale("fa","IR"),"%.1f%%",state.purchasingPower()));
        int health=financialHealth(); healthText.setText("سلامت مالی  "+nf.format(health)+" از ۱۰۰"); healthProgress.setProgress(health);
        mentorText.setText(StoryRepository.mentor(state.month));

        EventData e=events.get((state.month-1)%events.size());
        eventTitle.setText(e.title);
        eventDesc.setText(StoryRepository.story(e.id,prefs.getString("career","کارت")));
        choicesBox.removeAllViews(); lessonText.setVisibility(View.GONE); resultCard.setVisibility(View.GONE);

        int i=1;
        for(EventData.Choice c:e.choices){
            Button b=btn(nf.format(i)+"  ·  "+c.title,state.choiceMade?Color.rgb(220,224,228):NAVY,state.choiceMade?MUTED:WHITE);
            b.setEnabled(!state.choiceMade); b.setOnClickListener(v->choose(e,c)); choicesBox.addView(b); i++;
        }
        nextButton.setVisibility(state.choiceMade?View.VISIBLE:View.GONE);
        if(state.choiceMade){
            lessonText.setText("انتخاب ثبت شد. ماه بعد، اثرش را در اعداد زندگی‌ات می‌بینی.");
            lessonText.setVisibility(View.VISIBLE);
        }
    }

    private void choose(EventData e,EventData.Choice c){
        if(state.choiceMade)return;
        long cash0=state.cash,debt0=state.debt,flow0=state.cashFlow(),net0=state.netWorth();
        String lesson=EventEngine.apply(state,e.id,c.id);
        long dc=state.cash-cash0, dd=state.debt-debt0, df=state.cashFlow()-flow0, dn=state.netWorth()-net0;
        state.choiceMade=true; state.save(prefs); render();
        StringBuilder x=new StringBuilder();
        x.append("تو انتخاب کردی: «").append(c.title).append("»\n\n");
        if(dc!=0)x.append("پول نقد: ").append(signed(dc)).append("\n");
        if(dd!=0)x.append("بدهی: ").append(signed(dd)).append("\n");
        if(df!=0)x.append("جریان نقدی ماهانه: ").append(signed(df)).append("\n");
        if(dn!=0)x.append("دارایی خالص: ").append(signed(dn)).append("\n");
        if(dc==0&&dd==0&&df==0&&dn==0)x.append("اثر مالی فوری ندارد؛ نتیجه این انتخاب ممکن است بعداً ظاهر شود.\n");
        x.append("\nیادگیری: ").append(lesson);
        resultText.setText(x.toString()); resultCard.setVisibility(View.VISIBLE); animateIn(resultCard);
    }

    private String signed(long v){return (v>0?"+":"")+money(v);}

    private void showBalanceSheet(){
        String text="درآمد ماهانه: "+money(state.monthlyIncome())+"\nهزینه ماهانه: "+money(state.monthlyExpenses())+"\n\nپول نقد: "+money(state.cash)+"\nصندوق اضطراری: "+money(state.emergencyFund)+"\nطلا: "+money(state.gold)+"\nصندوق/سهام: "+money(state.funds)+"\nکسب‌وکار: "+money(state.business)+"\n\nبدهی کل: "+money(state.debt)+"\nقسط ماهانه: "+money(state.debtPayment)+"\n\nدارایی خالص: "+money(state.netWorth())+"\nثروت واقعی: "+money(state.realNetWorth());
        new AlertDialog.Builder(this).setTitle("داشبورد مالی").setMessage(text).setPositiveButton("بستن",null).show();
    }
}
