package ir.hooranet.vpnmanager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PersianDateUtil {
    private static int[] parseParts(String s) throws Exception {
        if (s == null) throw new Exception("تاریخ خالی است");
        String t=s.trim().replace('-', '/'); String[] p=t.split("/");
        if(p.length!=3) throw new Exception("فرمت تاریخ باید 1405/07/01 باشد");
        int y=Integer.parseInt(p[0]), m=Integer.parseInt(p[1]), d=Integer.parseInt(p[2]);
        if(y<1200||y>1700||m<1||m>12||d<1||d>31) throw new Exception("تاریخ نامعتبر است");
        int max=(m<=6?31:(m<=11?30:(isLeapJalali(y)?30:29)));
        if(d>max) throw new Exception("روز برای این ماه نامعتبر است");
        return new int[]{y,m,d};
    }

    public static long parse(String s) throws Exception {
        int[] j=parseParts(s); int[] g=jalaliToGregorian(j[0],j[1],j[2]);
        Calendar c=new GregorianCalendar(); c.clear(); c.setLenient(false); c.set(g[0],g[1]-1,g[2],12,0,0); return c.getTimeInMillis();
    }

    public static String format(long ms) {
        Calendar c=new GregorianCalendar(); c.setTimeInMillis(ms);
        int[] j=gregorianToJalali(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH));
        return String.format(Locale.US,"%04d/%02d/%02d",j[0],j[1],j[2]);
    }

    public static String today(){ return format(System.currentTimeMillis()); }

    public static long addMonths(long ms,int months){
        try{
            int[] j=parseParts(format(ms)); int total=j[0]*12+(j[1]-1)+months; int y=Math.floorDiv(total,12), m=Math.floorMod(total,12)+1;
            int max=(m<=6?31:(m<=11?30:(isLeapJalali(y)?30:29))); int d=Math.min(j[2],max);
            return parse(String.format(Locale.US,"%04d/%02d/%02d",y,m,d));
        }catch(Exception e){ return ms; }
    }

    public static int daysRemaining(long expiryMs){ return (int)Math.ceil((expiryMs-System.currentTimeMillis())/86400000.0); }

    public static boolean isLeapJalali(int jy){
        try{
            int[] g1=jalaliToGregorian(jy,1,1), g2=jalaliToGregorian(jy+1,1,1);
            Calendar a=new GregorianCalendar(g1[0],g1[1]-1,g1[2]); Calendar b=new GregorianCalendar(g2[0],g2[1]-1,g2[2]);
            return (b.getTimeInMillis()-a.getTimeInMillis())/86400000L==366;
        }catch(Exception e){ return false; }
    }

    public static int[] jalaliToGregorian(int jy,int jm,int jd){
        jy+=1595;
        int days=-355668 + (365*jy) + ((jy/33)*8) + (((jy%33)+3)/4) + jd + (jm<7 ? (jm-1)*31 : ((jm-7)*30)+186);
        int gy=400*(days/146097); days%=146097;
        if(days>36524){ days--; gy+=100*(days/36524); days%=36524; if(days>=365) days++; }
        gy+=4*(days/1461); days%=1461;
        if(days>365){ gy+=(days-1)/365; days=(days-1)%365; }
        int gd=days+1; int[] sal={0,31,((gy%4==0&&gy%100!=0)||(gy%400==0))?29:28,31,30,31,30,31,31,30,31,30,31};
        int gm=1; while(gm<=12 && gd>sal[gm]){ gd-=sal[gm]; gm++; }
        return new int[]{gy,gm,gd};
    }

    public static int[] gregorianToJalali(int gy,int gm,int gd){
        int[] gdm={0,31,59,90,120,151,181,212,243,273,304,334}; int jy;
        if(gy>1600){ jy=979; gy-=1600; } else { jy=0; gy-=621; }
        int gy2=(gm>2)?(gy+1):gy;
        int days=(365*gy)+((gy2+3)/4)-((gy2+99)/100)+((gy2+399)/400)-80+gd+gdm[gm-1];
        jy+=33*(days/12053); days%=12053; jy+=4*(days/1461); days%=1461;
        if(days>365){ jy+=(days-1)/365; days=(days-1)%365; }
        int jm,jd; if(days<186){ jm=1+(days/31); jd=1+(days%31); } else { jm=7+((days-186)/30); jd=1+((days-186)%30); }
        return new int[]{jy,jm,jd};
    }
}
