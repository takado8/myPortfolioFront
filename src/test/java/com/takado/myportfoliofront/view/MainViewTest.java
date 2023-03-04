package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.client.UserClient;
import com.takado.myportfoliofront.domain.UserDto;
import com.takado.myportfoliofront.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class MainViewTest {
    @InjectMocks
    MainView mainView;
    @Mock
    private AssetService assetService;
    @Mock
    private VsCurrencyService vsCurrencyService;
    @Mock
    private GridValueProvider gridValueProvider;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UserService userService;
    @Mock
    UserClient userClient;
    @Mock
    TickerService tickerService;
    @Mock
    TradeService tradeService;
    @Mock
    PricesService pricesService;
    @Mock
    GridService gridService;
    @Mock
    private NewAssetForm newAssetForm;

    @Test
    void testInit() {
//        var userDto = new UserDto("mail", "123", "12", Collections.emptyList());
//        when(userClient.getUser(any(String.class))).thenReturn(userDto);
//        when(userService.getUser(any(String.class))).thenReturn(userDto);
//        mainView = new MainView(assetService, authenticationService,
//                userService, tickerService, tradeService, pricesService, gridService, vsCurrencyService);
    }
}