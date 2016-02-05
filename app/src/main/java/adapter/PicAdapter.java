package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

import utils.ScreenUtils;

/**
 * Created by WSY on 2016-02-02-0002
 * 程序主界面的数据适配器
 * 展示的只是图片
 */
public class PicAdapter extends BaseAdapter {

    private List<Bitmap> picList; //数据集合
    private Context context;

    public PicAdapter(Context context, List<Bitmap> picList) {
        this.context = context;
        this.picList = picList;
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    @Override
    public Object getItem(int position) {
        return picList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv = null;
        int density = (int) ScreenUtils.getDensity(context);
        if (convertView == null) {
            iv = new ImageView(context);
            //设置iv大小，和图片缩放类型
            iv.setLayoutParams(new GridView.LayoutParams(80 * density, 100 * density));
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            iv = (ImageView) convertView;
        }

        //这里实打实的填充数据
        iv.setBackgroundColor(Color.BLACK);
        iv.setImageBitmap(picList.get(position));
        return iv;
    }
}
