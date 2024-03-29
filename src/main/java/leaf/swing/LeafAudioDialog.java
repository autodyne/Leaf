/*******************************************************************************
 * Java Swing Library 'Leaf' and 'Tsukishiro Editor' since 2009 February 24th
 * License: GNU General Public License v3+ (see LICENSE)
 * Author: Journal of Hamradio Informatics (http://pafelog.net)
*******************************************************************************/
package leaf.swing;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.util.AudioPlayer;

/**
 * 音声ファイルを再生する機能をGUIアプリケーション向けに提供します。
 *
 * @author 無線部開発班
 * @since 2009年3月12日
 */
public final class LeafAudioDialog extends LeafDialog {
	private final AudioPlayer player;
	private final JFileChooser chooser;
	private final IndicateListener listener;
	private final Icon playIcon, pauseIcon;
	private JProgressBar indicator;
	private JLabel label;
	private JButton bopen, bstop;
	private JToggleButton bplay, bloop;
	private PlayWorker worker;

	/**
	 * 親フレームを指定してモーダレスダイアログを生成します。
	 *
	 * @param owner 親フレーム
	 */
	public LeafAudioDialog(Frame owner) {
		super(owner, false);
		setResizable(false);
		setContentSize(new Dimension(280, 60));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (worker != null) {
					worker.cancel(true);
					worker = null;
				}
			}
		});
		playIcon = LeafIcons.getIcon("PLAY");
		pauseIcon = LeafIcons.getIcon("PAUSE");
		initialize();
		player = new AudioPlayer();
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("AIFC/AIFF/AU/SND/WAV", "aifc", "aif", "aiff", "au", "snd", "wav"));
		listener = new IndicateListener();
	}

	/**
	 * 親ダイアログを指定してモーダレスダイアログを生成します。
	 *
	 * @param owner 親ダイアログ
	 */
	public LeafAudioDialog(Dialog owner) {
		super(owner, false);
		setResizable(false);
		setContentSize(new Dimension(280, 60));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (worker != null) {
					worker.cancel(true);
					worker = null;
				}
			}
		});
		playIcon = LeafIcons.getIcon("PLAY");
		pauseIcon = LeafIcons.getIcon("PAUSE");
		initialize();
		player = new AudioPlayer();
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("AIFC/AIFF/AU/SND/WAV", "aifc", "aif", "aiff", "au", "snd", "wav"));
		listener = new IndicateListener();
	}

	/**
	 * 再生を停止してからダイアログを閉じます。
	 */
	@Override
	public void dispose() {
		if (worker != null) {
			worker.cancel(true);
			worker = null;
		}
		super.dispose();
	}

	private void initButton(AbstractButton button) {
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setRequestFocusEnabled(false);
	}

	@Override
	public void initialize() {
		setTitle(translate("title"));
		getContentPane().removeAll();
		var toolbar = new JToolBar();
		add(toolbar, BorderLayout.NORTH);
		toolbar.setFloatable(false);
		bopen = new JButton(LeafIcons.getIcon("OPEN"));
		bopen.setToolTipText(translate("button_open"));
		toolbar.add(bopen);
		initButton(bopen);
		bopen.addActionListener(e -> {
			if (chooser.showOpenDialog(LeafAudioDialog.this) == JFileChooser.APPROVE_OPTION) {
				try {
					load(chooser.getSelectedFile());
				} catch (IOException ex) {
				}
			}
		});
		bplay = new JToggleButton(playIcon);
		bplay.setToolTipText(translate("button_play"));
		bplay.setEnabled(false);
		toolbar.add(bplay);
		initButton(bplay);
		bplay.addActionListener(e -> {
			if (bplay.isSelected()) {
				bplay.setIcon(pauseIcon);
				bopen.setEnabled(false);
				bloop.setEnabled(false);
				bstop.setEnabled(true);
				worker = new PlayWorker();
				worker.addPropertyChangeListener(listener);
				player.start();
				worker.execute();
			} else if (worker != null) {
				player.pause();
				worker.cancel(true);
				worker = null;
			}
		});
		bstop = new JButton(LeafIcons.getIcon("SQUARE"));
		bstop.setToolTipText(translate("button_stop"));
		bstop.setEnabled(false);
		toolbar.add(bstop);
		initButton(bstop);
		bstop.addActionListener(e -> {
			worker.cancel(true);
			worker = null;
		});
		bloop = new JToggleButton(LeafIcons.getIcon("LOOP"));
		bloop.setToolTipText(translate("button_loop"));
		bloop.setEnabled(false);
		toolbar.add(bloop);
		initButton(bloop);
		bloop.addActionListener(e -> player.setLoopMode(bloop.isSelected()));
		add(indicator = new JProgressBar(), BorderLayout.CENTER);
		add(label = new JLabel(), BorderLayout.SOUTH);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setText(translate("label_no_file_selected"));
	}

	/**
	 * 指定された音声ファイルを読み込みます。
	 *
	 * @param file 音声ファイル
	 *
	 * @throws IOException 読み込みに失敗した場合
	 */
	public void load(File file) throws IOException {
		try {
			player.load(file);
			label.setText(file.getName());
			bplay.setEnabled(true);
			bstop.setEnabled(true);
			bloop.setEnabled(true);
			chooser.setSelectedFile(file);
		} catch (IOException ex) {
			label.setText(ex.toString());
			bplay.setEnabled(false);
			bstop.setEnabled(false);
			bloop.setEnabled(false);
			throw ex;
		}
	}

	private class PlayWorker extends SwingWorker<String, String> {
		@Override
		public String doInBackground() {
			final long length = player.getFrameLength();
			if (length == 0) return "Done";
			while (!isCancelled()) {
				long pos = player.getFramePosition();
				setProgress(((int) (100 * pos / length)) % 100);
				if (!player.isLoopMode() && pos >= length) break;
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}
			}
			player.setFramePosition(0);
			return "Done";
		}

		@Override
		public void done() {
			if (player.isPlaying()) {
				indicator.setValue(0);
				player.stop();
			}
			bplay.setIcon(playIcon);
			bplay.setSelected(false);
			bopen.setEnabled(true);
			bloop.setEnabled(true);
			bstop.setEnabled(false);
			worker = null;
		}
	}

	private class IndicateListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			try {
				indicator.setValue(worker.getProgress());
			} catch (NullPointerException ex) {
			}
		}
	}
}
