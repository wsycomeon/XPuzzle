package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.example.wsy.xpuzzle.PuzzleMain;
import com.example.wsy.xpuzzle.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import bean.ItemBean;

/**
 * Created by WSY on 2016-02-03-0003.、
 * 图像工具类：实现图像的分割与自适应
 */
public class ImageUtils {
    /**
     * 分割小图
     */
    public static void createInitBitmaps(int type, Bitmap picSelected, Context context) {
        GameUtils.mItembeans.clear();//擦！！！！
        System.gc();
        //求每个item的宽高
        int itemWidth = picSelected.getWidth() / type;
        int itemHeight = picSelected.getHeight() / type;
        //然后，分割出小的bitmap
        for (int i = 1; i <= type; i++) { //一行行的
            //先切出一行
            for (int j = 1; j <= type; j++) {
                Bitmap bitmap = Bitmap.createBitmap(picSelected, (j - 1) * itemWidth, (i - 1) * itemHeight, itemWidth, itemHeight);
                //然后，创建一个bean实体
                ItemBean itemBean = new ItemBean((i - 1) * type + j, (i - 1) * type + j, bitmap);
                //然后添加到全局的工具类中去
                GameUtils.mItembeans.add(itemBean);
            }
        }
        //将最后一个图片保存下来，最玩家拼图成功后，填充出来
        PuzzleMain.mLastBitmap = GameUtils.mItembeans.get(type * type - 1).getmBitmap();

        //然后，将最后一个置空
        GameUtils.mItembeans.remove(type * type - 1);
//        //创建出一个空图，设置上去--->操！内存溢出了！5.73m !!!!
//        BitmapFactory.Options options = new BitmapFactory.Options();
////        options.inSampleSize = 3; //宽高都压缩2倍
//        options.inPreferredConfig = Bitmap.Config.ALPHA_8;//即一个像素1字节
//        Bitmap blank = BitmapFactory.decodeResource(context.getResources(), R.drawable.blank, options);

        /**
         * 全部改用下面的神器方法！
         */
        Bitmap blank = readBitmap(context, R.drawable.blank);

        //切个小空图，直接create直接切出一个更小的图！！！
//        Bitmap blankP = Bitmap.createBitmap(blank, 0, 0, Math.min(itemWidth, blank.getWidth()),
//                Math.min(itemHeight, blank.getHeight()));

        Bitmap blankP = resizeBitmap(itemWidth, itemHeight, blank);
        //空图。最开始在最后一位，并且btId为0！
        GameUtils.mItembeans.add(new ItemBean(type * type, 0, blankP));
        GameUtils.mBlankItemBean = GameUtils.mItembeans.get(type * type - 1);
    }

    /**
     * 处理图片--》调整尺寸（不是裁剪！！！）
     * 通过变换矩阵
     */
    public static Bitmap resizeBitmap(float newWidth, float newHeight, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(newWidth / bitmap.getWidth(), newHeight / bitmap.getHeight());
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return newBitmap;
    }

    public static Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, options);
    }

    public static Bitmap readFileBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        FileInputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(is, null, options);
    }


}
