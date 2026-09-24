package ir.masirservat.app;

public class StoryDirector {
    public static final int PHONE_DEBT=1;
    public static final int FREELANCE=2;
    public static final int SKILL_COURSE=4;
    public static final int CREATOR=8;
    public static final int ROOMMATE=16;
    public static final int BUSINESS=32;
    public static final int CRYPTO_FOMO=64;

    public static void recordChoice(GameState s,String eventId,String choiceId){
        if("EV01".equals(eventId) && "A".equals(choiceId)) s.setStoryFlag(PHONE_DEBT);
        if("EV09".equals(eventId) && "A".equals(choiceId)) s.setStoryFlag(FREELANCE);
        if("EV12".equals(eventId) && "A".equals(choiceId)) s.setStoryFlag(SKILL_COURSE);
        if("EV17".equals(eventId) && "A".equals(choiceId)) s.setStoryFlag(CREATOR);
        if("EV21".equals(eventId) && "A".equals(choiceId)) s.setStoryFlag(ROOMMATE);
        if("EV22".equals(eventId) && "A".equals(choiceId)) s.setStoryFlag(BUSINESS);
        if("EV11".equals(eventId) && "A".equals(choiceId)) s.setStoryFlag(CRYPTO_FOMO);
    }

    public static String story(GameState s,EventData e){
        String prefix="";
        if("EV07".equals(e.id) && s.hasStoryFlag(PHONE_DEBT))
            prefix="قسط گوشی هنوز هر ماه از حسابت کم می‌شود. ";
        else if("EV19".equals(e.id) && s.hasStoryFlag(FREELANCE))
            prefix="همان مسیر فریلنس که از چند ماه قبل شروع کرده بودی، حالا جدی‌تر شده. ";
        else if("EV20".equals(e.id) && s.hasStoryFlag(SKILL_COURSE))
            prefix="مهارتی که برایش دوره رفتی باعث شده مسئولیت‌های بیشتری بگیری. ";
        else if("EV23".equals(e.id) && s.hasStoryFlag(CREATOR))
            prefix="پیجی که چند ماه پیش راه انداختی حالا اولین مخاطب‌های واقعی‌اش را دارد. ";
        else if("EV27".equals(e.id) && s.hasStoryFlag(ROOMMATE))
            prefix="همخانه‌ات هم برای این سفر وسوسه شده و فضای خانه پر از حرف سفر است. ";
        else if("EV28".equals(e.id) && s.hasStoryFlag(CRYPTO_FOMO))
            prefix="بعد از تجربه فوموی قبلی، این بار بازار قرمز حس آشنایی دارد. ";
        else if("EV29".equals(e.id) && s.hasStoryFlag(BUSINESS))
            prefix="میکروبیزینس‌ات این ماه بهتر از انتظار کار کرده و بخشی از این پول از همان مسیر آمده. ";
        return prefix + e.description;
    }

    public static String mood(EventData e){
        if(e==null) return "neutral";
        if("EV13".equals(e.id)||"EV19".equals(e.id)||"EV25".equals(e.id)||"EV28".equals(e.id)) return "tense";
        if("EV01".equals(e.id)||"EV06".equals(e.id)||"EV11".equals(e.id)||"EV27".equals(e.id)) return "excited";
        if("EV04".equals(e.id)||"EV12".equals(e.id)||"EV16".equals(e.id)||"EV22".equals(e.id)) return "hopeful";
        return "neutral";
    }
}
