package ir.masirservat.app;

public class GameLoopEngine {
    public static boolean spendEnergy(GameState s,int amount){
        if(s.energy<amount)return false;
        s.energy-=amount;
        s.weeklyActions++;
        return true;
    }

    public static long workReward(GameState s,String job,int score){
        double quality=Math.max(.35,score/100.0);
        long base;
        if("فریلنس".equals(job)){
            base=1_250_000L + s.skill*18_000L + s.laptopLevel*420_000L;
        }else if("ارسال".equals(job)){
            base=950_000L + s.transportLevel*260_000L;
        }else{
            base=700_000L + s.careerLevel*120_000L;
        }
        long reward=Math.round(base*(.70+quality*.65));
        s.workXp+=Math.max(8,score/5);
        if(s.workXp>=100){
            s.workXp-=100;
            s.careerLevel++;
            s.xp+=25;
        }
        if("فریلنس".equals(job)){
            s.skill=Math.min(100,s.skill+1);
            s.freedom=Math.min(100,s.freedom+1);
        }
        s.cash+=reward;
        s.xp+=Math.max(5,score/12);
        return reward;
    }

    public static int createContent(GameState s){
        int gain=55+s.phoneLevel*55+s.skill*2+s.social;
        if(s.mood>75)gain+=40;
        s.followers+=gain;
        s.social=Math.min(100,s.social+2);
        s.skill=Math.min(100,s.skill+1);
        s.xp+=8;
        return gain;
    }

    public static int study(GameState s,String course,long cost,int baseSkill){
        if(s.cash<cost || !spendEnergy(s,1))return 0;
        s.cash-=cost;
        int gain=baseSkill + (s.focus>=70?1:0);
        s.skill=Math.min(100,s.skill+gain);
        s.focus=Math.max(35,s.focus-4);
        s.xp+=gain*2;
        return gain;
    }

    public static String endWeek(GameState s){
        long recurring = s.salary/4 + s.sideIncome/4 + s.passiveIncome/4;
        long creator = s.followers>=1000 ? (s.followers/1000L)*220_000L : 0L;
        long costs=s.weeklyBaseCost();

        if(s.debtPayment>0){
            long installment=Math.max(1,s.debtPayment/4);
            costs+=installment;
            s.debt=Math.max(0,s.debt-installment);
            if(s.debt==0)s.debtPayment=0;
        }

        s.cash+=recurring+creator-costs;

        if(s.week%4==0){
            s.headlineIndex*=1.025;
            s.discretionary=Math.round(s.discretionary*1.025);
            s.food=Math.round(s.food*1.025);
            if(s.funds>0)s.funds=Math.round(s.funds*1.012);
            if(s.gold>0)s.gold=Math.round(s.gold*1.018);
        }

        if(s.cash<0){
            long need=Math.abs(s.cash);
            s.cash=0;
            s.debt+=need;
            s.debtPayment+=Math.max(250_000L,need/6);
            s.freedom=Math.max(0,s.freedom-4);
        }

        s.mood=Math.min(100,s.mood+5);
        s.focus=Math.min(100,s.focus+6);
        s.week++;
        s.month=1+(s.week-1)/4;
        s.energy=s.maxEnergy;
        s.weeklyActions=0;
        s.restedThisWeek=false;

        return "درآمد تکرارشونده: "+fmt(recurring+creator)+
                "\nهزینه زندگی: "+fmt(costs)+
                "\nمانده نقدی: "+fmt(s.cash);
    }

    private static String fmt(long v){
        if(Math.abs(v)>=1_000_000L)return String.format(java.util.Locale.US,"%.1f م",v/1_000_000.0);
        return (v/1000)+" هز";
    }
}
