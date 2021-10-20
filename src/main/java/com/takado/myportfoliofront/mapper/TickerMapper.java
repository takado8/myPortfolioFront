package com.takado.myportfoliofront.mapper;

import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.domain.TickerDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TickerMapper {
    public Ticker mapToTicker(TickerDto tickerDto) {
        return new Ticker(tickerDto.getId(), tickerDto.getTicker(), tickerDto.getCoinId());
    }

    public TickerDto mapToDto(Ticker ticker) {
        return new TickerDto(ticker.getId(), ticker.getTicker(), ticker.getCoinId());
    }

    public List<Ticker> mapToTickerList(List<TickerDto> tickerDtoList) {
        return tickerDtoList.stream().map(this::mapToTicker).collect(Collectors.toList());
    }
}
