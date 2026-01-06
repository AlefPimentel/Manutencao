package app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import app.model.Item;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ItemRowFactory {

    /**
     * Cria uma HBox representando a linha do item.
     *
     * @param item       Item a ser representado
     * @param onSelect   (item, hbox) -> chamado quando clica na linha
     * @param onRetirar  item -> chamado quando clica no botão de retirar
     * @return HBox pronta para ser adicionada ao layout
     */
    public HBox create(Item item, BiConsumer<Item, HBox> onSelect, Consumer<Item> onRetirar) {
        HBox linha = new HBox(10);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.getStyleClass().add("item-linha");

        Label lblId = new Label("#" + String.format("%02d", item.getIdItem()));
        lblId.setPrefWidth(40);
        lblId.getStyleClass().add("texto-id");

        Label lblNome = new Label(item.getNome());
        lblNome.setPrefWidth(150);
        HBox.setHgrow(lblNome, Priority.ALWAYS);
        lblNome.getStyleClass().add("texto-nome");

        Label lblQtd = new Label(String.format("%03d", item.getEstoque()));
        lblQtd.setPrefWidth(40);
        lblQtd.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        lblQtd.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold;");

        Button btnRetirar = new Button("-");
        btnRetirar.getStyleClass().add("botao-pequeno-retirar");
        btnRetirar.setTooltip(new javafx.scene.control.Tooltip("Retirar Item do Estoque"));

        btnRetirar.setOnAction(e -> {
            e.consume();
            if (onRetirar != null) onRetirar.accept(item);
        });

        linha.getChildren().addAll(lblId, lblNome, lblQtd, btnRetirar);

        linha.setOnMouseClicked(event -> {
            if (onSelect != null) onSelect.accept(item, linha);
        });

        return linha;
    }
}
