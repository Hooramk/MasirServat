package ir.masirservat.app;

public class CharacterSystem {
    public static void applyPersonality(GameState s, String type) {
        s.personalityType = type;
        if ("استراتژیست".equals(type)) {
            s.discipline=78; s.courage=52; s.calm=74;
        } else if ("جسور".equals(type)) {
            s.discipline=55; s.courage=84; s.calm=52;
        } else if ("فرصت‌جو".equals(type)) {
            s.discipline=64; s.courage=88; s.calm=58;
        } else if ("احساسی".equals(type)) {
            s.discipline=42; s.courage=67; s.calm=36;
        } else {
            s.personalityType="محافظ";
            s.discipline=72; s.courage=38; s.calm=80;
        }
    }

    public static String description(GameState s) {
        switch (s.personalityType) {
            case "استراتژیست": return "قبل از تصمیم، عددها را می‌سنجی. نقطه قوتت نظم است؛ خطرت تحلیل بیش از حد.";
            case "جسور": return "از فرصت نمی‌ترسی. اگر انضباطت پایین بیاید، شجاعت می‌تواند تبدیل به ریسک بی‌حساب شود.";
            case "فرصت‌جو": return "فرصت‌ها را سریع می‌بینی و دنبال رشد هستی. باید بین سرعت و کنترل تعادل بسازی.";
            case "احساسی": return "آدم‌ها و حس لحظه روی تصمیم‌هایت اثر زیادی دارند. بازی به تو تمرین فاصله گرفتن از هیجان می‌دهد.";
            default: return "امنیت برایت مهم است و قبل از ریسک فکر می‌کنی. مراقب باش ترس از اشتباه، فرصت رشد را از تو نگیرد.";
        }
    }

    public static void applyDecision(GameState s, String eventId, String choiceId) {
        int d=0,c=0,calm=0;
        if ("EV01".equals(eventId)) {
            if ("C".equals(choiceId)){d+=4;calm+=2;s.relAmir-=2;} else {c+=2;s.relAmir+=2;}
        } else if ("EV02".equals(eventId)) {
            if ("A".equals(choiceId)) d+=4; else d-=2;
        } else if ("EV03".equals(eventId)) {
            if ("A".equals(choiceId)){d+=5;calm+=3;}
        } else if ("EV05".equals(eventId)) {
            if ("A".equals(choiceId)) c+=5; else c-=1;
        } else if ("EV06".equals(eventId)) {
            if ("A".equals(choiceId)){calm+=5;d+=2;s.relSara+=3;} else {calm-=4;s.relSara-=1;}
        } else if ("EV07".equals(eventId)) {
            if ("B".equals(choiceId)) d+=4; else d-=3;
        } else if ("EV09".equals(eventId)) {
            if ("A".equals(choiceId)) c+=3;
        } else if ("EV10".equals(eventId)) {
            if ("A".equals(choiceId)){c+=2;s.relReza+=3;} else {calm+=1;s.relReza-=1;}
        } else if ("EV13".equals(eventId)) {
            if ("A".equals(choiceId)) d+=4;
        } else if ("EV15".equals(eventId)) {
            if ("A".equals(choiceId)) d+=3;
            else if ("B".equals(choiceId)){d-=4;calm-=2;}
        } else if ("EV17".equals(eventId)) {
            if ("B".equals(choiceId)){d+=4;calm+=4;} else {d-=4;}
        } else if ("EV18".equals(eventId)) {
            if ("A".equals(choiceId)) c+=3;
        } else if ("EV19".equals(eventId)) {
            if ("A".equals(choiceId)) calm+=3; else calm-=3;
        } else if ("EV21".equals(eventId)) {
            if ("B".equals(choiceId)) calm+=5; else calm-=4;
        } else if ("EV22".equals(eventId)) {
            if ("B".equals(choiceId)) d+=3;
        } else if ("EV24".equals(eventId)) {
            if ("A".equals(choiceId)) c+=4;
        } else if ("EV26".equals(eventId)) {
            if ("A".equals(choiceId)) d+=4;
        } else if ("EV27".equals(eventId)) {
            if ("A".equals(choiceId)) d+=5; else d-=3;
        } else if ("EV30".equals(eventId)) {
            if ("A".equals(choiceId)){d+=4;calm+=2;}
        }
        s.discipline=clamp(s.discipline+d);
        s.courage=clamp(s.courage+c);
        s.calm=clamp(s.calm+calm);
        s.relAmir=clamp(s.relAmir);
        s.relSara=clamp(s.relSara);
        s.relReza=clamp(s.relReza);
    }

    public static int level(GameState s){ return 1 + s.xp / 60; }

    public static String levelTitle(GameState s){
        int l=level(s);
        if(l>=6) return "معمار ثروت";
        if(l==5) return "سرمایه‌ساز";
        if(l==4) return "مدیر پول";
        if(l==3) return "تصمیم‌گیر";
        if(l==2) return "آگاه مالی";
        return "تازه‌کار";
    }

    private static int clamp(int x){ return Math.max(0,Math.min(100,x)); }
}
