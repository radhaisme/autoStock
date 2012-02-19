/**
 * 
 */
package com.autoStock.analysis.results;

import java.util.Date;

/**
 * @author Kevin Kowalewski
 *
 */
public class ResultsCommodityChannelIndex extends ResultsBase {
	
	public Date[] arrayOfDates;
	public double[] arrayOfCCI;
	public float[] arrayOfPrice;
	
	public ResultsCommodityChannelIndex(int length){
		this.arrayOfDates = new Date[length];
		this.arrayOfCCI = new double[length];
		this.arrayOfPrice = new float[length];
	}
}
