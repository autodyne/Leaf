/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.Position.Bias;

import leaf.manager.LeafLangManager;
import leaf.components.text.LeafTextField;

/**
*フォント設定ダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2008年10月 改良：2010年5月22日
*/
public class LeafFontDialog extends LeafDialog
	implements ActionListener,ListSelectionListener,DocumentListener{
	
	/**秘匿フィールド*/
	private Font font = new Font(Font.MONOSPACED,Font.PLAIN,13);
	private String hist="";
	private final String[] sname;
	private final String[] sst = {
		LeafLangManager.get("Plain","標準"),
		LeafLangManager.get("Italic","斜体"),
		LeafLangManager.get("Bold","太字"),
		LeafLangManager.get("Bold Italic","太字 斜体")
	};
	private final String[] ssize = {
		"8","9","10","11","12","13","14","15","16",
		"18","20","22","24","26","28","36","48","72"
	};
	private GraphicsEnvironment ge;
	private boolean change = CANCEL_OPTION;

	/**GUI*/
	private JLabel namelabel,stlabel,sizelabel,sample;
	private JList namelist,stlist,sizelist;
	private JScrollPane namesc,stsc,sizesc;
	private LeafTextField namef,stf,sizef,exp;
	private JButton bok,bcancel;
	private JPanel sampanel;
	
	/**
	*親フレームを指定してファイル選択ダイアログを生成します。
	*@param parent 親フレーム
	*/
	public LeafFontDialog(JFrame parent){
		
		super(parent,null,true);
		setSize(520,300);
		setResizable(false);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				change = CANCEL_OPTION;
				dispose();
			}
		});
		setLayout(null);

		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		sname = ge.getAvailableFontFamilyNames();
		
		init();
	}
	/**
	*ダイアログを初期化します。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("Font","フォント"));
		
		getContentPane().removeAll();
		
		//サンプル
		sampanel = new JPanel();
		sampanel.setBounds(10,180,380,60);
		sample = new JLabel(
			"<html><Font size=5>" + LeafLangManager.get("Aa Bb Cc","Aa あぁ アァ 亜宇")
		);
		sampanel.add(sample);
		
		sampanel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("Sample","サンプル")
		));
		add(sampanel);
		
		//フォント名
		namelabel = new JLabel(LeafLangManager.get("Name","フォント名"));
		namelabel.setBounds(10,5,220,20);
		add(namelabel);
		namef = new LeafTextField();
		namef.setBounds(10,25,220,20);
		add(namef);
		namelist = new JList(sname);
		namesc = new JScrollPane(namelist);
		namesc.setBounds(10,47,220,110);
		namelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		namelist.addListSelectionListener(this);
		namef.getDocument().addDocumentListener(this);
		add(namesc);
		
		//スタイル
		stlabel = new JLabel(LeafLangManager.get("Style","スタイル"));
		stlabel.setBounds(240,5,80,20);
		add(stlabel);
		stf = new LeafTextField();
		stf.setBounds(240,25,80,20);
		add(stf);
		stlist = new JList(sst);
		stsc = new JScrollPane(stlist);
		stsc.setBounds(240,47,80,110);
		stlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stlist.addListSelectionListener(this);
		stf.setEditable(false);
		add(stsc);
		
		//フォントサイズ
		sizelabel = new JLabel(LeafLangManager.get("Size","サイズ"));
		sizelabel.setBounds(330,5,60,20);
		add(sizelabel);
		sizef = new LeafTextField();
		sizef.setBounds(330,25,60,20);
		add(sizef);
		sizelist = new JList(ssize);
		sizesc = new JScrollPane(sizelist);
		sizesc.setBounds(330,47,60,88);
		sizelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sizelist.addListSelectionListener(this);
		sizef.setEditable(false);
		add(sizesc);
		
		//説明
		exp = new LeafTextField();
		exp.setBounds(330,137,60,20);
		exp.setEditable(false);
		add(exp);
				
		//ボタン
		bok = new JButton("OK");
		bok.setBounds(400,25,100,22);
		bok.addActionListener(this);
		add(bok);
		bcancel = new JButton(LeafLangManager.get("CANCEL","キャンセル"));
		bcancel.setBounds(400,49,100,22);
		bcancel.addActionListener(this);
		add(bcancel);
	}
	/**
	*フォントファミリ・字体・サイズの選択が変更されたときに呼び出されます。
	*/
	public void valueChanged(ListSelectionEvent e){
		Object obj = e.getSource();
		if(obj == namelist){
			namef.setText((String)(namelist.getSelectedValue()));
			font = new Font(
				(String)(namelist.getSelectedValue()),
				font.getStyle(),
				font.getSize()
			);
		}else if(obj == stlist){
			stf.setText((String)(stlist.getSelectedValue()));
			int si = stlist.getSelectedIndex();
			if(si==0)font = new Font(font.getFamily(),Font.PLAIN,font.getSize());
			else if(si==1)font = new Font(font.getFamily(),Font.ITALIC,font.getSize());
			else if(si==2)font = new Font(font.getFamily(),Font.BOLD,font.getSize());
			else font = new Font(font.getFamily(),Font.ITALIC|Font.BOLD,font.getSize());
		}else{
			sizef.setText((String)(sizelist.getSelectedValue()));
			font = new Font(
				font.getFamily(),
				font.getStyle(),
				Integer.parseInt((String)(sizelist.getSelectedValue()))
			);
		}
		sample.setFont(font);
		exp.setText(
			(getFontMetrics(font).charWidth('m')==getFontMetrics(font).charWidth('l'))?
			LeafLangManager.get("Fixed","等幅"):LeafLangManager.get("Unfixed","非等幅"));
	}
	public void actionPerformed(ActionEvent e){
		Object obj = e.getSource();
		if(obj == bok)change = OK_OPTION;
		else change = CANCEL_OPTION;
		dispose();
	}
	public void changedUpdate(DocumentEvent e){}
	public void insertUpdate(DocumentEvent e){update(e);}
	public void removeUpdate(DocumentEvent e){update(e);}
	private void update(DocumentEvent e){
		try{
			if(!namef.getText().equalsIgnoreCase(hist)){
				int index = namelist.getNextMatch((hist=namef.getText()),0,Bias.Forward);
				namelist.setSelectedIndex(index);
				namelist.ensureIndexIsVisible(index);
				font = new Font(
					(String)(namelist.getSelectedValue()),
					font.getStyle(),
					font.getSize()
				);
				sample.setFont(font);
				FontMetrics met = getFontMetrics(font);
				exp.setText(
					(met.charWidth('m')==met.charWidth('l'))?
					LeafLangManager.get("Fixed","等幅"):
					LeafLangManager.get("Unfixed","非等幅")
				);
			}namelist.repaint();
		}catch(Exception ex){}
	}
	/**
	*フォント設定画面をモーダルで表示します。<br>
	*{@link LeafDialog#setVisible(boolean)}は使用すべきではありません。
	*@param font デフォルトのフォント
	*@return 選択されたフォント
	*/
	public Font showDialog(Font font){
		this.font = font;
		//フォント名
		namelist.setSelectedValue(font.getFamily(),false);
		namef.setText(font.getFamily());
		//字体
		String fs;
		if(font.getStyle()==Font.PLAIN)fs=LeafLangManager.get("Plain","標準");
		else if(font.getStyle()==Font.ITALIC)fs=LeafLangManager.get("Italic","斜体");
		else if(font.getStyle()==Font.BOLD)fs=LeafLangManager.get("Bold","太字");
		else fs=LeafLangManager.get("Bold Italic","太字 斜体");
		stf.setText(fs);
		stlist.setSelectedValue(fs,true);
		//サイズ
		sizef.setText(""+font.getSize());
		sizelist.setSelectedValue(String.valueOf(font.getSize()),true);
		//表示(モーダル)
		super.setVisible(true);
		if(change==OK_OPTION)return this.font;
		else return font;//そのまま返す
	}
}