package app.controller.manutencao;

import javafx.collections.transformation.FilteredList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import app.dao.daomanutencao.TrocaDAO;
import app.model.manutencao.Troca;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class HistoricoManutencaoController {

    @FXML private ComboBox<String> cbFiltroEquipamento;
    @FXML private ComboBox<String> cbFiltroTipo;

    @FXML private TableView<Troca> tabelaHistorico;
    @FXML private TableColumn<Troca, String> colData, colEquipamento, colTipo, colEspecificacao, colResponsavel, colFiltro;
    @FXML private TableColumn<Troca, Integer> colHorimetro;

    // MUDANÇA: Singleton
    private final TrocaDAO trocaDAO = TrocaDAO.getInstance();
    private FilteredList<Troca> listaFiltrada;

    @FXML
    public void initialize() {
        configurarColunas();
        configurarFiltros();
        carregarDados();
    }

    private void configurarColunas() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colData.setCellValueFactory(cell -> {
            if (cell.getValue().getData() != null) return new SimpleStringProperty(cell.getValue().getData().format(dtf));
            return new SimpleStringProperty("-");
        });

        colEquipamento.setCellValueFactory(new PropertyValueFactory<>("nomeEquipamento"));
        colTipo.setCellValueFactory(cell -> new SimpleStringProperty(formatarTipo(cell.getValue().getTipo())));
        colEspecificacao.setCellValueFactory(new PropertyValueFactory<>("especificacao"));
        colHorimetro.setCellValueFactory(new PropertyValueFactory<>("horimetro"));
        colResponsavel.setCellValueFactory(new PropertyValueFactory<>("responsavel"));

        colFiltro.setCellValueFactory(cell -> {
            Troca t = cell.getValue();
            if (t.getTipo() != null && t.getTipo().toLowerCase().contains("oleo")) {
                return new SimpleStringProperty(t.isCfsf() ? "COM" : "SEM");
            }
            return new SimpleStringProperty("-");
        });
    }

    private void configurarFiltros() {
        cbFiltroEquipamento.setItems(FXCollections.observableArrayList("Todos", "HP400", "HP4", "C110"));
        cbFiltroEquipamento.getSelectionModel().selectFirst();

        cbFiltroTipo.setItems(FXCollections.observableArrayList("Todos", "Revestimento", "Correia", "Óleo Hidráulico", "Óleo Lubrificação"));
        cbFiltroTipo.getSelectionModel().selectFirst();

        cbFiltroEquipamento.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        cbFiltroTipo.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void carregarDados() {
        listaFiltrada = new FilteredList<>(FXCollections.observableArrayList(trocaDAO.listarTodos()), p -> true);
        tabelaHistorico.setItems(listaFiltrada);
    }

    private void aplicarFiltros() {
        if (listaFiltrada == null) return;
        String equipSelecionado = cbFiltroEquipamento.getValue();
        String tipoSelecionado = cbFiltroTipo.getValue();

        listaFiltrada.setPredicate(troca -> {
            boolean matchTipo = true;
            if (tipoSelecionado != null && !tipoSelecionado.equals("Todos")) {
                String tipoBanco = troca.getTipo() == null ? "" : troca.getTipo();
                if (tipoSelecionado.contains("Revestimento") && !tipoBanco.equals("Revestimento")) matchTipo = false;
                else if (tipoSelecionado.contains("Correia") && !tipoBanco.equals("Correia")) matchTipo = false;
                else if (tipoSelecionado.contains("Hidráulico") && !tipoBanco.equals("OleoHidraulico")) matchTipo = false;
                else if (tipoSelecionado.contains("Lubrificação") && !tipoBanco.equals("OleoLubrificacao")) matchTipo = false;
            }

            boolean matchEquip = true;
            if (equipSelecionado != null && !equipSelecionado.equals("Todos")) {
                String nomeNoBanco = troca.getNomeEquipamento();
                if (nomeNoBanco == null || !nomeNoBanco.equalsIgnoreCase(equipSelecionado)) matchEquip = false;
            }
            return matchTipo && matchEquip;
        });
    }

    @FXML
    void handleLimparFiltros(ActionEvent event) {
        cbFiltroEquipamento.getSelectionModel().selectFirst();
        cbFiltroTipo.getSelectionModel().selectFirst();
    }

    @FXML
    void handleVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ui/manutencao/TelaInicialManutencao.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Manutenção");
            stage.setScene(new Scene(root));
            stage.show();
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private String formatarTipo(String tipo) {
        if (tipo == null) return "";
        switch (tipo) {
            case "OleoHidraulico": return "Óleo Hidráulico";
            case "OleoLubrificacao": return "Óleo Lubrificação";
            default: return tipo;
        }
    }
}