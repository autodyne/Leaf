/*******************************************************************************
 * Java Swing Library 'Leaf' and 'Tsukishiro Editor' since 2009 February 24th
 * License: GNU General Public License v3+ (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package leaf.swing;

import java.awt.*;
import java.io.File;
import java.io.Serializable;

/**
 * 壁紙に関する設定を保持しておくためのBeanクラスです。
 *
 * @author 無線部開発班
 * @since 2011年12月18日
 */
public final class WallpaperSettings implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private String photoFilePath; // FileではXMLへの出力不能
	private Rectangle photoTrimRect;
	private float photoAlpha = 1f;
	private Color backgroundColor = Color.WHITE;

	/**
	 * 空のオブジェクトを生成します。
	 */
	public WallpaperSettings() {
	}

	/**
	 * このオブジェクトの複製を返します。
	 *
	 * @return 複製
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	/**
	 * 壁紙の基調背景色を返します。
	 *
	 * @return 背景色
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * 壁紙の基調背景色を設定します。
	 *
	 * @param color 背景色
	 */
	public void setBackgroundColor(Color color) {
		this.backgroundColor = color != null ? color : Color.WHITE;
	}

	/**
	 * 壁紙に用いる写真を表示する際の透明度を返します。
	 *
	 * @return アルファ値
	 */
	public float getPhotoAlpha() {
		return photoAlpha;
	}

	/**
	 * 壁紙に用いる写真を表示する際の透明度を設定します。
	 *
	 * @param alpha アルファ値
	 */
	public void setPhotoAlpha(float alpha) {
		this.photoAlpha = alpha;
	}

	/**
	 * 壁紙に用いる写真ファイルを返します。
	 *
	 * @return ファイル
	 */
	public File getPhotoFile() {
		return photoFilePath != null ? new File(photoFilePath) : null;
	}

	/**
	 * 壁紙に用いる写真ファイルへのパスを返します。
	 *
	 * @return ファイルパス
	 */
	public String getPhotoFilePath() {
		return photoFilePath;
	}

	/**
	 * 壁紙に用いる写真ファイルへのパスを設定します。
	 *
	 * @param path ファイルパス
	 */
	public void setPhotoFilePath(String path) {
		this.photoFilePath = path;
	}

	/**
	 * 壁紙に用いる写真を表示する矩形を返します。
	 *
	 * @return 矩形
	 */
	public Rectangle getPhotoTrimRect() {
		return photoTrimRect;
	}

	/**
	 * 壁紙に用いる写真を表示する矩形を設定します。
	 *
	 * @param rect 矩形
	 */
	public void setPhotoTrimRect(Rectangle rect) {
		this.photoTrimRect = rect;
	}

}
