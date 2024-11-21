package coin.algorithm.example.bot;

import coin.algorithm.domain.BaseBot;
import coin.algorithm.domain.TradeMetadata;
import org.ta4j.core.*;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.util.Map;

public class MyMacdBot extends BaseBot {
    private RSIIndicator rsi;
    private MACDIndicator macd;
    private EMAIndicator signal;
    private double tp;
    private double sl;
    @Override
    public void init(Map<String, String> config) {
        BarSeries series = getBarSeries(); // Assume this method exists in BaseBot
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        int rsiPeriod = Integer.parseInt(config.get("rsi"));
        int slow = Integer.parseInt(config.get("slow"));
        int fast = Integer.parseInt(config.get("fast"));
        int signalPeriod = Integer.parseInt(config.get("signal"));
        tp = Double.parseDouble(config.get("tp"));
        sl = Double.parseDouble(config.get("sl"));
        this.rsi = new RSIIndicator(closePrice, rsiPeriod);
        this.macd = new MACDIndicator(closePrice, fast, slow);
        this.signal = new EMAIndicator(macd, signalPeriod);
    }

    @Override
    public boolean isBuy(int idx) {
        Num macdValue = macd.getValue(idx);
        Num signalValue = signal.getValue(idx);
        Num prevMacdValue = macd.getValue(idx - 1);
        Num prevSignalValue = signal.getValue(idx - 1);

        if (macdValue.isGreaterThan(signalValue) && prevMacdValue.isLessThanOrEqual(prevSignalValue)) {
            if (rsi.getValue(idx).doubleValue() < 70) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSell(int idx) {
        Num macdValue = macd.getValue(idx);
        Num signalValue = signal.getValue(idx);
        Num prevMacdValue = macd.getValue(idx - 1);
        Num prevSignalValue = signal.getValue(idx - 1);

        if (macdValue.isLessThan(signalValue) && prevMacdValue.isGreaterThanOrEqual(prevSignalValue)) {
            if (rsi.getValue(idx).doubleValue() > 30) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TradeMetadata buy(int idx) {
        Bar currentBar = getBarSeries().getBar(idx);
        double btcAmount = 0.1;
        double entryPrice = currentBar.getClosePrice().doubleValue();
        double takeProfit = currentBar.getClosePrice().doubleValue() * (1 + tp/100);
        double stopLoss = currentBar.getClosePrice().doubleValue() * (1- sl/100);
        String tradeLog = String.format("MyMacdBot Buy, MACD-SIGNAL:%f-%f,PREV(MACD-SIGNAL):%f-%f, RSI: %f",
                macd.getValue(idx).doubleValue(),signal.getValue(idx).doubleValue(), macd.getValue(idx - 1).doubleValue(),
                signal.getValue(idx - 1).doubleValue(), rsi.getValue(idx).doubleValue());
        return new TradeMetadata(entryPrice, btcAmount, takeProfit, stopLoss, tradeLog, 30);
    }

    @Override
    public TradeMetadata sell(int idx) {
        Bar currentBar = getBarSeries().getBar(idx);
        double btcAmount = 0.1;
        double entryPrice = currentBar.getClosePrice().doubleValue();
        double takeProfit = currentBar.getClosePrice().doubleValue() * (1 - tp/100);
        double stopLoss = currentBar.getClosePrice().doubleValue() * (1+ sl/100);
        String tradeLog = String.format("MyMacdBot Sell, MACD-SIGNAL:%f-%f,PREV(MACD-SIGNAL):%f-%f, RSI: %f",
                macd.getValue(idx).doubleValue(),signal.getValue(idx).doubleValue(), macd.getValue(idx - 1).doubleValue(),
                signal.getValue(idx - 1).doubleValue(), rsi.getValue(idx).doubleValue());
        return new TradeMetadata(entryPrice, btcAmount, takeProfit, stopLoss, tradeLog, 30);
    }

    @Override
    public boolean isCloseBuyPosition(int idx) {
        return false;
    }

    @Override
    public boolean isCloseSellPosition(int idx) {
        return false;
    }
}