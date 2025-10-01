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
import net.weichware.jbdao.ui.dialog.ConfirmationDialog;
import net.weichware.jbdao.ui.dialog.ErrorDialog;
import net.weichware.jbdao.ui.grid.AdvancedGrid;
import net.weichware.jbdao.ui.grid.GridButton;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static net.weichware.jbdao.ui.dialog.EditDialogMode.*;

@Route(CustomerPage.Name)
@PageTitle(CustomerPage.Name)
public class CustomerPage extends VerticalLayout {
    public static final String Name = "Customer";
    private static DataSource dataSource;
    private final AdvancedGrid<Customer> grid;

    public CustomerPage() {
        List<GridButton<Customer>> gridButtonList = List.of(
                new GridButton<>(VaadinIcon.EDIT, "Edit", (e, item) -> new CustomerEditDialog(EDIT, Name, item, dataSource, this::update).open()),
                new GridButton<>(VaadinIcon.COPY, "Copy", (e, item) -> new CustomerEditDialog(COPY, Name, item, dataSource,this::update).open()),
                new GridButton<>(VaadinIcon.MINUS_CIRCLE_O, "Delete", (e, item) -> deleteItem(item))
        );
        grid = new AdvancedGrid<>(Name, gridButtonList, this::addItem, this::update);
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
        update();
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

    private void update() {
        try {
            grid.setDataProvider(DataProvider.ofCollection(Customer.getList(dataSource)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addItem() {
        new CustomerEditDialog(ADD, Name, new Customer(), dataSource, this::update).open();
    }

    private void deleteItem(Customer item) {
        new ConfirmationDialog("Delete Entry", "Do you really want to permanently delete this entry?\n", ok -> {
            try {
                item.delete(dataSource);
                update();
            } catch (SQLException e) {
                new ErrorDialog("Database Error", e.getMessage()).open();
            }
        }).open();
    }

    public static void setDataSource(DataSource dataSource) {
        CustomerPage.dataSource = dataSource;
    }

}
