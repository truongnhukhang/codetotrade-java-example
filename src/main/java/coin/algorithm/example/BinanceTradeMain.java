package coin.algorithm.example;

import coin.algorithm.example.bot.RSIBot;
import coin.algorithm.server.BinanceServer;

import java.io.IOException;

public class BinanceTradeMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        BinanceServer binanceServer = new BinanceServer("", "", true, 8888, RSIBot.class);
        binanceServer.start();
        binanceServer.blockUntilShutdown();
    }
}