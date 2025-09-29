package net.weichware.jbdaotest;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
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
        grid.addColumn("Index", Customer::getIndex).setSortable(true).setFrozen(true);
        grid.addColumn("Customer Id", Customer::getCustomerId).setSortable(true);
        grid.addColumn("First Name", Customer::getFirstName).setSortable(true);
        grid.addColumn("Last Name", Customer::getLastName).setSortable(true);
        grid.addColumn("Company", Customer::getCompany).setSortable(true);
        grid.addColumn("City", Customer::getCity).setSortable(true);
        grid.addColumn("Country", Customer::getCountry).setSortable(true);
        grid.addColumn("Phone 1", Customer::getPhone1).setSortable(true);
        grid.addColumn("Phone 2", Customer::getPhone2).setSortable(true);
        grid.addColumn("Email", Customer::getEmail).setSortable(true);
        grid.addColumn("Subscription Date", Customer::getSubscriptionDate).setSortable(true);
        grid.addColumn("Website", Customer::getWebsite).setSortable(true);

        add(grid);
        update(grid);
        setSizeFull();
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
