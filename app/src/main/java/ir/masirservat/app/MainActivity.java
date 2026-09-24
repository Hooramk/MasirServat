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
    private String lastOutcome = null;
    private String lastImpact = null;

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
        hero.setPadding(dp(20), dp(26), dp(20), dp(25));
        hero.setBackground(heroBg());
        hero.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView season = pill("داستان مالی تو · نسخه ۰.۲", NAVY, GOLD);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-2, -2);
        sp.gravity = Gravity.RIGHT;
        season.setLayoutParams(sp);
        hero.addView(season);
        hero.addView(spacer(16));

        TextView title = tv("مسیر ثروت", 36, Color.WHITE, true);
        hero.addView(title);

        TextView sub = tv("هر انتخاب، بخشی از داستان زندگی مالی توست.", 18, Color.rgb(223,229,236), false);
        sub.setPadding(0, dp(8), 0, 0);
        hero.addView(sub);

        box.addView(hero);
        box.addView(spacer(14));

        LinearLayout story = card();
        story.addView(tv("تهران، ۱۴۰۵", 16, GOLD, true));
        TextView intro = tv(
                "۲۵ سالته و تازه تصمیم گرفته‌ای کنترل پولت را جدی بگیری. تورم، اجاره، وام، دوستان، خانواده، شغل و فرصت‌های سرمایه‌گذاری یکی‌یکی وارد داستان می‌شوند. قرار نیست جواب‌های کتابی بدهی؛ باید با پیامد انتخاب‌ها زندگی کنی.",
                17, TEXT, false);
        intro.setPadding(0, dp(8), 0, dp(4));
        story.addView(intro);
        box.addView(story);

        if (state.profileChosen && prefs.contains("month")) {
            Button cont = button("ادامه داستان  ←", GOLD, NAVY);
            cont.setOnClickListener(v -> {
                if (state.month > 30) showEnding();
                else showGame();
            });
            box.addView(cont);

            TextView save = tv("ذخیره فعلی: " + safeName() + " · " + state.profession + " · ماه " + nf.format(state.month), 14, MUTED, false);
            save.setGravity(Gravity.CENTER);
            save.setPadding(0, dp(3), 0, dp(10));
            box.addView(save);
        }

        Button fresh = outlineButton(state.profileChosen ? "شروع یک داستان جدید" : "شروع داستان");
        fresh.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            state = new GameState();
            pendingProfile = "کارمند";
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

        TextView step = pill("مرحله ۱ از ۲", Color.WHITE, NAVY);
        LinearLayout.LayoutParams spp = new LinearLayout.LayoutParams(-2, -2);
        spp.gravity = Gravity.RIGHT;
        step.setLayoutParams(spp);
        box.addView(step);
        box.addView(spacer(14));

        box.addView(tv("شخصیت داستانت را بساز", 28, NAVY, true));
        TextView hint = tv("اسم و نقطه شروع تو روی روایت و اعداد اولیه اثر می‌گذارد.", 16, MUTED, false);
        hint.setPadding(0, dp(7), 0, dp(18));
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

        LinearLayout profileCard = card();
        profileCard.addView(tv("از کجا شروع می‌کنی؟", 16, TEXT, true));
        TextView ph = tv("هیچ مسیر کاملاً آسان یا سخت نیست؛ نوع ریسک‌ها فرق می‌کند.", 14, MUTED, false);
        ph.setPadding(0, dp(4), 0, dp(8));
        profileCard.addView(ph);

        RadioGroup group = new RadioGroup(this);
        group.setOrientation(RadioGroup.VERTICAL);
        group.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        RadioButton employee = radio("کارمند  ·  درآمد ثابت، رشد آهسته‌تر", true);
        RadioButton freelancer = radio("فریلنسر  ·  درآمد بیشتر، نوسان بالاتر", false);
        RadioButton owner = radio("صاحب کسب‌وکار  ·  سرمایه بیشتر، ریسک بیشتر", false);

        employee.setId(101);
        freelancer.setId(102);
        owner.setId(103);
        group.addView(employee);
        group.addView(freelancer);
        group.addView(owner);
        group.setOnCheckedChangeListener((g, id) -> {
            if (id == 102) pendingProfile = "فریلنسر";
            else if (id == 103) pendingProfile = "صاحب کسب‌وکار";
            else pendingProfile = "کارمند";
        });
        profileCard.addView(group);
        box.addView(profileCard);

        Button go = button("ساخت شخصیت و ادامه", GOLD, NAVY);
        go.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            state.playerName = n.isEmpty() ? "بازیکن" : n;
            state.applyProfile(pendingProfile);
            state.save(prefs);
            showPrologue();
        });
        box.addView(go);

        Button back = outlineButton("بازگشت");
        back.setOnClickListener(v -> showWelcome());
        box.addView(back);

        setContentView(sc);
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
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(20), dp(24), dp(20), dp(24));
        hero.setBackground(heroBg());
        hero.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        hero.addView(pill("مرحله ۲ از ۲", NAVY, GOLD));
        hero.addView(spacer(15));
        hero.addView(tv("صبحِ اولین ماه", 30, Color.WHITE, true));

        String profileLine;
        if ("فریلنسر".equals(state.profession)) {
            profileLine = "پروژه داری و درآمدت از کارمند معمولی بیشتر است، اما هیچ تضمینی نیست ماه بعد هم همین مقدار باشد.";
        } else if ("صاحب کسب‌وکار".equals(state.profession)) {
            profileLine = "کسب‌وکارت فروش دارد، اما پول شرکت و پول شخصی گاهی مرزشان را گم می‌کنند. رشد می‌تواند سریع باشد، سقوط هم.";
        } else {
            profileLine = "حقوقت هر ماه می‌آید و همین حس امنیت می‌دهد؛ اما تورم سریع‌تر از افزایش حقوق حرکت می‌کند.";
        }

        TextView p = tv(
                safeName() + "، امروز تصمیم گرفته‌ای فقط بیشتر پول درنیاوری؛ بهتر تصمیم بگیری. " +
                        profileLine + "\n\nیک قانون داری: هر ماه فقط یک تصمیم مهم، اما هر تصمیم روی ماه‌های بعد اثر می‌گذارد.",
                17, Color.rgb(226,232,238), false);
        p.setPadding(0, dp(12), 0, 0);
        hero.addView(p);
        box.addView(hero);

        LinearLayout goal = card();
        goal.addView(tv("🎯 هدف فصل اول", 19, NAVY, true));
        TextView gt = tv("۳۰ ماه دوام بیاور، بدهی مصرفی را کنترل کن، صندوق اضطراری بساز و کاری کن ثروت واقعی‌ات با وجود تورم رشد کند.", 16, TEXT, false);
        gt.setPadding(0, dp(7), 0, 0);
        goal.addView(gt);
        box.addView(goal);

        Button start = button("شروع ماه اول", GOLD, NAVY);
        start.setOnClickListener(v -> showGame());
        box.addView(start);

        setContentView(sc);
    }

    private void showGame() {
        if (state.month > 30) {
            showEnding();
            return;
        }

        ScrollView sc = new ScrollView(this);
        root = new LinearLayout(this);
        basePage(sc, root);

        buildTopHeader();
        buildStatusCard();
        buildStoryCard();
        buildBottomActions();

        setContentView(sc);
        render();
    }

    private void buildTopHeader() {
        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.VERTICAL);
        head.setPadding(dp(18), dp(18), dp(18), dp(18));
        head.setBackground(heroBg());
        head.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        chapterText = tv("", 15, GOLD, true);
        row.addView(chapterText, new LinearLayout.LayoutParams(0, -2, 1));

        monthText = pill("", NAVY, GOLD);
        row.addView(monthText);
        head.addView(row);

        playerText = tv("", 22, Color.WHITE, true);
        playerText.setPadding(0, dp(12), 0, dp(4));
        head.addView(playerText);

        storyProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        storyProgress.setMax(30);
        storyProgress.setProgressTintList(ColorStateList.valueOf(GOLD));
        storyProgress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(64,82,103)));
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(-1, dp(8));
        pp.setMargins(0, dp(9), 0, 0);
        storyProgress.setLayoutParams(pp);
        head.addView(storyProgress);

        TextView progressLabel = tv("پیشرفت داستان · ۳۰ ماه", 12, Color.rgb(190,201,213), false);
        progressLabel.setPadding(0, dp(5), 0, 0);
        head.addView(progressLabel);

        root.addView(head);
    }

    private void buildStatusCard() {
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
        hp.setMargins(0, dp(8), 0, dp(13));
        healthProgress.setLayoutParams(hp);
        status.addView(healthProgress);

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
        root.addView(status);
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

        speakerText = pill("", Color.WHITE, NAVY);
        meta.addView(speakerText);

        TextView decision = tv("  تصمیم این ماه", 13, MUTED, true);
        meta.addView(decision);
        story.addView(meta);

        eventTitle = tv("", 24, NAVY, true);
        eventTitle.setPadding(0, dp(14), 0, dp(6));
        story.addView(eventTitle);

        eventDesc = tv("", 17, TEXT, false);
        story.addView(eventDesc);

        TextView q = tv("چه کار می‌کنی؟", 15, GOLD, true);
        q.setPadding(0, dp(18), 0, dp(4));
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

        resultTitle = tv("پیامد انتخاب", 16, GREEN, true);
        resultText = tv("", 15, TEXT, false);
        resultText.setPadding(0, dp(5), 0, 0);
        resultCard.addView(resultTitle);
        resultCard.addView(resultText);
        story.addView(resultCard);

        root.addView(story);
    }

    private void buildBottomActions() {
        nextButton = button("رفتن به ماه بعد  ←", GOLD, NAVY);
        nextButton.setOnClickListener(v -> advanceMonth());
        root.addView(nextButton);

        Button sheet = outlineButton("دارایی‌ها، بدهی‌ها و هدف");
        sheet.setOnClickListener(v -> showBalanceSheet());
        root.addView(sheet);

        Button home = outlineButton("ذخیره و بازگشت به صفحه اول");
        home.setOnClickListener(v -> {
            state.save(prefs);
            showWelcome();
        });
        root.addView(home);
    }

    private void render() {
        EventData e = events.get((state.month - 1) % events.size());

        chapterText.setText(e.chapter != null ? e.chapter : state.chapterTitle());
        monthText.setText("ماه " + nf.format(state.month));
        playerText.setText(safeName() + "  ·  " + state.profession + "  ·  XP " + nf.format(state.xp));
        storyProgress.setProgress(Math.min(30, state.month));

        int health = state.financialHealth();
        healthProgress.setProgress(health);
        healthText.setText(nf.format(health) + " / ۱۰۰");
        if (health < 40) {
            healthText.setBackground(bg(RED, 18));
            healthProgress.setProgressTintList(ColorStateList.valueOf(RED));
        } else if (health < 70) {
            healthText.setBackground(bg(GOLD, 18));
            healthText.setTextColor(NAVY);
            healthProgress.setProgressTintList(ColorStateList.valueOf(GOLD));
        } else {
            healthText.setBackground(bg(GREEN, 18));
            healthText.setTextColor(Color.WHITE);
            healthProgress.setProgressTintList(ColorStateList.valueOf(GREEN));
        }

        cashText.setText("نقدینگی\n" + shortMoney(state.cash));
        long cf = state.cashFlow();
        flowText.setText("جریان نقدی\n" + shortMoney(cf));
        flowText.setTextColor(cf >= 0 ? GREEN : RED);
        worthText.setText("دارایی خالص\n" + shortMoney(state.netWorth()));

        speakerText.setText(e.speaker == null ? "داستان" : e.speaker);
        eventTitle.setText(e.title);
        eventDesc.setText(e.description);

        choicesBox.removeAllViews();
        for (EventData.Choice c : e.choices) {
            Button b = outlineButton(c.title);
            b.setTextSize(15);
            if (state.choiceMade) {
                b.setEnabled(false);
                b.setAlpha(.45f);
            }
            b.setOnClickListener(v -> choose(e, c));
            choicesBox.addView(b);
        }

        if (state.choiceMade) {
            resultCard.setVisibility(View.VISIBLE);
            if (lastOutcome != null) {
                resultText.setText(lastImpact + "\n\nنکته: " + lastOutcome);
            } else {
                resultText.setText("تصمیمت ثبت شده. نتیجه کامل‌تر این انتخاب در ماه‌های بعد خودش را نشان می‌دهد.");
            }
            nextButton.setVisibility(View.VISIBLE);
        } else {
            resultCard.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
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
        if (state.choiceMade) return;

        long beforeWorth = state.netWorth();
        long beforeFlow = state.cashFlow();
        int beforeHealth = state.financialHealth();

        String lesson = EventEngine.apply(state, e.id, c.id);

        long afterWorth = state.netWorth();
        long afterFlow = state.cashFlow();
        int afterHealth = state.financialHealth();

        state.choiceMade = true;
        state.xp += 10;
        if (afterHealth >= beforeHealth) state.goodDecisionStreak++;
        else state.goodDecisionStreak = 0;

        long dw = afterWorth - beforeWorth;
        long df = afterFlow - beforeFlow;
        int dh = afterHealth - beforeHealth;

        lastOutcome = lesson;
        lastImpact = impactLine(dw, df, dh);
        state.save(prefs);
        render();
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
                "پول نقد: " + money(state.cash) +
                "\nصندوق اضطراری: " + money(state.emergencyFund) +
                "\nطلا: " + money(state.gold) +
                "\nصندوق/سهام: " + money(state.funds) +
                "\nکسب‌وکار: " + money(state.business) +
                "\n\nبدهی کل: " + money(state.debt) +
                "\nقسط ماهانه: " + money(state.debtPayment) +
                "\n\nدارایی خالص: " + money(state.netWorth()) +
                "\nثروت واقعی به قیمت شروع: " + money(state.realNetWorth()) +
                "\n\nماه‌های صندوق اضطراری: " + String.format(new Locale("fa","IR"), "%.1f", emergencyMonths) +
                "\nنسبت استقلال مالی: " + String.format(new Locale("fa","IR"), "%.0f%%", fi * 100) +
                "\n\nهدف بلندمدت: درآمد غیرفعال ≥ هزینه ضروری + صندوق اضطراری حداقل ۶ ماه + بدون بدهی مصرفی سنگین.";

        new AlertDialog.Builder(this)
                .setTitle("ترازنامه و هدف")
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
