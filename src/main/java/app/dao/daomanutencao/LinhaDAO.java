package app.dao.daomanutencao;

import app.database.ConnectionFactory;
import app.model.manutencao.Linha;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LinhaDAO {

    private static LinhaDAO instance;

    private LinhaDAO() {}

    public static synchronized LinhaDAO getInstance() {
        if (instance == null) instance = new LinhaDAO();
        return instance;
    }

    public List<Linha> listarTodas() {
        List<Linha> lista = new ArrayList<>();
        String sql = "SELECT * FROM Linha";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Linha(rs.getInt("id"), rs.getString("nome")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean inserir(Linha linha) {
        String sql = "INSERT INTO Linha (nome) VALUES (?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, linha.getNome());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM Linha WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}