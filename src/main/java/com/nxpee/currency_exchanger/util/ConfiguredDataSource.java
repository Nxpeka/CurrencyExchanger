package com.nxpee.currency_exchanger.util;

import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfiguredDataSource {
    private static final PGSimpleDataSource INSTANCE = new PGSimpleDataSource();
    static  {
        propertiesLoad();
    }

    private ConfiguredDataSource() {
    }

    private static void propertiesLoad() {
        String driver = "database.driver";
        String url = "database.url";
        String username = "database.username";
        String password = "database.password";
        try(InputStream inputStream = ConfiguredDataSource.class.getResourceAsStream("/database.properties")){
            Properties properties = new Properties();
            properties.load(inputStream);

            INSTANCE.setUrl(properties.getProperty(url));
            INSTANCE.setUser(properties.getProperty(username));
            INSTANCE.setPassword(properties.getProperty(password));

            driverLoad(properties.getProperty(driver));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PGSimpleDataSource getINSTANCE() {
        return INSTANCE;
    }

    private static void driverLoad(String driver) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
