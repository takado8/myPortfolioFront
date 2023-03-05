package com.takado.myportfoliofront.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CssImport(include = "italicBoldFont", value = "./styles.css")
@CssImport(include = "italicFont", value = "./styles.css")
public class TradesGridNavigationPanel {
    private final List<Span> buttons = new ArrayList<>();
    PageButtonClickedEventListener listener;

    public HorizontalLayout initPagesButtonsPanel() {
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
            Span button = makeButton("" + (i + 1), "" + i, i == 0);
            buttons.add(button);
            layout.add(button);
        }
        return layout;
    }

    private Span makeButton(String txt, String id, boolean bold) {
        Span button = new Span(txt);
        button.setId(id);
        button.getElement().getThemeList().add("badge");
        button.getStyle().set("cursor", "pointer");
        button.addClickListener(this::buttonClicked);

        if (bold){
            button.getClassNames().add("italicBoldFont");
        }else {
            button.getClassNames().add("italicFont");
        }

        return button;
    }

    public void addListener(PageButtonClickedEventListener listener) {
        this.listener = listener;
    }

    private void buttonClicked(ClickEvent<Span> buttonClickEvent) {
        var clickedButton = buttonClickEvent.getSource();

        listener.callback(clickedButton);
        var clickedButtonPageValue = Integer.parseInt(clickedButton.getText());
        var middleButtonMinValue = 4;

        var middleButton = buttons.get(3);
        var middleButtonPageValue = Integer.parseInt(middleButton.getText());
        int step;
        if (clickedButtonPageValue < middleButtonMinValue) {
            step = middleButtonMinValue - middleButtonPageValue;
        }else {
            step = clickedButtonPageValue - middleButtonPageValue;
        }
        for (var button : buttons) {
            int newValue = Integer.parseInt(button.getText()) + step;
            button.setText("" + newValue);
            button.getClassNames().clear();
            if (newValue == clickedButtonPageValue){
                button.getClassNames().add("italicBoldFont");
            }
            else {
                button.getClassNames().add("italicFont");
            }
        }
    }
}
