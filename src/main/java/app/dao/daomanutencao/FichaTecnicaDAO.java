package app.dao.daomanutencao;

import app.database.ConnectionFactory;
import app.model.manutencao.FichaTecnica;
import app.model.manutencao.ModeloBritador;
import java.sql.*;

public class FichaTecnicaDAO {

    private static FichaTecnicaDAO instance;

    private FichaTecnicaDAO() {}

    public static synchronized FichaTecnicaDAO getInstance() {
        if (instance == null) instance = new FichaTecnicaDAO();
        return instance;
    }

    public boolean atualizar(FichaTecnica ficha) {
        String sql = "UPDATE ConfiguracaoMaquinas SET correia=?, revestimento=?, oleoLub=?, oleoHidr=? WHERE modelo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ficha.getCorreia());
            stmt.setString(2, ficha.getRevestimento());
            stmt.setString(3, ficha.getOleoLub());
            stmt.setString(4, ficha.getOleoHidr());
            stmt.setString(5, ficha.getModelo().getNome());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public FichaTecnica buscarPorModelo(ModeloBritador modeloAlvo) {
        String sql = "SELECT * FROM ConfiguracaoMaquinas WHERE modelo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, modeloAlvo.getNome());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new FichaTecnica(
                        ModeloBritador.fromString(rs.getString("modelo")),
                        rs.getString("correia"),
                        rs.getString("revestimento"),
                        rs.getString("oleoLub"),
                        rs.getString("oleoHidr")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}