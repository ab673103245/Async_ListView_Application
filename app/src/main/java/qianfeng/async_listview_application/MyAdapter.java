package qianfeng.async_listview_application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31 0031.
 */
public class MyAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Food> list;


    // 改动1
    ViewHolder holder;
    Food food;
    private String img;

    public MyAdapter(Context context, List<Food> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context); // context是传进来的上下文
    }

    public MyAdapter() {
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        // 来个最标准的ViewHolder


        // convertView 就是已有的item资源。 这时候，只需要更新数据源，而不需要每个数据源都给它开辟一个item视图。 直接利用原有的item视图就可以了
        // 原理就是这样，convertView 就是原有的 item视图
        if(convertView == null)
        { // 里面有一套内存回收机制
            convertView = inflater.inflate(R.layout.listview,parent,false);
            holder = new ViewHolder();
            holder.iv = ((ImageView) convertView.findViewById(R.id.iv));
            holder.tv_description = ((TextView) convertView.findViewById(R.id.tv_description));
            holder.tv_keywords = ((TextView) convertView.findViewById(R.id.tv_keywords));
            // 每一个convertView:就是一个item的视图
            convertView.setTag(holder); // 顾名思义，转换视图，
        }else
        {
            holder = ((ViewHolder) convertView.getTag());
        }
        food = list.get(position);
       // holder.iv.setImageResource();
        // 这里可以对 holder.iv.大作文章
        // 在这里发出 httpURLConnection 请求吧, 再启动一个线程类
//        holder.iv.
        img = food.getImg();
        new MyAsync().execute(img);  // 这里就是开启一个任务下载，可以在这里提取到你要的名字
//       holder.iv.setImageBitmap();
        holder.tv_description.setText(food.getDescription());
        holder.tv_keywords.setText(food.getKeywords());

        return convertView;
    }
    class ViewHolder
    {
        ImageView iv;
        TextView tv_description, tv_keywords;
    }
    class MyAsync extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bit = getImage(params[0]);
            return bit;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // 拿到数据，更新UI
            // 主线程才可以更新，所以并不会线程冲突
            if(bitmap!=null)
            {
                // 我是在这里下载哦！
                holder.iv.setImageBitmap(bitmap);
                // 开一个http请求，下载图片，缓冲至本地
                // 可以把这个bitmap缓存到本地，再从本地加载图片
                //保存bitmap图片到SDCard的私有Cache目录
//            File file = new File(context.getExternalCacheDir(), img);
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG,50,fos);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }finally {
//                if(fos!=null)
//                {
//                    try {
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }




            }
        }

        private Bitmap getImage(String str)
        {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(str);
                httpURLConnection = ((HttpURLConnection) url.openConnection());
                httpURLConnection.setConnectTimeout(5*1000);
                httpURLConnection.connect();
                if(httpURLConnection.getResponseCode() == 200)
                {
                    Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                    return bitmap;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(httpURLConnection  != null)
                {
                    httpURLConnection.disconnect();
                }
            }

            return  null;

        }
    }


}
