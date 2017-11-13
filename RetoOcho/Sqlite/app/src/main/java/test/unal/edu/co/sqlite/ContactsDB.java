package test.unal.edu.co.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ContactsDB {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_BUSINESS_NAME = "business_name";
    public static final String KEY_BUSINESS_WEB = "business_web";
    public static final String KEY_BUSINESS_PHONE = "business_phone";
    public static final String KEY_BUSINESS_EMAIL = "business_email";
    public static final String KEY_PRODUCTS = "_products";
    public static final String KEY_BUSINESS_TYPE = "business_type";

    private static String DATABASE_NAME = "ContactsDB";
    private static String DATABASE_TABLE = "ContactsTable";
    private static int DATABASE_VERSION = 1;

    private DBHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public ContactsDB(Context context) {
        ourContext = context;
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String sqlCode = "CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_BUSINESS_NAME + " TEXT NOT NULL, " +
                    KEY_BUSINESS_WEB + " TEXT NOT NULL, " +
                    KEY_BUSINESS_PHONE + " TEXT NOT NULL, " +
                    KEY_BUSINESS_EMAIL + " TEXT NOT NULL, " +
                    KEY_PRODUCTS + " TEXT NOT NULL, " +
                    KEY_BUSINESS_TYPE + " TEXT NOT NULL);";

            sqLiteDatabase.execSQL(sqlCode);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

    public ContactsDB open() throws SQLException {
        ourHelper =  new DBHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long createEntry(String business_name, String business_web, String business_phone, String business_email, String products, String business_type) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_BUSINESS_NAME, business_name);
        cv.put(KEY_BUSINESS_WEB, business_web);
        cv.put(KEY_BUSINESS_EMAIL, business_email);
        cv.put(KEY_PRODUCTS, products);
        cv.put(KEY_BUSINESS_TYPE, business_type);
        cv.put(KEY_BUSINESS_PHONE, business_phone);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        String [] columns = new String[] {KEY_ROWID, KEY_BUSINESS_NAME, KEY_BUSINESS_WEB, KEY_BUSINESS_EMAIL, KEY_PRODUCTS, KEY_BUSINESS_TYPE, KEY_BUSINESS_PHONE};

        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iRowId = c.getColumnIndex(KEY_ROWID);
        int businessName = c.getColumnIndex(KEY_BUSINESS_NAME);
        int businessType = c.getColumnIndex(KEY_BUSINESS_TYPE);
        int website = c.getColumnIndex(KEY_BUSINESS_WEB);
        int email = c.getColumnIndex(KEY_BUSINESS_EMAIL);
        int phone = c.getColumnIndex(KEY_BUSINESS_PHONE);
        int products = c.getColumnIndex(KEY_PRODUCTS);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            contacts.add(new Contact(c.getString(businessName), c.getString(businessType), c.getString(website),
                    c.getString(phone), c.getString(email), c.getString(products), c.getString(iRowId)));
        }

        c.close();

        return contacts;
    }

    public long deleteEntry(String rowId) {
        return ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=?", new String[]{rowId});
    }

    public long updateEntry(String rowId, String business_name, String business_web, String business_phone, String business_email, String products, String business_type) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_BUSINESS_NAME, business_name);
        cv.put(KEY_BUSINESS_WEB, business_web);
        cv.put(KEY_BUSINESS_EMAIL, business_email);
        cv.put(KEY_PRODUCTS, products);
        cv.put(KEY_BUSINESS_TYPE, business_type);
        cv.put(KEY_BUSINESS_PHONE, business_phone);

        return ourDatabase.update(DATABASE_TABLE, cv, KEY_ROWID + "=?", new String[]{rowId});

    }
}
