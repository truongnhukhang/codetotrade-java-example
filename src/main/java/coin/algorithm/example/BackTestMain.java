package coin.algorithm.example;

import coin.algorithm.example.bot.*;
import coin.algorithm.server.BackTestServer;
import coin.algorithm.server.BinanceServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BackTestMain {
    private static final Logger log = LoggerFactory.getLogger(BackTestMain.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        log.info("Starting BackTest {}", MyActivelyClosePositionTestBot.class);
        BackTestServer server = new BackTestServer(8888, MyActivelyClosePositionTestBot.class);
//        BinanceServer server = new BinanceServer("", "", false, 8888, MyMacdBot.class);
        server.start();
        server.blockUntilShutdown();
    }
}