package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.controllers.AuthStoreController;
import fr.s4e2.ouatelse.controllers.BaseController;
import fr.s4e2.ouatelse.controllers.HomeController;
import fr.s4e2.ouatelse.controllers.ProductsCatalogController;
import fr.s4e2.ouatelse.objects.Cart;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base Screen for all screens
 */
@Getter
@Setter
public abstract class BaseScreen {

    public static final Image OUATELSE_ICON = new Image(Objects.requireNonNull(Main.class.getClassLoader().getResource("images/ouatelse_icon.png")).toExternalForm());
    private static final String FXML_PATH = "fxml/";
    private static final String PREFIX = "Ouatelse - ";

    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    private Stage stage;

    /**
     * Creates a Base Screen
     *
     * @param fxml  name of the fxml file to load from (must be in the fxml folder in resources)
     * @param title the title of the window (the title suffixes the set prefix)
     */
    protected BaseScreen(String fxml, String title, Store authentificationStore) {
        this.stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource(FXML_PATH + fxml));
        stage.getIcons().add(OUATELSE_ICON);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(650);
        stage.setMinWidth(825);
        stage.setTitle(PREFIX + title);

        try {
            Parent parent = loader.load();

            BaseController controller = loader.getController();
            controller.setAuthentificationStore(authentificationStore);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add("css/base.css");
            stage.setScene(scene);
        } catch (IOException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Creates a Base Screen (Used for the {@link fr.s4e2.ouatelse.controllers.ProductsCatalogController})
     *
     * @param fxml  name of the fxml file to load from (must be in the fxml folder in resources)
     * @param title the title of the window (the title suffixes the set prefix)
     */
    protected BaseScreen(String fxml, String title, Store authentificationStore, Cart currentCart) {
        this.stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource(FXML_PATH + fxml));
        stage.getIcons().add(OUATELSE_ICON);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(650);
        stage.setMinWidth(825);
        stage.setTitle(PREFIX + title);

        try {
            Parent parent = loader.load();

            ProductsCatalogController controller = loader.getController();
            controller.setAuthentificationStore(authentificationStore);
            controller.setCurrentCart(currentCart);

            Scene scene = new Scene(parent);
            scene.getStylesheets().add("css/base.css");
            stage.setScene(scene);
        } catch (IOException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Creates a Base Screen (Used for the {@link fr.s4e2.ouatelse.controllers.AuthUserController})
     *
     * @param fxml  name of the fxml file to load from (must be in the fxml folder in resources)
     * @param title the title of the window (the title suffixes the set prefix)
     */
    protected BaseScreen(String fxml, String title) {
        this.stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource(FXML_PATH + fxml));
        stage.getIcons().add(OUATELSE_ICON);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(650);
        stage.setMinWidth(825);
        stage.setTitle(PREFIX + title);

        try {
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add("css/base.css");
            stage.setScene(scene);
        } catch (IOException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Creates a Base Screen (Used for the {@link HomeController})
     *
     * @param fxml  name of the fxml file to load from (must be in the fxml folder in resources)
     * @param title the title of the window (the title suffixes the set prefix)
     * @param user  the authentified user
     * @param store the authentified store
     */
    protected BaseScreen(String fxml, String title, Store store, User user) {
        this.stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource(FXML_PATH + fxml));
        stage.getIcons().add(OUATELSE_ICON);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(650);
        stage.setMinWidth(825);
        stage.setTitle(PREFIX + title);

        try {
            Parent parent = loader.load();

            HomeController homeController = loader.getController();
            homeController.setAuthentificationStore(store);
            homeController.setAuthentificationUser(user);

            Scene scene = new Scene(parent);
            stage.setScene(scene);
        } catch (IOException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    /**
     * Creates a Base Screen (Used for the {@link AuthStoreController})
     *
     * @param fxml  name of the fxml file to load from (must be in the fxml folder in resources)
     * @param title the title of the window (the title suffixes the set prefix)
     * @param user  the authentified user
     */
    protected BaseScreen(String fxml, String title, User user) {
        this.stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource(FXML_PATH + fxml));
        stage.getIcons().add(OUATELSE_ICON);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(650);
        stage.setMinWidth(825);
        stage.setTitle(PREFIX + title);

        try {
            Parent parent = loader.load();

            AuthStoreController authStoreController = loader.getController();
            authStoreController.setCurrentUser(user);

            Scene scene = new Scene(parent);
            stage.setScene(scene);
        } catch (IOException exception) {
            this.logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }


    /**
     * Allows to open the Stage
     */
    public void open() {
        stage.show();
    }

    /**
     * Allows to close the Stage
     */
    public void close() {
        stage.close();
    }
}
