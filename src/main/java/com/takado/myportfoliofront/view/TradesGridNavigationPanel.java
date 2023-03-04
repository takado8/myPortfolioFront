package com.takado.myportfoliofront.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CssImport(include = "italicBoldFont", value = "./styles.css")
@CssImport(include = "italicFont", value = "./styles.css")
public class TradesGridNavigationPanel {
    private int currentPageNb = 1;
    private final List<Span> buttons = new ArrayList<>();
    PageButtonClickedEventListener listener;

    public HorizontalLayout initPagesButtonsPanel() {
        currentPageNb = 1;
        buttons.clear();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMaxHeight(40F, Unit.PIXELS);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setMargin(false);
        layout.getThemeList().add("spacing-s");
        int buttonsCount = 7;
        for (int i = 0; i < buttonsCount; i++) {
            Span button = makeButton("" + (i + 1));
            buttons.add(button);
            layout.add(button);
        }
        return layout;
    }

    private Span makeButton(String txt) {
        Span button = new Span(txt);
        button.setClassName("italicFont");
        button.getElement().getThemeList().add("badge");
        button.getStyle().set("cursor", "pointer");
        button.addClickListener(this::buttonClicked);
        return button;
    }

    public void addListener(PageButtonClickedEventListener listener){
        this.listener = listener;
    }

    private void buttonClicked(ClickEvent<Span> buttonClickEvent) {
        var currentPageButton = buttons.get(currentPageNb - 1);
        currentPageButton.getClassNames().remove("italicBoldFont");
        currentPageButton.getClassNames().add("italicFont");

        var button = buttonClickEvent.getSource();
        button.getClassNames().remove("italicFont");
        button.getClassNames().add("italicBoldFont");

        currentPageNb = Integer.parseInt(button.getText());
        listener.callback(button);
    }
}
