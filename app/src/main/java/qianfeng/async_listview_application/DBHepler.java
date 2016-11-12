package qianfeng.async_listview_application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class DBHepler extends SQLiteOpenHelper{ // 这个是要继承的类，SQLiteOpenHelper

    private static final String DBNAME = "qf.db";
    private static final int CURRENTVERSION = 1;

    public static final String TABLENAME = "food";


    public DBHepler(Context context) {
        super(context, DBNAME, null, CURRENTVERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
                                                                                                                //keywords
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME + "(_id INTEGER PRIMARY KEY, DESCRIPTION,KEYWORDS,IMG TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
