package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.domain.Trade;
import com.vaadin.flow.component.grid.Grid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradesGridManagerImpl implements TradesGridManager {
    private final GridService gridService;

    @Override
    public void restoreTradesGridValueAndProfitColumns(Grid<Trade> tradesGrid) {
        gridService.restoreTradesGridValueAndProfitColumns(tradesGrid);
    }

    @Override
    public void setupTradesGrid(Grid<Trade> tradesGrid) {
        gridService.setupTradesGrid(tradesGrid);
    }
}
