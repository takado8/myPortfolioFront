package com.takado.myportfoliofront.service;

import com.vaadin.flow.component.Component;

public interface GridLayoutManager {
    void gridLayoutAdd(Component... components);
    void gridLayoutRemoveAll();
}
