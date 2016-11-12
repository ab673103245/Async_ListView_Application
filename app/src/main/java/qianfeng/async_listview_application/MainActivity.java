package qianfeng.async_listview_application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//http://www.tngou.net/api/food/list?id=1
public class MainActivity extends AppCompatActivity {

    private List<Food> list;
    private ListView lv;
    private FoodDB foodDB;
    private List<String> pictureList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodDB = new FoodDB(this);
        lv = (ListView) findViewById(R.id.lv);

        new MyAsyncTask().execute("http://www.tngou.net/api/food/list?id=1"); // 启动了这个 execute()方法后，要等你的操作全部完成后，
                    // 才可以回到这个方法，否则，会有线程安全问题，这不是我们希望看到的，希望引以为戒。 要操作全部完成后，才可以回到onCreate()方法

    }

    private class MyPictureTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            foodDB.saveImage(getPicture(params[0]),params[0]); // 下载并保存到本地

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }

        private Bitmap getPicture(String str)
        {
            HttpURLConnection con = null;
            try {
                URL url = new URL(str);
                con  = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5*1000);
                con.connect();
                if(con.getResponseCode() == 200)
                {
                    return BitmapFactory.decodeStream(con.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private class MyAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            // 获取网络管理的系统服务 ConnectivityManager
            ConnectivityManager systemService = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            // 得到网络连接的信息
            NetworkInfo activeNetwork = systemService.getActiveNetworkInfo();
            if(activeNetwork == null || !activeNetwork.isAvailable())
            {
                return null;
            }

            String result = getHttp(params[0]);
            Log.d("Debug_google:", "doInBackground: " + result);

            return result; // 数据已经成功拿到了

        }

        @Override
        protected void onPostExecute(String s) {

            // 取完数据之后，要解析，没问题，问题是这个解析后的数据源适配器问题，只能放在这里，而不能在onCreate方法中。
            // 因为如果放在onCreate()中，list可能没有new出来，这就会引发 空指针异常。
            parseJSON(s);

//            List<Food> dataByKeywords = foodDB.getDataByKeywords("白");
            List<Food> dataByKeywords = foodDB.getDataByKeywords("白");
            MyAdapter adapter = new MyAdapter(MainActivity.this, dataByKeywords);
            lv.setAdapter(adapter);





        }

        private void parseJSON(String ss)
        {
            // 下面要在这里进行解析
            // 开始解析了
            String description = null;
            String keywords = null;
            String img = null;
            list = new ArrayList<>();

            try {
                JSONObject js = new JSONObject(ss);
                JSONArray tngou = js.getJSONArray("tngou");
                for(int i = 0; i < tngou.length(); i++)
                {
                    JSONObject rr = tngou.getJSONObject(i);
                    description = rr.getString("description");
                    keywords = rr.getString("keywords");
                    img = "http://tnfs.tngou.net/image"+rr.getString("img");
                    Food food  = new Food(description,keywords,img);
                    Log.d("qianfeng", "onPostExecute: " + food + "\n");
                   new MyPictureTask().execute(img); // 开启下载图片到本地的任务！！
                    list.add(food); // 数据源已经有了
                    pictureList.add(img);
                }
                
                foodDB.saveData(list); // 将数据存储进数据库中

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private String getHttp(String str)
        {
            HttpURLConnection httpURLConnection = null;
            BufferedReader br = null;
            try {
                URL url = new URL(str);
                httpURLConnection  = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(5*1000);
                httpURLConnection.connect();
                if(httpURLConnection.getResponseCode() == 200)
                {
                    StringBuffer buffer = new StringBuffer();


                    br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String ret = null;
                    while((ret = br.readLine())!=null)
                    {
                        buffer.append(ret);
                    }
                    return buffer.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try{
                    if(br!= null)
                    {
                        br.close();
                    }
                    if(httpURLConnection != null)
                    {
                        httpURLConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }




}
