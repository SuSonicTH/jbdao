package net.weichware.jbdaotest;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.weichware.jbdao.Customer;

import javax.sql.DataSource;
import java.sql.SQLException;

@Route(CustomerPage.ROUTE)
@PageTitle("Customer")
public class CustomerPage extends VerticalLayout {
    public static final String ROUTE = "Customer";
    private static DataSource dataSource;

    public CustomerPage() throws SQLException {
        Grid<Customer> grid = new Grid<>(Customer.class, false);
        grid.addComponentColumn(this::createGridButtons)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("");

        grid.addColumn(Customer::getFirstName).setHeader("First name").setSortable(true);
        grid.addColumn(Customer::getLastName).setHeader("Last name").setSortable(true);
        grid.addColumn(Customer::getBirthDate).setHeader("Birth Date").setSortable(true);
        grid.addColumn(Customer::getAddress).setHeader("Address").setSortable(true);
        grid.addColumn(Customer::getCountry).setHeader("Country").setSortable(true);
        grid.addColumn(Customer::getPostalCode).setHeader("Postal Code").setSortable(true);
        grid.addColumn(Customer::getPhoneNumber).setHeader("Phone Number").setSortable(true);
        grid.addColumn(Customer::getKids).setHeader("Number Of Kids").setSortable(true);

        grid.setItems(Customer.getList(dataSource));
        add(grid);
    }

    public static void setDataSource(DataSource dataSource) {
        CustomerPage.dataSource = dataSource;
    }

    protected Component createGridButtons(Customer item) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.add(createRowButton("Edit", new Icon(VaadinIcon.EDIT), e -> Notification.show("EDIT:" + item.toString())));
        layout.add(createRowButton("Delete", new Icon(VaadinIcon.MINUS_CIRCLE_O), e -> Notification.show("DELETE:" + item.toString())));
        layout.add(createRowButton("Copy", new Icon(VaadinIcon.COPY), e -> Notification.show("COPY:" + item.toString())));
        return layout;
    }

    private Button createRowButton(String title, Icon icon, ComponentEventListener<ClickEvent<Button>> onCLick) {
        Button button = new Button(icon);
        button.getElement().setProperty("title", title);
        button.addClickListener(onCLick);
        return button;
    }
}
