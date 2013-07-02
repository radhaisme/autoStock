/**
 * 
 */
package com.autoStock.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.chart.ChartForAlgorithmTest.TimeSeriesType;
import com.autoStock.chart.ChartForAlgorithmTest.TimeSeriesTypePair;
import com.autoStock.signal.SignalDefinitions;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;

/**
 * @author Kevin Kowalewski
 * 
 */
public class CombinedLineChart {
	public int usedColor = -1;

	public class LineChartDisplay extends ApplicationFrame implements ActionListener {
		public String title;
		public TimeSeriesTypePair[] arrayOfTimeSeriesPair;
		public DefaultHighLowDataset defaultHighLowDataset;
		private AlgorithmBase algorithmBase;
		
		public LineChartDisplay(String title, DefaultHighLowDataset defaultHighLowDataset, AlgorithmBase algorithmBase, TimeSeriesTypePair... timeSeriesPairs) {
			super("autoStock - Chart - " + title);
			
			this.title = title;
			this.defaultHighLowDataset = defaultHighLowDataset;
			this.algorithmBase = algorithmBase;
			arrayOfTimeSeriesPair = timeSeriesPairs;

			ChartPanel chartPanel = (ChartPanel) createPanel();
			chartPanel.setPreferredSize(new Dimension(1600, 1000));
			
//			JButton button = new JButton("Add New Data Item");
//	        button.setActionCommand("ADD_DATA");
//	        button.addActionListener(this);
//	        chartPanel.add(button, BorderLayout.SOUTH);
			
	        setContentPane(chartPanel);
	        setVisible(true);
	        toFront();
	        pack();
	        
			RefineryUtilities.positionFrameOnScreen(this, 0, 0);
		}

		public JPanel createPanel() {
			JFreeChart chart = createChart();
			chart.setAntiAlias(true);
			chart.setTextAntiAlias(true);
			ChartPanel panel = new ChartPanel(chart);
			panel.setFillZoomRectangle(true);
			panel.setMouseWheelEnabled(true);
			return panel;
		}

		private JFreeChart createChart() {
			CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis("Domain"));
			plot.setGap(10);
			resetColor();
			
//			if (getPairForType(TimeSeriesType.type_signal_total) != null){
//				XYPlot subPlotForSignalTotal = new XYPlot(getPairForType(TimeSeriesType.type_signal_total).timeSeriesCollection, null, new NumberAxis(getPairForType(TimeSeriesType.type_signal_total).timeSeriesType.displayName), new StandardXYItemRenderer());
//				subPlotForSignalTotal.getRenderer().setSeriesPaint(0, Color.BLACK);
//
//				plot.add(subPlotForSignalTotal, 1);
//			}
			
			if (getPairForType(TimeSeriesType.type_signals) != null){
				resetColor();
				XYPlot subPlotForSignals = new XYPlot(getPairForType(TimeSeriesType.type_signals).timeSeriesCollection, null, new NumberAxis(getPairForType(TimeSeriesType.type_signals).timeSeriesType.displayName), new StandardXYItemRenderer());
				for (int i=0; i < getPairForType(TimeSeriesType.type_signals).timeSeriesCollection.getSeriesCount(); i++){
					subPlotForSignals.getRenderer().setSeriesPaint(i, getColor());	
				}
				subPlotForSignals.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
			
				StandardXYItemRenderer renderer  = (StandardXYItemRenderer) subPlotForSignals.getRenderer();
				renderer.setLegendLine(new Rectangle2D.Double(-4.0, -4.0, 4.0, 4.0));
//				renderer.setBaseLegendShape(new Rectangle2D.Double(-4.0, -4.0, 4.0, 4.0));

//			    subPlotForSignals.setRangeCrosshairVisible(true);
			    
			    for (TimeSeries timeSeries : (List<TimeSeries>) getPairForType(TimeSeriesType.type_signals).timeSeriesCollection.getSeries()){
			    	SignalMetricType signalMetricType = SignalDefinitions.SignalMetricType.valueOf(timeSeries.getDescription());
			    	
			    	int thresholdForLongEntry = algorithmBase.signalGroup.getSignalBaseForType(signalMetricType).signalParameters.arrayOfSignalGuageForLongEntry[0].threshold;
			    	int thresholdForLongExit = algorithmBase.signalGroup.getSignalBaseForType(signalMetricType).signalParameters.arrayOfSignalGuageForLongExit[0].threshold;
			    	int thresholdForShortEntry = algorithmBase.signalGroup.getSignalBaseForType(signalMetricType).signalParameters.arrayOfSignalGuageForShortEntry[0].threshold;
			    	int thresholdForShortExit = algorithmBase.signalGroup.getSignalBaseForType(signalMetricType).signalParameters.arrayOfSignalGuageForShortExit[0].threshold;
			    	
				    ValueMarker markerForLongEntry = new ValueMarker(thresholdForLongEntry);
				    markerForLongEntry.setPaint(Color.decode("#33AA00"));
				    markerForLongEntry.setAlpha(1.0f);
				    markerForLongEntry.setLabel(signalMetricType.name().replaceAll("metric_", ""));
					markerForLongEntry.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {4.0f, 2.0f}, 2.0f));
					markerForLongEntry.setLabelOffset(new RectangleInsets(8,10,8,0));
					markerForLongEntry.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
				    subPlotForSignals.addRangeMarker(markerForLongEntry);
				    
				    ValueMarker markerForLongExit = new ValueMarker(thresholdForLongExit);
				    markerForLongExit.setPaint(Color.decode("#FF0000"));
				    markerForLongExit.setAlpha(1.0f);
				    markerForLongExit.setLabel(signalMetricType.name().replaceAll("metric_", ""));
					markerForLongExit.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {4.0f, 2.0f}, 2.0f));
					markerForLongExit.setLabelOffset(new RectangleInsets(8,10,8,0));
					markerForLongExit.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
				    subPlotForSignals.addRangeMarker(markerForLongExit);
				    
				    ValueMarker markerForShortEntry = new ValueMarker(thresholdForShortEntry);
				    markerForShortEntry.setPaint(Color.decode("#33AA00"));
				    markerForShortEntry.setAlpha(1.0f);
				    markerForShortEntry.setLabel(signalMetricType.name().replaceAll("metric_", ""));
					markerForShortEntry.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {4.0f, 2.0f}, 2.0f));
					markerForShortEntry.setLabelOffset(new RectangleInsets(8,10,8,0));
					markerForShortEntry.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
				    subPlotForSignals.addRangeMarker(markerForShortEntry);
				    
				    ValueMarker markerForShortExit = new ValueMarker(thresholdForShortExit);
				    markerForShortExit.setPaint(Color.decode("#FF0000"));
				    markerForShortExit.setAlpha(1.0f);
				    markerForShortExit.setLabel(signalMetricType.name().replaceAll("metric_", ""));
					markerForShortExit.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {4.0f, 2.0f}, 2.0f));
					markerForShortExit.setLabelOffset(new RectangleInsets(8,10,8,0));
					markerForShortExit.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
				    subPlotForSignals.addRangeMarker(markerForShortExit);
			    }
			    
			    subPlotForSignals.getRenderer().setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
				
				plot.add(subPlotForSignals, 1);
			}
			
			if (getPairForType(TimeSeriesType.type_price) != null){
				XYPlot subPlotForPrice = new XYPlot(getPairForType(TimeSeriesType.type_price).timeSeriesCollection, null, new NumberAxis(getPairForType(TimeSeriesType.type_price).timeSeriesType.displayName), new StandardXYItemRenderer());
				subPlotForPrice.getRenderer().setSeriesPaint(0, Color.BLACK);
				subPlotForPrice.getRangeAxis().setAutoRange(true);
				((NumberAxis)subPlotForPrice.getRangeAxis()).setAutoRangeIncludesZero(false);
		        subPlotForPrice.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
		        
			    ValueMarker markerForZero = new ValueMarker(0);
			    markerForZero.setPaint(Color.decode("#33AA00"));
			    markerForZero.setAlpha(1.0f);
			    markerForZero.setLabel("");
				markerForZero.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {4.0f, 2.0f}, 2.0f));
				markerForZero.setLabelOffset(new RectangleInsets(8,10,8,0));
				markerForZero.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
		        
				subPlotForPrice.setDataset(2, getPairForType(TimeSeriesType.type_long_entry_price).timeSeriesCollection);
		        subPlotForPrice.setRenderer(2, new XYShapeRenderer());
		        subPlotForPrice.getRenderer(2).setSeriesShape(0, ShapeUtilities.createUpTriangle(4));
		        subPlotForPrice.getRenderer(2).setSeriesPaint(0, Color.decode("#33CC33"));
		        
				subPlotForPrice.setDataset(3, getPairForType(TimeSeriesType.type_long_exit_price).timeSeriesCollection);
		        subPlotForPrice.setRenderer(3, new XYShapeRenderer());
		        subPlotForPrice.getRenderer(3).setSeriesShape(0, ShapeUtilities.createDownTriangle(4));
		        subPlotForPrice.getRenderer(3).setSeriesPaint(0, Color.RED);
		        
		        subPlotForPrice.setDataset(4, getPairForType(TimeSeriesType.type_short_entry_price).timeSeriesCollection);
		        subPlotForPrice.setRenderer(4, new XYShapeRenderer());
		        subPlotForPrice.getRenderer(4).setSeriesShape(0, ShapeUtilities.createUpTriangle(4));
		        subPlotForPrice.getRenderer(4).setSeriesPaint(0, Color.decode("#8000FF"));
		        
		        subPlotForPrice.setDataset(5, getPairForType(TimeSeriesType.type_short_exit_price).timeSeriesCollection);
		        subPlotForPrice.setRenderer(5, new XYShapeRenderer());
		        subPlotForPrice.getRenderer(5).setSeriesShape(0, ShapeUtilities.createDownTriangle(4));
		        subPlotForPrice.getRenderer(5).setSeriesPaint(0, Color.decode("#FF8000"));
		        
		        subPlotForPrice.setDataset(6, getPairForType(TimeSeriesType.type_reentry_price).timeSeriesCollection);
		        subPlotForPrice.setRenderer(6, new XYShapeRenderer());
		        subPlotForPrice.getRenderer(6).setSeriesShape(0, ShapeUtilities.createUpTriangle(4.5f));
		        subPlotForPrice.getRenderer(6).setSeriesPaint(0, Color.decode("#00CCCC"));
		        
				subPlotForPrice.setDataset(7, getPairForType(TimeSeriesType.type_value).timeSeriesCollection);
		        subPlotForPrice.setRangeAxis(7, new NumberAxis(TimeSeriesType.type_value.displayName));
		        subPlotForPrice.setRangeAxisLocation(7, AxisLocation.BOTTOM_OR_RIGHT);
//		        ((NumberAxis)subPlotForPrice.getRangeAxis(7)).setRange(-7.0, 7.0);
		        subPlotForPrice.setRenderer(7, new PostivieNegativeXYBarRenderer(7));
		        ((XYBarRenderer)subPlotForPrice.getRenderer(7)).setShadowVisible(false);
		        ((XYBarRenderer)subPlotForPrice.getRenderer(7)).setBarPainter(new StandardXYBarPainter());
		        subPlotForPrice.mapDatasetToRangeAxis(7, 7);
		        
		        subPlotForPrice.getRenderer().setBaseToolTipGenerator(new StandardXYToolTipGenerator() {
					@Override
					public String generateToolTip(XYDataset dataset, int series, int item) {
						return "$" + String.valueOf(dataset.getY(0, item).doubleValue());
					}
		        });
		        
				plot.add(subPlotForPrice, 1);
			}
	        
//			subPlotForPrice.setDataset(2, getPairForType(TimeSeriesType.type_entry_price).timeSeriesCollection);
//	        subPlotForPrice.setRenderer(2, new XYShapeRenderer());
////	        subPlotForPrice.getRenderer(2).setSeriesShape(0, ShapeUtilities.createUpTriangle(5));
//	        subPlotForPrice.getRenderer(2).setSeriesPaint(0, Color.GREEN);
//	        
//			subPlotForPrice.setDataset(3, getPairForType(TimeSeriesType.type_exit_price).timeSeriesCollection);
//	        subPlotForPrice.setRenderer(3, new XYShapeRenderer());
////	        subPlotForPrice.getRenderer(3).setSeriesShape(0, ShapeUtilities.create(5));
//	        subPlotForPrice.getRenderer(3).setSeriesPaint(0, Color.RED);
	        
			try {
				XYPlot subPlotForCandleStick = ChartFactory.createCandlestickChart("Candlestick Demo", "Time", "Candle Stick", defaultHighLowDataset, false).getXYPlot();
				((NumberAxis)subPlotForCandleStick.getRangeAxis()).setAutoRangeIncludesZero(false);
				subPlotForCandleStick.setBackgroundPaint(Color.white);
				subPlotForCandleStick.setDomainGridlinePaint(Color.lightGray);
				subPlotForCandleStick.setRangeGridlinePaint(Color.lightGray);
				subPlotForCandleStick.setRangeAxis(0, new NumberAxis("Price"));
				subPlotForCandleStick.getRangeAxis(0).setAutoRange(true);
				((NumberAxis)subPlotForCandleStick.getRangeAxis(0)).setAutoRangeIncludesZero(false);
				subPlotForCandleStick.setRangeAxis(1, new NumberAxis("Volume"));
				
				((CandlestickRenderer)subPlotForCandleStick.getRenderer()).setUseOutlinePaint(true);
				
				plot.add(subPlotForCandleStick, 1);
			}catch(Exception e){}
			
			if (getPairForType(TimeSeriesType.type_debug) != null){
				XYPlot subPlotForDebug = new XYPlot(getPairForType(TimeSeriesType.type_debug).timeSeriesCollection, null, new NumberAxis(getPairForType(TimeSeriesType.type_debug).timeSeriesType.displayName), new StandardXYItemRenderer());
				subPlotForDebug.getRangeAxis().setAutoRange(true);
	//			((NumberAxis)subPlotForDebug.getRangeAxis()).setAutoRangeIncludesZero(false);
				
				subPlotForDebug.setRenderer(new XYShapeRenderer());
				subPlotForDebug.getRenderer().setSeriesShape(0, ShapeUtilities.createDiamond(4));
				subPlotForDebug.getRenderer().setSeriesPaint(0, Color.BLACK);
				
				plot.add(subPlotForDebug, 1);
			}
			
			plot.setOrientation(PlotOrientation.VERTICAL);
			plot.setBackgroundPaint(Color.lightGray);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
			plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
			
			DateAxis axis = (DateAxis) plot.getDomainAxis();
			axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
			
			JFreeChart chart = new JFreeChart("autoStock - Analysis - " + title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

			chart.setBackgroundPaint(Color.white);

			return chart;

		}
		
		public TimeSeriesTypePair getPairForType(TimeSeriesType timeSeriesType){
			for (TimeSeriesTypePair pair : arrayOfTimeSeriesPair){
				if (pair.timeSeriesType == timeSeriesType){
					return pair;
				}
			}
			
			return null;
		}
		
		public Color getColor(){
			Color[] arrayOfColors = new Color[]{Color.BLUE, Color.GREEN, Color.RED, Color.PINK, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.DARK_GRAY, Color.LIGHT_GRAY};
			if (usedColor >= arrayOfColors.length-1){usedColor = -1;}
			usedColor++;
			return arrayOfColors[usedColor];
		}
		
		public void resetColor(){
			usedColor = -1;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
	}
}
