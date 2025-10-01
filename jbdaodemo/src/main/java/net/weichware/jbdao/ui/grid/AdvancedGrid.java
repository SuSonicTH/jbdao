package net.weichware.jbdao.ui.grid;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.ValueProvider;
import net.weichware.jbdao.ui.dialog.ConfirmationDialog;

import java.util.ArrayList;
import java.util.List;

public class AdvancedGrid<T> extends Grid<T> {
    private final String name;
    private final transient Runnable addAction;
    private final transient Runnable updateAction;
    private final transient List<GridButton<T>> gridButtonsList;
    protected ListDataProvider<T> dataProvider;
    protected HeaderRow filterRow;
    protected transient List<FilterTextField> filterTextFields = new ArrayList<>();
    protected Column<?> buttonColumn;
    private SerializablePredicate<T> omniFilter;

    public AdvancedGrid(String name, List<GridButton<T>> gridButtonList, Runnable addAction, Runnable updateAction) {
        this.name = name;
        this.addAction = addAction;
        this.updateAction = updateAction;
        setSizeFull();
        setColumnReorderingAllowed(true);
        this.gridButtonsList = gridButtonList;
        createButtonColumn();
    }

    private void createButtonColumn() {
        buttonColumn = addComponentColumn(this::createGridButtons)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader(name);
    }

    public Column<T> addColumn(String columnHeader, ValueProvider<T, ? extends Comparable<?>> valueProvider) {
        Column<T> column = addColumn(valueProvider)
                .setHeader(columnHeader)
                .setAutoWidth(true)
                .setSortable(true);

        if (filterRow == null) {
            filterRow = appendHeaderRow();
            filterRow.getCell(buttonColumn).setComponent(createHeaderActions());
        }
        FilterTextField<T, ?> filterTextField = new FilterTextField<>(this, valueProvider, columnHeader);
        filterRow.getCell(column).setComponent(filterTextField);
        filterTextFields.add(filterTextField);
        return column;
    }

    private Component createHeaderActions() {
        HorizontalLayout layout = new HorizontalLayout();
        if (addAction != null) {
            layout.add(new GridButton<T>(GridButton.DisplayMode.ICON, "Add", VaadinIcon.PLUS_CIRCLE_O, (event, item) -> addAction.run(), e -> true, e -> true).create(null));
        }
        if (updateAction != null) {
            layout.add(new GridButton<T>(GridButton.DisplayMode.ICON, "Refresh", VaadinIcon.REFRESH, (event, item) -> updateAction.run(), e -> true, e -> true).create(null));
        }
        layout.add(new GridButton<T>(GridButton.DisplayMode.ICON, "Clear Filter", VaadinIcon.CLOSE_CIRCLE_O, this::clearFilter, e -> true, e -> true).create(null));
        return layout;
    }

    private void clearFilter(ClickEvent<?> clickEvent, T t) {
        filterTextFields.forEach(HasValue::clear);
        updateFilters();
    }

    public void clearColumnFilter() {
        filterTextFields.forEach(HasValue::clear);
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        super.setDataProvider(dataProvider);
        this.dataProvider = (ListDataProvider<T>) dataProvider;
        filterTextFields.forEach(FilterTextField::updateList);
        recalculateColumnWidths();
    }

    protected Component createGridButtons(T item) {
        HorizontalLayout layout = new HorizontalLayout();
        gridButtonsList.forEach(button -> layout.add(button.create(item)));
        return layout;
    }

    public void setOmniFilter(SerializablePredicate<T> omniFilter) {
        this.omniFilter = omniFilter;
    }

    public void updateFilters() {
        if (dataProvider != null) {
            dataProvider.clearFilters();
            if (omniFilter!=null) {
                dataProvider.addFilter(omniFilter);
            }
            filterTextFields.forEach(filterTextField -> {
                SerializablePredicate<T> filter = filterTextField.getFieldFilter();
                if (filter != null) {
                    dataProvider.addFilter(filterTextField.getValueProvider(), filter);
                }
            });
        }
    }

    @Override
    public void removeAllColumns() {
        super.removeAllColumns();
        createButtonColumn();
        if (filterRow != null) {
            filterRow.getCell(buttonColumn).setComponent(createHeaderActions());
        }
    }
}
