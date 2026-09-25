package ir.masirservat.app;

public class QuestSystem {
    public static class Quest {
        public String icon, title, progress;
        public int current, target;
        public long reward;
        public boolean claimed;
        public Quest(String icon,String title,int current,int target,long reward,boolean claimed){
            this.icon=icon; this.title=title; this.current=current; this.target=target;
            this.reward=reward; this.claimed=claimed;
            this.progress=Math.min(current,target)+" / "+target;
        }
        public boolean complete(){ return current>=target; }
    }

    public static Quest get(GameState s,int index){
        int cycle=(s.week-1)%4;
        if(index==0){
            if(cycle==0) return new Quest("💼","دو شیفت کار کن",s.workCount,2,1_500_000L,s.quest1Claimed);
            if(cycle==1) return new Quest("🧠","دو بار مهارت تمرین کن",s.studyCount,2,1_300_000L,s.quest1Claimed);
            if(cycle==2) return new Quest("📱","دو محتوا بساز",s.contentCount,2,1_400_000L,s.quest1Claimed);
            return new Quest("🏋","یک بار ورزش کن",s.workoutCount,1,900_000L,s.quest1Claimed);
        }
        if(index==1){
            if(cycle==0) return new Quest("🧠","یک مهارت تمرین کن",s.studyCount,1,900_000L,s.quest2Claimed);
            if(cycle==1) return new Quest("☕","یک فعالیت اجتماعی انجام بده",s.socialCount,1,850_000L,s.quest2Claimed);
            if(cycle==2) return new Quest("💼","دو شیفت کار کن",s.workCount,2,1_600_000L,s.quest2Claimed);
            return new Quest("📱","یک محتوا بساز",s.contentCount,1,950_000L,s.quest2Claimed);
        }
        if(cycle==0) return new Quest("📱","یک محتوا بساز",s.contentCount,1,1_000_000L,s.quest3Claimed);
        if(cycle==1) return new Quest("💼","یک شیفت کار کن",s.workCount,1,1_000_000L,s.quest3Claimed);
        if(cycle==2) return new Quest("🏦","۵ میلیون صندوق امن داشته باش",(int)Math.min(5,s.savings/1_000_000L),5,1_500_000L,s.quest3Claimed);
        return new Quest("🧠","یک مهارت تمرین کن",s.studyCount,1,1_000_000L,s.quest3Claimed);
    }

    public static long claim(GameState s,int index){
        Quest q=get(s,index);
        if(!q.complete()||q.claimed)return 0L;
        if(index==0)s.quest1Claimed=true;
        else if(index==1)s.quest2Claimed=true;
        else s.quest3Claimed=true;
        s.cash+=q.reward;
        s.xp+=12;
        s.reputation=Math.min(100,s.reputation+2);
        return q.reward;
    }

    public static void resetWeek(GameState s){
        s.workCount=0; s.studyCount=0; s.socialCount=0; s.contentCount=0; s.workoutCount=0;
        s.quest1Claimed=false; s.quest2Claimed=false; s.quest3Claimed=false;
    }
}
