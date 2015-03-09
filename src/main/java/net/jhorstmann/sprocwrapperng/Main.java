package net.jhorstmann.sprocwrapperng;

import com.impossibl.postgres.system.BasicContext;
import com.impossibl.postgres.types.Registry;

import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws SQLException, NoSuchFieldException, IllegalAccessException {
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");

        try (Connection connection = DriverManager.getConnection("jdbc:pgsql://localhost:5432/sprocwrapper_ng", props)) {
            try (Statement statement1 = connection.createStatement()) {
                try (ResultSet resultSet = statement1.executeQuery("SHOW search_path")) {
                    while (resultSet.next()) {
                        String searchPath = resultSet.getString(1);
                        System.out.println(searchPath);
                    }
                }
                statement1.execute("SET SEARCH_PATH TO api, PUBLIC");
                try (ResultSet resultSet = statement1.executeQuery("SHOW search_path")) {
                    while (resultSet.next()) {
                        String searchPath = resultSet.getString(1);
                        System.out.println(searchPath);
                    }
                }
            }

            System.out.println();

            BasicContext context = connection.unwrap(BasicContext.class);
            Registry registry = context.getRegistry();

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM order_load(?::TEXT) o")) {
                statement.setString(1, "1234");
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        final Order order = TypeMapper.map(rs, Order.class);
                        System.out.println(order.getOrderNumber());
                    }
                }
            }
        }
    }
}
