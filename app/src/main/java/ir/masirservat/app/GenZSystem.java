package ir.masirservat.app;

public class GenZSystem {
    public static void applyChoice(GameState s, String eventId, String choiceId) {
        int sk=0, fr=0, so=0;
        if ("EV01".equals(eventId)) {
            if ("A".equals(choiceId)) so+=5; else if ("B".equals(choiceId)){so+=2;fr+=1;} else {fr+=3;so-=1;}
        } else if ("EV02".equals(eventId)) {
            if ("A".equals(choiceId)) fr+=3; else so+=2;
        } else if ("EV03".equals(eventId)) {
            if ("A".equals(choiceId)){fr+=4;sk+=1;} else so+=3;
        } else if ("EV04".equals(eventId)) {
            if ("A".equals(choiceId)) sk+=8; else so+=3;
        } else if ("EV05".equals(eventId)) {
            if ("A".equals(choiceId)){sk+=4;fr-=2;} else fr+=3;
        } else if ("EV06".equals(eventId)) {
            if ("A".equals(choiceId)) so+=6; else {fr+=3;so-=2;}
        } else if ("EV07".equals(eventId)) {
            if ("A".equals(choiceId)){so+=3;fr-=5;} else fr+=4;
        } else if ("EV08".equals(eventId)) {
            if ("A".equals(choiceId)) so+=4; else fr+=2;
        } else if ("EV09".equals(eventId)) {
            if ("A".equals(choiceId)){sk+=6;fr+=3;} else fr+=1;
        } else if ("EV10".equals(eventId)) {
            if ("A".equals(choiceId)) fr+=4; else fr+=1;
        } else if ("EV11".equals(eventId)) {
            if ("A".equals(choiceId)){so+=2;fr-=5;} else {sk+=2;fr+=2;}
        } else if ("EV12".equals(eventId)) {
            if ("A".equals(choiceId)) sk+=7; else so+=5;
        } else if ("EV13".equals(eventId)) {
            if ("A".equals(choiceId)){fr-=7;so+=1;} else {sk+=2;fr+=3;}
        } else if ("EV14".equals(eventId)) {
            if ("A".equals(choiceId)) so+=4; else fr+=2;
        } else if ("EV15".equals(eventId)) {
            if ("A".equals(choiceId)){fr+=6;so+=2;} else fr+=2;
        } else if ("EV16".equals(eventId)) {
            if ("A".equals(choiceId)){sk+=7;fr+=4;} else so+=2;
        } else if ("EV17".equals(eventId)) {
            if ("A".equals(choiceId)){sk+=5;so+=4;fr-=2;} else fr+=2;
        } else if ("EV18".equals(eventId)) {
            if ("A".equals(choiceId)){fr+=5;sk+=1;} else {sk+=3;fr-=4;}
        } else if ("EV19".equals(eventId)) {
            if ("A".equals(choiceId)){fr+=2;sk+=3;} else fr-=4;
        } else if ("EV20".equals(eventId)) {
            if ("A".equals(choiceId)){sk+=2;fr+=3;} else fr+=1;
        } else if ("EV21".equals(eventId)) {
            if ("A".equals(choiceId)){fr+=5;so+=2;} else fr-=3;
        } else if ("EV22".equals(eventId)) {
            if ("A".equals(choiceId)){sk+=6;fr+=4;} else fr+=1;
        } else if ("EV23".equals(eventId)) {
            if ("A".equals(choiceId)){sk+=4;so+=3;} else so+=1;
        } else if ("EV24".equals(eventId)) {
            if ("A".equals(choiceId)) fr+=4; else so+=1;
        } else if ("EV25".equals(eventId)) {
            if ("A".equals(choiceId)) fr-=2; else fr-=5;
        } else if ("EV26".equals(eventId)) {
            if ("A".equals(choiceId)) so+=3; else fr+=2;
        } else if ("EV27".equals(eventId)) {
            if ("A".equals(choiceId)) so+=5; else fr+=3;
        } else if ("EV28".equals(eventId)) {
            if ("A".equals(choiceId)) fr-=4; else {fr+=3;sk+=1;}
        } else if ("EV29".equals(eventId)) {
            if ("A".equals(choiceId)) fr+=5; else so+=4;
        } else if ("EV30".equals(eventId)) {
            if ("A".equals(choiceId)){sk+=3;fr+=4;} else so+=1;
        }
        s.skill=clamp(s.skill+sk);
        s.freedom=clamp(s.freedom+fr);
        s.social=clamp(s.social+so);
    }

    public static String vibe(GameState s) {
        if (s.skill >= 75 && s.freedom >= 70) return "سازنده آینده";
        if (s.social >= 75 && s.moneyScore() < 55) return "محبوب اما تحت فشار مالی";
        if (s.moneyScore() >= 75 && s.freedom >= 70) return "آزاد و باثبات";
        if (s.skill >= 70) return "مهارت‌محور";
        if (s.freedom < 35) return "درگیر تعهدات";
        return "در حال ساخت مسیر";
    }

    private static int clamp(int x) { return Math.max(0, Math.min(100, x)); }
}
