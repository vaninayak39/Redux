package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
//import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
//import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.core.type.TypeRef
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
//import java.util.Date;
//import java.text.ParseException;
import java.nio.file.Paths;
import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
//import java.util.HashMap;
import java.util.List;
//import java.util.Set;
import java.util.UUID;
//import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.management.RuntimeErrorException;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;
//import java.util.logging.Level;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;

public class PortfolioManagerApplication {

  // CRIO_TASK_MODULE_JSON_PARSING
  // Read the json file provided in the argument[0]. The file will be available in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. Extract stock symbols from the json file with ObjectMapper provided by
  // #getObjectMapper.
  // 3. Return the list of all symbols in the same order as provided in json.

  // Note:
  // 1. There can be few unused imports, you will need to fix them to make the
  // build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] trades = objectMapper.readValue(resolveFileFromResources(args[0]), 
        PortfolioTrade[].class);
    List<String> ans = new ArrayList<String>();
    for (PortfolioTrade trade : trades) {
      ans.add(trade.getSymbol());
    }
    return ans;

    // System.out.println(Stream.of(trades).map(
    // PortfolioTrade::getSymbol).collect(Collectors.toList()));
    // return
    // Stream.of(trades).map(PortfolioTrade::getSymbol).collect(Collectors.toList());
    // return Collections.emptyList();
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) 
       throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader()
        .getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = 
        "File@51 '/home/crio-user/workspace/vaninayak39-ME_QMONEY/qmoney/bin/main/trades.json'";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@2f9f7dcf";
    String functionNameFromTestFileInStackTrace = ".mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "22";
    return Arrays.asList(new String[] { valueOfArgument0, 
        resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }

  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>

  // Note:
  // Remember to confirm that you are getting same results for annualized returns
  // as in Module 3.

  public static List<TotalReturnsDto> mainReadQuotesHelper(String[] args,
      List<PortfolioTrade> trades)
      throws IOException, URISyntaxException {
    List<TotalReturnsDto> tests = new ArrayList<TotalReturnsDto>();
    for (PortfolioTrade t : trades) {
      LocalDate dt = LocalDate.parse(args[1]);
      if (t.getPurchaseDate().isAfter(dt)) {
        throw new RuntimeException();
      } else {
        TiingoCandle[] results = resultFromApi(t, args);
        tests.add(new TotalReturnsDto(t.getSymbol(), results[results.length - 1].getClose()));
      }
    }
    return tests;
  }

  public static final Comparator<TotalReturnsDto> closingComparator =
      new Comparator<TotalReturnsDto>() {
    public int compare(TotalReturnsDto t1, TotalReturnsDto t2) {
      return (int) (t1.getClosingPrice().compareTo(t2.getClosingPrice()));
    }
  };

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    ObjectMapper objectMapper = getObjectMapper();
    List<PortfolioTrade> trades = Arrays
        .asList(objectMapper.readValue(resolveFileFromResources(args[0]), PortfolioTrade[].class));
    List<TotalReturnsDto> sortedByValue = mainReadQuotesHelper(args, trades);
    // Comparator<TotalReturnsDto> userComparator =
    // Comparator.comparing(TotalReturnsDto::getClosingPrice);
    // Collections.sort(sortedByValue, userComparator);
    Collections.sort(sortedByValue, closingComparator);
    List<String> stocks = new ArrayList<String>();
    for (TotalReturnsDto trd : sortedByValue) {
      stocks.add(trd.getSymbol());
    }
    return stocks;
  }
  // CRIO_TASK_MODULE_CALCULATIONS
  // Now that you have the list of PortfolioTrade and their data, calculate
  // annualized returns
  // for the stocks provided in the Json.
  // Use the function you just wrote #calculateAnnualizedReturns.
  // Return the list of AnnualizedReturns sorted by annualizedReturns in
  // descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
    List<AnnualizedReturn> ans = new ArrayList<AnnualizedReturn>();
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] trades = objectMapper.readValue(
        resolveFileFromResources(args[0]), PortfolioTrade[].class);
    LocalDate endDate = LocalDate.parse(args[1]);
    for (PortfolioTrade trade : trades) {
      TiingoCandle[] results = resultFromApi(trade, args);
      if (endDate.isAfter(trade.getPurchaseDate())) {
        ans.add(calculateAnnualizedReturns(endDate, trade, results[0].getOpen(),
            results[results.length - 1].getClose()));
      }
    }
    Collections.sort(ans, annualComparator);
    // Collections.sort(ans,
    // Comparator.comparing((AnnualizedReturn::getAnnualizedReturn)).reversed());
    return ans;
  }

  public static TiingoCandle[] resultFromApi(PortfolioTrade trade, String[] args)
      throws IOException, URISyntaxException {
    RestTemplate restTemplate = new RestTemplate();
    String uri = "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate="
        + trade.getPurchaseDate().toString() + "&endDate=" + args[1]
        + "&token=35388790d5696fd71ef95e094c51f0906bd7106b";
    TiingoCandle[] results = restTemplate.getForObject(uri, TiingoCandle[].class);
    return results;
  }

  // CRIO_TASK_MODULE_CALCULATIONS
  // Return the populated list of AnnualizedReturn for all stocks.
  // Annualized returns should be calculated in two steps:
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  // 1.1 Store the same as totalReturns
  // 2. Calculate extrapolated annualized returns by scaling the same in years
  // span.
  // The formula is:
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // 2.1 Store the same as annualized_returns
  // Test the same using below specified command. The build should be successful.
  // ./gradlew test --tests
  // PortfolioManagerApplicationTest.testCalculateAnnualizedReturn
  public static final Comparator<AnnualizedReturn> annualComparator =
      new Comparator<AnnualizedReturn>() {
    public int compare(AnnualizedReturn a1, AnnualizedReturn a2) {
      return (int) (a2.getAnnualizedReturn().compareTo(a1.getAnnualizedReturn()));
    }
  };

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, 
      PortfolioTrade trade, Double buyPrice,
      Double sellPrice) {
    LocalDate startDate = trade.getPurchaseDate();
    Double totalReturns = (sellPrice - buyPrice) / buyPrice;
    long days = ChronoUnit.DAYS.between(startDate, endDate);
    Double years = (double) days / 365;
    Double annualizedReturns = Math.pow(1 + totalReturns, 1 / years) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns);
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    // printJsonObject(mainReadFile(args));
    // printJsonObject(mainReadQuotes(args));
    // printJsonObject(mainCalculateSingleReturn(args));
    printJsonObject(mainCalculateReturnsAfterRefactor(args));

  }

  public static final RestTemplate restTemplate = new RestTemplate();
  public static final PortfolioManager portfolioManager =
      PortfolioManagerFactory.getPortfolioManager("alpha",restTemplate);

  // DO: CRIO_TASK_MODULE_REFACTOR
  // Once you are done with the implementation inside PortfolioManagerImpl and
  // PortfolioManagerFactory, create PortfolioManager using
  // PortfolioManagerFactory.
  // Refer to the code from previous modules to get the List<PortfolioTrades> and
  // endDate, and
  // call the newly implemented method in PortfolioManager to calculate the
  // annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns
  // as in Module 3.

  private static String readFileAsString(String filename) throws URISyntaxException, IOException {
    return new String(Files.readAllBytes(resolveFileFromResources(filename).toPath()), "UTF-8");
  }

  // PortfolioManagerFactory portfolioManager = new PortfolioManagerFactory();

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(
        String[] args) throws Exception {
    String file = args[0];
    String contents = readFileAsString(file);
    ObjectMapper objectMapper = getObjectMapper();
    LocalDate endDate = LocalDate.parse(args[1]);
    PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
    // RestTemplate restTemplate = new RestTemplate();
    // PortfolioManager portfolioManager =
    // PortfolioManagerFactory.getPortfolioManager(restTemplate);
    return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }
}
