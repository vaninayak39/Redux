package com.crio.warmup.stock.portfolio;

//import static java.time.temporal.ChronoUnit.DAYS;
//import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
//import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {
  private RestTemplate restTemplate;
  private StockQuotesService stockQuoteService;

  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;

  }

  protected PortfolioManagerImpl(StockQuotesService stockQuoteService) {
    this.stockQuoteService = stockQuoteService;

  }
  // DO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from
  // main anymore.
  // Copy your code from Module#3
  // PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and ensure it follows the
  // method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required
  // further as our
  // clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command
  // below:
  // ./gradlew test --tests PortfolioManagerTest

  // CHECKSTYLE:OFF

  public List<AnnualizedReturn> calculateAnnualizedReturn(
      List<PortfolioTrade> portfolioTrades, LocalDate endDate)
      throws StockQuoteServiceException {
    AnnualizedReturn annualizedReturn;
    List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();
    for (int i = 0; i < portfolioTrades.size(); i++) {
      annualizedReturn = calculate(portfolioTrades.get(i), endDate);
      annualizedReturns.add(annualizedReturn);
    }
    Collections.sort(annualizedReturns, getComparator());
    return annualizedReturns;
  }

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  public AnnualizedReturn calculate(PortfolioTrade trade, LocalDate endDate) 
      throws StockQuoteServiceException {
    AnnualizedReturn annualizedReturn;
    String symbol = trade.getSymbol();
    LocalDate startDate = trade.getPurchaseDate();
    try {
      List<Candle> stocksStartToEndDate;
      stocksStartToEndDate = getStockQuote(symbol, startDate, endDate);
      Candle stockStartDate = stocksStartToEndDate.get(0);
      Candle stockLastDate = stocksStartToEndDate.get(stocksStartToEndDate.size() - 1);
      Double buyPrice = stockStartDate.getOpen();
      Double sellPrice = stockLastDate.getClose();
      Double totalReturns = (sellPrice - buyPrice) / buyPrice;
      Double years = (double) ChronoUnit.DAYS.between(startDate, endDate) / 365;
      Double annualizedReturns = Math.pow(1 + totalReturns, 1 / years) - 1;
      annualizedReturn = new AnnualizedReturn(symbol, annualizedReturns, totalReturns);
    } catch (JsonProcessingException e) {
      annualizedReturn = new AnnualizedReturn(symbol, Double.NaN, Double.NaN);
    }
    return annualizedReturn;
  }

  // CHECKSTYLE:OFF

  // DO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo third-party APIs to a separate function.
  // Remember to fill out the buildUri function and use that.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException {
    // ObjectMapper objectMapper = new ObjectMapper();
    // if (from.compareTo(to) >= 0) {
    // throw new RuntimeException();
    // }
    // String url = buildUri(symbol, from, to);
    // TiingoCandle[] results = objectMapper.readValue(url, TiingoCandle[].class);
    // if (results == null) {
    // return new ArrayList<Candle>();
    // } else {
    // List<Candle> stockList = Arrays.asList(results);
    // return stockList;
    // }
    return stockQuoteService.getStockQuote(symbol, from, to);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "35388790d5696fd71ef95e094c51f0906bd7106b";
    String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
        + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
    String uri = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol)
        .replace("$STARTDATE", startDate.toString()).replace("$ENDDATE", endDate.toString());
    return uri;
  }

  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades,
      LocalDate endDate, int numThreads) throws InterruptedException, StockQuoteServiceException {
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    List<Future<AnnualizedReturn>> finalans = new ArrayList<Future<AnnualizedReturn>>();
    List<AnnualizedReturn> annual = new ArrayList<>();
    for (PortfolioTrade tr:portfolioTrades) {
      Callable<AnnualizedReturn> callableTask = () -> {
        AnnualizedReturn ans = calculate(tr, endDate);
        return ans;
      };
      Future<AnnualizedReturn> ans1 = executor.submit(callableTask);
      finalans.add(ans1);
      //submit Callable tasks to be executed by thread pool
      //add Future to the list, we can get return value using Future
    }
    for (Future<AnnualizedReturn> fut : finalans) {
      try {
        annual.add(fut.get());
      } catch (Exception e) {
        throw new StockQuoteServiceException(e.getMessage());
      }
      executor.shutdown();
    }    
    Collections.sort(annual, getComparator());
    // TODO Auto-generated method stub
    return annual;
  }
}
