package ir.masirservat.app;

public class EventEngine {
    private static long pct(long base,double p){ return Math.round(base*p); }
    private static long safeCash(GameState s,long desired){ return Math.min(Math.max(0,s.cash),Math.max(0,desired)); }

    public static String apply(GameState s,String eventId,String choiceId){
        switch(eventId){
            case "EV01":
                if("A".equals(choiceId)){ long x=90_000_000L; s.debt+=x; s.debtPayment+=7_500_000L; s.debtMonthsLeft=Math.max(s.debtMonthsLeft,12); }
                else if("B".equals(choiceId)) s.cash-=safeCash(s,35_000_000L);
                return "قسط کوچک اگر تکرار شود آزادی درآمد ماه‌های بعد را کم می‌کند.";
            case "EV02":
                if("A".equals(choiceId)) s.discretionary=Math.max(0,s.discretionary-1_500_000L);
                return "هزینه‌های تکرارشونده معمولاً بیشتر از چیزی هستند که در لحظه حس می‌شوند.";
            case "EV03":
                if("A".equals(choiceId)){ long x=safeCash(s,Math.max(5_000_000L,pct(s.monthlyIncome(),0.25))); s.cash-=x; s.emergencyFund+=x; }
                else s.cash-=safeCash(s,pct(Math.max(10_000_000L,s.monthlyIncome()),0.30));
                return "اولین درآمد می‌تواند اولین عادت مالی را هم بسازد.";
            case "EV04":
                if("A".equals(choiceId)){ long x=safeCash(s,55_000_000L); s.cash-=x; s.salary+=2_000_000L; }
                else { long x=95_000_000L; s.debt+=x; s.debtPayment+=8_000_000L; s.debtMonthsLeft=Math.max(s.debtMonthsLeft,12); }
                return "ابزار گران وقتی ارزش دارد که واقعاً توان درآمدسازی یا یادگیری تو را بالا ببرد.";
            case "EV05":
                if("A".equals(choiceId)){ s.sideIncome+=7_000_000L; s.discretionary+=500_000L; }
                return "درآمد اولیه مهم است، اما زمان و انرژی هم منابع محدود هستند.";
            case "EV06":
                if("A".equals(choiceId)) s.cash-=safeCash(s,18_000_000L);
                return "فومو اجتماعی هزینه واقعی دارد، حتی وقتی خرید لذت‌بخش است.";
            case "EV07":
                if("A".equals(choiceId)){ long x=24_000_000L; s.debt+=x; s.debtPayment+=4_000_000L; s.debtMonthsLeft=Math.max(s.debtMonthsLeft,6); }
                else s.discretionary=Math.max(0,s.discretionary-1_000_000L);
                return "پرداخت چندمرحله‌ای قیمت را کوچک نمی‌کند؛ فقط درد پرداخت را پخش می‌کند.";
            case "EV08":
                if("A".equals(choiceId)) s.discretionary+=800_000L;
                else s.discretionary=Math.max(0,s.discretionary-800_000L);
                return "بودجه اجتماعی قرار نیست حذف شود؛ باید قابل‌تکرار باشد.";
            case "EV09":
                if("A".equals(choiceId)) s.sideIncome+=6_000_000L;
                return "اولین مشتری ارزشش فقط پول نیست؛ سابقه و اعتمادبه‌نفس هم می‌سازد.";
            case "EV10":
                if("A".equals(choiceId)){ long x=safeCash(s,10_000_000L); s.cash-=x; s.gold+=x; }
                return "شروع کوچک، هزینه یادگیری سرمایه‌گذاری را پایین نگه می‌دارد.";
            case "EV11":
                if("A".equals(choiceId)){ long x=safeCash(s,25_000_000L); s.cash-=x; s.funds+=Math.round(x*0.70); }
                return "ریسکی که نمی‌فهمی، با هیجان شبکه‌های اجتماعی امن‌تر نمی‌شود.";
            case "EV12":
                if("A".equals(choiceId)){ s.cash-=safeCash(s,15_000_000L); s.salary+=1_500_000L; }
                else s.cash-=safeCash(s,16_000_000L);
                return "بعضی خرج‌ها مصرف‌اند و بعضی سرمایه انسانی؛ تفاوت‌شان در اثر آینده است.";
            case "EV13":
                if("A".equals(choiceId)) s.cash-=safeCash(s,30_000_000L);
                return "وعده سود بالا و ریسک صفر معمولاً نیاز به توقف و بررسی بیشتر دارد.";
            case "EV14":
                if("A".equals(choiceId)) s.cash-=safeCash(s,8_000_000L);
                return "کمک خوب است وقتی امنیت مالی خودت را هم نابود نکند.";
            case "EV15":
                if("A".equals(choiceId)){ s.cash-=safeCash(s,80_000_000L); s.transport=Math.max(1_500_000L,s.transport-1_000_000L); s.utilities+=700_000L; }
                return "آزادی رفت‌وآمد علاوه بر قیمت خرید، هزینه نگهداری هم دارد.";
            case "EV16":
                if("A".equals(choiceId)){ s.cash-=safeCash(s,18_000_000L); s.salary+=1_500_000L; }
                return "مهارتی مثل زبان می‌تواند گزینه‌های آینده را زیاد کند، حتی اگر بازده فوری نداشته باشد.";
            case "EV17":
                if("A".equals(choiceId)){ s.cash-=safeCash(s,12_000_000L); s.sideIncome+=3_000_000L; }
                return "برای شروع، استمرار مهم‌تر از تجهیزات حرفه‌ای است.";
            case "EV18":
                if("A".equals(choiceId)) s.health=Math.max(500_000L,s.health-200_000L);
                else s.health+=800_000L;
                return "درآمد بیشتر اگر با فرسودگی همراه شود همیشه برد نیست.";
            case "EV19":
                if("A".equals(choiceId)){ long x=Math.min(s.emergencyFund,s.essentialExpenses()); s.emergencyFund-=x; s.cash+=x; }
                else { long x=s.essentialExpenses(); s.debt+=x; s.debtPayment+=Math.max(1_000_000L,x/6); s.debtMonthsLeft=Math.max(s.debtMonthsLeft,6); }
                return "ذخیره اضطراری برای همین لحظه‌هاست: خریدن زمان بدون خریدن بدهی.";
            case "EV20":
                if("A".equals(choiceId)) s.salary=Math.round(s.salary*1.12);
                return "مذاکره وقتی با نتیجه و عدد همراه باشد بخشی از مدیریت درآمد است.";
            case "EV21":
                if("A".equals(choiceId)){ s.housing=Math.round(s.housing*0.65); s.discretionary+=500_000L; }
                else s.housing=Math.round(s.housing*1.30);
                return "استقلال فقط تنها زندگی کردن نیست؛ مدیریت انتخاب و هزینه هم هست.";
            case "EV22":
                if("A".equals(choiceId)){ long x=safeCash(s,20_000_000L); s.cash-=x; s.business+=x; s.sideIncome+=5_000_000L; }
                return "آزمایش کوچک بازار ریسک کمتری از شروع بزرگ دارد.";
            case "EV23":
                if("A".equals(choiceId)){ s.cash-=safeCash(s,6_000_000L); s.sideIncome+=1_500_000L; }
                else s.cash-=safeCash(s,3_000_000L);
                return "عدد ظاهری مخاطب با اعتماد واقعی مشتری یکی نیست.";
            case "EV24":
                if("A".equals(choiceId)) s.discretionary=Math.max(0,s.discretionary-1_200_000L);
                return "هزینه‌ای که دیگر ارزشی نمی‌سازد، فقط عادت است.";
            case "EV25":
                if("A".equals(choiceId)){ long x=Math.min(s.emergencyFund,25_000_000L); s.emergencyFund-=x; s.cash+=x; s.cash-=safeCash(s,25_000_000L); }
                else { long x=30_000_000L; s.debt+=x; s.debtPayment+=5_000_000L; s.debtMonthsLeft=Math.max(s.debtMonthsLeft,6); }
                return "صندوق اضطراری جلوی تبدیل یک خرابی به چند ماه بدهی را می‌گیرد.";
            case "EV26":
                if("A".equals(choiceId)) s.cash-=safeCash(s,5_000_000L);
                return "قرض به دوست بهتر است مبلغی باشد که در بدترین حالت هم تحمل از دست دادنش را داری.";
            case "EV27":
                if("A".equals(choiceId)) s.cash-=safeCash(s,18_000_000L);
                return "لذت و هدف مالی دشمن هم نیستند؛ مسئله انتخاب آگاهانه است.";
            case "EV28":
                s.funds=Math.round(s.funds*0.86);
                if("A".equals(choiceId)){ s.cash+=s.funds; s.funds=0; }
                return "بازار قرمز آزمون برنامه است، نه فقط اعصاب.";
            case "EV29":
                if("A".equals(choiceId)){ long bonus=20_000_000L; s.cash+=5_000_000L; s.emergencyFund+=7_000_000L; s.funds+=8_000_000L; }
                else { s.cash+=5_000_000L; s.discretionary+=3_000_000L; }
                return "پول غیرمنتظره می‌تواند سبک زندگی را بالا ببرد یا ترازنامه را قوی‌تر کند.";
            case "EV30":
                if("A".equals(choiceId)){ long x=safeCash(s,5_000_000L); s.cash-=x; s.emergencyFund+=x; }
                return "بازبینی منظم باعث می‌شود هدفت با زندگی واقعی هماهنگ بماند.";
            default:
                return "انتخاب ثبت شد.";
        }
    }
}
