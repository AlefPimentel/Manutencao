package app.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class JanelaService {

    private static JanelaService instance;

    private JanelaService() {}

    public static synchronized JanelaService getInstance() {
        if (instance == null) {
            instance = new JanelaService();
        }
        return instance;
    }

    public void abrirJanela(String fxmlPath, String titulo, Consumer<Object> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controllerConsumer != null) controllerConsumer.accept(controller);

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao abrir janela: " + fxmlPath);
            e.printStackTrace();
        }
    }
}