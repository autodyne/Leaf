/*******************************************************************************
 * Java Swing Library 'Leaf' and 'Tsukishiro Editor' since 2009 February 24th
 * License: GNU General Public License v3+ (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package leaf.util;

/**
 * 時間的コストの高いタスクを実行する基底クラスです。
 *
 * @author 無線部開発班
 * @since 2011年9月3日
 */
public class Task<V> {
	private boolean isCanceled = false;

	/**
	 * タスクを生成します。
	 */
	public Task() {
	}

	/**
	 * タスクの実行の中断を試みます。
	 */
	public final void cancel() {
		isCanceled = true;
	}

	/**
	 * タスクの中断を要求されたか返します。
	 *
	 * @return タスクが中断される場合true
	 */
	public final boolean isCancelled() {
		return isCanceled;
	}

	/**
	 * 現在の処理の内容の通知を受け取ります。
	 *
	 * @param obj   直後に処理するオブジェクト
	 * @param index 現在のステップ番号
	 * @param step  総ステップ数
	 */
	public void progress(V obj, int index, int step) {
	}
}
