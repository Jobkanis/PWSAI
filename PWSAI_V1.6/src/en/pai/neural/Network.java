package en.pai.neural;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import en.pai.IO.IOUtils;
import en.pai.UI.DrawPanel;
import en.pai.main.Main;
import en.pai.neural.levelnet.DegreeData;

public abstract class Network {
	private TrainingData data;
	private ArrayList<float[]> 
	weights = new ArrayList<float[]>(), 
	biases = new ArrayList<float[]>();
	
	private Random random;
	private int layerCount, totalInputNeurons, hiddenLayerNeurons, totalOutputNeurons;
	private int totalTraining = 0;
	
	public final static int N = 500;
	
	public final int batchSize;
	
	public boolean initializing = false;
	
	public Network(TrainingData data) {
		this.data = data;
		this.totalInputNeurons = data.getTrainingUnits().get(0).getInputs().length;
		this.totalOutputNeurons = data.getPossibleOutputs().size();
		this.batchSize = Math.max((int)((data.getSuitableUnits().size()*data.trainingPercentage)/30f), 1);
	}
	
	public void init(int hiddenLayerCount, int hiddenLayerNeurons) {
		initializing = true;
		reset();
		this.layerCount = 2+hiddenLayerCount;
		this.hiddenLayerNeurons = hiddenLayerNeurons;
		
		//Initialize the weights
		for (int i = 0; i < layerCount-1; i++) {
			weights.add(new float[getNeuronCount(i)*getNeuronCount(i+1)]);
		}
		
		for (float[] weightArray:weights) {
			for (int i = 0; i < weightArray.length; i++) {
				weightArray[i] = (random.nextFloat()-0.5f)*10;
			}
		}
		
		//Initialize the biases
		for (int i = 1; i < layerCount; i++) {
			biases.add(new float[getNeuronCount(i)]);
		}
		
		for (float[] biasArray:biases) {
			for (int i = 0; i < biasArray.length; i++) {
				biasArray[i] = (random.nextFloat()-0.5f)*1;
			}
		}
		initializing = false;
	}
	
	public void load(String path) {
		reset();
		String[][] saveData = null;
		try {
			saveData = IOUtils.commaSeparatedFileToArray(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int hiddenLayerCount = Integer.valueOf(saveData[0][0]);
		this.hiddenLayerNeurons = Integer.valueOf(saveData[0][1]);
		this.totalTraining = Integer.valueOf(saveData[0][2]);
		
		this.layerCount = 2+hiddenLayerCount;
		
		//Initialize the weights
		for (int i = 0; i < layerCount-1; i++) {
			weights.add(new float[getNeuronCount(i)*getNeuronCount(i+1)]);
		}
		
		for (int l = 0; l < weights.size(); l++) {
			float[] weightArray = weights.get(l);
			for (int i = 0; i < weightArray.length; i++) {
				weightArray[i] = Float.valueOf(saveData[1+l][i]);
			}
		}
		
		//Initialize the biases
		for (int i = 1; i < layerCount; i++) {
			biases.add(new float[getNeuronCount(i)]);
		}
		
		for (int l = 0; l < biases.size(); l++) {
			float[] biasArray = biases.get(l);
			for (int i = 0; i < biasArray.length; i++) {
				biasArray[i] = Float.valueOf(saveData[1+weights.size()+l][i]);
			}
		}
	}
	
	private void reset() {
		
		weights.clear();
		biases.clear();
		
		totalTraining = 0;
		random = new Random();
		averageLists.clear();
	}

	//public float lastTotalWeightDerivatives = -1, lastTotalBiasDerivatives = -1;
	
	/**
	 * @return A boolean indicating whether or not to continue training this network
	 */
	public boolean learn() {
		ArrayList<TrainingUnit> trainingUnits = data.getTrainingUnits();
		
		TrainingUnit[] currentTrainingUnits = new TrainingUnit[batchSize];
		for (int t = 0; t < batchSize; t++) {
			if (batchSize < trainingUnits.size()) {
				currentTrainingUnits[t] = trainingUnits.get(random.nextInt(trainingUnits.size()));
			} else {
				currentTrainingUnits[t] = trainingUnits.get(t);
			}
		}
		
		float currentBatchTotalCost = calculateBatchAverageCost(currentTrainingUnits);
		
		float totalWeightDerivatives = 0;
		ArrayList<float[]> currentBatchWeightDerivatives = calculateBatchTotalDerivatives(currentTrainingUnits, true);
		ArrayList<float[]> currentBatchBiasDerivatives = calculateBatchTotalDerivatives(currentTrainingUnits, false);
		
		for (int wa = 0; wa < weights.size(); wa++) {
			for (int wi = 0; wi < weights.get(wa).length; wi++) {
				//Apply the derivative to the weight
				weights.get(wa)[wi] += (-currentBatchTotalCost)*(currentBatchWeightDerivatives.get(wa)[wi]);
				totalWeightDerivatives += Math.abs((-currentBatchTotalCost)*(currentBatchWeightDerivatives.get(wa)[wi]));
			}
		}
		
		float totalBiasDerivatives = 0;
		for (int ba = 0; ba < biases.size(); ba++) {
			for (int bi = 0; bi < biases.get(ba).length; bi++) {
				//Apply the derivative to the biases
				biases.get(ba)[bi] += (-currentBatchTotalCost)*(currentBatchBiasDerivatives.get(ba)[bi]);
				totalBiasDerivatives += Math.abs((-currentBatchTotalCost)*(currentBatchBiasDerivatives.get(ba)[bi]));
			}
		}
		
		DrawPanel.trainingUnitsBatch = currentTrainingUnits;
		totalTraining += 1;
		updateAverages(currentBatchTotalCost, currentTrainingUnits, totalWeightDerivatives, totalBiasDerivatives);
		
		if (Main.learnLimit != -1 && totalTraining >= Main.learnLimit) {
			Date date = Calendar.getInstance().getTime();
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
			String strDate = dateFormat.format(date);
			float cost = getAverage("AverageCost", N, 4);
			float guesses = getAverage("CorrectGuesses", N*batchSize, 4);
			float roundedTest = Math.round(test()*1000)/1000f;
			//System.out.println("r=" + roundedTest + ", t=" + test());
			save("res/Saves/" + getTypeCode() + "Save[L=" + (layerCount-2) +
													",N=" + hiddenLayerNeurons + 
													",TrainAcc=" + guesses + 
													",TestAcc=" + roundedTest + 
													",Cost=" + cost + 
													",Date=" + strDate + 
													"].txt");
			
			return false;
		}
		
		return true;
	}
	
	private void updateAverages(float currentBatchTotalCost, TrainingUnit[] trainingUnitsBatch, float totalWeightDerivatives, float totalBiasDerivatives) {
		addToAverage("AverageCost", currentBatchTotalCost, N, 3);
		float totalCorrect = 0;
		for (int bg = 0; bg < trainingUnitsBatch.length; bg++) {
			if (calculateOutput(trainingUnitsBatch[bg]).equals(trainingUnitsBatch[bg].getOutput())) {
				totalCorrect+=1;
			}
		}
		addToAverage("CorrectGuesses", totalCorrect/trainingUnitsBatch.length, N, 3);
		addToAverage("WeightChanges", totalWeightDerivatives, N, 5);
		addToAverage("BiasChanges", totalBiasDerivatives, N, 5);
	}
	
	private HashMap<String, ArrayList<Float>> averageLists = new HashMap<String, ArrayList<Float>>();
	private void addToAverage(String ID, float number, int trackSize, int roundToDecimals) {
		if (!averageLists.containsKey(ID)) {
			averageLists.put(ID, new ArrayList<Float>());
			averageLists.get(ID).add(1f);
		}
		
		ArrayList<Float> currentAverageList = averageLists.get(ID);
		if (currentAverageList.size() < trackSize) {
			currentAverageList.add(number);
		} else {
			currentAverageList.set(Math.round(currentAverageList.get(0)), number);
		}
		
		//Change the index for the next time this function is called
		currentAverageList.set(0, currentAverageList.get(0)+1);
		if (currentAverageList.get(0) >= trackSize) {
			currentAverageList.set(0, 1f);
		}
	}
	
	public float getAverage(String ID, int size, int roundToDecimals) {
		if (!averageLists.containsKey(ID)) {
			return -1;
		}
	
		ArrayList<Float> currentAverageList = (ArrayList<Float>) averageLists.get(ID).clone();
		float total = 0;
		for (int i = 1; i < Math.min(currentAverageList.size(), size); i++) {
			total += currentAverageList.get(i);
		}
		float average = total/Math.min(currentAverageList.size(), size);
		
		 
		return (float)(Math.round(average*Math.pow(10, roundToDecimals-1))/Math.pow(10, roundToDecimals-1));
	}

	public float test() {
		int correct = 0;
		int total = 0;
		ArrayList<TrainingUnit> testingUnits = data.getTestingUnits();
		for (TrainingUnit tu:testingUnits) {
			if (tu.getOutput().equalsIgnoreCase(calculateOutput(tu))) {
				correct++;
			}
			total++;
		}
		return correct/(float)total;
	}
	
	/**
	 * A function to calculate the activation of neurons based on the set weights and biases
	 * @param trainingUnit The unit from which to take the inputs
	 * @return An arraylist of all the activations of each layer
	 */
	public ArrayList<float[]> calculateActivations(TrainingUnit trainingUnit) {
		ArrayList<float[]> neurons = new ArrayList<float[]>();
		//Get the input neurons
		neurons.add(trainingUnit.getInputs());
		
		//Calculate the activations of all layers
		for (int l = 1; l < layerCount; l++) {
			//Reserve space for each neuron in the array 
			neurons.add(new float[getNeuronCount(l)]);
			//Calculate the activation values of the current layer
			for (int n = 0; n < getNeuronCount(l); n++) {
				//Calculate the sum of all activations in the previous layer times their weights
				float sum = 0;
				for (int a = 0; a < neurons.get(l-1).length; a++) {
					sum += neurons.get(l-1)[a]*getWeight(l, n, a);
				}
				//Calculate the activation value of the current neuron
				neurons.get(l)[n] = sigmoid(biases.get(l-1)[n]+sum);
			}
		}
		
		return neurons;
	}
	

	/**
	 * Calculate the total derivatives of a batch
	 * @param trainingUnits
	 * @return
	 */
	private ArrayList<float[]> calculateBatchTotalDerivatives(TrainingUnit[] trainingUnits, boolean calcweights) {
		ArrayList<float[]> result = new ArrayList<float[]>();
		//Reserve space in result
		for (int w = 0; w < weights.size(); w++) {
			result.add(new float[weights.get(w).length]);
		}
		//Add up the totals
		for (int t = 0; t < trainingUnits.length; t++) {
			ArrayList<float[]> currentDerivatives = calculateDerivatives(trainingUnits[t], calcweights);
			for (int a = 0; a < currentDerivatives.size(); a++) {
				for (int f = 0; f < currentDerivatives.get(a).length; f++) {
					result.get(a)[f] += currentDerivatives.get(a)[f];
				}
			}
		}
	
		return result;
	}
	/**
	 * Calculates the derivatives for all the weights/biases regarding the Cost
	 * @return An arraylist containing the arrays of all the derivatives
	 */
	private ArrayList<float[]> calculateDerivatives(TrainingUnit trainingUnit, boolean calcweights) {
		ArrayList<float[]> activations = calculateActivations(trainingUnit);
		
		ArrayList<float[]> derivativesToCost = new ArrayList<float[]>();
		for (int l = 0; l < layerCount-1; l++) {
			//Reserve space for the derivatives of a single layer
			derivativesToCost.add(new float[weights.get(l).length]);
			//Loop through all weightindices
			for (int wi = 0; wi < weights.get(l).length; wi++) {
				//Add the average derivative of a weight
				derivativesToCost.get(l)[wi] = calculateAverageDerivative(l, wi, activations, trainingUnit, calcweights);
			}
		}
		
		return derivativesToCost;
	}
	/**
	 * There are different routes to get to the desired weight from the output neuron, this function calculates the average of all the derivatives of all routes
	 * @param layer The layer the weight resides in
	 * @param index Index of the weight in the given layer
	 * @param outputNeuronIndex 
	 * @param activations
	 * @param trainingUnit
	 * @return
	 */
	private float calculateAverageDerivative(int layer, int index, ArrayList<float[]> activations, TrainingUnit trainingUnit, boolean calcweights) {
		int lastNeuronIndex = index/getNeuronCount(layer+1);
		int secondToLastNeuronIndex = index%getNeuronCount(layer+1);
		//Returns the derivative if there is only one route possible 
		if (layer == layerCount-1) {
			return calculateDerivative(new int[] {lastNeuronIndex}, activations, trainingUnit, calcweights);
		} else if (layer == layerCount-2) {
			return calculateDerivative(new int[] {secondToLastNeuronIndex, lastNeuronIndex}, activations, trainingUnit, calcweights);
		}
		int routeCount = 0;
		float result = 0;
		int[] neuronIndices = new int[layerCount-layer];
		neuronIndices[neuronIndices.length-1] = lastNeuronIndex;
		neuronIndices[neuronIndices.length-2] = secondToLastNeuronIndex;
		
		boolean calculatingRoutes = true;
		while(calculatingRoutes) {
			//Add the derivative of a single "route" to the result
			result += calculateDerivative(neuronIndices, activations, trainingUnit, calcweights);
			routeCount++;
			neuronIndices[0]++;
			for (int i = 0; i < neuronIndices.length-1; i++) {
				if (neuronIndices[i] == getNeuronCount(layerCount-1-i)) {
					if (i != neuronIndices.length-3) {
						neuronIndices[i+1] += 1;
						neuronIndices[i] = 0;
					} else if (i == neuronIndices.length-3) {
						//Terminate the loop if all routes have been done
						calculatingRoutes = false;
						break;
					}
				}
			}
			
		}
		//Divide the result by the amount of routes to get the average derivative of all routes
		return result/routeCount;
	}
	/**
	 * Calculates a single derivative for the given weight/bias using the given route from the cost backwards
	 * @param backwardsIndices 
	 * @param activations
	 * @param trainingUnit
	 * @return The total derivative of the cost regarding the weight 
	 */
	private float calculateDerivative(int[] backwardsIndices, ArrayList<float[]> activations, TrainingUnit trainingUnit, boolean calcweights) {
		
		float[] backwardsActivations = new float[backwardsIndices.length];
		for (int a = 0; a < backwardsActivations.length; a++) {
			backwardsActivations[a] = activations.get(layerCount-1-a)[backwardsIndices[a]];
		}
		float result = 2*(activations.get(layerCount-1)[backwardsIndices[0]]-desiredOutput(trainingUnit, backwardsIndices[0]));
		
		for (int bL = 1; bL < backwardsIndices.length; bL++) {
			//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
			float currentActivation = backwardsActivations[bL-1];
			result *= currentActivation*(1-currentActivation);
			
			if (bL != backwardsIndices.length-1) {
				result *= getWeight(layerCount-bL, backwardsIndices[bL-1], backwardsIndices[bL]);
			} 
		}
		//if (backwardsIndices.length == 3 && backwardsIndices[2] == 13 && backwardsActivations[backwardsIndices.length-1] != 0.0f) {
			//System.out.println(trainingUnit.getInputNames()[13]);
		//}
		if (calcweights) {
			result *= backwardsActivations[backwardsIndices.length-1];
		}
		return result;
	}
	
	public HashMap<String, Float> getInputDerivatives(int outputIndex, TrainingUnit irrelevantUnitButNecessary) {
		HashMap<String, Float> result = new HashMap<String, Float>();
		String[] inputs = irrelevantUnitButNecessary.getInputNames();
		for (int i = 0; i < inputs.length; i++) {
			result.put(inputs[i], calculateInputAverageDerivative(i, outputIndex, calculateActivations(irrelevantUnitButNecessary)));
		}
		return result;
	}
	
	private float calculateInputAverageDerivative(int inputNeuronIndex, int outputNeuronIndex, ArrayList<float[]> activations) {
		//Returns the derivative if there is only one route possible 
		if (layerCount == 2) {
			return calculateInputDerivative(new int[] {outputNeuronIndex, inputNeuronIndex}, activations);
		}
		
		int routeCount = 0;
		float result = 0;
		int[] neuronIndices = new int[layerCount];
		neuronIndices[neuronIndices.length-1] = inputNeuronIndex;
		neuronIndices[0] = outputNeuronIndex;
		
		boolean calculatingRoutes = true;
		while(calculatingRoutes) {
			//Add the derivative of a single "route" to the result
			result += calculateInputDerivative(neuronIndices, activations);
			routeCount++;
			neuronIndices[1]++;
			for (int i = 1; i < neuronIndices.length-1; i++) {
				if (neuronIndices[i] == getNeuronCount(layerCount-1-i)) {
					if (i != neuronIndices.length-2) {
						neuronIndices[i+1] += 1;
						neuronIndices[i] = 0;
					} else if (i == neuronIndices.length-2) {
						//Terminate the loop if all routes have been done
						calculatingRoutes = false;
						break;
					}
				}
			}
			
		}
		//Divide the result by the amount of routes to get the average derivative of all routes
		return result/routeCount;
	}
	private float calculateInputDerivative(int[] backwardsIndices, ArrayList<float[]> activations) {
		float[] backwardsActivations = new float[backwardsIndices.length];
		for (int a = 0; a < backwardsActivations.length; a++) {
			backwardsActivations[a] = activations.get(layerCount-1-a)[backwardsIndices[a]];
		}
		
		float result = 1;
		for (int bL = 1; bL < backwardsIndices.length; bL++) {
			//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
			//float currentActivation = backwardsActivations[bL-1];
			//result *= currentActivation*(1-currentActivation);
			result *= getWeight(layerCount-bL, backwardsIndices[bL-1], backwardsIndices[bL]);
		}
		
		return result;
	}
	
	public float calculateBatchAverageCost(TrainingUnit[] trainingUnits) {
		float[] currentBatchCosts = calculateBatchAverageCosts(trainingUnits);
		float currentBatchTotalCost = 0;
		for (float f:currentBatchCosts) {
			currentBatchTotalCost+=f;
		}
		return currentBatchTotalCost;
	}
	/**
	 * Calculates the average costs for a given batch of training units
	 * @param trainingUnits
	 * @return
	 */
	private float[] calculateBatchAverageCosts(TrainingUnit[] trainingUnits) {
		float[] totalCosts = calculateBatchTotalCosts(trainingUnits);
		float[] averageCosts = new float[totalCosts.length];
		for (int i = 0; i < totalCosts.length; i++) {
			averageCosts[i] = totalCosts[i]/trainingUnits.length;
		}
		return averageCosts;
	}
	/**
	 * Calculates the total costs for a given batch of training units
	 * @param trainingUnits
	 * @return
	 */
	private float[] calculateBatchTotalCosts(TrainingUnit[] trainingUnits) {
		float[] result = new float[totalOutputNeurons];
		for (TrainingUnit t:trainingUnits) {
			float[] tCosts = calculateCosts(t);
			for (int i = 0; i < result.length; i++) {
				result[i] += tCosts[i];
			}
		}
		return result;
	}
	public float calculateTotalCost(TrainingUnit trainingUnit) {
		float[] costs = calculateCosts(trainingUnit);
		if (costs == null) {
			return -1;
		}
		float currentTotalCost = 0;
		for (float f:costs) {
			currentTotalCost+=f;
		}
		return currentTotalCost;
	}
	/**
	 * Calculates the costs of the neural network for the given training unit
	 * @param trainingUnit The unit for which to test the costs
	 * @return An array containing all the costs
	 */
	public float[] calculateCosts(TrainingUnit trainingUnit) {
		if (trainingUnit.getOutput() == "NULL") {
			System.err.println("Can't calculate cost for trainingUnit without output");
			return null;
		}
		float[] cost = new float[totalOutputNeurons];
		ArrayList<float[]> activations = calculateActivations(trainingUnit);
		
		for (int i = 0; i < totalOutputNeurons; i++) {
			cost[i] = (float)Math.pow(activations.get(layerCount-1)[i]-desiredOutput(trainingUnit, i), 2);
		}
		
		return cost;
	}
	
	public int layerCount() {
		return layerCount;
	}
	public int getNeuronCount(int layer) {
		if (layer == 0) {
			return totalInputNeurons;
		} else if (layer > 0 && layer < layerCount-1) {
			return hiddenLayerNeurons;
		} else if (layer == layerCount-1) {
			return totalOutputNeurons;
		} else {
			return -1;
		}
	}
	/**
	 * Get a specific weight
	 * @param layer The layer of the neuron to which the weight is connected
	 * @param neuron The index of the neuron
	 * @param index The index of the neuron in the previous layer, which the weight is connected to
	 * @return The weight
	 */
	public float getWeight(int layer, int neuron, int index) {
		if (weights.isEmpty()) {
			return -1;
		}
		return weights.get(layer-1)[neuron+index*getNeuronCount(layer)];
	}
	public float getBias(int layer, int neuron) {
		if (biases.isEmpty()) {
			return -1;
		}
		return biases.get(layer)[neuron];
	}
	
	public ArrayList<String> getPossibleOutputs() {
		return data.getPossibleOutputs();
	}
	private float desiredOutput(TrainingUnit trainingUnit, int index) {
		ArrayList<String> outputList = getPossibleOutputs();
		boolean shouldBeActive = outputList.get(index).equals(trainingUnit.getOutput());
		float outputActivation = shouldBeActive ? 1:0;
		return outputActivation;
	}
	
	private float outputThreshold = 0.0f;
	private float outputLimiter = 1.3f;
	public String calculateOutput(TrainingUnit trainingUnit) {
		ArrayList<float[]> activations = calculateActivations(trainingUnit);
		int indexResult = -1;
		float maxActivation = -1;
		float crossLimitCount = 0;
		for (int i = 0; i < activations.get(activations.size()-1).length; i++) {
			float currentOutputActivation = activations.get(activations.size()-1)[i];
			if (currentOutputActivation > maxActivation && currentOutputActivation > outputThreshold) {
				maxActivation = currentOutputActivation;
				indexResult = i;
			}
			if (currentOutputActivation > outputLimiter) {
				crossLimitCount++;
			}
			if (crossLimitCount > 1) {
				return "No Definitive Output";
			}
		}
		if (indexResult == -1) {
			return "No Definitive Output";
		}
		return getPossibleOutputs().get(indexResult);
	}
	
	public int getTraining() {
		return totalTraining;
	}
	
	public static float sigmoid(float f) {
		return (float)sigmoid((double)f);
	}
	public static double sigmoid(double f) {
		return 1/(1+Math.exp(-f));
	}
	
	public void save(String path) {
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.add((layerCount-2) + "," + hiddenLayerNeurons + "," + totalTraining);
		
		for (int l = 0; l < weights.size(); l++) {
			lines.add("");
			for (int w = 0; w < weights.get(l).length; w++) {
				lines.set(l+1, lines.get(l+1) + weights.get(l)[w] + ",");
			}
		}
		
		for (int l = 0; l < biases.size(); l++) {
			lines.add("");
			for (int b = 0; b < biases.get(l).length; b++) {
				lines.set(l+1+biases.size(), lines.get(l+1+biases.size()) + biases.get(l)[b] + ",");
			}
		}
		
		try {
			IOUtils.write(path, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract String getTypeCode();
}
