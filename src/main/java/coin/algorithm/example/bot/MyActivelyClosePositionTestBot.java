package coin.algorithm.example.bot;

import coin.algorithm.domain.BaseBot;
import coin.algorithm.domain.TradeMetadata;
import coin.algorithm.domain.chart.Chart;
import coin.algorithm.domain.chart.Plot;
import coin.algorithm.domain.chart.PlotStyle;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class MyActivelyClosePositionTestBot extends BaseBot {
    private RSIIndicator rsi;
    private MACDIndicator macd;
    private EMAIndicator signal;
    private double tp;
    private double sl;
    private BarSeries barSeries1h;

    @Override
    public void init(Map<String, String> config) {
        BarSeries series = getBarSeries();
        this.barSeries1h = otherBarSeries.get(Duration.ofMinutes(60));
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        ClosePriceIndicator closePrice1h = new ClosePriceIndicator(barSeries1h);
        int rsiPeriod = Integer.parseInt(config.get("rsi"));
        int slow = Integer.parseInt(config.get("slow"));
        int fast = Integer.parseInt(config.get("fast"));
        int signalPeriod = Integer.parseInt(config.get("signal"));
        tp = Double.parseDouble(config.getOrDefault("tp", "3"));
        sl = Double.parseDouble(config.getOrDefault("sl", "3"));
        this.rsi = new RSIIndicator(closePrice, rsiPeriod);
        this.macd = new MACDIndicator(closePrice1h, fast, slow);
        this.signal = new EMAIndicator(macd, signalPeriod);

        Plot rsi14 = new Plot("rsi-14").withColor("#FFC0CB").withIndicator(rsi).withStyle(PlotStyle.LINE)
                .withPricePrecision(2);
        Chart rsiChart = new Chart("RsiChart");
        rsiChart.setOverlay(false);
        rsiChart.setPlotList(Arrays.asList(rsi14));

        Plot macd_1h = new Plot("macd-1h").withColor("green").withIndicator(macd).withStyle(PlotStyle.LINE)
                .withPricePrecision(2);
        Plot signal_1h = new Plot("signal-1h").withColor("blue").withIndicator(signal).withStyle(PlotStyle.LINE)
                .withPricePrecision(2);
        Chart macdChart = new Chart("MacdChart");
        macdChart.setOverlay(false);
        macdChart.setPlotList(Arrays.asList(macd_1h, signal_1h));

        this.setChartList(Arrays.asList(rsiChart, macdChart));
    }

    @Override
    public boolean isBuy(int idx) {
        long startTime = barSeries.getBar(idx).getBeginTime().toInstant().toEpochMilli();
        int idx1h = getIndexOfBarSeriesByTime(barSeries1h, startTime);
        Num macdValue = macd.getValue(idx1h);
        Num signalValue = signal.getValue(idx1h);
        Num prevMacdValue = macd.getValue(idx1h - 1);
        Num prevSignalValue = signal.getValue(idx1h - 1);

        if (macdValue.isGreaterThan(signalValue) && prevMacdValue.isLessThanOrEqual(prevSignalValue)) {
            if (rsi.getValue(idx).doubleValue() < 70) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSell(int idx) {
        int idx1h = getIndexOfBarSeriesByTime(barSeries1h, barSeries.getBar(idx).getBeginTime().toInstant().toEpochMilli());
        Num macdValue = macd.getValue(idx1h);
        Num signalValue = signal.getValue(idx1h);
        Num prevMacdValue = macd.getValue(idx1h - 1);
        Num prevSignalValue = signal.getValue(idx1h - 1);

        if (macdValue.isLessThan(signalValue) && prevMacdValue.isGreaterThanOrEqual(prevSignalValue)) {
            if (rsi.getValue(idx).doubleValue() > 30) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TradeMetadata buy(int idx) {
        int idx1h = getIndexOfBarSeriesByTime(barSeries1h, barSeries.getBar(idx).getBeginTime().toInstant().toEpochMilli());
        Bar currentBar = getBarSeries().getBar(idx);
        double btcAmount = 0.1;
        double entryPrice = currentBar.getClosePrice().doubleValue();
        double takeProfit = currentBar.getClosePrice().doubleValue() * (1 + tp / 100);
        double stopLoss = currentBar.getClosePrice().doubleValue() * (1 - sl / 100);
        String tradeLog = String.format("MyMacdBot Buy, MACD-SIGNAL:%f-%f,PREV(MACD-SIGNAL):%f-%f, RSI: %f",
                macd.getValue(idx1h).doubleValue(),
                signal.getValue(idx1h).doubleValue(),
                macd.getValue(idx1h - 1).doubleValue(),
                signal.getValue(idx1h - 1).doubleValue(),
                rsi.getValue(idx).doubleValue());
        return new TradeMetadata(entryPrice, btcAmount, takeProfit, stopLoss, tradeLog, 30);
    }

    @Override
    public TradeMetadata sell(int idx) {
        int idx1h = getIndexOfBarSeriesByTime(barSeries1h, barSeries.getBar(idx).getBeginTime().toInstant().toEpochMilli());
        Bar currentBar = getBarSeries().getBar(idx);
        double btcAmount = 0.1;
        double entryPrice = currentBar.getClosePrice().doubleValue();
        double takeProfit = currentBar.getClosePrice().doubleValue() * (1 - tp / 100);
        double stopLoss = currentBar.getClosePrice().doubleValue() * (1 + sl / 100);
        String tradeLog = String.format("MyMacdBot Sell, MACD-SIGNAL:%f-%f,PREV(MACD-SIGNAL):%f-%f, RSI: %f",
                macd.getValue(idx1h).doubleValue(),
                signal.getValue(idx1h).doubleValue(),
                macd.getValue(idx1h - 1).doubleValue(),
                signal.getValue(idx1h - 1).doubleValue(),
                rsi.getValue(idx).doubleValue());
        return new TradeMetadata(entryPrice, btcAmount, takeProfit, stopLoss, tradeLog, 30);
    }

    @Override
    public boolean isCloseBuyPosition(int idx) {
        int idx1h = getIndexOfBarSeriesByTime(barSeries1h, barSeries.getBar(idx).getBeginTime().toInstant().toEpochMilli());
        Num macdValue = macd.getValue(idx1h);
        Num signalValue = signal.getValue(idx1h);
        Num prevMacdValue = macd.getValue(idx1h - 1);
        Num prevSignalValue = signal.getValue(idx1h - 1);
        return prevMacdValue.isGreaterThanOrEqual(prevSignalValue) && macdValue.isLessThan(signalValue);
    }

    @Override
    public boolean isCloseSellPosition(int idx) {
        int idx1h = getIndexOfBarSeriesByTime(barSeries1h, barSeries.getBar(idx).getBeginTime().toInstant().toEpochMilli());
        Num macdValue = macd.getValue(idx1h);
        Num signalValue = signal.getValue(idx1h);
        Num prevMacdValue = macd.getValue(idx1h - 1);
        Num prevSignalValue = signal.getValue(idx1h - 1);
        return prevMacdValue.isLessThanOrEqual(prevSignalValue) && macdValue.isGreaterThan(signalValue);
    }
}
