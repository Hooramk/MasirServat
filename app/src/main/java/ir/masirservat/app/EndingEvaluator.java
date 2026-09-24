package ir.masirservat.app;

import java.util.ArrayList;
import java.util.List;

public class EndingEvaluator {
    public static int lifeScore(GameState s){
        return Math.max(0,Math.min(100,
                Math.round((s.moneyScore()*0.35f)+(s.skill*0.25f)+(s.freedom*0.25f)+(s.social*0.15f))));
    }

    public static String title(GameState s){
        int money=s.moneyScore();
        if(money>=75 && s.freedom>=70 && s.skill>=65 && s.debt<=s.monthlyIncome())
            return "آزاد و آینده‌ساز";
        if(s.skill>=78 && money>=55)
            return "مهارت‌سازِ آینده‌دار";
        if(s.social>=75 && money<55)
            return "محبوب، اما زیر فشار مالی";
        if(money>=70 && s.freedom<45)
            return "پول داری؛ آزادی نه";
        if(s.debt>Math.max(1,s.monthlyIncome()*4))
            return "زندگیِ قسطی";
        if(money<42)
            return "زمانِ بازسازی";
        if(s.freedom>=70 && money>=55)
            return "مستقل و سبک‌بال";
        return "متعادل و رو به رشد";
    }

    public static String oneLine(GameState s){
        String t=title(s);
        if("آزاد و آینده‌ساز".equals(t))
            return "تو فقط پول جمع نکردی؛ برای خودت گزینه و آزادی ساختی.";
        if("مهارت‌سازِ آینده‌دار".equals(t))
            return "بزرگ‌ترین دارایی این مسیر، توانایی‌هایی است که می‌توانند درآمد آینده‌ات را بسازند.";
        if("محبوب، اما زیر فشار مالی".equals(t))
            return "اعتبار اجتماعی بالا رفت، اما بخشی از آزادی مالی را بابت آن پرداخت کردی.";
        if("پول داری؛ آزادی نه".equals(t))
            return "عددها خوب‌اند، اما تعهدات و سبک زندگی هنوز بخشی از اختیار آینده‌ات را گرفته‌اند.";
        if("زندگیِ قسطی".equals(t))
            return "بخشی از درآمد آینده قبل از رسیدن خرج شده؛ نقطه شروع فصل بعد کاهش تعهدات است.";
        if("زمانِ بازسازی".equals(t))
            return "این پایان شکست نیست؛ نقشه‌ای است که دقیقاً نشان می‌دهد کدام عادت‌ها باید عوض شوند.";
        if("مستقل و سبک‌بال".equals(t))
            return "تو یاد گرفتی پول را برای ساختن اختیار استفاده کنی، نه فقط برای خرید بیشتر.";
        return "نه همه‌چیز عالی شد و نه خراب؛ مهم این است که الگوی مالی خودت را حالا بهتر می‌شناسی.";
    }

    public static String strengths(GameState s){
        List<String> x=new ArrayList<>();
        if(s.moneyScore()>=65) x.add("مدیریت پول");
        if(s.skill>=65) x.add("سرمایه‌گذاری روی مهارت");
        if(s.freedom>=65) x.add("حفظ آزادی انتخاب");
        if(s.social>=65) x.add("روابط و سرمایه اجتماعی");
        if(s.discipline>=70) x.add("انضباط");
        if(s.calm>=70) x.add("کنترل هیجان");
        if(s.achievementCount()>=3) x.add("ثبات در تصمیم‌ها");
        if(x.isEmpty()) x.add("شناخت نقاط قابل بهبود");
        return join(x," · ");
    }

    public static String warnings(GameState s){
        List<String> x=new ArrayList<>();
        if(s.debt>Math.max(1,s.monthlyIncome()*2)) x.add("بدهی نسبت به درآمد بالاست");
        if(s.cashFlow()<0) x.add("جریان نقدی منفی است");
        if(s.emergencyFund<s.essentialExpenses()*3L) x.add("صندوق اضطراری کمتر از ۳ ماه است");
        if(s.freedom<45) x.add("تعهدات آزادی انتخاب را کم کرده‌اند");
        if(s.skill<45) x.add("رشد مهارت نیاز به توجه دارد");
        if(s.social>=75 && s.moneyScore()<55) x.add("خرج برای تصویر اجتماعی زیاد شده");
        if(s.calm<45) x.add("تصمیم‌های هیجانی هنوز ریسک هستند");
        if(x.isEmpty()) x.add("ریسک بحرانی دیده نمی‌شود؛ تمرکز بعدی روی رشد است");
        return join(x,"\n• ");
    }

    public static String footprint(GameState s){
        List<String> x=new ArrayList<>();
        if(s.hasStoryFlag(StoryDirector.PHONE_DEBT)) x.add("📱 گوشی قسطی؛ بخشی از درآمد آینده را خرج کردی");
        if(s.hasStoryFlag(StoryDirector.FREELANCE)) x.add("💻 فریلنس؛ اولین مسیر درآمد مستقل را باز کردی");
        if(s.hasStoryFlag(StoryDirector.SKILL_COURSE)) x.add("🎓 مهارت؛ روی خودت سرمایه‌گذاری کردی");
        if(s.hasStoryFlag(StoryDirector.CREATOR)) x.add("🎥 محتوا؛ یک دارایی رسانه‌ای را شروع کردی");
        if(s.hasStoryFlag(StoryDirector.ROOMMATE)) x.add("🏠 همخانه؛ استقلال را با هزینه کمتر تجربه کردی");
        if(s.hasStoryFlag(StoryDirector.BUSINESS)) x.add("🚀 میکروبیزینس؛ درآمد را به سیستم تبدیل کردی");
        if(s.hasStoryFlag(StoryDirector.CRYPTO_FOMO)) x.add("📉 فومو؛ هزینه تصمیم هیجانی بازار را لمس کردی");
        if(x.isEmpty()) x.add("🧭 بیشتر انتخاب‌هایت محتاطانه بود و شاخه‌های پرریسک را باز نکردی");
        return join(x,"\n");
    }

    public static String futureMessage(GameState s){
        int score=lifeScore(s);
        if(score>=75)
            return "منِ آینده‌ات ازت تشکر می‌کند که در ۱۸سالگی فقط دنبال درآمد بیشتر نبودی؛ یاد گرفتی انتخاب‌های بیشتری برای خودت بسازی. از اینجا به بعد بازی اصلی، رشد دارایی‌های مولد و زمان آزاد است.";
        if(score>=58)
            return "مسیرت خوب شروع شده، اما هنوز چند تصمیم می‌تواند تفاوت بزرگی بسازد. درآمدت را به مهارت و دارایی تبدیل کن و اجازه نده سبک زندگی تمام رشدت را مصرف کند.";
        return "چیزی که لازم داری معجزه مالی نیست؛ چند عادت تکرارشونده است: بدهی کمتر، ذخیره بیشتر، مهارت درآمدزا و تصمیم‌هایی که برای تأیید دیگران گرفته نمی‌شوند.";
    }

    private static String join(List<String> items,String sep){
        StringBuilder b=new StringBuilder();
        for(int i=0;i<items.size();i++){
            if(i>0)b.append(sep);
            b.append(items.get(i));
        }
        return b.toString();
    }
}
