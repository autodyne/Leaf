/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.math;

import java.math.BigDecimal;
import static java.math.RoundingMode.HALF_EVEN;
import static leaf.util.math.Constants.LN_2;
import static leaf.util.math.Constants.PI_2DIV;
import static leaf.util.math.Constants.PI_2MUL;
import static leaf.util.math.Constants.SCALE;

/**
 *指数関数、対数関数、三角関数など数値計算を50桁の高精度で実行します。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2012年2月2日
 */
public final class LeafMath{
	private static final Exponential exponential = new Exponential();
	private static final Logarithm logarithm = new Logarithm();
	private static final Cosine cosine = new Cosine();
	
	private LeafMath(){}
	
	/**
	 *指数関数を計算します。
	 *
	 *@param exp 指数
	 *@return  指数の計算値
	 */
	public static BigDecimal exp(BigDecimal exp){
		return exponential.value(exp);
	}
	/**
	 *累乗値を計算します。
	 *
	 *@param base ベース
	 *@param exp  指数
	 *@return 累乗の計算値
	 */
	public static BigDecimal pow(BigDecimal base, BigDecimal exp){
		return exp(exp.multiply(log(base)));
	}
	/**
	 *ネイピア数を底とする自然対数を計算します。
	 *
	 *@param val 0よりも大きい数
	 *@return 自然対数値
	 *@throws ArithmeticException valが0以下の場合
	 */
	public static BigDecimal log(BigDecimal val){
		if(val.compareTo(BigDecimal.ZERO) <= 0)
		throw new ArithmeticException("log(" + val + ")");
		
		if(val.compareTo(BigDecimal.ONE) < 0)
		return log(BigDecimal.ONE.divide(val, SCALE, HALF_EVEN)).negate();
		
		int n = 0;
		for(; val.compareTo(BigDecimal.ONE) >= 0; n++){
			val = val.divide(Constants.TWO, SCALE, HALF_EVEN);
		}
		return LN_2.multiply(BigDecimal.valueOf(n)).add(logarithm.value(val));
	}
	/**
	 *正弦関数を計算します。
	 *
	 *@param rad 孤度法での角度
	 *@return 正弦値
	 */
	public static BigDecimal sin(BigDecimal rad){
		return cosine.value(rad.remainder(PI_2MUL).subtract(PI_2DIV));
	}
	/**
	 *余弦関数を計算します。
	 *
	 *@param rad 孤度法での角度
	 *@return 余弦値
	 */
	public static BigDecimal cos(BigDecimal rad){
		return cosine.value(rad.remainder(PI_2MUL));
	}
	/**
	 *正接関数を計算します。
	 *
	 *@param rad 孤度法での角度
	 *@return 正接値
	 */
	public static BigDecimal tan(BigDecimal rad){
		rad = rad.remainder(PI_2MUL);
		BigDecimal sin = cosine.value(rad.subtract(PI_2DIV));
		BigDecimal cos = cosine.value(rad);
		return sin.divide(cos, SCALE, HALF_EVEN);
	}
}