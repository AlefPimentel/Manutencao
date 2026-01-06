package app.dao.daomanutencao;

import app.database.ConnectionFactory;
import app.model.manutencao.Troca;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrocaDAO {

    private static TrocaDAO instance;

    private TrocaDAO() {}

    public static synchronized TrocaDAO getInstance() {
        if (instance == null) instance = new TrocaDAO();
        return instance;
    }

    public boolean inserir(Troca obj) {
        String sql = "INSERT INTO Troca (data, responsavel, horimetro, tipo, especificacao, cfsf, nome_equipamento, id_equipamento) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (obj.getData() != null) stmt.setDate(1, Date.valueOf(obj.getData()));
            else stmt.setNull(1, Types.DATE);

            stmt.setString(2, obj.getResponsavel());
            stmt.setInt(3, obj.getHorimetro());
            stmt.setString(4, obj.getTipo());
            stmt.setString(5, obj.getEspecificacao());
            stmt.setBoolean(6, obj.isCfsf());
            stmt.setString(7, obj.getNomeEquipamento());
            if (obj.getIdEquipamento() > 0) stmt.setInt(8, obj.getIdEquipamento());
            else stmt.setNull(8, Types.INTEGER);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- ESTE MÉTODO ESTAVA FALTANDO ---
    public List<Troca> listarTodos() {
        List<Troca> lista = new ArrayList<>();
        String sql = "SELECT * FROM Troca ORDER BY data DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearTroca(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // --- ESTE MÉTODO ESTAVA FALTANDO (CAUSOU O ERRO) ---
    public List<Troca> listarPorTipo(String tipoAlvo) {
        List<Troca> lista = new ArrayList<>();
        String sql = "SELECT * FROM Troca WHERE tipo = ? ORDER BY data DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipoAlvo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearTroca(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Troca> listarUltimasLubrificacoesPorLinha(int idLinha) {
        List<Troca> lista = new ArrayList<>();
        String sql = """
            SELECT t.*, tc.nome AS nome_equipamento_real 
            FROM Troca t
            INNER JOIN Tc tc ON t.id_equipamento = tc.id 
            WHERE t.tipo LIKE '%Lubrificacao%' 
            AND tc.id_linha = ?
            ORDER BY t.data DESC 
            LIMIT 5
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idLinha);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Troca t = mapearTroca(rs);
                if (rs.getString("nome_equipamento_real") != null) {
                    t.setNomeEquipamento(rs.getString("nome_equipamento_real"));
                }
                lista.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private Troca mapearTroca(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("data");
        LocalDate localDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        Troca t = new Troca();
        t.setId(rs.getInt("id"));
        t.setData(localDate);
        t.setResponsavel(rs.getString("responsavel"));
        t.setHorimetro(rs.getInt("horimetro"));
        t.setTipo(rs.getString("tipo"));
        t.setEspecificacao(rs.getString("especificacao"));
        t.setCfsf(rs.getBoolean("cfsf"));
        t.setNomeEquipamento(rs.getString("nome_equipamento"));
        try { t.setIdEquipamento(rs.getInt("id_equipamento")); } catch (SQLException e) {}
        return t;
    }
}