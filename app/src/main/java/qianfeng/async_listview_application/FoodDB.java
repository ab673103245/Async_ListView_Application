package qianfeng.async_listview_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class FoodDB {
    private Context context;
    private SQLiteDatabase db; // 这是是使用的类

    public FoodDB(Context context) {
        this.context = context;
        db = new DBHepler(context).getReadableDatabase();  // 只实例化一次
    }


    // 下面是要保存多条数据，或者单条数据，弄两个重载方法吧，分别传入不同的参数，
    // 还要记得，保存集合类的数据进入数据库，要加入事务处理。
    public void saveData(List<Food> list)
    {
        // 从外界传进来一个list集合，里面存储的是Food对象的集合
        // 把他当成是事务来处理
        if(list != null && list.size() > 0) {
            // 在插入数据之前，先删除表中已有的数据，这样就可以保证表中的数据不会重复，在实际的工作中，表中哪些数据需要删除由你自己来写条件来判断
            delete(null,null);// 第一个参数是删除条件，第二个是删除条件对应的值，这个方法默认就是删除 DBHepler.TABLENAME 这个表中的数据
            db.beginTransaction();
            try {
                for (Food f : list) {
                    saveData(f);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }


    }

    public void saveData(Food food) // 里面传入的就是一个food
    {
        ContentValues values = new ContentValues();
        values.put("DESCRIPTION",food.getDescription());
        values.put("KEYWORDS",food.getKeywords());
        values.put("IMG",food.getImg());
        db.insert(DBHepler.TABLENAME,null,values);
    }

    // 删除数据
    public void delete(String clause, String[] args) // 要删除的表的条件
    {
        db.delete(DBHepler.TABLENAME,clause,args); // 第二个参数是：要删除的表的条件
    }

    // 修改数据
    public void updata(ContentValues values,String clause,String[] args)
    {
        db.update(DBHepler.TABLENAME,values,clause,args);
    }

    // 查询所有的数据,用集合的形式存储这些查询到的数据，而不是让他们在数据库中呆着
   public List<Food> getAllData() //
   {
       // 遍历db数据库
       List<Food> list = new ArrayList<>();

                        // 这是用Android平台查询的时候，最重要的方法 , 第二个参数是选择的条件，一般与数据库的占位符?问号，一起配合查询
       Cursor cursor = db.rawQuery("select * from " + DBHepler.TABLENAME,null);// 这个方法很重要，是按数据库中某个键来查询符合的数据!!!返回的是一个Cursor对象
       while (cursor.moveToNext())
       {
           String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
           String keywords = cursor.getString(cursor.getColumnIndex("KEYWORDS"));
           String img = cursor.getString(cursor.getColumnIndex("IMG"));

           Food food = new Food(description,keywords,img);
           list.add(food);
       }

       cursor.close();

       return list;
   }

    // 按条件查询数据
    public List<Food> getDataByKeywords(String sql)
    {
        List<Food> list = new ArrayList<>();

        // 需要用到从数据库里面返回出查询到的数据的话，就要用到db.rawQuery()方法了
        // 从数据库里面拿到数据，就要返回一个Cursor对象
        Cursor cursor = db.rawQuery("select * from " + DBHepler.TABLENAME + " where KEYWORDS like ?", new String[]{"%" + sql + "%"});
       // cursor拿到的，是所有符合条件的数据
        //在这里，要对cursor作遍历操作
        while(cursor.moveToNext())
        {
            String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
            String keywords = cursor.getString(cursor.getColumnIndex("KEYWORDS"));
            String img = cursor.getString(cursor.getColumnIndex("IMG"));
            list.add(new Food(description,keywords,img));
        }
        cursor.close();
        return  list;
    }


    public void close()  // 好的习惯，应该是要关闭数据库db的
    {
        db.close();
    }

    // 再写一个工具类，专门缓存从网上下载的图片
    public void saveImage(Bitmap bitmap,String filename)
    {
        // 存储到外部存储的私有目录的Cache目录下
        File file = new File(context.getExternalCacheDir(), filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,0,fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(fos!=null)
            {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 读取指定路径的图片
    public Bitmap getIamge(String filename,Bitmap bitmap)
    {
        File file = new File(context.getExternalCacheDir(), filename);
        if(file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }



}
