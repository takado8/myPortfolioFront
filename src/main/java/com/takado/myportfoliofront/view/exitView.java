package com.takado.myportfoliofront.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("exitPage")
@PageTitle("Exit")
public class exitView extends VerticalLayout {

    public exitView() {

        Button loginAgainButton = new Button("Login again");
        loginAgainButton.getStyle().set("cursor", "pointer");
        loginAgainButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);

        loginAgainButton.addClickListener(e -> UI.getCurrent().getPage().setLocation(""));
        FlexLayout toolbar = new FlexLayout(loginAgainButton);
        add(toolbar);
        setSizeFull();

        
    }

}