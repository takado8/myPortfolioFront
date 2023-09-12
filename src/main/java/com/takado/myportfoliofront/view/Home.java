package com.takado.myportfoliofront.view;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;

@Push
@Route("home")
@SessionScope
@RequiredArgsConstructor
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class Home extends VerticalLayout {
    private final MainView mainView;

    @PostConstruct
    public void initialize() {
        mainView.setIsGuest(false);
        add(mainView);
        setSizeFull();
    }
}
