package app.dao.daomanutencao;

import app.database.ConnectionFactory;
import app.model.manutencao.ModeloBritador;
import app.model.manutencao.StatusAviso;
import java.sql.*;

public class AvisoDAO {

    private static AvisoDAO instance;
    // Usa a instância única do BritadorDAO
    private final BritadorDAO britadorDAO = BritadorDAO.getInstance();

    private AvisoDAO() {}

    public static synchronized AvisoDAO getInstance() {
        if (instance == null) instance = new AvisoDAO();
        return instance;
    }

    public int getVidaUtilConfigurada(String modeloOuLinha, String componente) {
        String sql = "SELECT vida_util_horas FROM ConfiguracaoAvisos WHERE modelo_ou_linha = ? AND componente = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, modeloOuLinha);
            stmt.setString(2, componente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("vida_util_horas");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void setVidaUtilConfigurada(String modeloOuLinha, String componente, int horas) {
        String sql = "INSERT OR REPLACE INTO ConfiguracaoAvisos (modelo_ou_linha, componente, vida_util_horas) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, modeloOuLinha);
            stmt.setString(2, componente);
            stmt.setInt(3, horas);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public StatusAviso calcularStatusBritador(ModeloBritador modelo, String componente) {
        int horimetroAtualMaquina = britadorDAO.getHorimetro(modelo).valor;
        int horimetroUltimaTroca = getHorimetroUltimaTroca(modelo.getNome(), componente);
        int vidaUtil = getVidaUtilConfigurada(modelo.getNome(), componente);

        if (vidaUtil == 0) return new StatusAviso(componente, 0, "CINZA", "Não configurado");

        int horasRodadas = horimetroAtualMaquina - horimetroUltimaTroca;
        int horasRestantes = vidaUtil - horasRodadas;

        String status;
        if (horasRestantes <= 0) status = "VERMELHO";
        else if (horasRestantes <= 100) status = "AMARELO";
        else status = "VERDE";

        return new StatusAviso(componente, horasRestantes, status, horasRestantes + "h restantes");
    }

    private int getHorimetroUltimaTroca(String nomeEquipamento, String tipo) {
        String sql = "SELECT MAX(horimetro) as ult FROM Troca WHERE nome_equipamento = ? AND tipo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeEquipamento);
            stmt.setString(2, tipo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("ult");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}