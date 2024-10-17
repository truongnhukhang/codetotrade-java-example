package coin.algorithm.example.bot;

import coin.algorithm.domain.BaseBot;
import coin.algorithm.domain.TradeMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.Map;

public class RSIBot extends BaseBot {

    private static final Logger log = LoggerFactory.getLogger(RSIBot.class);
    RSIIndicator rsiIndicator;

    @Override
    public void init(Map<String, String> params) {
        log.info("RSI Bot init with params: {}", params);
        int rsiPeriod = Integer.parseInt(params.get("rsiPeriod"));
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
        this.rsiIndicator = new RSIIndicator(closePriceIndicator, rsiPeriod);
    }

    @Override
    public boolean isBuy(int index) {
        return rsiIndicator.getValue(index).doubleValue() < 30;
    }

    @Override
    public boolean isSell(int index) {
        return rsiIndicator.getValue(index).doubleValue() > 70;
    }

    @Override
    public TradeMetadata buy(int index) {
        long price = barSeries.getBar(index).getClosePrice().longValue();
        double amount = 0.1;
        double stopLoss = price * 0.95; // 5% below the current price
        double takeProfit = price * 1.05; // 5% above the current price
        return new TradeMetadata(
                price,
                amount,
                takeProfit,
                stopLoss,
                "RSI: " + rsiIndicator.getValue(index).doubleValue(),
                0
        );

    }

    @Override
    public TradeMetadata sell(int index) {
        long price = barSeries.getBar(index).getClosePrice().longValue();
        double amount = 0.1;
        double stopLoss = price * 1.05; // 5% above the current price
        double takeProfit = price * 0.95; // 5% below the current price
        return new TradeMetadata(
                price,
                amount,
                takeProfit,
                stopLoss,
                "RSI: " + rsiIndicator.getValue(index).doubleValue(),
                0
        );
    }
}
