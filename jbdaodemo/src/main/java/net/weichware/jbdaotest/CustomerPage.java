package net.weichware.jbdaotest;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.weichware.jbdao.Customer;
import net.weichware.jbdao.ui.grid.AdvancedGrid;
import net.weichware.jbdao.ui.grid.GridButton;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Route(CustomerPage.ROUTE)
@PageTitle("Customer")
public class CustomerPage extends VerticalLayout {
    public static final String ROUTE = "Customer";
    private static DataSource dataSource;
    private final AdvancedGrid<Customer> grid;

    public CustomerPage() {
        List<GridButton<Customer>> gridButtonList = List.of(
                new GridButton<>(VaadinIcon.EDIT, "Edit", (e, item) -> Notification.show("EDIT:" + item.toString())),
                new GridButton<>(VaadinIcon.COPY, "Copy", (e, item) -> Notification.show("COPY:" + item.toString())),
                new GridButton<>(VaadinIcon.MINUS_CIRCLE_O, "Delete", (e, item) -> Notification.show("DELETE:" + item.toString()))
        );
        grid = new AdvancedGrid<>(ROUTE, gridButtonList, this::addItem, this::update);
        grid.addColumn("Index", Customer::getIndex).setSortable(true).setAutoWidth(true).setKey("index");
        grid.addColumn("Customer Id", Customer::getCustomerId).setSortable(true).setAutoWidth(true).setKey("customerId");
        grid.addColumn("First Name", Customer::getFirstName).setSortable(true).setAutoWidth(true);
        grid.addColumn("Last Name", Customer::getLastName).setSortable(true).setAutoWidth(true);
        grid.addColumn("Company", Customer::getCompany).setSortable(true).setAutoWidth(true);
        grid.addColumn("City", Customer::getCity).setSortable(true).setAutoWidth(true);
        grid.addColumn("Country", Customer::getCountry).setSortable(true).setAutoWidth(true);
        grid.addColumn("Phone 1", Customer::getPhone1).setSortable(true).setAutoWidth(true);
        grid.addColumn("Phone 2", Customer::getPhone2).setSortable(true).setAutoWidth(true);
        grid.addColumn("Email", Customer::getEmail).setSortable(true).setAutoWidth(true);
        grid.addColumn("Subscription Date", Customer::getSubscriptionDate).setSortable(true).setAutoWidth(true);
        grid.addColumn("Website", Customer::getWebsite).setSortable(true).setAutoWidth(true);

        add(createOmniSearch(), grid);
        update(grid);
        setSizeFull();
    }

    private Component createOmniSearch() {
        TextField omniSearch = new TextField("Omni Search:");
        omniSearch.setId("omniSearchText");
        omniSearch.setWidth(220, Unit.PIXELS);
        omniSearch.addValueChangeListener(this::updateOmniFilter);
        omniSearch.setValueChangeMode(ValueChangeMode.LAZY);
        omniSearch.setValueChangeTimeout(200);
        Button clear = new Button(new Icon(VaadinIcon.CLOSE_CIRCLE_O));
        clear.getElement().setProperty("title", "Clear search");
        clear.addClickListener((_)-> omniSearch.setValue(""));

        HorizontalLayout layout = new HorizontalLayout(omniSearch, clear);
        layout.setAlignItems(Alignment.BASELINE);
        return layout;
    }

    private void updateOmniFilter(AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        final String search = event.getValue().toLowerCase();
        if (search.length()>1) {
            grid.setOmniFilter((item) -> (item.getIndex() + "").toLowerCase().contains(search) ||
                    item.getCustomerId().toLowerCase().contains(search) ||
                    item.getFirstName().toLowerCase().contains(search) ||
                    item.getLastName().toLowerCase().contains(search) ||
                    item.getCompany().toLowerCase().contains(search) ||
                    item.getCity().toLowerCase().contains(search) ||
                    item.getCountry().toLowerCase().contains(search) ||
                    item.getPhone1().toLowerCase().contains(search) ||
                    item.getPhone2().toLowerCase().contains(search) ||
                    item.getEmail().toLowerCase().contains(search) ||
                    item.getSubscriptionDate().toLowerCase().contains(search) ||
                    item.getWebsite().toLowerCase().contains(search));
        } else {
            grid.setOmniFilter(null);
        }
        grid.updateFilters();
    }

    private void update(AdvancedGrid<?> advancedGrid) {
        try {
            grid.setDataProvider(DataProvider.ofCollection(Customer.getList(dataSource)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        grid.recalculateColumnWidths();
    }

    private void addItem(AdvancedGrid<?> advancedGrid) {
        Notification.show("ADD ITEM");
    }

    public static void setDataSource(DataSource dataSource) {
        CustomerPage.dataSource = dataSource;
    }

}
