package app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class TelaInicialController {

    @FXML
    private void handleManutencao(ActionEvent event) {
        // CORREÇÃO: Apontando para dentro da pasta 'manutencao'
        String fxmlFile = "/fxml/ui/manutencao/TelaInicialManutencao.fxml";
        abrirNovaJanelaFXML(fxmlFile, "Manutenção");

        // Fecha a janela atual (Menu Principal)
        closeCurrentStage(event);
    }

    @FXML
    private void handleEstoque(ActionEvent event) {
        // Estoque continua no local padrão
        String fxmlFile = "/fxml/ui/TelaInicialEstoque.fxml";
        abrirNovaJanelaFXML(fxmlFile, "Estoque");

        closeCurrentStage(event);
    }

    private void abrirNovaJanelaFXML(String caminhoFxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao carregar FXML: " + caminhoFxml);
            e.printStackTrace();
        }
    }

    private void closeCurrentStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}