package graph;

import org.knowm.xchart.OHLCChart;
import org.knowm.xchart.OHLCChartBuilder;
import org.knowm.xchart.OHLCSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.OHLCStyler;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.*;
import ru.tinkoff.invest.openapi.models.market.Candle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CandlesPlotDrawer {

    private int resolutionX;
    private int resolutionY;

    public CandlesPlotDrawer(int resolutionX, int resolutionY) {
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
    }

    public OHLCChart drawCandlesPlot(List<Candle> candles, String plotName, String legendStr) {
        OHLCChart chart = new OHLCChartBuilder().width(800).height(600).title(plotName).build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setLegendLayout(Styler.LegendLayout.Horizontal);

        List<Date> xData = new ArrayList<Date>();
        List<Double> openData = new ArrayList<Double>();
        List<Double> highData = new ArrayList<Double>();
        List<Double> lowData = new ArrayList<Double>();
        List<Double> closeData = new ArrayList<Double>();

        populateData(candles, xData, openData, highData, lowData, closeData);

        //xData = null;
        chart.addSeries(legendStr, xData, openData, highData, lowData, closeData)
                .setOhlcSeriesRenderStyle(OHLCSeries.OHLCSeriesRenderStyle.Candle);
        chart.getStyler().setToolTipsEnabled(false);

        return chart;
    }

    public void saveToPng(OHLCChart chart, String path) throws IOException {
        BitmapEncoder.saveBitmap(chart, path, BitmapEncoder.BitmapFormat.PNG);
        //BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);
    }

    private void populateData(List<Candle> candles,
                              List<Date> xData, List<Double> openData,
                              List<Double> highData, List<Double> lowData,
                              List<Double> closeData) {
        for (var item : candles) {
            xData.add(Date.from(item.time.toInstant()));

            openData.add(item.openPrice.doubleValue());
            closeData.add(item.closePrice.doubleValue());
            lowData.add(item.lowestPrice.doubleValue());
            highData.add(item.highestPrice.doubleValue());
        }
    }

}
