package ir.masirservat.app;

public class EventEngine {
    private static long pct(long base, double p){ return Math.round(base*p); }

    public static String apply(GameState s, String eventId, String choiceId) {
        switch (eventId) {
            case "EV01":
                long phone=80_000_000L;
                if(choiceId.equals("A")) s.cash-=phone;
                else if(choiceId.equals("B")){ s.cash-=20_000_000L; s.debt+=84_000_000L; s.debtPayment+=7_000_000L; s.debtMonthsLeft=Math.max(s.debtMonthsLeft,12); }
                return "قسط کوچک ممکن است جریان نقدی آینده را محدود کند.";
            case "EV02":
                if(choiceId.equals("A")) s.food=Math.round(s.food*0.92); else s.food=Math.round(s.food*1.06);
                return "بودجه باید با تغییر قیمت‌ها دوباره تنظیم شود.";
            case "EV03":
                if(choiceId.equals("A")){ long x=Math.min(s.cash,s.essentialExpenses()); s.cash-=x; s.emergencyFund+=x; }
                return "صندوق اضطراری برای جلوگیری از بدهی در بحران است.";
            case "EV04":
                if(choiceId.equals("A")){ long x=pct(s.essentialExpenses(),0.60); s.emergencyFund=Math.max(0,s.emergencyFund-x); }
                else { long x=pct(s.essentialExpenses(),0.75); s.debt+=x; s.debtPayment+=Math.max(1,x/6); s.debtMonthsLeft=Math.max(s.debtMonthsLeft,6); }
                return "نقدینگی اضطراری جلوی بدهی ناگهانی را می‌گیرد.";
            case "EV05": if(choiceId.equals("A")) s.salary=Math.round(s.salary*1.12); return "رشد درآمد یکی از ابزارهای مقابله با تورم است.";
            case "EV06": if(choiceId.equals("A")) s.discretionary=Math.round(s.discretionary*0.90); else s.cash-=pct(s.monthlyIncome(),0.90); return "خرید از ترس گرانی می‌تواند تصمیم هیجانی باشد.";
            case "EV07": if(choiceId.equals("A")){ long x=2*s.monthlyIncome(); s.cash+=x; s.debt+=Math.round(x*1.25); s.debtPayment+=pct(s.monthlyIncome(),0.21); s.debtMonthsLeft=Math.max(s.debtMonthsLeft,12); } return "وام درآمد نیست؛ تعهد روی درآمد آینده است.";
            case "EV08": if(choiceId.equals("A")) s.housing=Math.round(s.housing*1.22); else { s.cash-=pct(s.monthlyIncome(),0.35); s.housing=Math.round(s.housing*0.90); } return "هزینه جابه‌جایی و نقدینگی را هم باید دید.";
            case "EV09": if(choiceId.equals("A")) s.sideIncome+=pct(s.salary,0.22); return "تنوع درآمد تاب‌آوری مالی را بالا می‌برد.";
            case "EV10": if(choiceId.equals("A")){ long x=pct(Math.max(0,s.cash),0.10); s.cash-=x; s.gold+=x; } return "تنوع دارایی باید با حفظ نقدینگی همراه باشد.";
            case "EV11": if(choiceId.equals("A")) s.cash-=pct(s.monthlyIncome(),0.45); else s.cash-=pct(s.monthlyIncome(),0.22); return "هزینه مالکیت خودرو فقط قیمت خرید نیست.";
            case "EV12": if(choiceId.equals("A")) s.cash-=pct(s.monthlyIncome(),0.65); else { long x=pct(s.monthlyIncome(),1.25); s.debt+=x; s.debtPayment+=Math.max(1,x/12); s.debtMonthsLeft=Math.max(s.debtMonthsLeft,12); } return "قیمت نهایی اقساط از مبلغ هر قسط مهم‌تر است.";
            case "EV13": if(choiceId.equals("A")) s.discretionary=Math.max(0,s.discretionary-pct(s.monthlyIncome(),0.05)); else s.discretionary+=pct(s.monthlyIncome(),0.05); return "هزینه‌های کوچک تکرارشونده بزرگ می‌شوند.";
            case "EV14": s.cash-=choiceId.equals("A")?pct(s.monthlyIncome(),0.15):pct(s.monthlyIncome(),0.45); return "برای فشارهای اجتماعی هم بودجه لازم است.";
            case "EV15": if(choiceId.equals("A")) s.cash-=pct(s.monthlyIncome(),0.35); else if(choiceId.equals("B")){ long x=pct(s.monthlyIncome(),1.10); s.debt+=x; s.debtPayment+=Math.max(1,x/6); s.debtMonthsLeft=Math.max(s.debtMonthsLeft,6); } return "بدهی بلندمدت برای مصرف کوتاه‌مدت ریسک دارد.";
            case "EV16": if(choiceId.equals("A")) s.health+=pct(s.monthlyIncome(),0.03); return "بیمه بخشی از ریسک را منتقل می‌کند.";
            case "EV17": if(choiceId.equals("A")) s.cash-=pct(s.monthlyIncome(),0.60); return "سود تضمینی غیرعادی نیاز به بررسی جدی دارد.";
            case "EV18": if(choiceId.equals("A")){ s.cash-=pct(s.monthlyIncome(),0.40); s.salary=Math.round(s.salary*1.05); } return "سرمایه انسانی می‌تواند بازده بلندمدت داشته باشد.";
            case "EV19": if(choiceId.equals("A")) s.emergencyFund=Math.max(0,s.emergencyFund-s.essentialExpenses()); else { s.debt+=s.essentialExpenses(); s.debtPayment+=Math.max(1,s.essentialExpenses()/6); s.debtMonthsLeft=Math.max(s.debtMonthsLeft,6); } return "صندوق اضطراری زمان تصمیم‌گیری می‌خرد.";
            case "EV20": if(choiceId.equals("A")){ s.salary=Math.round(s.salary*1.25); s.transport+=pct(s.salary,0.08); } return "درآمد خالص مهم‌تر از حقوق اسمی است.";
            case "EV21": s.funds=Math.round(s.funds*0.82); if(choiceId.equals("A")){ s.cash+=s.funds; s.funds=0; } return "واکنش هیجانی بخشی از ریسک بازار است.";
            case "EV22": if(choiceId.equals("A")){ s.cash-=pct(s.monthlyIncome(),1.50); s.transport+=pct(s.monthlyIncome(),0.08); } return "تورم سبک زندگی می‌تواند افزایش حقوق را خنثی کند.";
            case "EV23": if(choiceId.equals("A")){ long x=Math.min(s.monthlyIncome(),s.debt); s.cash-=Math.min(s.cash,x); s.debt-=x; } else { long x=Math.min(Math.max(0,s.cash),s.monthlyIncome()); s.cash-=x; s.funds+=x; } return "هزینه بدهی را با بازده مورد انتظار مقایسه کن.";
            case "EV24": if(choiceId.equals("A")){ long x=pct(s.monthlyIncome(),0.80); s.cash-=x; s.business+=x; s.sideIncome+=pct(s.salary,0.08); } else s.cash-=pct(s.monthlyIncome(),0.05); return "آزمون کوچک بازار ارزان‌تر از شروع بزرگ است.";
            case "EV25": if(choiceId.equals("B")) s.cash-=pct(s.monthlyIncome(),0.40); return "فروش بالا الزاماً سود بالا نیست.";
            case "EV26": if(choiceId.equals("A")){ long bonus=pct(s.salary,0.60); s.cash+=pct(bonus,0.50); s.emergencyFund+=pct(bonus,0.25); s.funds+=pct(bonus,0.25); } return "درآمد ناگهانی فرصت تقویت ترازنامه است.";
            case "EV27": if(choiceId.equals("A")) s.emergencyFund+=pct(s.salary,0.05); else s.discretionary+=pct(s.salary,0.10); return "فاصله درآمد و هزینه مهم‌تر از عدد حقوق است.";
            case "EV28": if(choiceId.equals("A")){ long x=pct(s.monthlyIncome(),2.0); if(s.cash>=x){s.cash-=x; s.housing=Math.round(s.housing*0.78);} } else s.housing=Math.round(s.housing*1.12); return "رهن بیشتر یعنی قفل شدن نقدینگی در برابر اجاره کمتر.";
            case "EV29": if(choiceId.equals("A")) s.cash-=pct(s.monthlyIncome(),0.15); else if(choiceId.equals("B")) s.cash-=pct(s.monthlyIncome(),0.80); return "قرض باید با توان تحمل عدم بازپرداخت سازگار باشد.";
            case "EV30": if(choiceId.equals("A")){ long x=Math.min(Math.max(0,s.cash),pct(s.monthlyIncome(),0.10)); s.cash-=x; s.emergencyFund+=x; } return "بازبینی دوره‌ای تصمیم‌ها را با هدف مالی هماهنگ می‌کند.";
            default: return "نتیجه ثبت شد.";
        }
    }
}
