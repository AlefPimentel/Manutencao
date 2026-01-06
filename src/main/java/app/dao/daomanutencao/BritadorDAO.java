package app.dao.daomanutencao;

import app.database.ConnectionFactory;
import app.model.manutencao.ModeloBritador;
import java.sql.*;
import java.time.LocalDate;

public class BritadorDAO {

    private static BritadorDAO instance;

    private BritadorDAO() {}

    public static synchronized BritadorDAO getInstance() {
        if (instance == null) instance = new BritadorDAO();
        return instance;
    }

    public static class DadosHorimetro {
        public int valor;
        public LocalDate data;
        public DadosHorimetro(int valor, LocalDate data) {
            this.valor = valor;
            this.data = data;
        }
    }

    public DadosHorimetro getHorimetro(ModeloBritador modelo) {
        String sql = "SELECT horimetro_atual, data_atualizacao_horimetro FROM ConfiguracaoMaquinas WHERE modelo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, modelo.getNome());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int valor = rs.getInt("horimetro_atual");
                Date dataSql = rs.getDate("data_atualizacao_horimetro");
                LocalDate data = (dataSql != null) ? dataSql.toLocalDate() : null;
                return new DadosHorimetro(valor, data);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new DadosHorimetro(0, null);
    }

    public boolean atualizarHorimetro(ModeloBritador modelo, int novoHorimetro) {
        String sql = "UPDATE ConfiguracaoMaquinas SET horimetro_atual = ?, data_atualizacao_horimetro = ? WHERE modelo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, novoHorimetro);
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.setString(3, modelo.getNome());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}