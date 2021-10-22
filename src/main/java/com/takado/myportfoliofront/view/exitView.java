package com.takado.myportfoliofront.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("exitPage")
@PageTitle("Exit")
public class exitView extends VerticalLayout {
    private final String apiRoot = "http://localhost:8080";

    public exitView() {
        Button loginAgainButton = new Button("Login again");
        loginAgainButton.getStyle().set("cursor", "pointer");
        loginAgainButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);

        loginAgainButton.addClickListener(e ->
                getUI().ifPresent(page -> page.getPage().setLocation(apiRoot)));
        FlexLayout toolbar = new FlexLayout(loginAgainButton);
        add(toolbar);
        setSizeFull();
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}