package ir.masirservat.app;

import android.app.*;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;

import java.text.NumberFormat;
import java.util.*;

public class MainActivity extends Activity {
    private static final int NAVY = Color.rgb(7,27,50);
    private static final int NAVY_2 = Color.rgb(18,48,78);
    private static final int GOLD = Color.rgb(200,164,93);
    private static final int BG = Color.rgb(244,246,249);
    private static final int TEXT = Color.rgb(28,39,52);
    private static final int MUTED = Color.rgb(100,111,123);
    private static final int RED = Color.rgb(176,55,55);
    private static final int GREEN = Color.rgb(25,130,84);
    private static final int LINE = Color.rgb(226,230,235);

    private GameState state;
    private List<EventData> events;
    private SharedPreferences prefs;
    private final NumberFormat nf = NumberFormat.getInstance(new Locale("fa","IR"));

    private LinearLayout root;
    private LinearLayout choicesBox;
    private TextView chapterText, monthText, playerText, healthText;
    private TextView cashText, flowText, worthText, eventTitle, eventDesc, speakerText;
    private LinearLayout resultCard;
    private TextView resultTitle, resultText;
    private ProgressBar storyProgress, healthProgress;
    private Button nextButton;

    private String pendingProfile = "کارمند";
    private int pendingAvatar = 1;
    private String lastOutcome = null;
    private String lastImpact = null;
    private CharacterAvatarView playerAvatar, speakerAvatar;
    private TextView personalityText, levelText, missionText, traitText, achievementText, locationText;
    private TextView moneyMeter, skillMeter, freedomMeter, socialMeter;

    // v0.5 visual-novel gameplay
    private SceneView vnScene;
    private TextView vnDate, vnLocation, vnSpeaker, vnTitle, vnDialog;
    private TextView vnMoney, vnSkill, vnFreedom, vnSocial;
    private LinearLayout vnChoices, vnResult;
    private TextView vnResultText;
    private Button vnNext;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        prefs = getSharedPreferences("game", MODE_PRIVATE);
        state = GameState.load(prefs);
        events = EventRepository.load(this);
        showWelcome();
    }

    private int dp(int x) {
        return Math.round(x * getResources().getDisplayMetrics().density);
    }

    private String money(long v) {
        String sign = v < 0 ? "−" : "";
        return sign + nf.format(Math.abs(v)) + " تومان";
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private GradientDrawable bordered(int color, int radius, int strokeColor) {
        GradientDrawable d = bg(color, radius);
        d.setStroke(dp(1), strokeColor);
        return d;
    }

    private GradientDrawable heroBg() {
        GradientDrawable d = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{NAVY, NAVY_2});
        d.setCornerRadius(dp(28));
        return d;
    }

    private TextView tv(String t, int s, int c, boolean bold) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextSize(s);
        v.setTextColor(c);
        v.setGravity(Gravity.RIGHT);
        v.setTextDirection(View.TEXT_DIRECTION_RTL);
        v.setLineSpacing(0, 1.14f);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    private TextView pill(String t, int fg, int bgColor) {
        TextView v = tv(t, 13, fg, true);
        v.setGravity(Gravity.CENTER);
        v.setPadding(dp(12), dp(6), dp(12), dp(6));
        v.setBackground(bg(bgColor, 18));
        return v;
    }

    private Button button(String t, int bgColor, int fg) {
        Button b = new Button(this);
        b.setText(t);
        b.setTextSize(16);
        b.setTextColor(fg);
        b.setAllCaps(false);
        b.setGravity(Gravity.CENTER);
        b.setTextDirection(View.TEXT_DIRECTION_RTL);
        b.setPadding(dp(12), dp(8), dp(12), dp(8));
        b.setBackground(bg(bgColor, 14));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(54));
        p.setMargins(0, dp(6), 0, dp(6));
        b.setLayoutParams(p);
        return b;
    }

    private Button outlineButton(String t) {
        Button b = button(t, Color.WHITE, NAVY);
        b.setBackground(bordered(Color.WHITE, 14, Color.rgb(205,213,221)));
        return b;
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(16), dp(15), dp(16), dp(15));
        c.setBackground(bg(Color.WHITE, 18));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(7), 0, dp(7));
        c.setLayoutParams(p);
        c.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        return c;
    }

    private View spacer(int h) {
        Space s = new Space(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(1, dp(h)));
        return s;
    }

    private void basePage(ScrollView sc, LinearLayout box) {
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(20), dp(16), dp(30));
        box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        box.setBackgroundColor(BG);
        sc.addView(box);
    }

    private void showWelcome() {
        ScrollView sc = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        basePage(sc, box);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(20), dp(24), dp(20), dp(24));
        hero.setBackground(heroBg());
        hero.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView season = pill("نسخه ۰.۵ · Visual Story", NAVY, GOLD);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-2, -2);
        sp.gravity = Gravity.RIGHT;
        season.setLayoutParams(sp);
        hero.addView(season);
        hero.addView(spacer(12));

        LinearLayout introRow = new LinearLayout(this);
        introRow.setOrientation(LinearLayout.HORIZONTAL);
        introRow.setGravity(Gravity.CENTER_VERTICAL);
        introRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        CharacterAvatarView coverAvatar = new CharacterAvatarView(this);
        coverAvatar.setCharacter(state.profileChosen ? state.avatarStyle : 4, "player");
        introRow.addView(coverAvatar, new LinearLayout.LayoutParams(dp(100), dp(118)));

        LinearLayout introText = new LinearLayout(this);
        introText.setOrientation(LinearLayout.VERTICAL);
        TextView title = tv("مسیر ثروت", 34, Color.WHITE, true);
        TextView sub = tv("۱۸ سالته؛ آینده‌ات از همین انتخاب‌های کوچک ساخته می‌شود.", 17, Color.rgb(223,229,236), false);
        sub.setPadding(0, dp(7), 0, 0);
        introText.addView(title);
        introText.addView(sub);
        introRow.addView(introText, new LinearLayout.LayoutParams(0, -2, 1));
        hero.addView(introRow);
        box.addView(hero);

        LinearLayout story = card();
        story.addView(tv("تهران، ۱۴۰۵", 16, GOLD, true));
        TextView intro = tv(
                "کاراکتر خودت را می‌سازی و از ۱۸ سالگی وارد یک زندگی ایرانی می‌شوی: دانشگاه، اولین درآمد، گوشی قسطی، فریلنس، سفر، کریپتو، همخانه، مهارت و استقلال. هر انتخاب روی پول، مهارت، آزادی و اعتبار اجتماعی تو اثر دارد.",
                17, TEXT, false);
        intro.setPadding(0, dp(8), 0, dp(4));
        story.addView(intro);
        box.addView(story);

        if (state.profileChosen && prefs.contains("month")) {
            Button cont = button("ادامه بازی  ←", GOLD, NAVY);
            cont.setOnClickListener(v -> {
                if (!state.personalityChosen) showPersonalityQuiz(0,0,0,0);
                else if (state.month > 30) showEnding();
                else showGame();
            });
            box.addView(cont);

            String p = state.personalityChosen ? " · " + state.personalityType : "";
            TextView save = tv("ذخیره: " + safeName() + " · " + state.profession + p + " · ماه " + nf.format(state.month), 14, MUTED, false);
            save.setGravity(Gravity.CENTER);
            save.setPadding(0, dp(3), 0, dp(10));
            box.addView(save);
        }

        Button fresh = outlineButton(state.profileChosen ? "ساخت کاراکتر جدید" : "ساخت کاراکتر");
        fresh.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            state = new GameState();
            pendingProfile = "کارمند";
            pendingAvatar = 1;
            showSetup();
        });
        box.addView(fresh);

        setContentView(sc);
    }

    private String safeName() {
        return state.playerName == null || state.playerName.trim().isEmpty() ? "بازیکن" : state.playerName.trim();
    }

    private void showSetup() {
        ScrollView sc = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        basePage(sc, box);

        TextView step = pill("مرحله ۱ از ۳", Color.WHITE, NAVY);
        LinearLayout.LayoutParams spp = new LinearLayout.LayoutParams(-2, -2);
        spp.gravity = Gravity.RIGHT;
        step.setLayoutParams(spp);
        box.addView(step);
        box.addView(spacer(12));

        box.addView(tv("کاراکترت را بساز", 28, NAVY, true));
        TextView hint = tv("اسم، ظاهر و مسیر شغلی را انتخاب کن. بعد بازی تیپ مالی تو را پیدا می‌کند.", 16, MUTED, false);
        hint.setPadding(0, dp(6), 0, dp(14));
        box.addView(hint);

        LinearLayout nameCard = card();
        nameCard.addView(tv("اسمت چیست؟", 16, TEXT, true));
        EditText name = new EditText(this);
        name.setHint("مثلاً هورام");
        name.setTextColor(TEXT);
        name.setHintTextColor(Color.rgb(150,157,165));
        name.setTextSize(17);
        name.setSingleLine(true);
        name.setInputType(InputType.TYPE_CLASS_TEXT);
        name.setPadding(dp(12), dp(8), dp(12), dp(8));
        name.setBackground(bordered(Color.rgb(249,250,252), 12, LINE));
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, dp(52));
        np.setMargins(0, dp(8), 0, 0);
        name.setLayoutParams(np);
        nameCard.addView(name);
        box.addView(nameCard);

        LinearLayout avatarCard = card();
        avatarCard.addView(tv("ظاهر کاراکتر", 16, TEXT, true));
        TextView ah = tv("یکی را انتخاب کن؛ بعداً می‌توانیم لباس و آیتم‌های بیشتری هم باز کنیم.", 13, MUTED, false);
        ah.setPadding(0,dp(3),0,dp(8));
        avatarCard.addView(ah);

        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.setHorizontalScrollBarEnabled(false);
        LinearLayout avatars = new LinearLayout(this);
        avatars.setOrientation(LinearLayout.HORIZONTAL);
        avatars.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        for (int i=1;i<=4;i++) {
            final int style=i;
            LinearLayout choice=new LinearLayout(this);
            choice.setOrientation(LinearLayout.VERTICAL);
            choice.setGravity(Gravity.CENTER);
            choice.setPadding(dp(6),dp(4),dp(6),dp(4));
            CharacterAvatarView av=new CharacterAvatarView(this);
            av.setCharacter(style,"player");
            choice.addView(av,new LinearLayout.LayoutParams(dp(92),dp(108)));
            TextView label=tv("کاراکتر "+nf.format(i),13,NAVY,true);
            label.setGravity(Gravity.CENTER);
            choice.addView(label);
            choice.setOnClickListener(v -> {
                pendingAvatar=style;
                Toast.makeText(this,"کاراکتر "+nf.format(style)+" انتخاب شد",Toast.LENGTH_SHORT).show();
            });
            avatars.addView(choice,new LinearLayout.LayoutParams(dp(108),dp(142)));
        }
        hsv.addView(avatars);
        avatarCard.addView(hsv);
        box.addView(avatarCard);

        LinearLayout profileCard = card();
        profileCard.addView(tv("مسیر شغلی", 16, TEXT, true));
        TextView ph = tv("هر مسیر درآمد و ریسک متفاوتی دارد.", 14, MUTED, false);
        ph.setPadding(0, dp(4), 0, dp(8));
        profileCard.addView(ph);

        RadioGroup group = new RadioGroup(this);
        group.setOrientation(RadioGroup.VERTICAL);
        group.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        RadioButton employee = radio("کارمند  ·  درآمد ثابت، رشد آهسته‌تر", true);
        RadioButton freelancer = radio("فریلنسر  ·  درآمد بیشتر، نوسان بالاتر", false);
        RadioButton owner = radio("صاحب کسب‌وکار  ·  سرمایه بیشتر، ریسک بیشتر", false);

        employee.setId(101); freelancer.setId(102); owner.setId(103);
        group.addView(employee); group.addView(freelancer); group.addView(owner);
        group.setOnCheckedChangeListener((g, id) -> {
            if (id == 102) pendingProfile = "فریلنسر";
            else if (id == 103) pendingProfile = "صاحب کسب‌وکار";
            else pendingProfile = "کارمند";
        });
        profileCard.addView(group);
        box.addView(profileCard);

        Button go = button("ادامه به تست شخصیت  ←", GOLD, NAVY);
        go.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            state.playerName = n.isEmpty() ? "بازیکن" : n;
            state.avatarStyle = pendingAvatar;
            state.applyProfile(pendingProfile);
            state.save(prefs);
            showPersonalityQuiz(0,0,0,0);
        });
        box.addView(go);

        Button back = outlineButton("بازگشت");
        back.setOnClickListener(v -> showWelcome());
        box.addView(back);

        setContentView(sc);
    }

    private void showPersonalityQuiz(int q, int risk, int disciplineScore, int calmScore) {
        final String[] questions = {
                "یک مبلغ اضافه دستت آمده. اولین واکنشت چیست؟",
                "سرمایه‌گذاری‌ات ناگهان ۱۵٪ افت کرده. چه می‌کنی؟",
                "یک فرصت کاری پردرآمد اما نامطمئن پیشنهاد شده.",
                "دوستانت برای یک خرید گران هیجان‌زده‌اند.",
                "برای یک تصمیم مالی بزرگ معمولاً به چه چیزی تکیه می‌کنی؟"
        };
        final String[] a = {
                "اول بخشی را برای آینده کنار می‌گذارم",
                "دلیل افت را بررسی می‌کنم و عجله نمی‌کنم",
                "عددها را بررسی می‌کنم و اگر منطقی بود امتحان می‌کنم",
                "بودجه‌ام را نگاه می‌کنم، بعد تصمیم می‌گیرم",
                "مقایسه، عدد و سناریوی بدبینانه"
        };
        final String[] b = {
                "حالا که آمده، ازش لذت می‌برم",
                "می‌ترسم بیشتر بریزد؛ سریع می‌فروشم",
                "یا سریع می‌پرم وسطش یا کلاً ردش می‌کنم",
                "احتمالاً همراه جمع می‌شوم",
                "حس لحظه و شهود"
        };

        if (q >= questions.length) {
            String type;
            if (risk >= 3 && disciplineScore >= 2) type="فرصت‌جو";
            else if (risk >= 3) type="جسور";
            else if (disciplineScore >= 4 && calmScore >= 2) type="استراتژیست";
            else if (calmScore <= 1 && disciplineScore <= 2) type="احساسی";
            else type="محافظ";

            CharacterSystem.applyPersonality(state,type);
            state.personalityChosen=true;
            state.save(prefs);
            showPersonalityResult();
            return;
        }

        ScrollView sc=new ScrollView(this);
        LinearLayout box=new LinearLayout(this);
        basePage(sc,box);

        TextView step=pill("مرحله ۲ از ۳ · سؤال "+nf.format(q+1)+" از ۵",Color.WHITE,NAVY);
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-2,-2);
        pp.gravity=Gravity.RIGHT; step.setLayoutParams(pp);
        box.addView(step);
        box.addView(spacer(14));

        CharacterAvatarView av=new CharacterAvatarView(this);
        av.setCharacter(state.avatarStyle,"player");
        LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(dp(125),dp(145));
        ap.gravity=Gravity.CENTER;
        av.setLayoutParams(ap);
        box.addView(av);

        TextView qt=tv(questions[q],23,NAVY,true);
        qt.setGravity(Gravity.CENTER);
        qt.setPadding(dp(8),dp(6),dp(8),dp(16));
        box.addView(qt);

        Button ba=outlineButton(a[q]);
        ba.setOnClickListener(v -> {
            int nr=risk, nd=disciplineScore+1, nc=calmScore+1;
            if(q==2) nr++;
            showPersonalityQuiz(q+1,nr,nd,nc);
        });
        box.addView(ba);

        Button bb=outlineButton(b[q]);
        bb.setOnClickListener(v -> showPersonalityQuiz(q+1,risk+1,disciplineScore,Math.max(0,calmScore-1)));
        box.addView(bb);

        setContentView(sc);
    }

    private void showPersonalityResult() {
        ScrollView sc=new ScrollView(this);
        LinearLayout box=new LinearLayout(this);
        basePage(sc,box);

        LinearLayout hero=new LinearLayout(this);
        hero.setOrientation(LinearLayout.HORIZONTAL);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(dp(18),dp(20),dp(18),dp(20));
        hero.setBackground(heroBg());
        hero.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        CharacterAvatarView av=new CharacterAvatarView(this);
        av.setCharacter(state.avatarStyle,"player");
        hero.addView(av,new LinearLayout.LayoutParams(dp(115),dp(135)));

        LinearLayout txt=new LinearLayout(this);
        txt.setOrientation(LinearLayout.VERTICAL);
        txt.addView(tv("تیپ مالی تو",14,GOLD,true));
        txt.addView(tv(state.personalityType,29,Color.WHITE,true));
        TextView desc=tv(CharacterSystem.description(state),15,Color.rgb(224,231,238),false);
        desc.setPadding(0,dp(7),0,0);
        txt.addView(desc);
        hero.addView(txt,new LinearLayout.LayoutParams(0,-2,1));
        box.addView(hero);

        LinearLayout traits=card();
        traits.addView(tv("ویژگی‌های شروع",18,NAVY,true));
        traits.addView(traitRow("انضباط",state.discipline));
        traits.addView(traitRow("جسارت",state.courage));
        traits.addView(traitRow("آرامش",state.calm));
        box.addView(traits);

        Button go=button("ورود به داستان  ←",GOLD,NAVY);
        go.setOnClickListener(v -> showPrologue());
        box.addView(go);
        setContentView(sc);
    }

    private LinearLayout traitRow(String name,int value) {
        LinearLayout row=new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView n=tv(name+"  "+nf.format(value),14,TEXT,true);
        row.addView(n,new LinearLayout.LayoutParams(dp(110),dp(38)));
        ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        p.setMax(100); p.setProgress(value);
        p.setProgressTintList(ColorStateList.valueOf(GOLD));
        p.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(232,235,238)));
        row.addView(p,new LinearLayout.LayoutParams(0,dp(7),1));
        return row;
    }

    private RadioButton radio(String text, boolean checked) {
        RadioButton r = new RadioButton(this);
        r.setText(text);
        r.setTextSize(16);
        r.setTextColor(TEXT);
        r.setChecked(checked);
        r.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        r.setTextDirection(View.TEXT_DIRECTION_RTL);
        r.setPadding(dp(8), dp(9), dp(8), dp(9));
        r.setButtonTintList(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{GOLD, Color.rgb(160,168,176)}
        ));
        return r;
    }

    private void showPrologue() {
        ScrollView sc = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        basePage(sc, box);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.HORIZONTAL);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(dp(18), dp(22), dp(18), dp(22));
        hero.setBackground(heroBg());
        hero.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        CharacterAvatarView av=new CharacterAvatarView(this);
        av.setCharacter(state.avatarStyle,"player");
        hero.addView(av,new LinearLayout.LayoutParams(dp(120),dp(145)));

        LinearLayout txt=new LinearLayout(this);
        txt.setOrientation(LinearLayout.VERTICAL);
        txt.addView(pill("مرحله ۳ از ۳",NAVY,GOLD));
        TextView h=tv("صبحِ اولین ماه",28,Color.WHITE,true);
        h.setPadding(0,dp(10),0,0);
        txt.addView(h);
        TextView who=tv(safeName()+" · "+state.profession+" · "+state.personalityType,15,Color.rgb(222,230,238),false);
        who.setPadding(0,dp(5),0,0);
        txt.addView(who);
        hero.addView(txt,new LinearLayout.LayoutParams(0,-2,1));
        box.addView(hero);

        String profileLine;
        if ("فریلنسر".equals(state.profession)) {
            profileLine = "پروژه داری و درآمدت بیشتر است، اما هیچ تضمینی نیست ماه بعد هم همین مقدار باشد.";
        } else if ("صاحب کسب‌وکار".equals(state.profession)) {
            profileLine = "کسب‌وکارت فروش دارد، اما رشد سریع و ریسک افت کنار هم هستند.";
        } else {
            profileLine = "حقوقت هر ماه می‌آید، اما تورم از افزایش حقوق سریع‌تر حرکت می‌کند.";
        }

        LinearLayout story=card();
        story.addView(tv("داستان تو شروع می‌شود",19,NAVY,true));
        TextView p=tv(profileLine+"\n\n"+CharacterSystem.description(state)+"\n\nاز اینجا به بعد انتخاب‌ها روی پول، شخصیت و رابطه‌هایت اثر می‌گذارند.",17,TEXT,false);
        p.setPadding(0,dp(8),0,0);
        story.addView(p);
        box.addView(story);

        LinearLayout goal=card();
        goal.addView(tv("🎯 مأموریت اول",18,GOLD,true));
        goal.addView(tv(state.currentMission(),16,TEXT,false));
        box.addView(goal);

        Button start=button("شروع بازی",GOLD,NAVY);
        start.setOnClickListener(v -> showGame());
        box.addView(start);

        setContentView(sc);
    }

    private void showGame() {
        if (!state.personalityChosen) {
            showPersonalityQuiz(0,0,0,0);
            return;
        }
        if (state.month > 30) {
            showEnding();
            return;
        }

        ScrollView sc=new ScrollView(this);
        sc.setFillViewport(true);
        LinearLayout page=new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        page.setBackgroundColor(Color.rgb(235,239,244));
        sc.addView(page);

        // CINEMATIC STAGE
        FrameLayout stage=new FrameLayout(this);
        stage.setBackgroundColor(NAVY);
        page.addView(stage,new LinearLayout.LayoutParams(-1,dp(340)));

        vnScene=new SceneView(this);
        stage.addView(vnScene,new FrameLayout.LayoutParams(-1,-1));

        // top HUD
        LinearLayout hud=new LinearLayout(this);
        hud.setOrientation(LinearLayout.VERTICAL);
        hud.setPadding(dp(10),dp(10),dp(10),dp(8));
        hud.setBackgroundColor(Color.argb(145,7,27,50));
        FrameLayout.LayoutParams hudp=new FrameLayout.LayoutParams(-1,-2,Gravity.TOP);
        hudp.setMargins(dp(8),dp(8),dp(8),0);
        stage.addView(hud,hudp);

        LinearLayout hudTop=new LinearLayout(this);
        hudTop.setOrientation(LinearLayout.HORIZONTAL);
        hudTop.setGravity(Gravity.CENTER_VERTICAL);
        hudTop.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        vnDate=pill("",NAVY,GOLD);
        TextView profile=tv(safeName()+"  ·  "+state.personalityType,13,Color.WHITE,true);
        hudTop.addView(profile,new LinearLayout.LayoutParams(0,-2,1));
        hudTop.addView(vnDate);
        hud.addView(hudTop);

        LinearLayout meters=new LinearLayout(this);
        meters.setOrientation(LinearLayout.HORIZONTAL);
        meters.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        meters.setPadding(0,dp(8),0,0);
        vnMoney=vnHudStat("💰","پول");
        vnSkill=vnHudStat("🧠","مهارت");
        vnFreedom=vnHudStat("🕊","آزادی");
        vnSocial=vnHudStat("🔥","اعتبار");
        meters.addView(vnMoney,new LinearLayout.LayoutParams(0,dp(50),1));
        meters.addView(vnSkill,new LinearLayout.LayoutParams(0,dp(50),1));
        meters.addView(vnFreedom,new LinearLayout.LayoutParams(0,dp(50),1));
        meters.addView(vnSocial,new LinearLayout.LayoutParams(0,dp(50),1));
        hud.addView(meters);

        vnLocation=pill("",Color.WHITE,Color.argb(155,7,27,50));
        FrameLayout.LayoutParams locp=new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.LEFT);
        locp.setMargins(dp(12),0,0,dp(12));
        stage.addView(vnLocation,locp);

        // story sheet
        LinearLayout sheet=new LinearLayout(this);
        sheet.setOrientation(LinearLayout.VERTICAL);
        sheet.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        sheet.setPadding(dp(16),dp(14),dp(16),dp(18));
        sheet.setBackgroundColor(Color.WHITE);
        page.addView(sheet,new LinearLayout.LayoutParams(-1,-2));

        LinearLayout speakerRow=new LinearLayout(this);
        speakerRow.setOrientation(LinearLayout.HORIZONTAL);
        speakerRow.setGravity(Gravity.CENTER_VERTICAL);
        speakerRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        vnSpeaker=pill("",Color.WHITE,NAVY);
        speakerRow.addView(vnSpeaker);
        TextView chapter=tv(state.chapterTitle(),13,GOLD,true);
        chapter.setGravity(Gravity.LEFT);
        speakerRow.addView(chapter,new LinearLayout.LayoutParams(0,-2,1));
        sheet.addView(speakerRow);

        vnTitle=tv("",24,NAVY,true);
        vnTitle.setPadding(0,dp(11),0,dp(8));
        sheet.addView(vnTitle);

        vnDialog=tv("",17,TEXT,false);
        vnDialog.setPadding(dp(15),dp(13),dp(15),dp(13));
        vnDialog.setBackground(bg(Color.rgb(242,245,248),18));
        sheet.addView(vnDialog);

        TextView choicePrompt=tv("تو چه کار می‌کنی؟",14,GOLD,true);
        choicePrompt.setPadding(0,dp(15),0,dp(3));
        sheet.addView(choicePrompt);

        vnChoices=new LinearLayout(this);
        vnChoices.setOrientation(LinearLayout.VERTICAL);
        sheet.addView(vnChoices);

        vnResult=new LinearLayout(this);
        vnResult.setOrientation(LinearLayout.VERTICAL);
        vnResult.setPadding(dp(14),dp(13),dp(14),dp(13));
        vnResult.setBackground(bordered(Color.rgb(239,248,243),16,Color.rgb(188,224,203)));
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);
        rp.setMargins(0,dp(10),0,dp(4));
        vnResult.setLayoutParams(rp);
        TextView rt=tv("نتیجه انتخاب",15,GREEN,true);
        vnResultText=tv("",14,TEXT,false);
        vnResultText.setPadding(0,dp(5),0,0);
        vnResult.addView(rt);
        vnResult.addView(vnResultText);
        sheet.addView(vnResult);

        vnNext=button("ادامه داستان  ←",GOLD,NAVY);
        vnNext.setOnClickListener(v -> advanceMonth());
        sheet.addView(vnNext);

        // compact game nav
        LinearLayout nav=new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        nav.setPadding(dp(12),dp(6),dp(12),dp(14));
        nav.setBackgroundColor(Color.rgb(235,239,244));

        Button map=vnNav("🗺","نقشه");
        map.setOnClickListener(v -> showCityMap());
        Button profileBtn=vnNav("👤","پروفایل");
        profileBtn.setOnClickListener(v -> showCharacterSheet());
        Button wallet=vnNav("💳","دارایی");
        wallet.setOnClickListener(v -> showBalanceSheet());
        Button exit=vnNav("⌂","خروج");
        exit.setOnClickListener(v -> {state.save(prefs);showWelcome();});
        nav.addView(map,new LinearLayout.LayoutParams(0,dp(58),1));
        nav.addView(profileBtn,new LinearLayout.LayoutParams(0,dp(58),1));
        nav.addView(wallet,new LinearLayout.LayoutParams(0,dp(58),1));
        nav.addView(exit,new LinearLayout.LayoutParams(0,dp(58),1));
        page.addView(nav);

        setContentView(sc);
        render();
    }

    private TextView vnHudStat(String icon,String label){
        TextView v=tv(icon+"\n"+label,11,Color.WHITE,true);
        v.setGravity(Gravity.CENTER);
        v.setBackground(bg(Color.argb(90,255,255,255),12));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(50),1);
        p.setMargins(dp(2),0,dp(2),0);
        v.setLayoutParams(p);
        return v;
    }

    private Button vnNav(String icon,String label){
        Button b=new Button(this);
        b.setText(icon+"\n"+label);
        b.setTextSize(11);
        b.setTextColor(NAVY);
        b.setAllCaps(false);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(2),0,dp(2),0);
        b.setBackground(bg(Color.WHITE,14));
        return b;
    }

    private void buildTopHeader() {
        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.VERTICAL);
        head.setPadding(dp(16), dp(16), dp(16), dp(16));
        head.setBackground(heroBg());
        head.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        chapterText = tv("", 14, GOLD, true);
        top.addView(chapterText, new LinearLayout.LayoutParams(0, -2, 1));
        monthText = pill("", NAVY, GOLD);
        top.addView(monthText);
        head.addView(top);

        LinearLayout identity=new LinearLayout(this);
        identity.setOrientation(LinearLayout.HORIZONTAL);
        identity.setGravity(Gravity.CENTER_VERTICAL);
        identity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        identity.setPadding(0,dp(8),0,0);

        playerAvatar=new CharacterAvatarView(this);
        playerAvatar.setCharacter(state.avatarStyle,"player");
        identity.addView(playerAvatar,new LinearLayout.LayoutParams(dp(82),dp(96)));

        LinearLayout names=new LinearLayout(this);
        names.setOrientation(LinearLayout.VERTICAL);
        playerText=tv("",20,Color.WHITE,true);
        personalityText=tv("",14,Color.rgb(215,224,233),false);
        levelText=tv("",13,GOLD,true);
        personalityText.setPadding(0,dp(2),0,dp(3));
        names.addView(playerText); names.addView(personalityText); names.addView(levelText);
        identity.addView(names,new LinearLayout.LayoutParams(0,-2,1));
        head.addView(identity);

        storyProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        storyProgress.setMax(30);
        storyProgress.setProgressTintList(ColorStateList.valueOf(GOLD));
        storyProgress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(64,82,103)));
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(-1, dp(8));
        pp.setMargins(0, dp(8), 0, 0);
        storyProgress.setLayoutParams(pp);
        head.addView(storyProgress);

        root.addView(head);
    }

    private void buildStatusCard() {
        LinearLayout mission=card();
        missionText=tv("",15,TEXT,true);
        mission.addView(tv("🎯 مأموریت فعلی",14,GOLD,true));
        mission.addView(missionText);
        root.addView(mission);

        LinearLayout life=card();
        LinearLayout lifeHead=new LinearLayout(this);
        lifeHead.setOrientation(LinearLayout.HORIZONTAL);
        lifeHead.setGravity(Gravity.CENTER_VERTICAL);
        lifeHead.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        lifeHead.addView(tv("وضعیت زندگی",17,NAVY,true),new LinearLayout.LayoutParams(0,-2,1));
        TextView vibe=pill(GenZSystem.vibe(state),NAVY,Color.rgb(237,229,208));
        lifeHead.addView(vibe);
        life.addView(lifeHead);

        LinearLayout row1=new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        moneyMeter=lifeMeter("💰","پول");
        skillMeter=lifeMeter("🧠","مهارت");
        row1.addView(moneyMeter,new LinearLayout.LayoutParams(0,dp(72),1));
        row1.addView(skillMeter,new LinearLayout.LayoutParams(0,dp(72),1));
        life.addView(row1);

        LinearLayout row2=new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        freedomMeter=lifeMeter("🕊","آزادی");
        socialMeter=lifeMeter("🔥","اعتبار");
        row2.addView(freedomMeter,new LinearLayout.LayoutParams(0,dp(72),1));
        row2.addView(socialMeter,new LinearLayout.LayoutParams(0,dp(72),1));
        life.addView(row2);
        root.addView(life);

        LinearLayout status = card();

        LinearLayout healthRow = new LinearLayout(this);
        healthRow.setOrientation(LinearLayout.HORIZONTAL);
        healthRow.setGravity(Gravity.CENTER_VERTICAL);
        healthRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView ht = tv("سلامت مالی", 16, TEXT, true);
        healthRow.addView(ht, new LinearLayout.LayoutParams(0, -2, 1));
        healthText = pill("", Color.WHITE, GREEN);
        healthRow.addView(healthText);
        status.addView(healthRow);

        healthProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        healthProgress.setMax(100);
        healthProgress.setProgressTintList(ColorStateList.valueOf(GREEN));
        healthProgress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(232,235,238)));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, dp(7));
        hp.setMargins(0, dp(8), 0, dp(10));
        healthProgress.setLayoutParams(hp);
        status.addView(healthProgress);

        traitText=tv("",13,MUTED,true);
        traitText.setGravity(Gravity.CENTER);
        traitText.setPadding(0,0,0,dp(10));
        status.addView(traitText);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        cashText = statBox("نقدینگی", "");
        flowText = statBox("جریان نقدی", "");
        worthText = statBox("دارایی خالص", "");

        stats.addView(cashText, new LinearLayout.LayoutParams(0, dp(75), 1));
        stats.addView(flowText, new LinearLayout.LayoutParams(0, dp(75), 1));
        stats.addView(worthText, new LinearLayout.LayoutParams(0, dp(75), 1));

        status.addView(stats);

        achievementText=tv("",13,GOLD,true);
        achievementText.setGravity(Gravity.CENTER);
        achievementText.setPadding(0,dp(11),0,0);
        status.addView(achievementText);

        root.addView(status);
    }

    private TextView lifeMeter(String icon,String label){
        TextView v=tv(icon+"  "+label+"\n۰ / ۱۰۰",15,TEXT,true);
        v.setGravity(Gravity.CENTER);
        v.setBackground(bg(Color.rgb(247,249,251),14));
        v.setPadding(dp(6),dp(5),dp(6),dp(5));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(72),1);
        p.setMargins(dp(4),dp(5),dp(4),dp(2));
        v.setLayoutParams(p);
        return v;
    }

    private TextView statBox(String label, String value) {
        TextView v = tv(label + "\n" + value, 13, TEXT, true);
        v.setGravity(Gravity.CENTER);
        v.setBackground(bg(Color.rgb(247,249,251), 12));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(75), 1);
        p.setMargins(dp(3), 0, dp(3), 0);
        v.setLayoutParams(p);
        return v;
    }

    private void buildStoryCard() {
        LinearLayout story = card();

        LinearLayout meta = new LinearLayout(this);
        meta.setOrientation(LinearLayout.HORIZONTAL);
        meta.setGravity(Gravity.CENTER_VERTICAL);
        meta.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        speakerAvatar=new CharacterAvatarView(this);
        meta.addView(speakerAvatar,new LinearLayout.LayoutParams(dp(72),dp(82)));

        LinearLayout who=new LinearLayout(this);
        who.setOrientation(LinearLayout.VERTICAL);
        speakerText = tv("",16,NAVY,true);
        locationText = tv("",13,GOLD,true);
        TextView decision = tv("یک پیام برای تو", 12, MUTED, true);
        who.addView(speakerText);
        who.addView(locationText);
        who.addView(decision);
        meta.addView(who,new LinearLayout.LayoutParams(0,-2,1));
        story.addView(meta);

        eventTitle = tv("", 23, NAVY, true);
        eventTitle.setPadding(0, dp(10), 0, dp(7));
        story.addView(eventTitle);

        eventDesc = tv("", 17, TEXT, false);
        eventDesc.setPadding(dp(14),dp(12),dp(14),dp(12));
        eventDesc.setBackground(bg(Color.rgb(241,244,247),16));
        story.addView(eventDesc);

        TextView q = tv("پاسخت چیه؟", 15, GOLD, true);
        q.setPadding(0, dp(17), 0, dp(4));
        story.addView(q);

        choicesBox = new LinearLayout(this);
        choicesBox.setOrientation(LinearLayout.VERTICAL);
        story.addView(choicesBox);

        resultCard = new LinearLayout(this);
        resultCard.setOrientation(LinearLayout.VERTICAL);
        resultCard.setPadding(dp(14), dp(13), dp(14), dp(13));
        resultCard.setBackground(bg(Color.rgb(240,248,244), 14));
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
        rp.setMargins(0, dp(14), 0, 0);
        resultCard.setLayoutParams(rp);

        resultTitle = tv("بعدش چی شد؟", 16, GREEN, true);
        resultText = tv("", 15, TEXT, false);
        resultText.setPadding(0, dp(5), 0, 0);
        resultCard.addView(resultTitle);
        resultCard.addView(resultText);
        story.addView(resultCard);

        root.addView(story);
    }

    private void buildBottomActions() {
        nextButton = button("ادامه زندگی  ←", GOLD, NAVY);
        nextButton.setOnClickListener(v -> advanceMonth());
        root.addView(nextButton);

        Button map = outlineButton("🗺 نقشه زندگی");
        map.setOnClickListener(v -> showCityMap());
        root.addView(map);

        Button character = outlineButton("👤 پروفایل من");
        character.setOnClickListener(v -> showCharacterSheet());
        root.addView(character);

        Button sheet = outlineButton("💳 کیف پول و دارایی‌ها");
        sheet.setOnClickListener(v -> showBalanceSheet());
        root.addView(sheet);

        Button home = outlineButton("ذخیره و خروج");
        home.setOnClickListener(v -> {
            state.save(prefs);
            showWelcome();
        });
        root.addView(home);
    }

    private void showCityMap() {
        ScrollView sc=new ScrollView(this);
        LinearLayout box=new LinearLayout(this);
        basePage(sc,box);

        LinearLayout hero=new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(18),dp(20),dp(18),dp(20));
        hero.setBackground(heroBg());
        hero.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        hero.addView(tv("🗺 نقشه زندگی",29,Color.WHITE,true));
        hero.addView(tv("تهران ۱۴۰"+nf.format(state.persianYear()%10)+" · "+nf.format(state.age())+" سالگی",15,Color.rgb(220,228,236),false));
        box.addView(hero);

        EventData current=events.get((state.month-1)%events.size());
        String[] names={"خانه","دانشگاه","محل کار","کافه","فروشگاه","بانک","باشگاه","آنلاین"};
        String[] icons={"🏠","🎓","💼","☕","🛍","🏦","🏋","📱"};
        for(int i=0;i<names.length;i++){
            final String name=names[i];
            boolean active=name.equals(current.location);
            LinearLayout place=card();
            place.setBackground(active?bordered(Color.rgb(255,249,232),18,GOLD):bg(Color.WHITE,18));
            LinearLayout row=new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            TextView icon=tv(icons[i],28,NAVY,true);
            icon.setGravity(Gravity.CENTER);
            row.addView(icon,new LinearLayout.LayoutParams(dp(58),dp(58)));
            LinearLayout txt=new LinearLayout(this);
            txt.setOrientation(LinearLayout.VERTICAL);
            txt.addView(tv(name,18,NAVY,true));
            txt.addView(tv(active?"اتفاق این ماه اینجاست":"رویدادهای این لوکیشن در طول داستان باز می‌شوند",13,active?GOLD:MUTED,false));
            row.addView(txt,new LinearLayout.LayoutParams(0,-2,1));
            place.addView(row);
            place.setOnClickListener(v -> Toast.makeText(this,active?"الان اینجایی":"بعداً اتفاق‌های اینجا را می‌بینی",Toast.LENGTH_SHORT).show());
            box.addView(place);
        }

        Button back=button("برگشت به داستان",GOLD,NAVY);
        back.setOnClickListener(v -> showGame());
        box.addView(back);
        setContentView(sc);
    }

    private void showCharacterSheet() {
        String badges=achievementList();
        String text=
                "سن: "+nf.format(state.age())+" سال · سال "+nf.format(state.persianYear())+
                "\nمسیر: "+state.profession+
                "\nتیپ مالی: "+state.personalityType+
                "\nاستایل زندگی: "+GenZSystem.vibe(state)+
                "\n\n💰 پول: "+nf.format(state.moneyScore())+"/۱۰۰"+
                "\n🧠 مهارت: "+nf.format(state.skill)+"/۱۰۰"+
                "\n🕊 آزادی: "+nf.format(state.freedom)+"/۱۰۰"+
                "\n🔥 اعتبار اجتماعی: "+nf.format(state.social)+"/۱۰۰"+
                "\n\nLevel "+nf.format(CharacterSystem.level(state))+" · "+CharacterSystem.levelTitle(state)+
                "\nXP: "+nf.format(state.xp)+
                "\n\nصفات شخصیتی"+
                "\nانضباط: "+nf.format(state.discipline)+
                "\nجسارت: "+nf.format(state.courage)+
                "\nآرامش: "+nf.format(state.calm)+
                "\n\nروابط"+
                "\nامیر: "+nf.format(state.relAmir)+"/۱۰۰"+
                "\nسارا: "+nf.format(state.relSara)+"/۱۰۰"+
                "\nرضا: "+nf.format(state.relReza)+"/۱۰۰"+
                "\n\nAchievementها ("+nf.format(state.achievementCount())+"/۵)"+
                "\n"+badges;
        new AlertDialog.Builder(this)
                .setTitle("پروفایل "+safeName())
                .setMessage(text)
                .setPositiveButton("بستن",null)
                .show();
    }

    private String achievementList() {
        StringBuilder x=new StringBuilder();
        if(state.achEmergency)x.append("🛡 صندوق امن\n");
        if(state.achDebtFree)x.append("⛓ بدون بدهی\n");
        if(state.achInvestor)x.append("📈 اولین دارایی\n");
        if(state.achSideIncome)x.append("💼 درآمد دوم\n");
        if(state.achHealth80)x.append("🏆 سلامت ۸۰\n");
        if(x.length()==0)x.append("هنوز Achievement باز نشده.");
        return x.toString().trim();
    }

    private int npcStyle(String speaker) {
        if(speaker==null)return 6;
        if(speaker.contains("سارا"))return 2;
        if(speaker.contains("امیر"))return 1;
        if(speaker.contains("رضا"))return 3;
        if(speaker.contains("مادر")||speaker.contains("خانواده"))return 5;
        if(speaker.contains("مدیر")||speaker.contains("شرکت"))return 4;
        return 6;
    }

    private void render() {
        EventData e=events.get((state.month-1)%events.size());

        int monthOfYear=((state.month-1)%12)+1;
        vnDate.setText(nf.format(state.age())+" سال · "+nf.format(state.persianYear())+"/"+nf.format(monthOfYear));
        vnLocation.setText("📍 "+(e.location==null?"تهران":e.location));
        vnSpeaker.setText(e.speaker==null?"داستان":e.speaker);
        vnTitle.setText(e.title);
        vnDialog.setText(StoryDirector.story(state,e));

        vnMoney.setText("💰\n"+nf.format(state.moneyScore()));
        vnSkill.setText("🧠\n"+nf.format(state.skill));
        vnFreedom.setText("🕊\n"+nf.format(state.freedom));
        vnSocial.setText("🔥\n"+nf.format(state.social));

        String sp=e.speaker==null?"داستان":e.speaker;
        vnScene.setScene(e.location,StoryDirector.mood(e),npcStyle(sp),state.avatarStyle,state.month);

        vnChoices.removeAllViews();
        for(EventData.Choice c:e.choices){
            Button b=new Button(this);
            b.setText(c.title);
            b.setTextSize(15);
            b.setTextColor(NAVY);
            b.setAllCaps(false);
            b.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            b.setTextDirection(View.TEXT_DIRECTION_RTL);
            b.setPadding(dp(15),0,dp(15),0);
            b.setBackground(bordered(Color.WHITE,15,Color.rgb(209,216,223)));
            LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,dp(55));
            bp.setMargins(0,dp(5),0,dp(5));
            b.setLayoutParams(bp);
            if(state.choiceMade){b.setEnabled(false);b.setAlpha(.40f);}
            b.setOnClickListener(v -> choose(e,c));
            vnChoices.addView(b);
        }

        if(state.choiceMade){
            vnResult.setVisibility(View.VISIBLE);
            vnNext.setVisibility(View.VISIBLE);
            if(lastOutcome!=null) vnResultText.setText(lastImpact+"\n\n"+lastOutcome);
            else vnResultText.setText("انتخاب ثبت شده؛ بخشی از نتیجه ممکن است بعداً برگردد.");
        }else{
            vnResult.setVisibility(View.GONE);
            vnNext.setVisibility(View.GONE);
        }
    }

    private String shortMoney(long v) {
        double a = Math.abs(v);
        String sign = v < 0 ? "−" : "";
        if (a >= 1_000_000_000d) {
            return sign + String.format(new Locale("fa","IR"), "%.1f م", a / 1_000_000_000d);
        }
        return sign + nf.format(Math.round(a / 1_000_000d)) + " م";
    }

    private void choose(EventData e, EventData.Choice c) {
        if(state.choiceMade)return;

        long beforeWorth=state.netWorth();
        long beforeFlow=state.cashFlow();
        int beforeHealth=state.financialHealth();
        int beforeD=state.discipline, beforeC=state.courage, beforeCalm=state.calm;
        int beforeSkill=state.skill, beforeFreedom=state.freedom, beforeSocial=state.social;

        String lesson=EventEngine.apply(state,e.id,c.id);
        CharacterSystem.applyDecision(state,e.id,c.id);
        GenZSystem.applyChoice(state,e.id,c.id);
        StoryDirector.recordChoice(state,e.id,c.id);

        long afterWorth=state.netWorth();
        long afterFlow=state.cashFlow();
        int afterHealth=state.financialHealth();

        state.choiceMade=true;
        int earned=12;
        if(afterHealth>=beforeHealth){
            state.goodDecisionStreak++;
            earned+=Math.min(8,state.goodDecisionStreak);
        }else state.goodDecisionStreak=0;
        if(state.skill>beforeSkill)earned+=2;
        state.xp+=earned;

        String unlocked=state.unlockAchievements();
        if(!unlocked.isEmpty())state.xp+=15;

        long dw=afterWorth-beforeWorth;
        long df=afterFlow-beforeFlow;
        int dh=afterHealth-beforeHealth;

        String life="🧠 "+signedInt(state.skill-beforeSkill)+
                "   🕊 "+signedInt(state.freedom-beforeFreedom)+
                "   🔥 "+signedInt(state.social-beforeSocial);
        String character="انضباط "+signedInt(state.discipline-beforeD)+
                " · جسارت "+signedInt(state.courage-beforeC)+
                " · آرامش "+signedInt(state.calm-beforeCalm);

        lastOutcome="یادگیری: "+lesson;
        lastImpact=impactLine(dw,df,dh)+"\n"+life+"\n"+character+
                "\nXP +"+nf.format(earned)+
                (unlocked.isEmpty()?"":"\n\n🏆 "+unlocked.replace("\n","  ·  "));

        state.save(prefs);
        render();

        if(vnResult!=null){
            vnResult.setAlpha(0f);
            vnResult.setTranslationY(dp(18));
            vnResult.animate().alpha(1f).translationY(0f).setDuration(280).start();
            vnResult.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM);
        }
    }

    private String signedInt(int v){
        return v>0 ? "+"+nf.format(v) : v<0 ? "−"+nf.format(Math.abs(v)) : "۰";
    }

    private String impactLine(long dw, long df, int dh) {
        List<String> parts = new ArrayList<>();
        if (dw != 0) parts.add("دارایی خالص " + signedMoney(dw));
        if (df != 0) parts.add("جریان نقدی ماهانه " + signedMoney(df));
        if (dh != 0) parts.add("سلامت مالی " + (dh > 0 ? "+" : "") + nf.format(dh));
        if (parts.isEmpty()) return "اثر فوری بزرگی دیده نمی‌شود؛ این تصمیم بیشتر پیامد بلندمدت دارد.";
        StringBuilder s = new StringBuilder();
        for (int i=0;i<parts.size();i++) {
            if (i>0) s.append("  ·  ");
            s.append(parts.get(i));
        }
        return s.toString();
    }

    private String signedMoney(long v) {
        return (v > 0 ? "+" : v < 0 ? "−" : "") + nf.format(Math.abs(v)) + " تومان";
    }

    private void advanceMonth() {
        state.settleMonth();
        state.save(prefs);
        lastOutcome = null;
        lastImpact = null;

        if (state.month > 30) {
            showEnding();
            return;
        }
        if (state.month == 6 || state.month == 11 || state.month == 16 || state.month == 21 || state.month == 26) {
            showChapterTransition();
        } else {
            showGame();
        }
    }

    private void showChapterTransition() {
        ScrollView sc = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        basePage(sc, box);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(22), dp(30), dp(22), dp(30));
        hero.setBackground(heroBg());
        hero.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        hero.addView(pill("فصل جدید", NAVY, GOLD));
        hero.addView(spacer(18));
        hero.addView(tv(state.chapterTitle(), 30, Color.WHITE, true));

        String line;
        if (state.month == 6) line = "حالا دیگر مسئله فقط خرج روزمره نیست. تورم وارد تمام تصمیم‌ها شده است.";
        else if (state.month == 11) line = "انتخاب‌ها سخت‌تر می‌شوند؛ بعضی تصمیم‌ها بین «خوب» و «بد» نیستند، بین دو هزینه‌اند.";
        else if (state.month == 16) line = "هر برنامه مالی روزی با بحران امتحان می‌شود. این فصل همان آزمون است.";
        else if (state.month == 21) line = "از مدیریت خرج عبور کرده‌ای. حالا باید یاد بگیری دارایی بسازی و نوسان را تحمل کنی.";
        else line = "این‌جا دیگر تصمیم‌های کوچکِ تکرارشونده مهم‌تر از حرکت‌های نمایشی‌اند.";

        TextView desc = tv(line, 18, Color.rgb(225,232,239), false);
        desc.setPadding(0, dp(13), 0, 0);
        hero.addView(desc);
        box.addView(hero);

        LinearLayout stats = card();
        stats.addView(tv("وضعیت تو تا اینجا", 18, NAVY, true));
        TextView st = tv(
                "دارایی خالص: " + money(state.netWorth()) +
                "\nسلامت مالی: " + nf.format(state.financialHealth()) + " از ۱۰۰" +
                "\nXP: " + nf.format(state.xp) +
                "\nقدرت خرید باقی‌مانده: " + String.format(new Locale("fa","IR"), "%.1f%%", state.purchasingPower()),
                16, TEXT, false);
        st.setPadding(0, dp(8), 0, 0);
        stats.addView(st);
        box.addView(stats);

        Button go = button("ورود به فصل بعد", GOLD, NAVY);
        go.setOnClickListener(v -> showGame());
        box.addView(go);

        setContentView(sc);
    }

    private void showBalanceSheet() {
        double emergencyMonths = state.essentialExpenses() > 0
                ? state.emergencyFund / (double)state.essentialExpenses() : 0;
        double fi = state.essentialExpenses() > 0
                ? state.passiveIncome / (double)state.essentialExpenses() : 0;

        String text =
                "🎯 مأموریت: " + state.currentMission() +
                "\n\nپول نقد: " + money(state.cash) +
                "\nصندوق اضطراری: " + money(state.emergencyFund) +
                "\nطلا: " + money(state.gold) +
                "\nصندوق/سهام: " + money(state.funds) +
                "\nکسب‌وکار: " + money(state.business) +
                "\n\nبدهی کل: " + money(state.debt) +
                "\nقسط ماهانه: " + money(state.debtPayment) +
                "\n\nدارایی خالص: " + money(state.netWorth()) +
                "\nثروت واقعی: " + money(state.realNetWorth()) +
                "\nماه‌های صندوق اضطراری: " + String.format(new Locale("fa","IR"), "%.1f", emergencyMonths) +
                "\nنسبت استقلال مالی: " + String.format(new Locale("fa","IR"), "%.0f%%", fi * 100);

        new AlertDialog.Builder(this)
                .setTitle("ترازنامه و مأموریت")
                .setMessage(text)
                .setPositiveButton("بستن", null)
                .show();
    }

    private void showEnding() {
        ScrollView sc = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        basePage(sc, box);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(22), dp(30), dp(22), dp(30));
        hero.setBackground(heroBg());
        hero.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        hero.addView(pill("پایان فصل اول", NAVY, GOLD));
        hero.addView(spacer(18));
        hero.addView(tv(safeName() + "، ۳۰ ماه گذشت.", 29, Color.WHITE, true));

        TextView end = tv(
                "تو همه‌چیز را درست انجام ندادی — و قرار هم نبود انجام بدهی. چیزی که ساختی، الگوی تصمیم‌گیری خودت بود. حالا می‌توانی ببینی پولت زیر فشار تورم، بدهی، فرصت و سبک زندگی چه مسیری طی کرده.",
                17, Color.rgb(225,232,239), false);
        end.setPadding(0, dp(12), 0, 0);
        hero.addView(end);
        box.addView(hero);

        LinearLayout score = card();
        int h = state.financialHealth();
        String rank = h >= 80 ? "باثبات و آماده رشد" : h >= 60 ? "رو به رشد" : h >= 40 ? "شکننده اما قابل اصلاح" : "نیازمند بازسازی";
        score.addView(tv("نتیجه این فصل", 20, NAVY, true));
        TextView summary = tv(
                "وضعیت مالی: " + rank +
                "\nسلامت مالی: " + nf.format(h) + " از ۱۰۰" +
                "\nدارایی خالص: " + money(state.netWorth()) +
                "\nثروت واقعی: " + money(state.realNetWorth()) +
                "\nXP: " + nf.format(state.xp),
                17, TEXT, false);
        summary.setPadding(0, dp(9), 0, 0);
        score.addView(summary);
        box.addView(score);

        Button replay = button("شروع داستان جدید", GOLD, NAVY);
        replay.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            state = new GameState();
            pendingProfile = "کارمند";
            showSetup();
        });
        box.addView(replay);

        Button home = outlineButton("صفحه اول");
        home.setOnClickListener(v -> showWelcome());
        box.addView(home);

        setContentView(sc);
    }
}
