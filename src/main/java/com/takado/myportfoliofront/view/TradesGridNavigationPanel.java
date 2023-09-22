package com.takado.myportfoliofront.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@CssImport(include = "italicBoldFont", value = "./styles.css")
@CssImport(include = "italicFont", value = "./styles.css")
public class TradesGridNavigationPanel {
    private final static int MAX_BUTTONS_COUNT = 7; // should be an odd number
    private final List<Span> buttons = new ArrayList<>();
    private int currentPageNb = 1;
    private boolean isButtonsScrollingEnabled = false;
    PageButtonClickedEventListener listener;
    private  HorizontalLayout buttonsLayout;

    @PostConstruct
    private void postConstruct() throws Exception {
        if (MAX_BUTTONS_COUNT % 2 == 0) {
            throw new Exception("MAX_BUTTONS_COUNT should be an odd number. in com.takado.myportfoliofront.view.TradesGridNavigationPanel");
        }
    }

    public HorizontalLayout initPagesButtonsPanel(int buttonsCount) {
        currentPageNb = 1;
        buttonsCount = adjustButtonsCount(buttonsCount);
        buttons.clear();
        buttonsLayout = new HorizontalLayout();
        buttonsLayout.setMaxHeight(40F, Unit.PIXELS);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonsLayout.setSpacing(false);
        buttonsLayout.setPadding(false);
        buttonsLayout.setMargin(false);
        buttonsLayout.getThemeList().add("spacing-s");
        createButtons(buttonsCount);
        return buttonsLayout;
    }

    public void refreshPagesButtonsPanel(int buttonsCount) {
        if (buttonsLayout != null) {
            buttonsCount = adjustButtonsCount(buttonsCount);
            buttons.clear();
            buttonsLayout.removeAll();
            createButtons(buttonsCount);
        }
    }

    private void createButtons(int buttonsCount) {
        if (buttonsLayout != null) {
            for (int i = 0; i < buttonsCount; i++) {
                Span button = makeButton("" + (i + 1), "" + i, i == currentPageNb - 1);
                buttons.add(button);
                buttonsLayout.add(button);
            }
        }
    }

    private int adjustButtonsCount(int buttonsCount) {
        if (buttonsCount > MAX_BUTTONS_COUNT){
            buttonsCount = MAX_BUTTONS_COUNT;
            isButtonsScrollingEnabled = true;
        }else {
            isButtonsScrollingEnabled = false;
        }
        if (buttonsCount < 2){
            buttonsCount = 0;
        }
        return buttonsCount;
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
        int middleButtonIdx = (MAX_BUTTONS_COUNT - 1) / 2;
        var middleButtonMinValue = middleButtonIdx + 1;
        var middleButton = buttons.get(middleButtonIdx);
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
