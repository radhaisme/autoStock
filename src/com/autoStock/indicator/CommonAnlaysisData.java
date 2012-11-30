package com.autoStock.indicator;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 * 
 */
public class CommonAnlaysisData {
	public double[] arrayOfPriceOpen;
	public double[] arrayOfPriceHigh;
	public double[] arrayOfPriceLow;
	public double[] arrayOfPriceClose;
	public double[] arrayOfPriceBid;
	public double[] arrayOfPriceAsk;
	public int[] arrayOfSizeVolume;
	public Date[] arrayOfDates;

	public void setAnalysisData(ArrayList<QuoteSlice> listOfQuoteSlice) {
		arrayOfPriceOpen = new double[listOfQuoteSlice.size()];
		arrayOfPriceHigh = new double[listOfQuoteSlice.size()];
		arrayOfPriceLow = new double[listOfQuoteSlice.size()];
		arrayOfPriceClose = new double[listOfQuoteSlice.size()];
		arrayOfPriceBid = new double[listOfQuoteSlice.size()];
		arrayOfPriceAsk = new double[listOfQuoteSlice.size()];
		arrayOfSizeVolume = new int[listOfQuoteSlice.size()];
		arrayOfDates = new Date[listOfQuoteSlice.size()];
		
		extractDataFromQuoteSlice(listOfQuoteSlice, null, arrayOfPriceOpen, arrayOfPriceHigh, arrayOfPriceLow, arrayOfPriceClose, arrayOfPriceBid, arrayOfPriceAsk, arrayOfSizeVolume, arrayOfDates);
	}

	private void extractDataFromQuoteSlice(ArrayList<QuoteSlice> listOfQuoteSlice, String field, double[] arrayOfPriceOpen, double[] arrayOfPriceHigh, double[] arrayOfPriceLow, double[] arrayOfPriceClose, double[] arrayOfPriceBid, double[] arrayOfPriceAsk, int[] arrayOfSizeVolume, Date[] arrayOfDates) {
		int i = 0;

		for (QuoteSlice quoteSlice : listOfQuoteSlice) {
			arrayOfPriceOpen[i] = quoteSlice.priceOpen;
			arrayOfPriceHigh[i] = quoteSlice.priceHigh;
			arrayOfPriceLow[i] = quoteSlice.priceLow;
			arrayOfPriceClose[i] = quoteSlice.priceClose;
			arrayOfPriceBid[i] = quoteSlice.priceBid;
			arrayOfPriceAsk[i] = quoteSlice.priceAsk;
			arrayOfSizeVolume[i] = quoteSlice.sizeVolume;
			arrayOfDates[i] = quoteSlice.dateTime;
			i++;
		}
	}
}
