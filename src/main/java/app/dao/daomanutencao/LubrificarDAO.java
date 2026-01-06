package app.dao.daomanutencao;

import app.database.ConnectionFactory;
import app.model.manutencao.Lubrificar;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LubrificarDAO {

    private static LubrificarDAO instance;

    private LubrificarDAO() {}

    public static synchronized LubrificarDAO getInstance() {
        if (instance == null) instance = new LubrificarDAO();
        return instance;
    }

    public boolean inserir(Lubrificar obj) {
        String sql = "INSERT INTO Lubrificar (data, responsavel, horimetro, graxa, id_linha) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (obj.getData() != null) stmt.setDate(1, Date.valueOf(obj.getData()));
            else stmt.setNull(1, Types.DATE);

            stmt.setString(2, obj.getResponsavel());
            stmt.setInt(3, obj.getHorimetro());
            stmt.setString(4, obj.getGraxa());
            if (obj.getIdLinha() > 0) stmt.setInt(5, obj.getIdLinha());
            else stmt.setNull(5, Types.INTEGER);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Lubrificar> listarPorLinha(int idLinha) {
        List<Lubrificar> lista = new ArrayList<>();
        String sql = """
            SELECT l.*, lin.nome AS nome_linha_real
            FROM Lubrificar l
            INNER JOIN Linha lin ON l.id_linha = lin.id
            WHERE l.id_linha = ?
            ORDER BY l.data DESC, l.id DESC
            LIMIT 50
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idLinha);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date sqlDate = rs.getDate("data");
                LocalDate localDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                Lubrificar l = new Lubrificar();
                l.setId(rs.getInt("id"));
                l.setData(localDate);
                l.setResponsavel(rs.getString("responsavel"));
                l.setHorimetro(rs.getInt("horimetro"));
                l.setGraxa(rs.getString("graxa"));
                l.setIdLinha(rs.getInt("id_linha"));
                l.setNomeEquipamento(rs.getString("nome_linha_real"));
                lista.add(l);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}