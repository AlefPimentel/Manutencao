package app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import app.model.Item;
import app.model.Particao;
import app.service.DescricaoService;
import app.service.ItemService;
import app.service.JanelaService;
import app.service.ParticaoService;
import app.ui.ItemDetalhesView;
import app.ui.ParticaoView;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TelaInicialEstoqueController {

    @FXML private VBox vboxParticoes;
    @FXML private VBox paneDetalhes;
    @FXML private Label lblMensagemVazia, lblNomeItem, lblEstoqueItem, lblLocalItem, lblLocalArmazenamento;
    @FXML private Label lblEspecificacaoItem, lblDataItem, lblDescricaoItem;
    @FXML private ImageView imgFotoItem;
    @FXML private Button btnEditar, btnRegistros;

    // --- Serviços (Singleton) ---
    private final ParticaoService particaoService = ParticaoService.getInstance();
    private final ItemService itemService = ItemService.getInstance();
    private final DescricaoService descricaoService = DescricaoService.getInstance();
    private final JanelaService janelaService = JanelaService.getInstance();

    private ItemDetalhesView detalhesView;
    private Item itemSelecionado = null;
    private HBox linhaSelecionadaAnterior = null;

    private final Map<Integer, ParticaoView> mapaViewsParticao = new HashMap<>();

    @FXML
    private void initialize() {
        detalhesView = new ItemDetalhesView(
                paneDetalhes, lblMensagemVazia, lblNomeItem, lblEstoqueItem,
                lblLocalItem, lblLocalArmazenamento, lblEspecificacaoItem,
                lblDataItem, lblDescricaoItem, imgFotoItem, itemService
        );
        carregarListaCompleta();
        detalhesView.limpar();
    }

    private void carregarListaCompleta() {
        vboxParticoes.getChildren().clear();
        mapaViewsParticao.clear();

        List<Particao> particoes = particaoService.listarTodas();
        List<Item> todosOsItens = itemService.listarTodos();

        Map<Integer, List<Item>> itensPorParticao = todosOsItens.stream()
                .collect(Collectors.groupingBy(Item::getIdParticao));

        for (Particao p : particoes) {
            List<Item> itens = itensPorParticao.getOrDefault(p.getIdParticao(), Collections.emptyList());
            adicionarParticaoVisualmente(p, itens);
        }
    }

    private void adicionarParticaoVisualmente(Particao p, List<Item> itens) {
        ParticaoView view = new ParticaoView(
                p, itens,
                this::editarParticao,
                this::selecionarItem,
                this::abrirTelaRetirada
        );
        vboxParticoes.getChildren().add(view);
        mapaViewsParticao.put(p.getIdParticao(), view);
    }

    // --- AÇÕES SEGURAS (SEM REFLECTION) ---

    @FXML
    void handleCadastrarItem(ActionEvent event) {
        abrirModal("/fxml/ui/TelaCadastroItem.fxml", "Cadastrar Novo Item", null);
        carregarListaCompleta();
    }

    @FXML
    void handleCadastrarParticao(ActionEvent event) {
        abrirModal("/fxml/ui/CadastroParticao.fxml", "Nova Partição", null);
        carregarListaCompleta();
    }

    @FXML
    void handleEditar(ActionEvent event) {
        if (itemSelecionado != null) {
            int idParticaoAntiga = itemSelecionado.getIdParticao();

            // MUDANÇA: Cast direto e seguro
            abrirModal("/fxml/ui/EditarGeral.fxml", "Editar Item", controller -> {
                if (controller instanceof EditarGeralController c) {
                    c.setItem(itemSelecionado);
                }
            });

            // Atualização Otimizada
            Item itemAtualizado = itemService.buscarPorId(itemSelecionado.getIdItem());
            if (itemAtualizado != null) {
                if (idParticaoAntiga != itemAtualizado.getIdParticao()) {
                    if (mapaViewsParticao.containsKey(idParticaoAntiga)) {
                        mapaViewsParticao.get(idParticaoAntiga).removerItemVisual(itemAtualizado.getIdItem());
                    }
                    if (mapaViewsParticao.containsKey(itemAtualizado.getIdParticao())) {
                        mapaViewsParticao.get(itemAtualizado.getIdParticao()).adicionarItemVisual(itemAtualizado);
                    }
                } else {
                    if (mapaViewsParticao.containsKey(idParticaoAntiga)) {
                        mapaViewsParticao.get(idParticaoAntiga).atualizarItemVisual(itemAtualizado);
                    }
                }
                selecionarItem(itemAtualizado, null);
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Selecione um item primeiro.").show();
        }
    }

    private void editarParticao(Particao p) {
        // MUDANÇA: Cast direto e seguro
        abrirModal("/fxml/ui/EditarGeral.fxml", "Editar Partição", controller -> {
            if (controller instanceof EditarGeralController c) {
                c.setParticao(p);
            }
        });
        carregarListaCompleta();
    }

    private void abrirTelaRetirada(Item item) {
        // MUDANÇA: Cast direto e seguro
        abrirModal("/fxml/ui/TelaRetiradaItem.fxml", "Retirada - " + item.getNome(), controller -> {
            if (controller instanceof RetiradaItemController c) {
                c.setItem(item);
            }
        });

        Item itemAtualizado = itemService.buscarPorId(item.getIdItem());
        if (itemAtualizado != null && mapaViewsParticao.containsKey(item.getIdParticao())) {
            mapaViewsParticao.get(item.getIdParticao()).atualizarItemVisual(itemAtualizado);
            selecionarItem(itemAtualizado, null);
        }
    }

    @FXML void handleRegistros(ActionEvent event) {
        abrirModal("/fxml/ui/TelaMovimentacao.fxml", "Registros Gerais", null);
    }

    // --- MÉTODOS AUXILIARES ---

    private void abrirModal(String fxmlPath, String titulo, Consumer<Object> configController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            if (configController != null) configController.accept(loader.getController());
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir tela: " + fxmlPath).show();
            e.printStackTrace();
        }
    }

    private void selecionarItem(Item item, HBox linha) {
        if (linhaSelecionadaAnterior != null) linhaSelecionadaAnterior.getStyleClass().remove("item-selecionado");
        if (linha != null) {
            linha.getStyleClass().add("item-selecionado");
            linhaSelecionadaAnterior = linha;
        }
        itemSelecionado = item;
        String local = descricaoService.extrairLocalArmazem(item);
        String desc = descricaoService.extrairDescricaoLimpa(item);
        detalhesView.mostrar(item, local, desc);
    }

    @FXML void handleVoltar(ActionEvent event) {
        janelaService.abrirJanela("/fxml/ui/TelaInicial.fxml", "Menu Principal", c -> {});
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    @FXML void handleFechar(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}