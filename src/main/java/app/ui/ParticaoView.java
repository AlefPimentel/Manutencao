package app.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import app.model.Item;
import app.model.Particao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ParticaoView extends TitledPane {

    private final Particao particao;
    private final ItemRowFactory itemRowFactory;

    // Armazena o container para podermos adicionar/remover filhos
    private VBox contentBox;

    // Mapeia ID do item -> Node na tela (para encontrar rápido na hora de excluir/editar)
    private final Map<Integer, HBox> mapaLinhas = new HashMap<>();

    // Callbacks salvos para reutilizar
    private final BiConsumer<Item, HBox> onSelecionarItem;
    private final Consumer<Item> onRetirarItem;

    public ParticaoView(
            Particao particao,
            List<Item> itens,
            Consumer<Particao> onEditarParticao,
            BiConsumer<Item, HBox> onSelecionarItem,
            Consumer<Item> onRetirarItem
    ) {
        this.particao = particao;
        this.itemRowFactory = new ItemRowFactory();
        this.onSelecionarItem = onSelecionarItem;
        this.onRetirarItem = onRetirarItem;

        configurarEstilo();
        configurarCabecalho(onEditarParticao);
        construirConteudoInicial(itens);
    }

    // --- MÉTODOS DE MANIPULAÇÃO DINÂMICA (A MÁGICA ACONTECE AQUI) ---

    public void adicionarItemVisual(Item item) {
        // Remove a mensagem de "Vazio" se existir
        contentBox.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("Vazio"));

        // Cria a linha
        HBox linha = itemRowFactory.create(item, onSelecionarItem, onRetirarItem);

        // Adiciona na tela e no mapa
        contentBox.getChildren().add(linha);
        mapaLinhas.put(item.getIdItem(), linha);

        // Expande para mostrar o novo item
        this.setExpanded(true);
    }

    public void atualizarItemVisual(Item item) {
        // Remove a linha antiga
        removerItemVisual(item.getIdItem());
        // Adiciona a nova (com dados atualizados)
        adicionarItemVisual(item);
    }

    public void removerItemVisual(int idItem) {
        HBox linha = mapaLinhas.remove(idItem);
        if (linha != null) {
            contentBox.getChildren().remove(linha);
        }
        // Se ficou vazio, mostra o label "Vazio"
        if (mapaLinhas.isEmpty()) {
            contentBox.getChildren().add(criarLabelVazio());
        }
    }

    // --- MÉTODOS DE CONSTRUÇÃO ---

    private void configurarEstilo() {
        this.setExpanded(false);
        this.getStyleClass().add("titulo-particao");
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private void configurarCabecalho(Consumer<Particao> onEditar) {
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setMaxWidth(Double.MAX_VALUE);

        Label lblNome = new Label(particao.getNomeParticao());
        lblNome.setStyle("-fx-font-weight: bold; -fx-text-fill: #1f2937; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEditar = new Button();
        btnEditar.getStyleClass().add("botao-editar-particao");
        btnEditar.setTooltip(new Tooltip("Editar esta Partição"));
        btnEditar.setPrefSize(24, 24);
        btnEditar.setOnAction(e -> { e.consume(); onEditar.accept(particao); });

        headerBox.getChildren().addAll(lblNome, spacer, btnEditar);
        this.setGraphic(headerBox);
    }

    private void construirConteudoInicial(List<Item> itens) {
        contentBox = new VBox(5);
        contentBox.getChildren().add(criarCabecalhoTabela());

        if (itens.isEmpty()) {
            contentBox.getChildren().add(criarLabelVazio());
        } else {
            for (Item item : itens) {
                HBox linha = itemRowFactory.create(item, onSelecionarItem, onRetirarItem);
                contentBox.getChildren().add(linha);
                mapaLinhas.put(item.getIdItem(), linha);
            }
        }
        this.setContent(contentBox);
    }

    private Label criarLabelVazio() {
        Label lblVazio = new Label("Vazio");
        lblVazio.setStyle("-fx-text-fill: #999; -fx-padding: 10; -fx-alignment: center;");
        lblVazio.setMaxWidth(Double.MAX_VALUE);
        return lblVazio;
    }

    private HBox criarCabecalhoTabela() {
        HBox header = new HBox();
        header.getStyleClass().add("header-tabela");
        Label hId = new Label("ID"); hId.setPrefWidth(40);
        Label hNome = new Label("Nome"); hNome.setPrefWidth(150); HBox.setHgrow(hNome, Priority.ALWAYS);
        Label hQtd = new Label("Qntd"); hQtd.setPrefWidth(60); hQtd.setAlignment(Pos.CENTER_RIGHT);
        header.getChildren().addAll(hId, hNome, hQtd);
        return header;
    }
}