package app.dao.daomanutencao;

import app.database.ConnectionFactory;
import app.model.manutencao.Agenda;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AgendaDAO {

    private static AgendaDAO instance;

    private AgendaDAO() {}

    public static synchronized AgendaDAO getInstance() {
        if (instance == null) instance = new AgendaDAO();
        return instance;
    }

    public boolean inserir(Agenda obj) {
        String sql = "INSERT INTO Agenda (data, tipo) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (obj.getData() != null) stmt.setDate(1, Date.valueOf(obj.getData()));
            else stmt.setNull(1, Types.DATE);

            stmt.setString(2, obj.getTipo());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM Agenda WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Agenda> listarTodos() {
        List<Agenda> lista = new ArrayList<>();
        String sql = "SELECT * FROM Agenda ORDER BY data ASC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date sqlDate = rs.getDate("data");
                LocalDate localDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;
                lista.add(new Agenda(rs.getInt("id"), localDate, rs.getString("tipo")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}