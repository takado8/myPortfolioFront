package com.takado.myportfoliofront.service.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GridLayoutManagerImpl implements GridLayoutManager {
    private final GridService gridService;
    private HorizontalLayout gridLayout = new HorizontalLayout();

    @Override
    public void gridLayoutAdd(Component... components) {
        gridLayout.add(components);
    }

    @Override
    public void gridLayoutBringBackMainGrid() {
        gridLayout.add(gridService.grid);
    }

    @Override
    public void gridLayoutRemoveAll() {
        gridLayout.removeAll();
    }

    @Override
    public void gridLayoutSetSizeFull() {
        gridLayout.setSizeFull();
    }

    @Override
    public HorizontalLayout getGridLayout() {
        return gridLayout;
    }

    @Override
    public void initNewLayout() {
        gridLayout = new HorizontalLayout();
    }
}
