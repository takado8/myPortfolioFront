package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.domain.Trade;
import com.vaadin.flow.component.grid.Grid;

public interface TradesGridManager {
     void restoreTradesGridValueAndProfitColumns(Grid<Trade> tradesGrid);
     void setupTradesGrid(Grid<Trade> tradesGrid);
}
