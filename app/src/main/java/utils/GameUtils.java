package utils;

import com.example.wsy.xpuzzle.PuzzleMain;

import java.util.ArrayList;
import java.util.List;

import bean.ItemBean;

/**
 * Created by WSY on 2016-02-02-0002.
 * 拼图工具类：实现拼图的交换与生成算法
 */
public class GameUtils {
    //游戏信息单元格bean
    public static List<ItemBean> mItembeans = new ArrayList<ItemBean>();
    public static ItemBean mBlankItemBean; //空图信息实体

    /**
     * 生成随机的item
     */
    public static void getPuzzleGenerator() {
        //随机打算顺序
        for (int i = 0; i < mItembeans.size(); i++) {
            //生成一个范围内的随机数-->取出一个item
            int index = (int) (Math.random() * (PuzzleMain.TYPE * PuzzleMain.TYPE));
            //然后将这个位置与blank对调
            swapItems(mItembeans.get(index), GameUtils.mBlankItemBean);
        }
        /**
         * 经过前面这么多次的交换移动与对调！即完全打乱顺序之后
         * 我们还要判断下这个顺序是否有解！
         */
        List<Integer> bitmapIDs = new ArrayList<Integer>();
        //前面交换的主要是每个item上的bt,所以，将每个item上的btId收进来
        for (int i = 0; i < mItembeans.size(); i++) {
            bitmapIDs.add(mItembeans.get(i).getmBitmapId());
        }

        //然后做判断
        if (canSolve(bitmapIDs)) {
            //有解，直接结束了
            return;
        } else {
            //否则，重新生成！！！
            getPuzzleGenerator();
        }


    }

    /**
     * 判断一个集合是否有解
     * 条件：
     * （1）奇数个：倒置变量值之和sum为偶数
     * （2）偶数个：
     * 若，空格的position在从下往上数的奇数行，则sum为偶数
     * 或者，在偶数行，sum为奇数
     */
    private static boolean canSolve(List<Integer> bitmapIDs) {
        //首先获取现在空格图所在的位置
        int position = GameUtils.mBlankItemBean.getmItemId();
        //然后开始判断
        if (bitmapIDs.size() % 2 == 1) {  //奇数个
            return getInversions(bitmapIDs) % 2 == 0;
        } else { //偶数个
            if (inOushuhang(position, PuzzleMain.TYPE)) {
                return getInversions(bitmapIDs) % 2 == 1;
            } else {
                return getInversions(bitmapIDs) % 2 == 0;
            }
        }
    }

    /**
     * 判断某position是不是在从下往上数的偶数行！！！
     */
    public static boolean inOushuhang(int position, int type) {
        //这是参考答案的！明显感觉有错误！！！
//        if (((position - 1) / type) % 2 == 0) {
//            return true;
//        } else {
//            return false;
//        }

        //自己写的
        int lineNumber = (type * type - position) / type + 1;
        if (lineNumber % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取出一个集合的倒置和sum
     */
    private static int getInversions(List<Integer> bitmapIDs) {
        int sum = 0;
        int count = 0;
        for (int i = 0; i < bitmapIDs.size(); i++) {
            Integer num = bitmapIDs.get(i); //先取出这个数，然后从这个数后面开始继续遍历
            for (int j = i + 1; j < bitmapIDs.size(); j++) {
                int next = bitmapIDs.get(j);
                //如果，next不为0，并且，也比num小，符合条件！可以
                if (next != 0 && next < num) {
                    count++;
                }
            }
            //遍历结束后，整个叠加，当前count置空
            sum = sum + count;
            count = 0;
        }
        return sum;
    }

    /**
     * 交换blank与点击item的位置
     */
    public static void swapItems(ItemBean from, ItemBean blank) {
        ItemBean tempBean = new ItemBean();
        //其实主要是交换item上的bt（空图）与btID（ 0 ）
        tempBean.setmBitmap(from.getmBitmap());
        from.setmBitmap(blank.getmBitmap());
        blank.setmBitmap(tempBean.getmBitmap());

        tempBean.setmBitmapId(from.getmBitmapId());
        from.setmBitmapId(blank.getmBitmapId());
        blank.setmBitmapId(tempBean.getmBitmapId());

        //bt交换玩之后，要把工具类代表最后一个item的静态变量换掉
        GameUtils.mBlankItemBean = from;
    }

    /**
     * 判断点击的item是否可以移动
     * 看这个position是否在blank的上下左右
     */
    public static boolean isMoveable(int position) {
        int type = PuzzleMain.TYPE;
        //blank的itemId是从1开始的，所以为了和从0开始的相匹配，需要-1
        int blankPosition = GameUtils.mBlankItemBean.getmItemId() - 1;
//        System.out.println("position = " + position + " blankPosition = " + blankPosition);

        //不同行的话，差值为type
        if (Math.abs(position - blankPosition) == type) {
            return true;
        }
        if ((blankPosition / type == position / type) && Math.abs(blankPosition - position) == 1) {
            //如果同行，并且差值为1
            return true;
        }
        return false;
    }

    /**
     * 判断拼图是否已经成功-->
     * 即看btid是否和itemId一一对应！！！
     * 有一个错的，就表示没成功!!!
     */
    public static boolean isSuccess() {
        //遍历bean集合
        for (ItemBean itemBean : GameUtils.mItembeans) {
            //非blank图，
            if (itemBean.getmBitmapId() != 0 && itemBean.getmBitmapId() == itemBean.getmItemId()) {
                continue; //这个ok，继续下一个判断
            } else if (itemBean.getmBitmapId() == 0 && itemBean.getmItemId() == PuzzleMain.TYPE * PuzzleMain.TYPE) {
                continue;
            } else {
                return false;
            }
        }
        //如果前面都ok这里就返回true！
        return true;
    }

}
