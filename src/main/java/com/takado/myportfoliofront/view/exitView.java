package com.takado.myportfoliofront.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.takado.myportfoliofront.config.AddressConfig.SERVER_ADDRESS;

@Route("exitPage")
@PageTitle("Exit")
public class exitView extends VerticalLayout {


    public exitView() {
        Button loginAgainButton = new Button("Login again");
        loginAgainButton.getStyle().set("cursor", "pointer");
        loginAgainButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);

        loginAgainButton.addClickListener(e -> getUI().ifPresent(page -> page.getPage().setLocation("/")));
        FlexLayout toolbar = new FlexLayout(loginAgainButton);
        add(toolbar);
        setSizeFull();
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}