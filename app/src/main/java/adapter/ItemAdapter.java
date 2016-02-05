package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by WSY on 2016-02-03-0003.
 * 小图item的适配器
 */
public class ItemAdapter extends BaseAdapter {

    private List<Bitmap> mBitmapList;
    private Context mContext;

    public ItemAdapter(List<Bitmap> mBitmapList, Context mContext) {
        this.mBitmapList = mBitmapList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mBitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBitmapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitmap bt = mBitmapList.get(position);
        ImageView iv = null;
        if (convertView == null) {
            iv = new ImageView(mContext);
            iv.setLayoutParams(new GridView.LayoutParams(bt.getWidth(),
                    bt.getHeight()));
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            iv = (ImageView) convertView;
        }
        iv.setImageBitmap(bt);
        return iv;
    }
}
