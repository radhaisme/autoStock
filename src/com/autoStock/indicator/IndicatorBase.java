/**
 * 
 */
package com.autoStock.indicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.taLib.Core;
import com.autoStock.taLib.RetCode;
import com.autoStock.types.basic.MutableInteger;

/**
 * @author Kevin Kowalewski
 *
 */
public abstract class IndicatorBase {
	public Core taLibCore;
	private CommonAnalysisData commonAnlaysisData;
	
	public final IndicatorParameters indicatorParameters;
	public int datasetLength;
	public Date[] arrayOfDates;
	public double[] arrayOfPriceOpen;
	public double[] arrayOfPriceHigh;
	public double[] arrayOfPriceLow;
	public double[] arrayOfPriceClose;
	public int[] arrayOfSizeVolume;
	public int endIndex = 0;
	private ArrayList<SignalMetricType> listOfSignalMetricType = new ArrayList<SignalMetricType>();
	
	public IndicatorBase(IndicatorParameters indicatorParameters, CommonAnalysisData commonAnlaysisData, Core taLibCore, SignalMetricType signalMetricType){
		this.indicatorParameters = indicatorParameters;
		this.commonAnlaysisData = commonAnlaysisData;
		this.taLibCore = taLibCore;
		
		listOfSignalMetricType.add(signalMetricType);
	}

	public void setDataSet(){
		if (commonAnlaysisData.arrayOfDates.length < indicatorParameters.periodLength.value){
			throw new IllegalArgumentException("List size was too small: " + getClass().getSimpleName() + ", " + commonAnlaysisData.arrayOfDates.length + ", expected: " + indicatorParameters.periodLength.value);
		}
		
		if (indicatorParameters.periodLength.value == 0){
			return;
		}
		
		int initialLength = commonAnlaysisData.arrayOfDates.length;
		
		if (getRequiredDatasetLength() > initialLength){
			throw new IllegalArgumentException("Input length is smaller than required length (needed, supplied): " + getRequiredDatasetLength() + ", " + initialLength);
		}
	
		if (initialLength != getRequiredDatasetLength()){
			Co.println("-->  N " + (initialLength - getRequiredDatasetLength()) + ", " + getRequiredDatasetLength() + ", " + initialLength);
			arrayOfDates = Arrays.copyOfRange(commonAnlaysisData.arrayOfDates, initialLength - getRequiredDatasetLength(), initialLength);
			arrayOfPriceOpen = Arrays.copyOfRange(commonAnlaysisData.arrayOfPriceOpen, initialLength - getRequiredDatasetLength(), initialLength);
			arrayOfPriceHigh = Arrays.copyOfRange(commonAnlaysisData.arrayOfPriceHigh, initialLength -getRequiredDatasetLength(), initialLength);
			arrayOfPriceLow = Arrays.copyOfRange(commonAnlaysisData.arrayOfPriceLow, initialLength - getRequiredDatasetLength(), initialLength);
			arrayOfPriceClose = Arrays.copyOfRange(commonAnlaysisData.arrayOfPriceClose, initialLength - getRequiredDatasetLength(), initialLength);
			arrayOfSizeVolume = Arrays.copyOfRange(commonAnlaysisData.arrayOfSizeVolume, initialLength - getRequiredDatasetLength(), initialLength);
		}else{
			arrayOfDates = commonAnlaysisData.arrayOfDates;
			arrayOfPriceOpen = commonAnlaysisData.arrayOfPriceOpen;
			arrayOfPriceHigh = commonAnlaysisData.arrayOfPriceHigh;
			arrayOfPriceLow = commonAnlaysisData.arrayOfPriceLow;
			arrayOfPriceClose = commonAnlaysisData.arrayOfPriceClose;
			arrayOfSizeVolume = commonAnlaysisData.arrayOfSizeVolume;
		}
		
		datasetLength = arrayOfPriceClose.length;
		endIndex = datasetLength-1;
		
//		Co.println("--> this: " + this.getClass().getName() + ", " + resultsetLength);
	}
	
	public void addSignalMetricType(SignalMetricType signalMetricType){
		listOfSignalMetricType.add(signalMetricType);
	}
	
	public void removeSignalMetricType(SignalMetricType signalMetricType){
		listOfSignalMetricType.remove(signalMetricType);
	}
	
	public ArrayList<SignalMetricType> getSignalMetricType(){
		return listOfSignalMetricType;
	}
	
	public int getRequiredDatasetLength(){
		return indicatorParameters.periodLength.value + indicatorParameters.resultSetLength -1;
	}
	
	public void setDataSetFromDatabase(ArrayList<DbStockHistoricalPrice> listOfDbStockHistoricalPrice){
		if (listOfDbStockHistoricalPrice.size() == 0){
			throw new IllegalArgumentException();
		}
		
		this.datasetLength = listOfDbStockHistoricalPrice.size();
		this.endIndex = datasetLength -1;
	}
	
	public void handleAnalysisResult(RetCode returnCode){
		if (returnCode != RetCode.Success){
			throw new IllegalStateException("Result code was not success...");
		}
	}
}
