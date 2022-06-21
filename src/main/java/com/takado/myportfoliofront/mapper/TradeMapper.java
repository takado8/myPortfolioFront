package com.takado.myportfoliofront.mapper;

import com.takado.myportfoliofront.domain.Trade;
import com.takado.myportfoliofront.domain.TradeDto;
import com.takado.myportfoliofront.service.TickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TradeMapper {
    private final TickerService tickerService;

    public TradeDto mapToDto(Trade trade) {
        return new TradeDto(trade.getId(), trade.getUserId(), trade.getTicker().getId(), trade.getAmount(),
                trade.getValueIn(), trade.getType(), trade.getDateTime());
    }

    public Trade mapToTrade(TradeDto tradeDto) {
        return new Trade(tradeDto.getId(), tradeDto.getUserId(), tickerService.getTicker(tradeDto.getTickerId()),
                tradeDto.getAmount(), tradeDto.getValueIn(), tradeDto.getType(), tradeDto.getDateTime());
    }

    public List<Trade> mapToTrade(List<TradeDto> tradeDtoList) {
        return tradeDtoList.stream().map(this::mapToTrade).collect(Collectors.toList());
    }
}
