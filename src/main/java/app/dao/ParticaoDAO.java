package app.dao;

import app.model.Particao;
import app.database.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticaoDAO {

    // ============================
    // INSERIR
    // ============================
    public boolean inserir(Particao particao) {
        String sql = "INSERT INTO Particao (nome_particao) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, particao.getNomeParticao());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao inserir partição: " + e.getMessage());
            return false;
        }
    }

    // ============================
    // LISTAR TODAS
    // ============================
    public List<Particao> listar() {
        List<Particao> lista = new ArrayList<>();
        String sql = "SELECT idParticao, nome_particao FROM Particao";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Particao p = new Particao();
                p.setIdParticao(rs.getInt("idParticao"));
                p.setNomeParticao(rs.getString("nome_particao"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar partições: " + e.getMessage());
        }

        return lista;
    }

    // ============================
    // ATUALIZAR
    // ============================
    public boolean atualizar(Particao particao) {
        String sql = "UPDATE Particao SET nome_particao=? WHERE idParticao=?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, particao.getNomeParticao());
            stmt.setInt(2, particao.getIdParticao());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar partição: " + e.getMessage());
            return false;
        }
    }

    // ============================
    // DELETAR
    // ============================
    public boolean deletar(int id) {
        String sql = "DELETE FROM Particao WHERE idParticao=?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar partição: " + e.getMessage());
            return false;
        }
    }

    // ============================
    // CONTAR ITENS (NOVO)
    // ============================
    public int contarItens(int idParticao) {
        String sql = "SELECT COUNT(*) FROM Item WHERE idParticao = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idParticao);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1); // Retorna a quantidade de itens vinculados
            }
        } catch (SQLException e) {
            System.err.println("Erro ao contar itens da partição: " + e.getMessage());
        }
        return 0;
    }
}