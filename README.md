# codetotrade-java-example
a complete example build a trading bot by using codetotrade.app
## Bot Algorithm
- Buy : RSI < 30
- Sell : RSI > 70
- Take profit : 5%
- Stop loss : 5%
## Requirement
- Java 21 or higher
- Maven

## Quick start
```
mvn clean compile package
```

#### Run BackTest Server
```
java -jar target/back-test.jar
```

#### Run Exchange Server
```
java -jar target/exchange.jar
```
