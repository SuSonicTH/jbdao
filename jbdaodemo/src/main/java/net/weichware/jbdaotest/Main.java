package net.weichware.jbdaotest;

import com.github.mvysny.vaadinboot.VaadinBoot;
import org.jetbrains.annotations.NotNull;

public final class Main {
    public static void main(@NotNull String[] args) throws Exception {
        try(TestDatabase testDatabase = new TestDatabase()) {
            CustomerPage.setDataSource(testDatabase.getDataSource());
            testDatabase.execute("create table CUSTOMER (" +
                    "id number not null," +
                    "FIRST_NAME varchar2 not null," +
                    "LAST_NAME varchar2 not null," +
                    "BIRTH_DATE date," +
                    "ADDRESS varchar2," +
                    "COUNTRY varchar2," +
                    "POSTAL_CODE varchar2," +
                    "PHONE_NUMBER varchar2," +
                    "KIDS number" +
                    ")");
            testDatabase.execute("Insert into CUSTOMER (ID,FIRST_NAME,LAST_NAME, BIRTH_DATE, KIDS) values (1, 'Michael', 'Wolf', date '1980-03-20', 3)");
            testDatabase.execute("Insert into CUSTOMER (ID,FIRST_NAME,LAST_NAME, BIRTH_DATE, KIDS) values (2, 'Barbara', 'Wolf', date '1986-03-23', 3)");
            new VaadinBoot().run();
        }
    }
}