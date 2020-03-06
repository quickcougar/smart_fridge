package com.github.quickcougar.smart_fridge.services;

import com.github.quickcougar.smart_fridge.DataProcessException;
import com.github.quickcougar.smart_fridge.domain.entities.Fridge;
import com.github.quickcougar.smart_fridge.domain.entities.Item;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.exec.Operation;
import ratpack.exec.Promise;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FridgeService {

    Logger log = LoggerFactory.getLogger(FridgeService.class);

    private final DataSource dataSource;

    @Inject
    FridgeService(DataSource ds) {
        this.dataSource = ds;
    }

    public Promise<Fridge> addFridge(Fridge fridge) {
        return Blocking.get(() -> {
            final String insertFridge = "insert into fridge (name) values(?)";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement fridgeInsert = connection.prepareStatement(insertFridge,
                        Statement.RETURN_GENERATED_KEYS);
                fridgeInsert.setString(1, fridge.getName());
                fridgeInsert.executeUpdate();
                ResultSet resultSet = fridgeInsert.getGeneratedKeys();
                if (resultSet.next()) {
                    fridge.setId(resultSet.getInt(1));
                    addFridgeItems(fridge);
                    return fridge;
                } else {
                    throw new DataProcessException("Failed to add fridge");
                }
            }
        });
    }

    public void addFridgeItems(Fridge fridge) {
        if (fridge.getItems() != null && fridge.getItems().size() > 0) {
            final String insertItem = "insert into item (fridge_id,name,type) values(?,?,?)";
            try (Connection connection = dataSource.getConnection()) {
                for (Item item : fridge.getItems()) {
                    PreparedStatement itemInsert = connection.prepareStatement(insertItem,
                            Statement.RETURN_GENERATED_KEYS);
                    itemInsert.setInt(1, fridge.getId());
                    itemInsert.setString(2, item.getName());
                    itemInsert.setString(3, item.getType());
                    int itemCount = itemInsert.executeUpdate();
                    if (itemCount < 1) {
                        throw new DataProcessException(String.format("Failed to add item in fridge: %s",
                                fridge.getId().toString()));
                    }
                    ResultSet resultSet = itemInsert.getGeneratedKeys();
                    if (resultSet.next()) {
                        item.setId(resultSet.getInt(1));
                    } else {
                        throw new DataProcessException("Failed to add fridge item");
                    }
                }
            } catch (SQLException e) {
                throw new DataProcessException(String.format("Failed to add fridge items: %s", e.getMessage()));
            }
        } else {
            fridge.setItems(new ArrayList<>());
        }
    }

    public Promise<Fridge> getFridge(Integer id) {
        final String selectFridge = "select * from fridge where id = ?";
        return Blocking.get(() -> {
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement fridgeSelect = connection.prepareStatement(selectFridge);
                fridgeSelect.setInt(1, id);
                ResultSet resultSet = fridgeSelect.executeQuery();
                if (resultSet.next()) {
                    List<Item> items = getFridgeItems(resultSet.getInt(1));
                    Fridge fridge = new Fridge(resultSet.getInt(1), resultSet.getString(2), items);
                    return fridge;
                } else {
                    return null;
                }
            }
        });
    }

    public List<Item> getFridgeItems(Integer id) {
        final String selectItem = "select id, name, type from item where fridge_id = ?";
        List<Item> items = new ArrayList<Item>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement itemSelect = connection.prepareStatement(selectItem);
            itemSelect.setInt(1, id);
            ResultSet resultSet = itemSelect.executeQuery();
            while (resultSet.next()) {
                items.add(new Item(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
            }
            itemSelect.close();
            if (!itemSelect.isClosed()) {
                log.error("FAILED to close statement connection");
            }
        } catch (SQLException e) {
            throw new DataProcessException(String.format("Failed to get fridge items: %s", e.getMessage()));
        }
        return items;
    }

    public Promise<List<Fridge>> listFridges() {
        return Blocking.get(() -> {
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement("select * from fridge");
                ResultSet resultSet = statement.executeQuery();
                List<Fridge> fridges = new ArrayList<>();
                while (resultSet.next()) {
                    List<Item> items = getFridgeItems(resultSet.getInt(1));
                    fridges.add(new Fridge(resultSet.getInt(1), resultSet.getString(2), items));
                }
                statement.close();
                if (!statement.isClosed()) {
                    log.error("FAILED to close statement connection");
                }
                return fridges;
            }
        });
    }

    public Promise<Fridge> updateFridge(Fridge fridge, boolean isMerge) {
        return Blocking.get(() -> {
            final String updateFridge = "merge into fridge key (id) values (?, ?)";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(updateFridge);
                statement.setInt(1, fridge.getId());
                statement.setString(2, fridge.getName());
                int count = statement.executeUpdate();
                statement.close();
                if (!statement.isClosed()) {
                    log.error("FAILED to close statement connection");
                }
                if (count == 1) {
                    updateFridgeItems(fridge, isMerge);
                } else {
                    throw new DataProcessException(String.format("Failed to update fridge: %s",
                            fridge.getId().toString()));
                }
            }
            return fridge;
        });
    }

    public void updateFridgeItems(Fridge fridge, boolean isMerge) {
        try {
            if (!isMerge) {
                deleteFridgeItems(fridge.getId());
            }
            for (Item item : fridge.getItems()) {
                if (item.getId() != null) {
                    updateItem(item, fridge.getId());
                } else {
                    insertItem(item, fridge.getId());
                }
            }
            fridge.setItems(getFridgeItems(fridge.getId()));
        } catch (SQLException e) {
            throw new DataProcessException(String.format("Failed to update fridge items: %s", e.getMessage()));
        }
    }

    private void insertItem(Item item, Integer fridgeId) throws SQLException {
        final String insertItem = "insert into item (fridge_id,name,type) values(?,?,?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement itemInsert = connection.prepareStatement(insertItem, Statement.RETURN_GENERATED_KEYS);
        itemInsert.setInt(1, fridgeId);
        itemInsert.setString(2, item.getName());
        itemInsert.setString(3, item.getType());
        int itemCount = itemInsert.executeUpdate();
        if (itemCount < 1) {
            throw new DataProcessException(String.format("Failed to add item in fridge: %s", fridgeId.toString()));
        }
        ResultSet resultSet = itemInsert.getGeneratedKeys();
        if (resultSet.next()) {
            item.setId(resultSet.getInt(1));
        } else {
            throw new DataProcessException("Failed to add fridge item");
        }
        itemInsert.close();
        if (!itemInsert.isClosed()) {
            log.error("FAILED to close statement connection");
        }
    }

    private void updateItem(Item item, Integer fridgeId) throws SQLException {
        final String updateItems = "merge into item key (id) values (?, ?, ?, ?)";
        Connection connection = dataSource.getConnection();
        PreparedStatement itemUpdate = connection.prepareStatement(updateItems);
        itemUpdate.setInt(1, item.getId());
        itemUpdate.setInt(2, fridgeId);
        itemUpdate.setString(3, item.getName());
        itemUpdate.setString(4, item.getType());
        int itemCount = itemUpdate.executeUpdate();
        itemUpdate.close();
        if (!itemUpdate.isClosed()) {
            log.error("FAILED to close statement connection");
        }
        if (itemCount < 1) {
            throw new DataProcessException(String.format("Failed to update item id %s in fridge %s",
                    item.getId().toString(), fridgeId.toString()));
        }
    }

    public Operation deleteFridge(Integer id) {
        return Blocking.op(() -> {
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement("delete from fridge where id = ?");
                statement.setInt(1, id);
                statement.execute();
                statement.close();
                if (!statement.isClosed()) {
                    log.error("FAILED to close statement connection");
                }
            } catch (SQLException e) {
                throw new DataProcessException(String.format("Failed to delete fridge %s: %s", id.toString(),
                        e.getMessage()));
            }
        });
    }

    public void deleteFridgeItems(Integer fridgeId) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("delete from item where fridge_id = ?");
            statement.setInt(1, fridgeId);
            statement.executeUpdate();
            statement.close();
            if (!statement.isClosed()) {
                log.error("FAILED to close statement connection");
            }
        } catch (SQLException e) {
            throw new DataProcessException(String.format("Failed to delete fridge %s items: %s", fridgeId.toString(),
                    e.getMessage()));
        }
    }

}
