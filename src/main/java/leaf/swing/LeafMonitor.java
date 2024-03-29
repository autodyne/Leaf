/*******************************************************************************
 * Java Swing Library 'Leaf' and 'Tsukishiro Editor' since 2009 February 24th
 * License: GNU General Public License v3+ (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package leaf.swing;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

/**
 * 離散量をリアルタイムに監視するユーザーインターフェースです。
 *
 * @author 無線部開発班
 * @since 2011年7月23日
 */
public abstract class LeafMonitor extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final int BAR_CELL_WIDTH = 20;
	private static final int BAR_NUMBER_OF_CELLS = 20;
	private static final int GRAPH_GRID_INTERVAL = 20;
	private static final int GRAPH_NUMBER_OF_GRIDS = 20;
	private static final int MARGIN = 5;

	private final int step;
	private final Line2D tangent;
	private final Rectangle mfrect;
	private final Rectangle murect;
	private BufferedImage image;
	private Color gridColor = Color.GRAY;
	private Graphics2D graphics;
	private Timer timer;
	private int interval = 100, sample, bufindex = 0, column = 0;
	private Dimension size;
	private int[] buffer = new int[100];

	/**
	 * 100段階表示のグラフモニターを作成します。
	 */
	public LeafMonitor() {
		this(100);
	}

	/**
	 * 観測対象量の量子化段階数を指定してグラフモニターを作成します。
	 *
	 * @param step 量子化段階数
	 */
	public LeafMonitor(final int step) {
		super();
		this.step = step;
		setForeground(Color.GREEN);
		setBackground(Color.BLACK);
		tangent = new Line2D.Float();
		mfrect = new Rectangle();
		murect = new Rectangle();
		setPreferredSize(new Dimension(200, 110));
		addComponentListener(new ResizeListener());
	}

	/**
	 * モニターの表示を初期化します。
	 */
	public void clear() {
		clear(buffer.length);
	}

	private void clear(int width) {
		buffer = new int[width];
		bufindex = 0;
	}

	/**
	 * モニターのグリッドの表示色を返します。
	 *
	 * @return グリッドを表示するのに用いる色
	 */
	public Color getGridColor() {
		return gridColor;
	}

	/**
	 * モニターのグリッドの表示色を設定します。
	 *
	 * @param color グリッドを表示するのに用いる色
	 */
	public void setGridColor(Color color) {
		final var old = gridColor;
		this.gridColor = (color == null) ? Color.WHITE : color;
		firePropertyChange("gridColor", old, color);
	}

	/**
	 * モニターのサンプリング周期の設定値をミリ秒単位で返します。
	 *
	 * @return サンプリング周期
	 */
	public int getSamplingInterval() {
		return interval;
	}

	/**
	 * モニターのサンプリング周期をミリ秒単位で設定します。
	 *
	 * @param ms サンプリング周期
	 *
	 * @throws IllegalArgumentException 正数でない周期を指定した場合
	 */
	public void setSamplingInterval(int ms) throws IllegalArgumentException {
		final var old = this.interval;
		if (ms > 0) this.interval = ms;
		else throw new IllegalArgumentException("not positive : " + ms);
		firePropertyChange("samplingInterval", old, ms);
	}

	/**
	 * モニターの自動的なサンプリング動作が稼働中であるか返します。
	 *
	 * @return 稼働中である場合は真 停止中である場合は偽
	 */
	public boolean isAutoSamplingEnabled() {
		return timer != null;
	}

	/**
	 * モニターの自動的なサンプリング動作を開始または停止します。
	 *
	 * @param b 開始する場合は真 停止する場合は偽
	 */
	public synchronized void setAutoSamplingEnabled(boolean b) {
		if (b != (timer == null)) return;
		if (b) {
			timer = new Timer(true);
			timer.schedule(new AutoSamplingTask(), 0, interval);
		} else {
			timer.cancel();
			timer = null;
		}
		firePropertyChange("autoSamplingEnabled", !b, b);
	}

	private void paintBarGraph(final int value) {
		final var height = size.height - MARGIN;
		final var cellHeight = height / BAR_NUMBER_OF_CELLS;
		final var filledLevel = BAR_NUMBER_OF_CELLS * value / step;
		mfrect.setSize(BAR_CELL_WIDTH - 1, cellHeight);
		murect.setSize(BAR_CELL_WIDTH, cellHeight - 1);
		graphics.setColor(getForeground());
		var i = 0;
		for (; i < filledLevel; i++) {
			mfrect.setLocation(MARGIN, MARGIN + i * cellHeight);
			graphics.draw(mfrect);
		}
		for (; i < BAR_NUMBER_OF_CELLS; i++) {
			murect.setLocation(MARGIN, MARGIN + i * cellHeight);
			graphics.fill(murect);
		}
	}

	/**
	 * モニターを描画します。
	 *
	 * @param g モニタを描画するのに用いるグラフィックス
	 */
	@Override
	protected void paintComponent(Graphics g) {
		if (image != null) {
			graphics.setColor(getBackground());
			graphics.fillRect(0, 0, size.width, size.height);
			graphics.setColor(getForeground());
			paintBarGraph(sample);
			paintCurvedGraph();
			g.drawImage(image, 0, 0, this);
		} else super.paintComponent(g);
	}

	private void paintCurve(int x, int y, int w, int h) {
		graphics.setColor(getForeground());
		buffer[bufindex] = 0;
		for (int n = x, m = bufindex; n < x + w; n++, m++) {
			if (m == buffer.length) m = 0;
			var buf1 = (m >= 0) ? buffer[m] : buffer[w - 1];
			var buf2 = (m >= 1) ? buffer[m - 1] : buffer[w - 1];
			if (buf1 == 0 || buf2 == 0) continue;
			if (buf1 == buf2) graphics.fillRect(n, y + buf1 * h / step, 1, 1);
			else graphics.drawLine(n - 1, y + buf2 * h / step, n, y + buf1 * h / step);
		}
	}

	private void paintCurvedGraph() {
		final var height = size.height - MARGIN;
		final var gridHeight = height / GRAPH_NUMBER_OF_GRIDS;
		if (gridHeight > 0) {
			final var x = BAR_CELL_WIDTH + 10;
			final var y = MARGIN;
			final var w = size.width - x - MARGIN;
			final var h = gridHeight * GRAPH_NUMBER_OF_GRIDS;
			paintGrid(gridHeight, x, y, w, h);
			if (w > 0 && w != buffer.length) clear(w);
			paintCurve(x, y, w, h);
		}
	}

	private void paintGrid(int gridHeight, int x, int y, int w, int h) {
		graphics.setColor(gridColor);
		graphics.drawRect(x, y, w, h);
		for (var n = y; n <= h; n += gridHeight) {
			tangent.setLine(x, n, x + w, n);
			graphics.draw(tangent);
		}
		for (var n = x + column; n < w + x; n += GRAPH_GRID_INTERVAL) {
			tangent.setLine(n, y, n, y + h);
			graphics.draw(tangent);
		}
	}

	/**
	 * モニターによって自動的にトリガーされ、観測対象量をサンプリングします。
	 *
	 * @return モニターが定期的にサンプリングする量
	 */
	protected abstract int sample();

	private class ResizeListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			final int width = getWidth(), height = getHeight();
			if (width > 0 && height > 0) {
				size = new Dimension(width, height);
				image = (BufferedImage) createImage(width, height);
				if (image != null) graphics = image.createGraphics();
			}
		}
	}

	private class AutoSamplingTask extends TimerTask {
		@Override
		public void run() {
			sample = Math.min(step, Math.max(0, sample()));
			buffer[bufindex] = sample = step - sample;
			if (++bufindex == buffer.length) bufindex = 0;
			if (column == 0) column = GRAPH_GRID_INTERVAL;
			if (isShowing()) repaint();
			column--;
		}
	}

}
