package coin.algorithm.example.bot;

import coin.algorithm.domain.BaseBot;
import coin.algorithm.domain.TradeMetadata;
import coin.algorithm.domain.chart.Chart;
import coin.algorithm.domain.chart.Plot;
import coin.algorithm.domain.chart.PlotStyle;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class MyChartingBot extends BaseBot {
    @Override
    public void init(Map<String, String> map) {
        BarSeries barSeries = getBarSeries();
        BarSeries barSeries1h = otherBarSeries.get(Duration.ofMinutes(60));
        ClosePriceIndicator closePrice = new ClosePriceIndicator(barSeries);
        ClosePriceIndicator closePrice1h = new ClosePriceIndicator(barSeries1h);
        EMAIndicator ema = new EMAIndicator(closePrice, 34);
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);

        EMAIndicator ema1h = new EMAIndicator(closePrice1h, 34);
        MACDIndicator macd1h = new MACDIndicator(closePrice1h, 12, 26);
        EMAIndicator signal1h = new EMAIndicator(macd1h, 9);

        Plot ema34 = new Plot("ema-34").withColor("red").withIndicator(ema).withStyle(PlotStyle.LINE)
                                       .withPricePrecision(2);
        Plot ema34_1h = new Plot("ema-34-1h").withColor("#32CD32").withIndicator(ema1h).withStyle(PlotStyle.LINE)
                                             .withPricePrecision(2);


        Chart chart = new Chart("MainChart");
        chart.setOverlay(true);
        chart.setPlotList(Arrays.asList(ema34, ema34_1h));

        Plot rsi14 = new Plot("rsi-14").withColor("#FFC0CB").withIndicator(rsi).withStyle(PlotStyle.LINE)
                                       .withPricePrecision(2);
        Chart rsiChart = new Chart("RsiChart");
        rsiChart.setOverlay(false);
        rsiChart.setPlotList(Arrays.asList(rsi14));

        Plot macd_1h = new Plot("macd-1h").withColor("green").withIndicator(macd1h).withStyle(PlotStyle.LINE)
                                          .withPricePrecision(2);
        Plot signal_1h = new Plot("signal-1h").withColor("blue").withIndicator(signal1h).withStyle(PlotStyle.LINE)
                                              .withPricePrecision(2);
        Chart macdChart = new Chart("MacdChart");
        macdChart.setOverlay(false);
        macdChart.setPlotList(Arrays.asList(macd_1h, signal_1h));

        this.setChartList(Arrays.asList(chart, rsiChart, macdChart));

    }

    @Override
    public boolean isBuy(int i) {
        return false;
    }

    @Override
    public boolean isSell(int i) {
        return false;
    }

    @Override
    public TradeMetadata buy(int i) {
        return null;
    }

    @Override
    public TradeMetadata sell(int i) {
        return null;
    }
}
