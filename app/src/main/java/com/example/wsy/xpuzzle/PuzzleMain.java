package com.example.wsy.xpuzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import adapter.ItemAdapter;
import bean.ItemBean;
import utils.GameUtils;
import utils.ImageUtils;
import utils.ScreenUtils;

public class PuzzleMain extends AppCompatActivity implements View.OnClickListener {
    //拼图成功后，显示的最后一张小图片
    public static Bitmap mLastBitmap;
    //难度等级
    public static int TYPE = 2;
    private static int STEP_COUNT = 0;
    private static int TIMER_COUNT = 0; //时间显示
    /**
     * 更新时间
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //更新计时器
                    TIMER_COUNT++;
                    tv_time.setText(TIMER_COUNT + " 秒");
                    break;
            }
        }
    };

    private TimerTask mTimerTask;//计时器分支线程
    private Bitmap mPic; //用于切图的大图
    private Button mBtnImage;
    private Button mBtnBack;
    private Button mBtnRestart;
    private GridView mGV;
    private TextView tv_steps;
    private TextView tv_time;
    private ItemAdapter adapter;
    private Timer mTimer;
    private ImageView mIv;
    private String mPicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_main);
        Bundle bundle = getIntent().getExtras();
        //获取目标大图
        Bitmap picSelected;
        //看看传来的是默认图片还是自定义图片
        int picSelectedID = bundle.getInt("picSelectedID", 0);
        mPicPath = bundle.getString("mPicPath");
        if (picSelectedID != 0) {
//            picSelected = BitmapFactory.decodeResource(getResources(), picSelectedID);
            picSelected = ImageUtils.readBitmap(this, picSelectedID);
        } else {
            //否则就是自定义的图片
            picSelected = BitmapFactory.decodeFile(mPicPath);
//            picSelected = ImageUtils.readFileBitmap(mPicPath);
        }

        System.out.println("选中的图片是： " + picSelected);

        //取出难度
        TYPE = bundle.getInt("mType", 2);
        //先对大图进行缩放--->得到合适的图片
        handleImage(picSelected);
        //初始化views
        initViews();
        //生成游戏数据
        generateGame();
        //给gv上点击
        mGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (GameUtils.isMoveable(position)) { //是否可以移动
                    GameUtils.swapItems(GameUtils.mItembeans.get(position),
                            GameUtils.mBlankItemBean);
                    //重置集合数据
                    recreateData();
                    //刷新Ui
                    adapter.notifyDataSetChanged();
                    //更新步数
                    STEP_COUNT++;
                    tv_steps.setText("" + STEP_COUNT);
                    //否段是否已经成功！
                    if (GameUtils.isSuccess()) {
                        //将最后一张图显示完整
                        mPicList.remove(TYPE * TYPE - 1);
                        mPicList.add(mLastBitmap);
                        //再次刷新Ui
                        adapter.notifyDataSetChanged();
                        Toast.makeText(PuzzleMain.this, "拼图成功！！！",
                                Toast.LENGTH_LONG).show();
                        mGV.setEnabled(false);//设置gv不可用，至于怎么不可用的。根据子类的不同而不同。。
                        //取消定时器
                        mTimer.cancel();
                        mTimerTask.cancel();
                    }
                }
            }
        });
        //给3个button上监听
        mBtnBack.setOnClickListener(this);
        mBtnImage.setOnClickListener(this);
        mBtnRestart.setOnClickListener(this);
    }

    private void recreateData() {
        mPicList.clear();
        for (ItemBean bean : GameUtils.mItembeans) {
            mPicList.add(bean.getmBitmap());
        }
    }

    private List<Bitmap> mPicList = new ArrayList<Bitmap>();

    /**
     * 切图，填充，开始计时
     */
    private void generateGame() {
        ImageUtils.createInitBitmaps(TYPE, mPic, this); //切图-->初始化GameUtils数据
        GameUtils.getPuzzleGenerator(); //数据随机打乱
        mPicList.clear();
        //填充bt集合
        for (ItemBean bean : GameUtils.mItembeans) {
            mPicList.add(bean.getmBitmap());
        }
        adapter = new ItemAdapter(mPicList, this);
        mGV.setAdapter(adapter);

        //启动计时器
        mTimer = new Timer(true);
        mTimerTask = new TimerTask() {
            @Override
            public void run() { //分支线程具体内容
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };
        //立即执行，每次间隔1s
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private boolean mIsShowing = false; //原图是否正在显示

    private void initViews() {
        mBtnImage = (Button) findViewById(R.id.btn_puzzle_main_img);
        mBtnRestart = (Button) findViewById(R.id.btn_puzzle_main_restart);
        mBtnBack = (Button) findViewById(R.id.btn_puzzle_main_back);

        //如果用代码设置了params!!!就一定要设置完全！因为xml中设置的被抵消了！！！
        mGV = (GridView) findViewById(R.id.gv_puzzle_main_detail);
        mGV.setNumColumns(TYPE); //设置列数

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                mPic.getWidth(), mPic.getHeight());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);//水平居中
        params.addRule(RelativeLayout.BELOW, R.id.ll_puzzle_main_spinner); //在spinner之下
        params.addRule(RelativeLayout.ABOVE, R.id.ll_puzzle_main_btnss);
        mGV.setLayoutParams(params);
        mGV.setHorizontalSpacing(0);//item的水平间距为0
        mGV.setVerticalSpacing(0); //xml中本来可以设置

        tv_steps = (TextView) findViewById(R.id.tv_puzzle_main_counts);
        tv_steps.setText(STEP_COUNT + ""); //设置步数
        tv_time = (TextView) findViewById(R.id.tv_puzzle_main_time);
        tv_time.setText("0 秒");
        addOriginImage(); //先把原图添加上去，并且GONE
    }

    private void addOriginImage() {
        RelativeLayout root = (RelativeLayout) findViewById(R.id.rl_puzzle_main_main_layout);
        mIv = new ImageView(this);
        mIv.setImageBitmap(mPic);
        int width = (int) (mPic.getWidth() * 0.9f);
        int height = (int) (mPic.getHeight() * 0.9f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mIv.setLayoutParams(params);
        root.addView(mIv); //添加上去
        mIv.setVisibility(View.GONE);
    }

    /**
     * 将图片变为屏幕宽高的0.8！！！！这尼玛肯定不合适啊！
     */
    private void handleImage(Bitmap picSelected) {
        int screenWidth = ScreenUtils.getMetrics(this).widthPixels;
        int screenHeight = ScreenUtils.getMetrics(this).heightPixels;
        //调整成适当大小
        mPic = ImageUtils.resizeBitmap(screenWidth * 0.7f, screenHeight * 0.7f, picSelected);

    }

    /**
     * 处理btn的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_puzzle_main_img: //显示原图
                Animation animation_show = AnimationUtils.loadAnimation(this, R.anim.anim_show);
                Animation animation_hide = AnimationUtils.loadAnimation(this, R.anim.anim_hide);
                if (mIsShowing) { //已经打开了
                    mIv.startAnimation(animation_hide);
                    mIv.setVisibility(View.GONE);
                    mIsShowing = false;
                } else {
                    mIv.startAnimation(animation_show);
                    mIv.setVisibility(View.VISIBLE);
                    mIsShowing = true;
                }
                break;
            case R.id.btn_puzzle_main_restart: //重置按钮
                cleanConfig();
                generateGame();
                tv_steps.setText("" + STEP_COUNT);
                mGV.setEnabled(true);
                break;
            case R.id.btn_puzzle_main_back: //返回键
                cleanConfig();
                finish();
                break;
        }
    }


    /**
     * 清空相关参数设置
     */
    private void cleanConfig() {
        GameUtils.mItembeans.clear();
        mTimerTask.cancel();
        mTimer.cancel();
        STEP_COUNT = 0;
        TIMER_COUNT = 0;
        //清除拍摄的图片-->擦！
        if (mPicPath != null) {
            System.out.println("------清除图片-------------" + mPicPath);
            File file = new File(mPicPath);
//            if (file.exists()) {
//                if (file.delete()) {
//                    System.out.println("-------清除成功--------");
//                }
//            }
        }
    }
}
