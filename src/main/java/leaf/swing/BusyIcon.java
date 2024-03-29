/*******************************************************************************
 * Java Swing Library 'Leaf' and 'Tsukishiro Editor' since 2009 February 24th
 * License: GNU General Public License v3+ (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package leaf.swing;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.*;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 * タスクが進行中であることを視覚的に表示するアイコンです。
 *
 * @author 無線部開発班
 * @since 2010年5月23日
 */
public class BusyIcon implements Icon {
	private final Color col = Color.DARK_GRAY;
	private final double r = 2d;
	private final double sx = 0d;
	private final double sy = 0d;
	private final Dimension dim;
	private final java.util.List<Shape> list = new LinkedList<>(Arrays.asList(new Ellipse2D.Double(sx + 3 * r, sy + 0 * r, 2 * r, 2 * r), new Ellipse2D.Double(sx + 5 * r, sy + 1 * r, 2 * r, 2 * r), new Ellipse2D.Double(sx + 6 * r, sy + 3 * r, 2 * r, 2 * r), new Ellipse2D.Double(sx + 5 * r, sy + 5 * r, 2 * r, 2 * r), new Ellipse2D.Double(sx + 3 * r, sy + 6 * r, 2 * r, 2 * r), new Ellipse2D.Double(sx + 1 * r, sy + 5 * r, 2 * r, 2 * r), new Ellipse2D.Double(sx + 0 * r, sy + 3 * r, 2 * r, 2 * r), new Ellipse2D.Double(sx + 1 * r, sy + 1 * r, 2 * r, 2 * r)));

	/**
	 * アイコンを生成します。
	 */
	public BusyIcon() {
		dim = new Dimension((int) (r * 8 + sx * 2), (int) (r * 8 + sy * 2));
	}

	/**
	 * アイコンの回転アニメーションを１コマ先に進めます。
	 */
	public void next() {
		list.add(list.remove(0));
	}

	/**
	 * アイコンを描画します。
	 *
	 * @param g グラフィックス
	 * @param x 描画位置
	 * @param y 描画位置
	 */
	@Override
	public void paintIcon(Component comp, Graphics g, int x, int y) {
		var g2 = (Graphics2D) g;
		g2.setPaint(new Color(0, 0, 0, 0));
		g2.fillRect(x, y, getIconWidth(), getIconHeight());
		g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g2.setColor(col);
		g2.translate(x, y);
		var alpha = 0.0f;
		for (var s : list) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			alpha += 0.125f;
			g2.fill(s);
		}
		g2.translate(-x, -y);
	}

	/**
	 * アイコンの幅を返します。
	 *
	 * @return アイコンの幅
	 */
	@Override
	public int getIconWidth() {
		return dim.width;
	}

	/**
	 * アイコンの高さを返します。
	 *
	 * @return アイコンの高さ
	 */
	@Override
	public int getIconHeight() {
		return dim.height;
	}
}
