package com.example.wsy.xpuzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.PicAdapter;
import utils.ImageUtils;
import utils.ScreenUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PopupWindow mPopupWindow;
    private int[] mResPicId; //图片资源ID
    private GridView mGv;

    public static String TEMP_IMAGE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/test.png";

    private int mType = 2; //游戏类型，默认是2*2
    private TextView tv_selected;
    private LayoutInflater mInflator;
    private TextView mTvType2;
    private TextView mTvType3;
    private TextView mTvType4;
    private TextView mTvType5;
    private TextView mTvType6;
    private String[] mCustomItems = new String[]{"本地图库", "相机拍照"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPicList = new ArrayList<Bitmap>();
        initViews();

        mGv.setAdapter(new PicAdapter(this, mPicList));
        mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断下点击的是不是最后一个item
                if (position == mPicList.size() - 1) {
                    //弹框选择
                    showChooseDialog();
                } else { //其他的话，直接将图片的资源id和难度等级带走，跳转到新的页面
                    Intent intent = new Intent(MainActivity.this, PuzzleMain.class);
                    intent.putExtra("picSelectedID", mResPicId[position]);
                    intent.putExtra("mType", mType);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 弹框让用户选择是图库浏览还是直接照相
     */
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择： ");
        builder.setItems(mCustomItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://本地图库
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setAction("android.intent.action.PICK");
                        intent.setType("image/*");
//                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, RESULT_IMAGE);
                        break;
                    case 1: //相机
                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //拍照保存到指定位置
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TEMP_IMAGE_PATH)));
                        startActivityForResult(intent1, RESULT_CAMERA);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { //正常返回才会继续
            if (requestCode == RESULT_CAMERA) {
                //相机页面返回
                Intent intent = new Intent(this, PuzzleMain.class);
                intent.putExtra("mPicPath", TEMP_IMAGE_PATH);
                intent.putExtra("mType", mType);
                startActivity(intent);
            } else if (requestCode == RESULT_IMAGE && data != null) {
                Uri uri = data.getData();
                String uriString = uri.toString();
                String path = null;
                if (uriString.startsWith("file")) { //miui--》直接給你路徑了！就不需要在查詢了
                    path = uriString.substring(7);//即 地址
                    System.out.println("图片路徑是： " + path);

                } else if (uriString.startsWith("content")) {
                    /**
                     * 当然！我们不是从广播中获取所有字段，只需要利用ContentResolver获取特定字段就好了
                     * 比如图片的id name path了
                     * 这里就获取media数据库中该图片的存储路径！是一个封装好的字符串常量
                     */
                    String[] projection = new String[]{MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    //注意了，刚获取的时候，游标位于表头那一行。。且只有1个字段。。。
                    cursor.moveToNext();
                    path = cursor.getString(0);
                    cursor.close();
                    System.out.println("这个图片的路径是： " + path);
                }

                Intent intent = new Intent(this, PuzzleMain.class);
                intent.putExtra("mPicPath", path);
                intent.putExtra("mType", mType);
                startActivity(intent);
            }
        }
    }

    // 返回码：系统图库
    private static final int RESULT_IMAGE = 100;
    // 返回码：相机
    private static final int RESULT_CAMERA = 200;
    private View mPopupView;

    /**
     * 显示popup window
     */
    public void popupShow(View view) {
        int density = (int) ScreenUtils.getDensity(this);
        //创建一个200dp *50dp的popup(挺大的！)，第一个参数是要显示出来的view
        mPopupWindow = new PopupWindow(mPopupView, 200 * density, 50 * density);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        //给popup上一个透明背景，否则可能出各种莫名其妙的问题
        Drawable transparent = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(transparent);
        //获取位置
        int[] location = new int[2];
        view.getLocationOnScreen(location); //存进去位置信息
        System.out.println("tv的位置： " + location[0] + " : " + location[1]);
        //让其显示在指定位置！
        mPopupWindow.showAtLocation(view,
                Gravity.NO_GRAVITY, location[0] - 40 * density,
                location[1] + 30 * density);
    }

    private List<Bitmap> mPicList; //真正的bm集合

    /**
     * 初始化views
     */
    private void initViews() {
        mPicList.clear();

        mGv = (GridView) findViewById(R.id.gv_xpuzzle_main_pic_list);

        //初始化bitmap数据
        mResPicId = new int[]{
                R.drawable.pic1, R.drawable.pic2, R.drawable.pic3,
                R.drawable.pic4, R.drawable.pic5, R.drawable.pic6,
                R.drawable.pic7, R.drawable.pic8, R.drawable.pic9,
                R.drawable.pic10, R.drawable.pic11, R.drawable.pic12,
                R.drawable.pic13, R.drawable.pic14, R.drawable.pic15,
                R.mipmap.ic_launcher};
//        Bitmap[] bitmaps = new Bitmap[mResPicId.length];
        //然后将所有bitmap读取出来，放到bitmaps中去
        for (int i = 0; i < mResPicId.length; i++) {
            //原始解析方法decodeResource()，弱爆了！
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mResPicId[i], options);
//            System.out.println("图片的尺寸是： " + options.outWidth + " x " + options.outHeight);

            //我靠。简直神器啊 ！！！
            Bitmap bitmap = ImageUtils.readBitmap(this, mResPicId[i]);
            mPicList.add(bitmap); //并且添加到集合中去
        }

        //显示难度等级的tv--》xml不居中已经设置好初始的了。
        tv_selected = (TextView) findViewById(R.id.tv_puzzle_main_type_selected);

        //获取布局填充器->填充出待选的几个难度选项
        mInflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mPopupView = mInflator.inflate(R.layout.xpuzzle_main_type_selected, null);
        //找到3个tv，上监听
        mTvType2 = (TextView) mPopupView.findViewById(R.id.tv_main_type_2);
        mTvType3 = (TextView) mPopupView.findViewById(R.id.tv_main_type_3);
        mTvType4 = (TextView) mPopupView.findViewById(R.id.tv_main_type_4);
        mTvType5 = (TextView) mPopupView.findViewById(R.id.tv_main_type_5);
        mTvType6 = (TextView) mPopupView.findViewById(R.id.tv_main_type_6);
        mTvType2.setOnClickListener(this);
        mTvType3.setOnClickListener(this);
        mTvType4.setOnClickListener(this);
        mTvType5.setOnClickListener(this);
        mTvType6.setOnClickListener(this);

    }

    /**
     * 难度选择tv的点击侦听
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_type_2:
                mType = 2;
                tv_selected.setText("2 X 2");
                break;
            case R.id.tv_main_type_3:
                mType = 3;
                tv_selected.setText("3 X 3");
                break;
            case R.id.tv_main_type_4:
                mType = 4;
                tv_selected.setText("4 X 4");
                break;
            case R.id.tv_main_type_5:
                mType = 5;
                tv_selected.setText("5 X 5");
                break;
            case R.id.tv_main_type_6:
                mType = 6;
                tv_selected.setText("6 X 6");
                break;
        }
        //关闭弹窗
        mPopupWindow.dismiss();
    }

}
