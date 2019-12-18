package en.pai.UI;

import javax.swing.JFrame;

import en.pai.neural.Network;

public class Frame extends JFrame {
	public DrawPanel dPanel;
	public Frame(Network NN) {
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		dPanel = new DrawPanel(NN);
		add(dPanel);
		pack();
	}
}
