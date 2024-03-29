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
 * カーソルに関する設定を保持しておくためのBeanクラスです。
 *
 * @since 2011年12月28日
 */
public final class CursorSettings implements Serializable, Cloneable {

	/**
	 * このオブジェクトをシリアライズする場合に用いられます。
	 */
	private static final long serialVersionUID = 1L;

	private String photoFilePath = null;
	private Rectangle photoTrimRect = null;
	private int type = Cursor.DEFAULT_CURSOR;

	/**
	 * 空のオブジェクトを生成します。
	 */
	public CursorSettings() {
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
	 * カーソルに用いる写真ファイルを返します。
	 *
	 * @return ファイル
	 */
	public File getPhotoFile() {
		return photoFilePath != null ? new File(photoFilePath) : null;
	}

	/**
	 * カーソルに用いる写真ファイルへのパスを返します。
	 *
	 * @return ファイルパス
	 */
	public String getPhotoFilePath() {
		return photoFilePath;
	}

	/**
	 * カーソルに用いる写真ファイルへのパスを設定します。
	 *
	 * @param path ファイルパス
	 */
	public void setPhotoFilePath(String path) {
		this.photoFilePath = path;
	}

	/**
	 * カーソルに用いる写真を表示する矩形を返します。
	 *
	 * @return 矩形
	 */
	public Rectangle getPhotoTrimRect() {
		return photoTrimRect;
	}

	/**
	 * カーソルに用いる写真を表示する矩形を設定します。
	 *
	 * @param rect 矩形
	 */
	public void setPhotoTrimRect(Rectangle rect) {
		this.photoTrimRect = rect;
	}

	/**
	 * 矢印や十字など、カーソルの形式を返します。
	 *
	 * @return カーソルの形式
	 */
	public int getType() {
		return type;
	}

	/**
	 * 矢印や十字など、カーソルの形式を設定します。
	 *
	 * @param type カーソルの形式
	 */
	public void setType(int type) {
		this.type = type;
	}
}
