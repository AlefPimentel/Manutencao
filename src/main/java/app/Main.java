package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import app.database.DatabaseSetup;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Criar tabelas do banco
            DatabaseSetup.criarTabelas();

            // Carregar FXML correto
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/ui/TelaInicial.fxml")
            );

            Parent root = fxmlLoader.load(); // <<<<<< ESSENCIAL

            // Configurar a janela
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            primaryStage.setTitle("Grupo BASE - Sistema");
            primaryStage.setResizable(false);
            primaryStage.show();               // <<<<<< ESSENCIAL

        } catch (Exception e) {
            System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
