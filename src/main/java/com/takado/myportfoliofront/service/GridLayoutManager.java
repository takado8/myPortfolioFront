package com.takado.myportfoliofront.service;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface GridLayoutManager {
    void gridLayoutAdd(Component... components);
    void gridLayoutBringBackMainGrid();
    void gridLayoutRemoveAll();
    void gridLayoutSetSizeFull();
    HorizontalLayout getGridLayout();
    void initNewLayout();
}
