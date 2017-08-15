package com.yjkmust.arcgisdemo.Utils;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * 像素相关工具类
 * 
 * 安卓代码中，凡是方法里需要尺寸的地方，如果直接使用数字均是以像素数来衡量的
 * 例如 etName.setTextSize(16)，这里是将etName中使用的字体大小设置为16px
 * 如果要使用布局文件中经常使用的sp或dp，需要利用TypedValue进行一下转换
 */
public class PixelUtils {
	private static PixelUtils mInstance ;
	public static PixelUtils getInstance() {
		if (mInstance == null) {
			synchronized (PixelUtils.class) {
				if (mInstance == null) {
					mInstance = new PixelUtils();
				}
			}
		}
		return mInstance;
	}
	/**
	 * 将sp转为px
	 * 
	 * @param res 
	 * @param sp 数字使用的是sp单位
	 * @return 将sp转为对应的px值
	 */
	public int sp2Px(Resources res, int sp){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, res.getDisplayMetrics());
	}
	
	/**
	 * 将dp转为px
	 * @param res
	 * @param dp 数字使用的是dp单位
	 * @return 将dp转为对应的px值
	 */
	public int dp2Px(Resources res, int dp){
		//dp设置padding值
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}
}
