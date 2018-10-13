package com.stock.repositories;

import com.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByName(String name);

    Stock findBySymbol(String symbol);

}
