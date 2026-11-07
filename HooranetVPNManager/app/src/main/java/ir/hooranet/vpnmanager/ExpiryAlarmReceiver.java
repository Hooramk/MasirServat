package ir.hooranet.vpnmanager;
import android.app.*;
import android.content.*;
import android.os.Build;
import android.database.Cursor;
import java.util.Calendar;
public class ExpiryAlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL="hooranet_expiry";
    private static long dayStart(long ms){
        Calendar c=Calendar.getInstance(); c.setTimeInMillis(ms);
        c.set(Calendar.HOUR_OF_DAY,0); c.set(Calendar.MINUTE,0); c.set(Calendar.SECOND,0); c.set(Calendar.MILLISECOND,0);
        return c.getTimeInMillis();
    }
    @Override public void onReceive(Context context,Intent intent){ runNow(context); }
    public static void runNow(Context context){
        DbHelper db=new DbHelper(context);
        long today=dayStart(System.currentTimeMillis());
        Cursor c=db.expiryCandidates();
        while(c.moveToNext()){
            long id=c.getLong(0); String name=c.getString(1); long expiry=c.getLong(2);
            long notified=c.getLong(3); boolean archived=c.getInt(4)==1; long expiryDay=dayStart(expiry);
            if(!archived && today>=expiryDay) db.archiveClient(id);
            else if(!archived && today>=expiryDay-86400000L && today<expiryDay && notified!=expiry){
                if(sendNotification(context,id,name,PersianDateUtil.format(expiry))) db.markExpiryNotified(id,expiry);
            }
        }
        c.close(); db.close(); scheduleNext(context);
    }
    private static boolean sendNotification(Context context,long id,String name,String expiry){
        if(Build.VERSION.SDK_INT>=33 && context.checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=android.content.pm.PackageManager.PERMISSION_GRANTED) return false;
        NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(new NotificationChannel(CHANNEL,"یادآوری انقضای اشتراک",NotificationManager.IMPORTANCE_DEFAULT));
        Intent open=new Intent(context,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(context,(int)id,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(context,CHANNEL):new Notification.Builder(context);
        Notification n=b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("یک روز تا پایان اشتراک")
                .setContentText(name+" — تاریخ انقضا: "+expiry).setAutoCancel(true).setContentIntent(pi).build();
        nm.notify((int)(id%Integer.MAX_VALUE),n); return true;
    }
    public static void scheduleNext(Context context){
        Calendar next=Calendar.getInstance(); next.set(Calendar.HOUR_OF_DAY,9); next.set(Calendar.MINUTE,0);
        next.set(Calendar.SECOND,0); next.set(Calendar.MILLISECOND,0);
        if(next.getTimeInMillis()<=System.currentTimeMillis()) next.add(Calendar.DAY_OF_YEAR,1);
        Intent i=new Intent(context,ExpiryAlarmReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(context,704,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(am!=null) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,next.getTimeInMillis(),pi);
    }
}