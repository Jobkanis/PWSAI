package en.pai.UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import en.pai.main.Main;
import en.pai.neural.Network;
import en.pai.neural.TrainingUnit;
import en.pai.neural.levelnet.DegreeStudentUnit;
import en.pai.util.Util;

public class DrawPanel extends JPanel {
	private Network NN;
	public static TrainingUnit[] trainingUnitsBatch = null;
	public DrawPanel(Network NN) {
		this.NN = NN;
		
		setPreferredSize(new Dimension(1600, 800));
	}
	private static final int neuronOutline = 2, neuronSize = 20, startX = 60, startY = 30, debugX = 500, debugY = 600;
	private static final float xGap = 8, yGap = 2.2f;
	private static final Color positive = new Color(0, 191, 255), negative = new Color(255, 0, 0);
	
	//private static ArrayList<Float> costsPastN = new ArrayList<Float>();
	
	
	//private static ArrayList<Boolean> guesses = new ArrayList<Boolean>();
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, getWidth(), getHeight());
		try {
			if (trainingUnitsBatch != null && !NN.initializing) {
				TrainingUnit CTU = trainingUnitsBatch[(int)(Math.random()*trainingUnitsBatch.length)];
				ArrayList<float[]> currentActivations = NN.calculateActivations(CTU);
				for (int l = 0; l < NN.layerCount(); l++) {
					for (int n = 0; n < NN.getNeuronCount(l); n++) {
						float activation = Math.abs(currentActivations.get(l)[n]);
						int neuronX = (int)(neuronSize*l*xGap)+startX, neuronY = (int)(neuronSize*n*yGap)+startY;
						
						//Draw weights
						if (l != 0) {
							for (int wi = 0; wi < NN.getNeuronCount(l-1); wi++) {
								float weight = NN.getWeight(l, n, wi);
								if (weight > 0) {
									g2.setColor(positive);
								} else {
									g2.setColor(negative);
								}
								
								float strokeSize = currentActivations.get(l-1)[wi]*Math.abs(Network.sigmoid(weight)-0.5f)*2*4;
								//float strokeSize = 1;
								if (strokeSize > 0.1) {
									g2.setStroke(new BasicStroke(Math.abs(strokeSize)));
									g2.drawLine(neuronX+neuronSize/2, neuronY+neuronSize/2, (int)(neuronSize*(l-1)*xGap)+startX+neuronSize/2, (int)(neuronSize*wi*yGap)+startY+neuronSize/2);
									g2.setStroke(new BasicStroke(1));
								}
								
							}
						}
						
						//Draw neurons with relevant information
						g2.setColor(Color.white);
						g2.fillOval(neuronX-neuronOutline, neuronY-neuronOutline, neuronSize+neuronOutline*2, neuronSize+neuronOutline*2);
						g2.setColor(new Color(activation, activation, activation));
						g2.fillOval(neuronX, neuronY, neuronSize, neuronSize);
						
						//Draw activation
						g2.setColor(Color.BLACK);
						g2.drawString(String.valueOf(Math.round(activation*100)/100.0), neuronX, neuronY+neuronSize/2+6);
						
						//Draw biases
						if (l != 0) {
							g2.setColor(Color.GREEN);
							g2.drawString(String.valueOf(Math.round(NN.getBias(l-1, n)*10)/10.0), neuronX, neuronY-3);
						}
						
						
						//Draw names of input neurons
						g2.setColor(Color.WHITE);
						if (l == 0) {
							g2.drawString(trainingUnitsBatch[0].getInputNames()[n], neuronX-neuronSize*2, neuronY+neuronSize);
						}
						
						//Draw names of output neurons
						g2.setColor(Color.WHITE);
						if (l == NN.layerCount()-1) {
							g2.drawString(NN.getPossibleOutputs().get(n), neuronX+neuronSize*2, neuronY+neuronSize);
						}
						
						//Draw debug info
						int N = Network.N;
						g2.drawString("Correct output: " + CTU.getOutput(), debugX, debugY);
						g2.drawString("Is correct?: " + NN.calculateOutput(CTU).equals(CTU.getOutput()), debugX, debugY+20);
						g2.drawString("Average cost past " + N + " batches: " + NN.getAverage("AverageCost", N, 4), debugX, debugY+40);
						
						g2.drawString("Percentage correct guesses past " + N + " batches: " + NN.getAverage("CorrectGuesses", N*NN.batchSize, 4), debugX, debugY+60);
						g2.drawString("Total training done: " + NN.getTraining(), debugX, debugY+80);
						g2.drawString("Weight changes past " + N + " batches: " + NN.getAverage("WeightChanges", N, 5), debugX, debugY+100);
						g2.drawString("Bias changes past " + N + " batches: " + NN.getAverage("BiasChanges", N, 5), debugX, debugY+120);
						
						//Draw derivatives
						for (int i = 0; i < NN.getPossibleOutputs().size(); i++) {
							String output = NN.getPossibleOutputs().get(i);
							g2.drawString("\"" + output + "\"", 800+(i%3)*260, 40+(i/3)*175);
							
							drawInfoSquares(NN.getInputDerivatives(i, CTU), 5, 800+(i%3)*260, 50+(i/3)*175, 50, g2);
							
						}
					}
				}
			} 
		} catch (Exception e) {
			System.out.println("Painting failed!");
		}
	}
	
	private void drawInfoSquares(HashMap<String, Float> info, int columns, int startX, int startY, int blockSize, Graphics2D g2) {
		int keyIndex = 0;
		float maxValue = 0;
		for (String key:info.keySet()) {
			if (info.get(key) > maxValue) {
				maxValue = info.get(key);
			}
		}
		for (String key:info.keySet()) {
			Float value = info.get(key);
			int currentX = startX+blockSize*(keyIndex%columns);
			int currentY = startY+blockSize*(keyIndex/columns);
			
			
			if (value < 0) {
				g2.setColor(new Color(Util.toUnitInterval(Math.abs(value), 0f, maxValue), 0f, 0f));
			} else if (value > 0) {
				g2.setColor(new Color(0f, 0f, Util.toUnitInterval(Math.abs(value), 0f, maxValue)));
			} else {
				g2.setColor(Color.BLACK);
			}
			g2.fillRect(currentX, currentY, blockSize, blockSize);
			
			g2.setColor(Color.white);
			g2.drawRect(currentX, currentY, blockSize, blockSize);
			g2.drawString(key, currentX+blockSize/4, currentY+blockSize/2);
			
			keyIndex++;
		}
	}
	
	public static float toUnitInterval(float number, float lower, float upper) {
		if (number < lower) {
			return 0;
		} else if (number > upper) {
			return 1;
		}
		return (number-lower)/upper;
	}
	
}
