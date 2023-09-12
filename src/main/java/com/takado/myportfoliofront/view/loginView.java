package com.takado.myportfoliofront.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;


@Route("")
@PageTitle("Login")
public class loginView extends VerticalLayout {

    public loginView(){
        ClassPathResource resource = new ClassPathResource("google-icon.png");
        InputStreamFactory inputStreamFactory = () -> {
            try {
                return resource.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };
        StreamResource streamResource = new StreamResource("google-icon.png", inputStreamFactory);
        Image googleIcon = new Image(streamResource, "Google Icon");
        googleIcon.setWidth("24px");

        Button loginGoogle = new Button(" Sign in with Google", googleIcon);
        loginGoogle.getStyle().set("cursor", "pointer");
        loginGoogle.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
        loginGoogle.addClickListener(e -> getUI().ifPresent(page -> page.getPage().setLocation("/home")));

        Icon vaadinIcon = new Icon(VaadinIcon.USER);

        Button loginGuest = new Button("Sign in as guest", vaadinIcon);
        loginGuest.getStyle().set("cursor", "pointer");
        loginGuest.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
        loginGuest.addClickListener(e -> getUI().ifPresent(page -> page.getPage().setLocation("/guest")));

        VerticalLayout buttons = new VerticalLayout(loginGoogle, loginGuest);
        buttons.setSizeFull();
        buttons.setAlignItems(Alignment.CENTER);
        buttons.setJustifyContentMode(JustifyContentMode.CENTER);

        add(buttons);
        setSizeFull();
    }
}