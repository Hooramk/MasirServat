package ir.masirservat.app;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.text.NumberFormat;
import java.util.*;

public class MainActivity extends Activity {
    private static final int NAVY=Color.rgb(7,27,50), GOLD=Color.rgb(200,164,93), BG=Color.rgb(244,246,249), RED=Color.rgb(166,45,45), GREEN=Color.rgb(22,115,73);
    private GameState state; private List<EventData> events; private SharedPreferences prefs;
    private LinearLayout root,choicesBox;
    private TextView monthText,cashText,incomeText,expenseText,netWorthText,realWorthText,powerText,eventTitle,eventDesc,lessonText,flowText;
    private Button nextButton;
    private final NumberFormat nf=NumberFormat.getInstance(new Locale("fa","IR"));

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(NAVY);
        prefs=getSharedPreferences("game",MODE_PRIVATE);
        state=GameState.load(prefs);
        events=EventRepository.load(this);
        showWelcome();
    }

    private int dp(int x){return Math.round(x*getResources().getDisplayMetrics().density);}
    private String money(long v){return nf.format(v)+" تومان";}
    private TextView tv(String t,int s,int c,boolean b){
        TextView v=new TextView(this); v.setText(t); v.setTextSize(s); v.setTextColor(c);
        v.setGravity(Gravity.RIGHT); v.setTextDirection(View.TEXT_DIRECTION_RTL);
        v.setPadding(dp(14),dp(8),dp(14),dp(8)); if(b)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return v;
    }
    private Button btn(String t){
        Button b=new Button(this); b.setText(t); b.setTextSize(16); b.setTextColor(Color.WHITE);
        b.setBackgroundColor(NAVY); b.setAllCaps(false); b.setGravity(Gravity.CENTER); b.setTextDirection(View.TEXT_DIRECTION_RTL);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(6),0,dp(6)); b.setLayoutParams(p); return b;
    }
    private View line(){View v=new View(this);v.setBackgroundColor(Color.rgb(225,228,232));v.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(1)));return v;}

    private void showWelcome(){
        ScrollView sc=new ScrollView(this); LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(22),dp(50),dp(22),dp(30)); box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); box.setBackgroundColor(BG); sc.addView(box);
        TextView logo=tv("مسیر ثروت",34,NAVY,true); logo.setGravity(Gravity.CENTER); box.addView(logo);
        TextView sub=tv("بازی سواد مالی برای زندگی واقعی در ایران",18,GOLD,true); sub.setGravity(Gravity.CENTER); box.addView(sub);
        TextView intro=tv("از ۲۵ سالگی شروع کن. هر ماه با یک تصمیم مالی روبه‌رو می‌شوی: تورم، اجاره، وام، طلا، درآمد دوم، هزینه‌های ناگهانی و کسب‌وکار. هدف، رشد ثروت واقعی و رسیدن به استقلال مالی است.",17,NAVY,false);
        intro.setPadding(dp(10),dp(28),dp(10),dp(26)); box.addView(intro);
        Button start=btn(prefs.contains("month")?"ادامه بازی":"شروع بازی"); start.setBackgroundColor(GOLD); start.setTextColor(NAVY); start.setOnClickListener(v->showGame()); box.addView(start);
        Button reset=btn("شروع بازی جدید"); reset.setBackgroundColor(Color.DKGRAY); reset.setOnClickListener(v->{prefs.edit().clear().apply();state=new GameState();showGame();}); box.addView(reset);
        setContentView(sc);
    }

    private void showGame(){
        ScrollView sc=new ScrollView(this); root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(14),dp(18),dp(14),dp(28));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); root.setBackgroundColor(BG); sc.addView(root);
        LinearLayout top=new LinearLayout(this); top.setOrientation(LinearLayout.HORIZONTAL); top.setGravity(Gravity.CENTER_VERTICAL); top.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView title=tv("مسیر ثروت",25,NAVY,true); top.addView(title,new LinearLayout.LayoutParams(0,-2,1)); monthText=tv("",16,GOLD,true); top.addView(monthText); root.addView(top); root.addView(line());

        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(10),dp(8),dp(10),dp(8)); card.setBackgroundColor(Color.WHITE);
        cashText=tv("",18,NAVY,true); incomeText=tv("",15,NAVY,false); expenseText=tv("",15,NAVY,false); flowText=tv("",17,NAVY,true); netWorthText=tv("",15,NAVY,false); realWorthText=tv("",15,NAVY,false); powerText=tv("",15,NAVY,false);
        card.addView(cashText);card.addView(incomeText);card.addView(expenseText);card.addView(flowText);card.addView(netWorthText);card.addView(realWorthText);card.addView(powerText);root.addView(card);

        TextView section=tv("تصمیم این ماه",20,NAVY,true); section.setPadding(dp(8),dp(22),dp(8),dp(6)); root.addView(section);
        LinearLayout evCard=new LinearLayout(this); evCard.setOrientation(LinearLayout.VERTICAL); evCard.setPadding(dp(12),dp(10),dp(12),dp(12)); evCard.setBackgroundColor(Color.WHITE);
        eventTitle=tv("",21,GOLD,true); eventDesc=tv("",17,NAVY,false); choicesBox=new LinearLayout(this); choicesBox.setOrientation(LinearLayout.VERTICAL); lessonText=tv("",15,GREEN,true); lessonText.setVisibility(View.GONE);
        evCard.addView(eventTitle);evCard.addView(eventDesc);evCard.addView(choicesBox);evCard.addView(lessonText);root.addView(evCard);

        nextButton=btn("ادامه به ماه بعد"); nextButton.setBackgroundColor(GOLD); nextButton.setTextColor(NAVY); nextButton.setOnClickListener(v->{state.settleMonth();state.save(prefs);render();}); root.addView(nextButton);
        Button info=btn("دارایی‌ها و بدهی‌ها"); info.setBackgroundColor(Color.rgb(60,72,88)); info.setOnClickListener(v->showBalanceSheet()); root.addView(info);
        Button home=btn("بازگشت به صفحه اول"); home.setBackgroundColor(Color.rgb(100,105,112)); home.setOnClickListener(v->{state.save(prefs);showWelcome();}); root.addView(home);
        setContentView(sc); render();
    }

    private void render(){
        monthText.setText("ماه "+nf.format(state.month));
        cashText.setText("موجودی نقد: "+money(state.cash));
        incomeText.setText("درآمد ماهانه: "+money(state.monthlyIncome()));
        expenseText.setText("هزینه ماهانه: "+money(state.monthlyExpenses()));
        flowText.setText("جریان نقدی: "+money(state.cashFlow())); flowText.setTextColor(state.cashFlow()>=0?GREEN:RED);
        netWorthText.setText("دارایی خالص: "+money(state.netWorth()));
        realWorthText.setText("ثروت واقعی به قیمت شروع بازی: "+money(state.realNetWorth()));
        powerText.setText("قدرت خرید نسبت به شروع: "+String.format(new Locale("fa","IR"),"%.1f%%",state.purchasingPower()));

        EventData e=events.get((state.month-1)%events.size());
        eventTitle.setText(e.title);eventDesc.setText(e.description);choicesBox.removeAllViews();lessonText.setVisibility(View.GONE);
        for(EventData.Choice c:e.choices){
            Button b=btn(c.title); if(state.choiceMade){b.setEnabled(false);b.setAlpha(.55f);}
            b.setOnClickListener(v->choose(e,c)); choicesBox.addView(b);
        }
        nextButton.setVisibility(state.choiceMade?View.VISIBLE:View.GONE);
    }

    private void choose(EventData e,EventData.Choice c){
        if(state.choiceMade)return;
        String lesson=EventEngine.apply(state,e.id,c.id); state.choiceMade=true; state.save(prefs); render();
        lessonText.setText("نکته مالی: "+lesson); lessonText.setVisibility(View.VISIBLE);
    }

    private void showBalanceSheet(){
        String text="پول نقد: "+money(state.cash)+"\nصندوق اضطراری: "+money(state.emergencyFund)+"\nطلا: "+money(state.gold)+"\nصندوق/سهام: "+money(state.funds)+"\nکسب‌وکار: "+money(state.business)+"\n\nبدهی کل: "+money(state.debt)+"\nقسط ماهانه: "+money(state.debtPayment)+"\n\nدارایی خالص: "+money(state.netWorth());
        new AlertDialog.Builder(this).setTitle("ترازنامه مالی").setMessage(text).setPositiveButton("بستن",null).show();
    }
}
