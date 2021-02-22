package fr.s4e2.ouatelse.controllers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.objects.Address;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import fr.s4e2.ouatelse.utils.Utils;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManagementStoreController extends BaseController {
    private static final String TEXT_FIELD_EMPTY_HINT = "Champ(s) Vide!";
    private static final String STORE_ALREADY_EXISTS = "Ce magasin existe déjà!";
    private static final String NOT_A_ZIPCODE = "Le code postal est incorrect!";
    private static final String MANAGER_NOT_FOUND = "Responsable du magasin pas trouvé!";
    private static final String PASSWORD_NOT_MATCHING = "Mot de passe non concordants!";

    public ListView<Store> storesListView;
    public TextField newStoreNameField;
    public TextField newStoreManagerField;
    public TextField newStoreAddressField;
    public TextField newStoreCityField;
    public TextField newStoreZipcodeField;
    public PasswordField newStorePasswordField;
    public PasswordField newStoreConfirmPasswordField;
    public Label errorMessage;

    private Dao<Store, String> storeDao;
    private Dao<Address, String> addressDao;
    private Dao<User, String> userDao;
    private Store currentStore;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.storeDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Store.class);
            this.addressDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), Address.class);
            this.userDao = DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), User.class);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        // selected element in the list box
        this.storesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            newStoreNameField.setDisable(false);

            if (newValue != null) {
                currentStore = newValue;
                loadStoreInformation(newValue);
            } else {
                currentStore = null;
            }
        });

        // escape to unselect item in the list box
        this.storesListView.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ESCAPE) return;

            Store store = storesListView.getSelectionModel().getSelectedItem();
            if (store == null) return;
            storesListView.getSelectionModel().clearSelection();
            this.clearStoreInformation();
        });

        this.loadStoresList();
    }

    /**
     * Creates and stores a new role with empty permissions
     */
    public void onAddButtonClick() throws SQLException {
        // fields that must be filled in
        if (newStoreNameField.getText().trim().isEmpty() || newStoreAddressField.getText().trim().isEmpty() || newStoreCityField.getText().trim().isEmpty()
                || newStoreZipcodeField.getText().trim().isEmpty()) {
            this.errorMessage.setText(TEXT_FIELD_EMPTY_HINT);
            this.newStoreNameField.getParent().requestFocus();
            return;
        }

        if (!this.isEditing() && (newStorePasswordField.getText().isEmpty() || newStoreConfirmPasswordField.getText().isEmpty())) {
            this.errorMessage.setText(TEXT_FIELD_EMPTY_HINT);
            this.newStorePasswordField.getParent().requestFocus();
            return;
        }

        // incorrect zipcode
        Integer zipCode = Utils.getNumber(newStoreZipcodeField.getText().trim());
        if (zipCode == null || zipCode > 99999) {
            this.errorMessage.setText(NOT_A_ZIPCODE);
            this.newStoreNameField.getParent().requestFocus();
            return;
        }

        // passwords don't match
        if (!newStorePasswordField.getText().equals(newStoreConfirmPasswordField.getText())) {
            this.errorMessage.setText(PASSWORD_NOT_MATCHING);
            this.newStoreNameField.getParent().requestFocus();
            return;
        }

        // store exists already!
        if (!this.isEditing()) {
            for (Store store : storeDao) {
                if (store.getId().equals(newStoreNameField.getText().trim())) {
                    this.newStoreNameField.clear();
                    this.errorMessage.setText(STORE_ALREADY_EXISTS);
                    this.newStoreNameField.getParent().requestFocus();
                    return;
                }
            }
        }

        // store manager (can be their credentials or email)
        String managerInput = this.newStoreManagerField.getText().trim();
        User manager = null;
        if (!managerInput.isEmpty()) {
            manager = userDao.query(userDao.queryBuilder()
                    .where().eq("credentials", managerInput)
                    .or().eq("email", managerInput)
                    .prepare()
            ).stream().findFirst().orElse(null);
            if (manager == null) {
                this.errorMessage.setText(MANAGER_NOT_FOUND);
                this.newStoreNameField.getParent().requestFocus();
                return;
            }
        }

        if (this.isEditing()) {
            // edits store
            try {
                this.currentStore.getAddress().setZipCode(zipCode);
                this.currentStore.getAddress().setCity(newStoreCityField.getText().trim());
                this.currentStore.getAddress().setAddress(newStoreAddressField.getText().trim());
                this.addressDao.update(currentStore.getAddress());

                if (!newStoreConfirmPasswordField.getText().isEmpty()) {
                    this.currentStore.setPassword(newStoreConfirmPasswordField.getText());
                }
                this.currentStore.setManager(manager);
                this.storeDao.update(currentStore);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            this.storesListView.getSelectionModel().select(currentStore);
        } else {
            // creates address
            Address newAddress = null;
            try {
                newAddress = new Address(zipCode, newStoreCityField.getText().trim(), newStoreAddressField.getText().trim());
                this.addressDao.create(newAddress);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            // creates store
            Store newStore = null;
            try {
                newStore = new Store(newStoreNameField.getText().trim());
                newStore.setPassword(newStoreConfirmPasswordField.getText());
                newStore.setManager(manager);
                newStore.setAddress(newAddress);
                this.storeDao.create(newStore);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            this.storesListView.getSelectionModel().select(newStore);
        }

        this.loadStoresList();
        this.clearStoreInformation();
    }

    /**
     * Deletes a store
     *
     */
    public void onDeleteButtonClick() {
        try {
            Store selectedStore = storesListView.getSelectionModel().getSelectedItem();
            this.storeDao.delete(selectedStore);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        this.loadStoresList();
        this.clearStoreInformation();
    }

    /**
     * Load the selected store's informations into the editable fields
     *
     * @param store The store to view / edit the informations from
     */
    private void loadStoreInformation(Store store) {
        this.clearStoreInformation();

        if (store == null) return;
        this.newStoreNameField.setText(store.getId());
        this.newStoreNameField.setDisable(true);

        if (store.getManager() != null) {
            this.newStoreManagerField.setText(store.getManager().getEmail());
        }

        if (store.getAddress() == null) return;
        this.newStoreAddressField.setText(store.getAddress().getAddress());
        this.newStoreCityField.setText(store.getAddress().getCity());
        this.newStoreZipcodeField.setText(String.valueOf(store.getAddress().getZipCode()));
    }

    /**
     * Clears the editable fields from a store's informations
     */
    private void clearStoreInformation() {
        this.newStoreNameField.setText("");
        this.newStoreManagerField.setText("");
        this.newStoreAddressField.setText("");
        this.newStoreCityField.setText("");
        this.newStoreZipcodeField.setText("");
        this.newStorePasswordField.setText("");
        this.newStoreConfirmPasswordField.setText("");
        this.errorMessage.setText("");
    }

    /**
     * Loads the stores into the ListView storesListView
     */
    private void loadStoresList() {
        this.storesListView.getItems().clear();
        this.storeDao.forEach(store -> this.storesListView.getItems().add(store));
    }

    private boolean isEditing() {
        return this.currentStore != null;
    }
}