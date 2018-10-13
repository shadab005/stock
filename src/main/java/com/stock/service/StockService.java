package com.stock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.model.Stock;
import com.stock.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@RestController
@RequestMapping(value = "/stock")
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private S3Service s3Service;

    private final String STOCK_BUCKET = "stock-backup-hassan";

    @GetMapping("/hello")
    public String hello() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        System.out.println("IP Address:- " + inetAddress.getHostAddress());
        System.out.println("Host Name:- " + inetAddress.getHostName());
        String hostinfo = "{ hostname :" + inetAddress.getHostName() + ", hostAddress : " + inetAddress.getHostAddress() + " }";
        return "Hello From Stock service " + hostinfo;
    }

    @GetMapping("/symbol")
    public Stock getStockByCode(String symbol) {
        return stockRepository.findBySymbol(symbol);
    }

    @GetMapping("/name")
    public Stock getStockByName(String name) {
        return stockRepository.findByName(name);
    }

    @GetMapping("/all")
    public List<Stock> getAllStocks() {
        System.out.println("Get All Stocks called");
        List<Stock> stocks = stockRepository.findAll();
        System.out.println(stocks);
        return stocks;
    }

    @PostMapping("/save")
    public Stock saveStock(@RequestBody Stock stock) {
        return stockRepository.save(stock);
    }

    @GetMapping("/backup")
    public String backup() throws IOException {
        List<Stock> stockList = getAllStocks();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(stockList);
        File file = new File("test.json");
        FileWriter fr = new FileWriter(file);
        fr.write(json);
        fr.close();
        s3Service.upload(file, "stock-data."+System.currentTimeMillis(), STOCK_BUCKET);
        return json;
    }

    @GetMapping("/readBackUp")
    public String readFromBackUp(String fileName) {
        return s3Service.readFile(STOCK_BUCKET, fileName);
    }
}
