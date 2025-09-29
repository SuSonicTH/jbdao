package net.weichware.jbdaotest;

import com.github.mvysny.vaadinboot.VaadinBoot;
import net.weichware.jbdao.Customer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.sql.SQLException;


public final class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(@NotNull String[] args) throws Exception {
        try (TestDatabase testDatabase = new TestDatabase()) {
            CustomerPage.setDataSource(testDatabase.getDataSource());
            testDatabase.execute("create table CUSTOMER (" +
                    "index number not null," +
                    "customer_id varchar2," +
                    "first_name varchar2," +
                    "last_name varchar2," +
                    "company varchar2," +
                    "city varchar2," +
                    "country varchar2," +
                    "phone1 varchar2," +
                    "phone2 varchar2," +
                    "email varchar2," +
                    "subscription_date varchar2," +
                    "website varchar2" +
                    ")");

            log.info("Start loading data");
            Customer.streamCsv(Paths.get("customers-100000.csv")).forEach(customer -> {
                try {
                    customer.insert(testDatabase.getConnection());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("finished loading data");
            new VaadinBoot().run();
        }
    }
}