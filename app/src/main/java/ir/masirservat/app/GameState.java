package ir.masirservat.app;

import android.content.SharedPreferences;

public class GameState {
    public String playerName = "";
    public String profession = "";
    public boolean profileChosen = false;

    public int avatarStyle = 1;
    public String personalityType = "";
    public boolean personalityChosen = false;
    public int discipline = 60;
    public int courage = 50;
    public int calm = 60;
    public int relAmir = 50;
    public int relSara = 50;
    public int relReza = 50;

    public int skill = 35;
    public int freedom = 42;
    public int social = 50;
    public int storyFlags = 0;

    // v0.6 real gameplay loop
    public int loopVersion = 0;
    public int week = 1;
    public int energy = 5;
    public int maxEnergy = 5;
    public int roomLevel = 1;
    public int laptopLevel = 0;
    public int phoneLevel = 1;
    public int transportLevel = 0;
    public int careerLevel = 1;
    public int workXp = 0;
    public int followers = 120;
    public int mood = 72;
    public int focus = 65;
    public int weeklyActions = 0;
    public boolean restedThisWeek = false;
    public long savings = 0L;

    public boolean achEmergency = false;
    public boolean achDebtFree = false;
    public boolean achInvestor = false;
    public boolean achSideIncome = false;
    public boolean achHealth80 = false;

    public long cash = 60_000_000L;
    public long salary = 35_000_000L;
    public long sideIncome = 0L;
    public long passiveIncome = 0L;
    public long housing = 12_000_000L;
    public long food = 7_000_000L;
    public long transport = 3_000_000L;
    public long utilities = 1_000_000L;
    public long health = 1_000_000L;
    public long discretionary = 6_000_000L;
    public long debt = 0L;
    public long debtPayment = 0L;
    public int debtMonthsLeft = 0;
    public long emergencyFund = 30_000_000L;
    public long gold = 0L;
    public long funds = 0L;
    public long business = 0L;
    public double headlineIndex = 1.0;
    public int month = 1;
    public int xp = 0;
    public int goodDecisionStreak = 0;
    public boolean choiceMade = false;

    public void applyProfile(String p) {
        profession = p;
        profileChosen = true;
        initLifeGame();
        if ("کارآموز".equals(p)) {
            cash = 18_000_000L;
            salary = 14_000_000L;
            skill = Math.max(skill, 38);
            focus = 72;
        } else if ("فریلنسر تازه‌کار".equals(p)) {
            cash = 22_000_000L;
            salary = 0L;
            sideIncome = 10_000_000L;
            skill = Math.max(skill, 42);
            freedom = Math.max(freedom, 52);
            laptopLevel = 1;
        } else {
            profession = "دانشجو";
            cash = 15_000_000L;
            salary = 0L;
            sideIncome = 3_000_000L;
            skill = Math.max(skill, 34);
            social = Math.max(social, 55);
        }
    }

    public void initLifeGame() {
        loopVersion = 6;
        week = 1;
        energy = 5;
        maxEnergy = 5;
        roomLevel = 1;
        laptopLevel = 0;
        phoneLevel = 1;
        transportLevel = 0;
        careerLevel = 1;
        workXp = 0;
        followers = 120;
        mood = 72;
        focus = 65;
        weeklyActions = 0;
        restedThisWeek = false;
        savings = 0L;

        cash = 15_000_000L;
        salary = 0L;
        sideIncome = 3_000_000L;
        passiveIncome = 0L;
        housing = 0L;
        food = 2_500_000L;
        transport = 1_500_000L;
        utilities = 600_000L;
        health = 400_000L;
        discretionary = 1_500_000L;
        debt = 0L;
        debtPayment = 0L;
        debtMonthsLeft = 0;
        emergencyFund = 0L;
        gold = 0L;
        funds = 0L;
        business = 0L;
        headlineIndex = 1.0;
        month = 1;
        xp = 0;
        goodDecisionStreak = 0;
        choiceMade = false;
        storyFlags = 0;
    }

    public long weeklyBaseCost() {
        long base = 850_000L + 350_000L * roomLevel;
        if ("دانشجو".equals(profession)) base += 450_000L;
        else if ("کارآموز".equals(profession)) base += 700_000L;
        else base += 900_000L;
        if (transportLevel == 0) base += 450_000L;
        return base;
    }

    public String loopMission() {
        if (laptopLevel == 0) return "برای خرید اولین لپ‌تاپ کاری پول جمع کن.";
        if (skill < 45) return "مهارتت را به ۴۵ برسان و فریلنس را باز کن.";
        if (savings < 10_000_000L) return "۱۰ میلیون تومان صندوق امن بساز.";
        if (followers < 1000) return "پیجت را به ۱۰۰۰ فالوئر برسان.";
        if (careerLevel < 3) return "سطح شغلی‌ات را به ۳ برسان.";
        return "دارایی مولد بساز و آزادی مالی‌ات را بالا ببر.";
    }

    public int age() { return 18 + Math.max(0, month - 1) / 12; }
    public int persianYear() { return 1405 + Math.max(0, month - 1) / 12; }
    public int moneyScore() { return financialHealth(); }

    public long monthlyIncome() { return salary + sideIncome + passiveIncome; }
    public long monthlyExpenses() { return housing + food + transport + utilities + health + discretionary + debtPayment; }
    public long essentialExpenses() { return housing + food + transport + utilities + health + debtPayment; }
    public long netWorth() { return cash + savings + emergencyFund + gold + funds + business - debt; }
    public long realNetWorth() { return Math.round(netWorth() / headlineIndex); }
    public long cashFlow() { return monthlyIncome() - monthlyExpenses(); }
    public double purchasingPower() { return 100.0 / headlineIndex; }

    public int financialHealth() {
        long essential = Math.max(1, essentialExpenses());
        double emergencyMonths = (emergencyFund + savings) / (double) essential;
        double savings = monthlyIncome() > 0 ? cashFlow() / (double) monthlyIncome() : -1;
        double debtRatio = monthlyIncome() > 0 ? debtPayment / (double) monthlyIncome() : (debt > 0 ? 1 : 0);
        int score = 42;
        score += (int)Math.round(Math.min(25, emergencyMonths * 5));
        score += (int)Math.round(Math.max(-20, Math.min(20, savings * 100)));
        score -= (int)Math.round(Math.min(25, debtRatio * 100));
        score += (discipline - 50) / 12;
        score += (calm - 50) / 18;
        if (netWorth() > 0) score += 5;
        return Math.max(0, Math.min(100, score));
    }

    public String chapterTitle() {
        int m = Math.max(1, month);
        if (m <= 5) return "فصل ۱ · شروع استقلال";
        if (m <= 10) return "فصل ۲ · فشار تورم";
        if (m <= 15) return "فصل ۳ · انتخاب‌های سخت";
        if (m <= 20) return "فصل ۴ · بحران و فرصت";
        if (m <= 25) return "فصل ۵ · ساختن دارایی";
        return "فصل ۶ · بازی بلندمدت";
    }

    public String currentMission() {
        if (month <= 5) return "صندوق اضطراری را به ۲ ماه هزینه ضروری برسان.";
        if (month <= 10) return "جریان نقدی را مثبت نگه دار و قدرت خریدت را حفظ کن.";
        if (month <= 15) return "بدهی مصرفی را کنترل کن؛ برای ظاهر بدهکار نشو.";
        if (month <= 20) return "از بحران عبور کن بدون اینکه ترازنامه‌ات فروبریزد.";
        if (month <= 25) return "حداقل یک دارایی مولد یا سرمایه‌گذاری بساز.";
        return "تا پایان داستان، سلامت مالی را بالای ۷۰ نگه دار.";
    }

    public void setStoryFlag(int flag) { storyFlags |= flag; }
    public boolean hasStoryFlag(int flag) { return (storyFlags & flag) != 0; }

    public int achievementCount() {
        int n=0;
        if(achEmergency)n++;
        if(achDebtFree)n++;
        if(achInvestor)n++;
        if(achSideIncome)n++;
        if(achHealth80)n++;
        return n;
    }

    public String unlockAchievements() {
        StringBuilder out=new StringBuilder();
        long essential=Math.max(1,essentialExpenses());

        if(!achEmergency && emergencyFund >= essential*3L){
            achEmergency=true;
            append(out,"🛡 صندوق امن · ۳ ماه هزینه ضروری");
        }
        if(!achDebtFree && month>2 && debt==0 && debtPayment==0){
            achDebtFree=true;
            append(out,"⛓ بدون بدهی · بدهی مصرفی صفر");
        }
        if(!achInvestor && (gold+funds+business) >= Math.max(10_000_000L, monthlyIncome()/2)){
            achInvestor=true;
            append(out,"📈 اولین دارایی · سرمایه‌گذاری واقعی");
        }
        if(!achSideIncome && sideIncome>0 && !"فریلنسر".equals(profession) && !"صاحب کسب‌وکار".equals(profession)){
            achSideIncome=true;
            append(out,"💼 درآمد دوم · منبع درآمد تازه");
        }
        if(!achHealth80 && financialHealth()>=80){
            achHealth80=true;
            append(out,"🏆 سلامت ۸۰ · تراز مالی قدرتمند");
        }
        return out.toString();
    }

    private static void append(StringBuilder s,String x){
        if(s.length()>0)s.append("\n");
        s.append(x);
    }

    public void settleMonth() {
        cash += monthlyIncome() - monthlyExpenses();
        if (debtMonthsLeft > 0) {
            debt = Math.max(0, debt - debtPayment);
            debtMonthsLeft--;
            if (debtMonthsLeft == 0 || debt == 0) debtPayment = 0;
        }
        applyInflation();
        month++;
        choiceMade = false;
    }

    private void applyInflation() {
        double wave = Math.sin(month * 1.73) * 0.007;
        double headline = clamp(0.034 + wave, 0.015, 0.065);
        double foodRate = clamp(headline * 1.45 + Math.cos(month * 0.91) * 0.006, 0.015, 0.11);
        double housingRate = clamp(headline * 0.70 + Math.sin(month * 0.47) * 0.004, 0.008, 0.055);
        double serviceRate = clamp(headline * 0.90, 0.012, 0.07);
        double transportRate = clamp(headline * 0.85, 0.01, 0.075);

        headlineIndex *= (1.0 + headline);
        food = Math.round(food * (1.0 + foodRate));
        housing = Math.round(housing * (1.0 + housingRate));
        utilities = Math.round(utilities * (1.0 + serviceRate));
        health = Math.round(health * (1.0 + serviceRate));
        discretionary = Math.round(discretionary * (1.0 + serviceRate));
        transport = Math.round(transport * (1.0 + transportRate));
    }

    private static double clamp(double x, double min, double max) {
        return Math.max(min, Math.min(max, x));
    }

    public void save(SharedPreferences p) {
        p.edit()
                .putString("playerName", playerName).putString("profession", profession).putBoolean("profileChosen", profileChosen)
                .putInt("avatarStyle",avatarStyle).putString("personalityType",personalityType).putBoolean("personalityChosen",personalityChosen)
                .putInt("discipline",discipline).putInt("courage",courage).putInt("calm",calm)
                .putInt("relAmir",relAmir).putInt("relSara",relSara).putInt("relReza",relReza)
                .putInt("skill",skill).putInt("freedom",freedom).putInt("social",social).putInt("storyFlags",storyFlags)
                .putInt("loopVersion",loopVersion).putInt("week",week).putInt("energy",energy).putInt("maxEnergy",maxEnergy)
                .putInt("roomLevel",roomLevel).putInt("laptopLevel",laptopLevel).putInt("phoneLevel",phoneLevel)
                .putInt("transportLevel",transportLevel).putInt("careerLevel",careerLevel).putInt("workXp",workXp)
                .putInt("followers",followers).putInt("mood",mood).putInt("focus",focus).putInt("weeklyActions",weeklyActions)
                .putBoolean("restedThisWeek",restedThisWeek).putLong("savings",savings)
                .putBoolean("achEmergency",achEmergency).putBoolean("achDebtFree",achDebtFree).putBoolean("achInvestor",achInvestor)
                .putBoolean("achSideIncome",achSideIncome).putBoolean("achHealth80",achHealth80)
                .putLong("cash", cash).putLong("salary", salary).putLong("sideIncome", sideIncome)
                .putLong("passiveIncome", passiveIncome).putLong("housing", housing).putLong("food", food)
                .putLong("transport", transport).putLong("utilities", utilities).putLong("health", health)
                .putLong("discretionary", discretionary).putLong("debt", debt).putLong("debtPayment", debtPayment)
                .putInt("debtMonthsLeft", debtMonthsLeft).putLong("emergencyFund", emergencyFund)
                .putLong("gold", gold).putLong("funds", funds).putLong("business", business)
                .putLong("headlineIndexBits", Double.doubleToRawLongBits(headlineIndex)).putInt("month", month)
                .putInt("xp", xp).putInt("goodDecisionStreak", goodDecisionStreak)
                .putBoolean("choiceMade", choiceMade).apply();
    }

    public static GameState load(SharedPreferences p) {
        GameState s = new GameState();
        if (!p.contains("month")) return s;
        s.playerName=p.getString("playerName","");
        s.profession=p.getString("profession","");
        s.profileChosen=p.getBoolean("profileChosen",false);
        s.avatarStyle=p.getInt("avatarStyle",1);
        s.personalityType=p.getString("personalityType","");
        s.personalityChosen=p.getBoolean("personalityChosen",false);
        s.discipline=p.getInt("discipline",60);
        s.courage=p.getInt("courage",50);
        s.calm=p.getInt("calm",60);
        s.relAmir=p.getInt("relAmir",50);
        s.relSara=p.getInt("relSara",50);
        s.relReza=p.getInt("relReza",50);
        s.skill=p.getInt("skill",35);
        s.freedom=p.getInt("freedom",42);
        s.social=p.getInt("social",50);
        s.storyFlags=p.getInt("storyFlags",0);
        s.loopVersion=p.getInt("loopVersion",0);
        s.week=p.getInt("week",1);
        s.energy=p.getInt("energy",5);
        s.maxEnergy=p.getInt("maxEnergy",5);
        s.roomLevel=p.getInt("roomLevel",1);
        s.laptopLevel=p.getInt("laptopLevel",0);
        s.phoneLevel=p.getInt("phoneLevel",1);
        s.transportLevel=p.getInt("transportLevel",0);
        s.careerLevel=p.getInt("careerLevel",1);
        s.workXp=p.getInt("workXp",0);
        s.followers=p.getInt("followers",120);
        s.mood=p.getInt("mood",72);
        s.focus=p.getInt("focus",65);
        s.weeklyActions=p.getInt("weeklyActions",0);
        s.restedThisWeek=p.getBoolean("restedThisWeek",false);
        s.savings=p.getLong("savings",0L);
        s.achEmergency=p.getBoolean("achEmergency",false);
        s.achDebtFree=p.getBoolean("achDebtFree",false);
        s.achInvestor=p.getBoolean("achInvestor",false);
        s.achSideIncome=p.getBoolean("achSideIncome",false);
        s.achHealth80=p.getBoolean("achHealth80",false);
        s.cash=p.getLong("cash",s.cash); s.salary=p.getLong("salary",s.salary); s.sideIncome=p.getLong("sideIncome",0);
        s.passiveIncome=p.getLong("passiveIncome",0); s.housing=p.getLong("housing",s.housing); s.food=p.getLong("food",s.food);
        s.transport=p.getLong("transport",s.transport); s.utilities=p.getLong("utilities",s.utilities); s.health=p.getLong("health",s.health);
        s.discretionary=p.getLong("discretionary",s.discretionary); s.debt=p.getLong("debt",0); s.debtPayment=p.getLong("debtPayment",0);
        s.debtMonthsLeft=p.getInt("debtMonthsLeft",0); s.emergencyFund=p.getLong("emergencyFund",s.emergencyFund);
        s.gold=p.getLong("gold",0); s.funds=p.getLong("funds",0); s.business=p.getLong("business",0);
        s.headlineIndex=Double.longBitsToDouble(p.getLong("headlineIndexBits",Double.doubleToRawLongBits(1.0)));
        s.month=p.getInt("month",1); s.xp=p.getInt("xp",0); s.goodDecisionStreak=p.getInt("goodDecisionStreak",0);
        s.choiceMade=p.getBoolean("choiceMade",false);
        return s;
    }
}
