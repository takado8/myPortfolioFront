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
    private int currentPageNb = 1;
    private boolean isButtonsScrollingEnabled = false;
    PageButtonClickedEventListener listener;

    public HorizontalLayout initPagesButtonsPanel(int buttonsCount) {
        if (buttonsCount > 7){
            buttonsCount = 7;
            isButtonsScrollingEnabled = true;
        }else {
            isButtonsScrollingEnabled = false;
        }
        if (buttonsCount < 2){
            buttonsCount = 0;
        }
        buttons.clear();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMaxHeight(40F, Unit.PIXELS);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setMargin(false);
        layout.getThemeList().add("spacing-s");
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

        var clickedButtonPageValue = Integer.parseInt(clickedButton.getText());

        if (isButtonsScrollingEnabled) {
            int step = getButtonsSlideStep(clickedButtonPageValue);
            for (var button : buttons) {
                int newValue = Integer.parseInt(button.getText()) + step;
                button.setText("" + newValue);
                setButtonStyle(button, newValue, clickedButtonPageValue);
            }
        } else {
            for (var button : buttons) {
                int newValue = Integer.parseInt(button.getText());
                setButtonStyle(button, newValue, clickedButtonPageValue);
            }
        }
        currentPageNb = clickedButtonPageValue;
        listener.pageButtonClickedCallback();
    }

    private void setButtonStyle(Span button, int newValue, int clickedButtonPageValue) {
        button.getClassNames().clear();
        if (newValue == clickedButtonPageValue){
            button.getClassNames().add("italicBoldFont");
        }
        else {
            button.getClassNames().add("italicFont");
        }
    }

    private int getButtonsSlideStep(int clickedButtonPageValue) {
        var middleButtonMinValue = 4;
        var middleButton = buttons.get(3);
        var middleButtonPageValue = Integer.parseInt(middleButton.getText());

        if (clickedButtonPageValue < middleButtonMinValue) {
            return middleButtonMinValue - middleButtonPageValue;
        }
        return clickedButtonPageValue - middleButtonPageValue;
    }

    public int getCurrentPageNb(){
        return currentPageNb;
    }
}
