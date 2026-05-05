package com.blockmart.donutorders.managers;

import com.blockmart.donutorders.DonutOrders;
import com.blockmart.donutorders.models.DonutOrder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class DatabaseManager {

    private final DonutOrders plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(DonutOrders plugin) {
        this.plugin = plugin;
    }

    public void loadDatabase() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + new File(plugin.getDataFolder(), "donutorders.db").getAbsolutePath());
        config.setMaximumPoolSize(10);
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("DonutOrders-Pool");

        this.dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "CREATE TABLE IF NOT EXISTS donut_orders (" +
                                 "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                 "player_uuid VARCHAR(36) NOT NULL," +
                                 "donut_flavor VARCHAR(64) NOT NULL," +
                                 "donut_topping VARCHAR(64) NOT NULL," +
                                 "cost DOUBLE NOT NULL," +
                                 "order_time LONG NOT NULL);" )) {
                statement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create tables", e);
            }
        });
    }

    public void insertDonutOrder(DonutOrder order) throws SQLException {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO donut_orders (player_uuid, donut_flavor, donut_topping, cost, order_time) VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, order.getPlayerUuid());
                statement.setString(2, order.getDonutFlavor());
                statement.setString(3, order.getDonutTopping());
                statement.setDouble(4, order.getCost());
                statement.setLong(5, order.getOrderTime());
                statement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to insert donut order", e);
            }
        });
    }

    public void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    // You could add methods here to retrieve orders, delete orders, etc.
    // Example:
    /*
    public CompletableFuture<List<DonutOrder>> getOrdersByPlayer(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<DonutOrder> orders = new ArrayList<>();
            String sql = "SELECT * FROM donut_orders WHERE player_uuid = ? ORDER BY order_time DESC";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUuid.toString());
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    orders.add(new DonutOrder(
                            rs.getString("player_uuid"),
                            rs.getString("donut_flavor"),
                            rs.getString("donut_topping"),
                            rs.getDouble("cost"),
                            rs.getLong("order_time")
                    ));
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to retrieve donut orders for player " + playerUuid, e);
            }
            return orders;
        });
    }
    */
}