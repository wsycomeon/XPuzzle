package bean;

import android.graphics.Bitmap;

/**
 * Created by WSY on 2016-02-03-0003.
 * 单个拼图Item的逻辑实体类：封装逻辑相关属性
 */
public class ItemBean {
    private int mItemId; //iv的id
    private int mBitmapId;//空格bitmap为0，图片的id
    private Bitmap mBitmap;//bitmap实体

    public ItemBean(int mItemId, int mBitmapId, Bitmap mBitmap) {
        this.mItemId = mItemId;
        this.mBitmapId = mBitmapId;
        this.mBitmap = mBitmap;
    }

    public ItemBean() {
    }

    public int getmItemId() {
        return mItemId;
    }

    public int getmBitmapId() {
        return mBitmapId;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmItemId(int mItemId) {
        this.mItemId = mItemId;
    }

    public void setmBitmapId(int mBitmapId) {
        this.mBitmapId = mBitmapId;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }
}
