package ir.hooranet.vpnmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONObject;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context c) { super(c, "hooranet_vpn.db", null, 1); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE clients (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,phone TEXT,username TEXT NOT NULL UNIQUE,vpn_type TEXT,server_name TEXT,start_jalali TEXT,expiry_jalali TEXT,start_epoch INTEGER,expiry_epoch INTEGER,amount INTEGER DEFAULT 0,paid INTEGER DEFAULT 1,notes TEXT,created_at INTEGER)");
        db.execSQL("CREATE TABLE renewals (_id INTEGER PRIMARY KEY AUTOINCREMENT,client_id INTEGER,old_expiry_jalali TEXT,new_expiry_jalali TEXT,amount INTEGER DEFAULT 0,paid INTEGER DEFAULT 1,created_at INTEGER)");
        db.execSQL("CREATE INDEX idx_clients_expiry ON clients(expiry_epoch)");
        db.execSQL("CREATE INDEX idx_clients_name ON clients(name)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {}

    public long saveClient(Long id, String name, String phone, String username, String type,
                           String server, String start, String expiry, long startEpoch, long expiryEpoch,
                           long amount, boolean paid, String notes) {
        ContentValues v = new ContentValues();
        v.put("name", name.trim()); v.put("phone", phone.trim()); v.put("username", username.trim());
        v.put("vpn_type", type); v.put("server_name", server.trim());
        v.put("start_jalali", start); v.put("expiry_jalali", expiry);
        v.put("start_epoch", startEpoch); v.put("expiry_epoch", expiryEpoch);
        v.put("amount", amount); v.put("paid", paid ? 1 : 0); v.put("notes", notes.trim());
        if (id == null) v.put("created_at", System.currentTimeMillis());
        SQLiteDatabase db = getWritableDatabase();
        if (id == null) return db.insertOrThrow("clients", null, v);
        db.update("clients", v, "_id=?", new String[]{String.valueOf(id)});
        return id;
    }

    public Cursor list(String q) {
        SQLiteDatabase db = getReadableDatabase();
        if (q == null || q.trim().isEmpty())
            return db.rawQuery("SELECT * FROM clients ORDER BY expiry_epoch ASC", null);
        String s = "%" + q.trim() + "%";
        return db.rawQuery("SELECT * FROM clients WHERE name LIKE ? OR phone LIKE ? OR username LIKE ? OR server_name LIKE ? ORDER BY expiry_epoch ASC", new String[]{s,s,s,s});
    }

    public Cursor getClient(long id) {
        return getReadableDatabase().rawQuery("SELECT * FROM clients WHERE _id=?", new String[]{String.valueOf(id)});
    }

    public int count(String where, String[] args) {
        Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM clients" + (where==null?"":" WHERE "+where), args);
        c.moveToFirst(); int n=c.getInt(0); c.close(); return n;
    }

    public long sumRevenue() {
        Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE((SELECT SUM(amount) FROM clients),0)+COALESCE((SELECT SUM(amount) FROM renewals),0)", null);
        c.moveToFirst(); long n=c.getLong(0); c.close(); return n;
    }

    public int renewalCount(long id) {
        Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM renewals WHERE client_id=?", new String[]{String.valueOf(id)});
        c.moveToFirst(); int n=c.getInt(0); c.close(); return n;
    }

    public void deleteClient(long id) {
        SQLiteDatabase db=getWritableDatabase();
        db.delete("renewals","client_id=?",new String[]{String.valueOf(id)});
        db.delete("clients","_id=?",new String[]{String.valueOf(id)});
    }

    public void renew(long id, int months, long amount, boolean paid) {
        Cursor c=getClient(id);
        if(!c.moveToFirst()){ c.close(); return; }
        long oldEpoch=c.getLong(c.getColumnIndexOrThrow("expiry_epoch"));
        String oldJ=c.getString(c.getColumnIndexOrThrow("expiry_jalali"));
        c.close();
        long base=Math.max(oldEpoch,System.currentTimeMillis());
        long newEpoch=PersianDateUtil.addMonths(base, months);
        String newJ=PersianDateUtil.format(newEpoch);

        SQLiteDatabase db=getWritableDatabase();
        ContentValues v=new ContentValues();
        v.put("expiry_epoch",newEpoch); v.put("expiry_jalali",newJ);
        db.update("clients",v,"_id=?",new String[]{String.valueOf(id)});

        ContentValues r=new ContentValues();
        r.put("client_id",id); r.put("old_expiry_jalali",oldJ); r.put("new_expiry_jalali",newJ);
        r.put("amount",amount); r.put("paid",paid?1:0); r.put("created_at",System.currentTimeMillis());
        db.insert("renewals",null,r);
    }

    private JSONArray tableToJson(String table) throws Exception {
        JSONArray a=new JSONArray();
        Cursor c=getReadableDatabase().rawQuery("SELECT * FROM "+table,null);
        String[] cols=c.getColumnNames();
        while(c.moveToNext()){
            JSONObject o=new JSONObject();
            for(String col:cols){
                int i=c.getColumnIndex(col);
                int type=c.getType(i);
                if(type==Cursor.FIELD_TYPE_INTEGER) o.put(col,c.getLong(i));
                else if(type==Cursor.FIELD_TYPE_NULL) o.put(col,JSONObject.NULL);
                else o.put(col,c.getString(i));
            }
            a.put(o);
        }
        c.close();
        return a;
    }

    public String exportJson() throws Exception {
        JSONObject root=new JSONObject();
        root.put("version",1);
        root.put("exported_at",System.currentTimeMillis());
        root.put("clients",tableToJson("clients"));
        root.put("renewals",tableToJson("renewals"));
        return root.toString(2);
    }

    public void importJson(String json) throws Exception {
        JSONObject root=new JSONObject(json);
        JSONArray clients=root.getJSONArray("clients");
        JSONArray renewals=root.optJSONArray("renewals");
        SQLiteDatabase db=getWritableDatabase();
        db.beginTransaction();
        try{
            db.delete("renewals",null,null);
            db.delete("clients",null,null);
            for(int i=0;i<clients.length();i++){
                ContentValues v=jsonValues(clients.getJSONObject(i));
                db.insertOrThrow("clients",null,v);
            }
            if(renewals!=null){
                for(int i=0;i<renewals.length();i++){
                    ContentValues v=jsonValues(renewals.getJSONObject(i));
                    db.insert("renewals",null,v);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private ContentValues jsonValues(JSONObject o) throws Exception {
        ContentValues v=new ContentValues();
        JSONArray names=o.names();
        if(names==null) return v;
        for(int i=0;i<names.length();i++){
            String k=names.getString(i);
            Object x=o.get(k);
            if(x==JSONObject.NULL) v.putNull(k);
            else if(x instanceof Number) v.put(k,((Number)x).longValue());
            else v.put(k,String.valueOf(x));
        }
        return v;
    }
}
