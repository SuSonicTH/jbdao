package net.weichware.jbdaotest;

import net.weichware.jbdao.Customer;
import net.weichware.jbdao.ui.dialog.ErrorDialog;
import net.weichware.jbdao.ui.dialog.EditDialog;
import net.weichware.jbdao.ui.dialog.EditDialogMode;

import javax.sql.DataSource;
import java.sql.SQLException;

public class CustomerEditDialog extends EditDialog<Customer> {
    private final DataSource dataSource;

    public CustomerEditDialog(EditDialogMode mode, String name, Customer customer, DataSource dataSource, Runnable update) {
        super(mode, name, customer, update);
        this.dataSource = dataSource;
    }

    @Override
    protected void createFields(Customer customer) {
        addNumberField("index", "Index", (double) customer.getIndex(), mode!= EditDialogMode.EDIT, true);
        addTextField("customerId","Customer Id", customer.getCustomerId(), true, true);
        addTextField("firstName","First Name", customer.getFirstName(), true, true);
        addTextField("lastName","Last Name", customer.getLastName(), true, true);
        addTextField("company","Company", customer.getCompany(), true, true);
        addTextField("city","City", customer.getCity(), true, true);
        addTextField("country","Country", customer.getCountry(), true, true);
        addTextField("phone1","Phone 1", customer.getPhone1(), true, true);
        addTextField("phone2","Phone 2", customer.getPhone2(), true, true);
        addTextField("email","Email", customer.getEmail(), true, true);
        addTextField("subscriptionDate","Subscription Date", customer.getSubscriptionDate(), true, true);
        addTextField("website","Website", customer.getWebsite(), true, true);
    }

    @Override
    protected boolean save() {
        Customer customer;
        try {
            customer = new Customer(
                    getValueLong("index"),
                    getValue("customerId"),
                    getValue("firstName"),
                    getValue("lastName"),
                    getValue("company"),
                    getValue("city"),
                    getValue("country"),
                    getValue("phone1"),
                    getValue("phone2"),
                    getValue("email"),
                    getValue("subscriptionDate"),
                    getValue("website")
            );
        } catch (Exception e) {
            new ErrorDialog("Validation Error", e.getMessage()).open();
            return false;
        }
        try {
            customer.persist(dataSource);
        } catch (SQLException e) {
            new ErrorDialog("Database Error", e.getMessage()).open();
        }
        return true;
    }
}
