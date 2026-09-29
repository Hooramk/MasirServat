package ir.hooranet.vpnmanager;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int NAVY=Color.rgb(11,31,51);
    private static final int GOLD=Color.rgb(201,162,39);
    private static final int BG=Color.rgb(246,247,249);
    private static final int RED=Color.rgb(185,28,28);
    private static final int GREEN=Color.rgb(21,128,61);
    private static final int ORANGE=Color.rgb(194,65,12);
    private static final int REQ_EXPORT=401, REQ_IMPORT=402;

    private DbHelper db;
    private LinearLayout content;
    private EditText search;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        db=new DbHelper(this);
        showDashboard();
    }

    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density); }

    private TextView tv(String t,int sp,int color,boolean bold){
        TextView x=new TextView(this);
        x.setText(t);
        x.setTextSize(sp);
        x.setTextColor(color);
        if(bold)x.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        x.setGravity(Gravity.RIGHT);
        x.setPadding(dp(8),dp(7),dp(8),dp(7));
        return x;
    }

    private GradientDrawable bg(int color,int radius){
        GradientDrawable g=new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        return g;
    }

    private Button btn(String t){
        Button b=new Button(this);
        b.setText(t);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14);
        b.setBackground(bg(NAVY,12));
        b.setPadding(dp(8),0,dp(8),0);
        return b;
    }

    private EditText input(String hint){
        EditText e=new EditText(this);
        e.setHint(hint);
        e.setTextSize(15);
        e.setGravity(Gravity.RIGHT);
        e.setSingleLine(true);
        e.setPadding(dp(12),dp(10),dp(12),dp(10));
        e.setBackground(bg(Color.WHITE,10));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52));
        p.setMargins(0,0,0,dp(9));
        e.setLayoutParams(p);
        return e;
    }

    private void shell(String title){
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LinearLayout head=new LinearLayout(this);
        head.setOrientation(LinearLayout.VERTICAL);
        head.setPadding(dp(18),dp(16),dp(18),dp(14));
        head.setBackgroundColor(NAVY);

        TextView brand=tv("HOORANET",22,GOLD,true);
        brand.setGravity(Gravity.CENTER);
        head.addView(brand,new LinearLayout.LayoutParams(-1,-2));

        TextView sub=tv("VPN Manager  •  "+title,14,Color.WHITE,false);
        sub.setGravity(Gravity.CENTER);
        head.addView(sub,new LinearLayout.LayoutParams(-1,-2));
        root.addView(head);

        content=new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(14),dp(14),dp(14),dp(24));

        ScrollView sv=new ScrollView(this);
        sv.addView(content);
        root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout nav=new LinearLayout(this);
        final int navLeft=dp(8), navTop=dp(7), navRight=dp(8), navBottom=dp(8);
        nav.setPadding(navLeft,navTop,navRight,navBottom);
        nav.setBackgroundColor(Color.WHITE);

        // Android 15 / Samsung One UI can draw the app under the system navigation bar.
        // Keep the bottom menu inside the safe area on both 3-button and gesture navigation.
        nav.setOnApplyWindowInsetsListener((v,insets)->{
            int bottomInset;
            if(android.os.Build.VERSION.SDK_INT>=30){
                bottomInset=insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
            }else{
                bottomInset=insets.getSystemWindowInsetBottom();
            }
            v.setPadding(navLeft,navTop,navRight,navBottom+bottomInset);
            return insets;
        });

        String[] n={"داشبورد","مشتریان","+ ثبت","پشتیبان"};
        for(int i=0;i<n.length;i++){
            Button b=btn(n[i]);
            if(i==2)b.setBackground(bg(GOLD,12));
            final int k=i;
            b.setOnClickListener(v->{
                if(k==0)showDashboard();
                else if(k==1)showList("");
                else if(k==2)showForm(null);
                else showBackup();
            });
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(48),1);
            p.setMargins(dp(3),0,dp(3),0);
            nav.addView(b,p);
        }
        root.addView(nav);
        setContentView(root);
        nav.requestApplyInsets();
    }

    private void showDashboard(){
        shell("داشبورد");
        long now=System.currentTimeMillis();
        long soon=now+7L*86400000L;

        int total=db.count(null,null);
        int active=db.count("expiry_epoch>=?",new String[]{String.valueOf(now)});
        int expiring=db.count("expiry_epoch>=? AND expiry_epoch<=?",new String[]{String.valueOf(now),String.valueOf(soon)});
        int expired=db.count("expiry_epoch<?",new String[]{String.valueOf(now)});

        LinearLayout grid=new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        LinearLayout r1=new LinearLayout(this),r2=new LinearLayout(this);

        r1.addView(card("کل مشتری‌ها",String.valueOf(total),NAVY),new LinearLayout.LayoutParams(0,dp(105),1));
        r1.addView(card("فعال",String.valueOf(active),GREEN),new LinearLayout.LayoutParams(0,dp(105),1));
        r2.addView(card("تا ۷ روز",String.valueOf(expiring),ORANGE),new LinearLayout.LayoutParams(0,dp(105),1));
        r2.addView(card("منقضی",String.valueOf(expired),RED),new LinearLayout.LayoutParams(0,dp(105),1));

        grid.addView(r1);
        grid.addView(r2);
        content.addView(grid);

        TextView revenue=tv("مجموع مبلغ ثبت‌شده:  "+money(db.sumRevenue())+" تومان",16,NAVY,true);
        revenue.setBackground(bg(Color.WHITE,12));
        revenue.setPadding(dp(14),dp(14),dp(14),dp(14));
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);
        rp.setMargins(0,dp(10),0,dp(12));
        content.addView(revenue,rp);

        TextView h=tv("اشتراک‌های نیازمند پیگیری",18,NAVY,true);
        content.addView(h);
        addRows("",true);
    }

    private View card(String label,String value,int accent){
        LinearLayout c=new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setGravity(Gravity.CENTER);
        c.setPadding(dp(8),dp(8),dp(8),dp(8));
        c.setBackground(bg(Color.WHITE,14));
        c.setElevation(dp(2));

        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-1);
        p.setMargins(dp(5),dp(5),dp(5),dp(5));
        c.setLayoutParams(p);

        TextView v=tv(value,28,accent,true);
        v.setGravity(Gravity.CENTER);
        TextView l=tv(label,13,Color.DKGRAY,false);
        l.setGravity(Gravity.CENTER);
        c.addView(v);
        c.addView(l);
        return c;
    }

    private void showList(String q){
        shell("مشتریان");
        search=input("جست‌وجو: نام، موبایل، یوزرنیم یا سرور");
        search.setText(q);
        content.addView(search);

        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        content.addView(box);

        search.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int b,int c){ fillRows(box,s.toString(),false); }
            public void afterTextChanged(Editable e){}
        });

        fillRows(box,q,false);
    }

    private void addRows(String q,boolean followup){
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        content.addView(box);
        fillRows(box,q,followup);
    }

    private void fillRows(LinearLayout box,String q,boolean followup){
        box.removeAllViews();
        Cursor c=db.list(q);
        long now=System.currentTimeMillis();
        long limit=now+7L*86400000L;
        int shown=0;

        while(c.moveToNext()){
            long exp=c.getLong(c.getColumnIndexOrThrow("expiry_epoch"));
            if(followup && exp>limit) continue;

            shown++;
            long id=c.getLong(c.getColumnIndexOrThrow("_id"));
            String name=c.getString(c.getColumnIndexOrThrow("name"));
            String user=c.getString(c.getColumnIndexOrThrow("username"));
            String expiry=c.getString(c.getColumnIndexOrThrow("expiry_jalali"));
            String server=c.getString(c.getColumnIndexOrThrow("server_name"));
            int d=PersianDateUtil.daysRemaining(exp);
            int color=d<0?RED:(d<=7?ORANGE:GREEN);

            LinearLayout row=new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(dp(13),dp(11),dp(13),dp(11));
            row.setBackground(bg(Color.WHITE,12));
            row.setElevation(dp(1));

            TextView a=tv(name+"   •   "+user,16,NAVY,true);
            TextView b=tv((server==null||server.isEmpty()?"بدون نام سرور":server)+"   |   انقضا: "+expiry,13,Color.DKGRAY,false);
            TextView st=tv(d<0?"منقضی شده "+Math.abs(d)+" روز قبل":(d==0?"امروز منقضی می‌شود":d+" روز مانده"),13,color,true);

            row.addView(a);
            row.addView(b);
            row.addView(st);
            row.setOnClickListener(v->showDetail(id));

            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
            p.setMargins(0,0,0,dp(9));
            box.addView(row,p);
        }

        c.close();

        if(shown==0){
            TextView x=tv("موردی برای نمایش نیست.",15,Color.GRAY,false);
            x.setGravity(Gravity.CENTER);
            x.setPadding(0,dp(30),0,dp(30));
            box.addView(x);
        }
    }

    private void showForm(Long id){
        shell(id==null?"ثبت اشتراک":"ویرایش اشتراک");

        EditText name=input("نام مشتری *");
        EditText phone=input("شماره موبایل");
        EditText username=input("Username *");
        EditText server=input("نام سرور / لوکیشن");
        EditText start=input("تاریخ شروع شمسی - مثال 1405/07/01");
        EditText expiry=input("تاریخ انقضا شمسی");
        EditText amount=input("مبلغ فروش (تومان)");
        EditText notes=input("توضیحات");

        start.setText(PersianDateUtil.today());

        Spinner type=new Spinner(this);
        type.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,
                new String[]{"WireGuard","L2TP/IPsec","OpenVPN","V2Ray/Xray","SSTP","سایر"}));

        Spinner paid=new Spinner(this);
        paid.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,
                new String[]{"پرداخت شده","پرداخت نشده"}));

        styleSpinner(type);
        styleSpinner(paid);

        content.addView(name);
        content.addView(phone);
        content.addView(username);
        content.addView(type);
        content.addView(server);
        content.addView(start);
        content.addView(expiry);
        content.addView(amount);
        content.addView(paid);
        content.addView(notes);

        if(id!=null){
            Cursor c=db.getClient(id);
            if(c.moveToFirst()){
                name.setText(c.getString(c.getColumnIndexOrThrow("name")));
                phone.setText(c.getString(c.getColumnIndexOrThrow("phone")));
                username.setText(c.getString(c.getColumnIndexOrThrow("username")));
                server.setText(c.getString(c.getColumnIndexOrThrow("server_name")));
                start.setText(c.getString(c.getColumnIndexOrThrow("start_jalali")));
                expiry.setText(c.getString(c.getColumnIndexOrThrow("expiry_jalali")));
                amount.setText(String.valueOf(c.getLong(c.getColumnIndexOrThrow("amount"))));
                notes.setText(c.getString(c.getColumnIndexOrThrow("notes")));
                setSpinner(type,c.getString(c.getColumnIndexOrThrow("vpn_type")));
                paid.setSelection(c.getInt(c.getColumnIndexOrThrow("paid"))==1?0:1);
            }
            c.close();
        }

        Button save=btn(id==null?"ذخیره مشتری و اشتراک":"ذخیره تغییرات");
        save.setBackground(bg(GOLD,12));
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,dp(52));
        sp.setMargins(0,dp(8),0,dp(15));
        content.addView(save,sp);

        save.setOnClickListener(v->{
            try{
                if(name.getText().toString().trim().isEmpty()||username.getText().toString().trim().isEmpty())
                    throw new Exception("نام مشتری و Username اجباری است");

                long s=PersianDateUtil.parse(start.getText().toString());
                long e=PersianDateUtil.parse(expiry.getText().toString());

                if(e<s)throw new Exception("تاریخ انقضا باید بعد از شروع باشد");

                long money=parseMoney(amount.getText().toString());

                db.saveClient(
                        id,
                        name.getText().toString(),
                        phone.getText().toString(),
                        username.getText().toString(),
                        String.valueOf(type.getSelectedItem()),
                        server.getText().toString(),
                        PersianDateUtil.format(s),
                        PersianDateUtil.format(e),
                        s,e,money,
                        paid.getSelectedItemPosition()==0,
                        notes.getText().toString()
                );

                toast("ذخیره شد");
                showList("");
            }catch(Exception ex){
                alert("خطا",ex.getMessage());
            }
        });
    }

    private void styleSpinner(Spinner s){
        s.setBackground(bg(Color.WHITE,10));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52));
        p.setMargins(0,0,0,dp(9));
        s.setLayoutParams(p);
    }

    private void setSpinner(Spinner s,String value){
        for(int i=0;i<s.getCount();i++){
            if(String.valueOf(s.getItemAtPosition(i)).equals(value)){
                s.setSelection(i);
                break;
            }
        }
    }

    private void showDetail(long id){
        Cursor c=db.getClient(id);
        if(!c.moveToFirst()){
            c.close();
            return;
        }

        String name=c.getString(c.getColumnIndexOrThrow("name"));
        String phone=c.getString(c.getColumnIndexOrThrow("phone"));
        String user=c.getString(c.getColumnIndexOrThrow("username"));
        String type=c.getString(c.getColumnIndexOrThrow("vpn_type"));
        String server=c.getString(c.getColumnIndexOrThrow("server_name"));
        String start=c.getString(c.getColumnIndexOrThrow("start_jalali"));
        String expiry=c.getString(c.getColumnIndexOrThrow("expiry_jalali"));
        String notes=c.getString(c.getColumnIndexOrThrow("notes"));
        long amount=c.getLong(c.getColumnIndexOrThrow("amount"));
        long expEpoch=c.getLong(c.getColumnIndexOrThrow("expiry_epoch"));
        boolean paid=c.getInt(c.getColumnIndexOrThrow("paid"))==1;
        c.close();

        int days=PersianDateUtil.daysRemaining(expEpoch);
        int renews=db.renewalCount(id);

        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(18),dp(4),dp(18),dp(4));
        box.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        String[] lines={
                "نام: "+name,
                "موبایل: "+phone,
                "Username: "+user,
                "نوع: "+type,
                "سرور: "+server,
                "شروع: "+start,
                "انقضا: "+expiry,
                "مانده: "+(days<0?"منقضی":days+" روز"),
                "مبلغ: "+money(amount)+" تومان",
                "پرداخت: "+(paid?"شده":"نشده"),
                "تعداد تمدید: "+renews,
                "توضیحات: "+notes
        };

        for(String s:lines){
            box.addView(tv(s,14,NAVY,s.startsWith("نام:")||s.startsWith("Username:")));
        }

        AlertDialog d=new AlertDialog.Builder(this)
                .setTitle("جزئیات اشتراک")
                .setView(box)
                .setPositiveButton("تمدید",null)
                .setNeutralButton("ویرایش",null)
                .setNegativeButton("بستن",null)
                .create();

        d.setOnShowListener(x->{
            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(GREEN);
            d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->renewDialog(id,d));
            d.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(GOLD);
            d.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v->{
                d.dismiss();
                showForm(id);
            });
        });

        d.show();

        box.setOnLongClickListener(v->{
            new AlertDialog.Builder(this)
                    .setTitle("حذف مشتری")
                    .setMessage("این مشتری و سابقه تمدید او حذف شود؟")
                    .setPositiveButton("حذف",(a,b)->{
                        db.deleteClient(id);
                        d.dismiss();
                        showList("");
                    })
                    .setNegativeButton("انصراف",null)
                    .show();
            return true;
        });
    }

    private void renewDialog(long id,Dialog parent){
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(20),0,dp(20),0);

        EditText amount=input("مبلغ تمدید (تومان)");

        Spinner months=new Spinner(this);
        months.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,
                new String[]{"۱ ماه","۲ ماه","۳ ماه","۶ ماه","۱۲ ماه"}));

        Spinner paid=new Spinner(this);
        paid.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,
                new String[]{"پرداخت شده","پرداخت نشده"}));

        styleSpinner(months);
        styleSpinner(paid);

        box.addView(months);
        box.addView(amount);
        box.addView(paid);

        new AlertDialog.Builder(this)
                .setTitle("تمدید اشتراک")
                .setView(box)
                .setPositiveButton("ثبت تمدید",(a,b)->{
                    int[] m={1,2,3,6,12};
                    db.renew(id,m[months.getSelectedItemPosition()],parseMoney(amount.getText().toString()),paid.getSelectedItemPosition()==0);
                    toast("تمدید ثبت شد");
                    parent.dismiss();
                    showList("");
                })
                .setNegativeButton("انصراف",null)
                .show();
    }

    private void showBackup(){
        shell("پشتیبان‌گیری");

        TextView info=tv("اطلاعات فقط داخل گوشی ذخیره می‌شود. هر چند وقت یک‌بار فایل پشتیبان JSON بگیر و در جای امن نگه دار.",15,NAVY,false);
        info.setBackground(bg(Color.WHITE,12));
        info.setPadding(dp(14),dp(14),dp(14),dp(14));
        content.addView(info);

        Button ex=btn("خروجی پشتیبان JSON");
        Button im=btn("بازیابی از فایل JSON");
        ex.setBackground(bg(GOLD,12));

        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52));
        p.setMargins(0,dp(14),0,dp(10));
        content.addView(ex,p);
        content.addView(im,new LinearLayout.LayoutParams(-1,dp(52)));

        ex.setOnClickListener(v->{
            Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("application/json");
            i.putExtra(Intent.EXTRA_TITLE,"HooranetVPN_Backup_"+PersianDateUtil.today().replace('/','-')+".json");
            startActivityForResult(i,REQ_EXPORT);
        });

        im.setOnClickListener(v->{
            Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("application/json");
            startActivityForResult(i,REQ_IMPORT);
        });
    }

    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);

        if(result!=RESULT_OK||data==null||data.getData()==null)return;

        Uri uri=data.getData();

        try{
            if(request==REQ_EXPORT){
                String json=db.exportJson();
                try(OutputStream os=getContentResolver().openOutputStream(uri)){
                    if(os==null) throw new IOException("فایل برای نوشتن باز نشد");
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }
                toast("فایل پشتیبان ذخیره شد");
            } else if(request==REQ_IMPORT){
                StringBuilder sb=new StringBuilder();
                try(BufferedReader br=new BufferedReader(new InputStreamReader(
                        getContentResolver().openInputStream(uri),StandardCharsets.UTF_8))){
                    String line;
                    while((line=br.readLine())!=null)sb.append(line).append('\n');
                }

                final String payload=sb.toString();

                new AlertDialog.Builder(this)
                        .setTitle("بازیابی اطلاعات")
                        .setMessage("اطلاعات فعلی با فایل پشتیبان جایگزین شود؟")
                        .setPositiveButton("بازیابی",(a,b)->{
                            try{
                                db.importJson(payload);
                                toast("بازیابی انجام شد");
                                showDashboard();
                            }catch(Exception e){
                                alert("خطا",e.getMessage());
                            }
                        })
                        .setNegativeButton("انصراف",null)
                        .show();
            }
        } catch(Exception e){
            alert("خطا",e.getMessage());
        }
    }

    private long parseMoney(String s){
        try{
            return Long.parseLong(s.replace(",","").replace("٬","").trim());
        }catch(Exception e){
            return 0;
        }
    }

    private String money(long v){
        return NumberFormat.getNumberInstance(Locale.US).format(v);
    }

    private void toast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    private void alert(String t,String m){
        new AlertDialog.Builder(this)
                .setTitle(t)
                .setMessage(m)
                .setPositiveButton("باشه",null)
                .show();
    }
}
