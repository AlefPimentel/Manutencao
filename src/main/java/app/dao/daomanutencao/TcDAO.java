package app.dao.daomanutencao;

import app.database.ConnectionFactory;
import app.model.manutencao.Tc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TcDAO {

    private static TcDAO instance;

    private TcDAO() {}

    public static synchronized TcDAO getInstance() {
        if (instance == null) instance = new TcDAO();
        return instance;
    }

    public boolean inserir(Tc obj) {
        String sql = """
            INSERT INTO Tc (nome, roletec, roleter, correia, cavaletec, cavaleter, descricao, rolo, mancal, motor, id_linha) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, obj.getNome());
            stmt.setString(2, obj.getRoletec());
            stmt.setString(3, obj.getRoleter());
            stmt.setString(4, obj.getCorreia());
            stmt.setString(5, obj.getCavaletec());
            stmt.setString(6, obj.getCavaleter());
            stmt.setString(7, obj.getDesc());
            stmt.setString(8, obj.getRolo());
            stmt.setString(9, obj.getMancal());
            stmt.setString(10, obj.getMotor());
            if (obj.getIdLinha() > 0) stmt.setInt(11, obj.getIdLinha());
            else stmt.setNull(11, Types.INTEGER);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean atualizar(Tc obj) {
        String sql = """
            UPDATE Tc SET 
            nome=?, roletec=?, roleter=?, correia=?, cavaletec=?, cavaleter=?, descricao=?, rolo=?, mancal=?, motor=?, id_linha=? 
            WHERE id=?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, obj.getNome());
            stmt.setString(2, obj.getRoletec());
            stmt.setString(3, obj.getRoleter());
            stmt.setString(4, obj.getCorreia());
            stmt.setString(5, obj.getCavaletec());
            stmt.setString(6, obj.getCavaleter());
            stmt.setString(7, obj.getDesc());
            stmt.setString(8, obj.getRolo());
            stmt.setString(9, obj.getMancal());
            stmt.setString(10, obj.getMotor());
            stmt.setInt(11, obj.getIdLinha());
            stmt.setInt(12, obj.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM Tc WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Tc> listarPorLinha(int idLinha) {
        List<Tc> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tc WHERE id_linha = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLinha);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTc(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private Tc mapearTc(ResultSet rs) throws SQLException {
        return new Tc(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("roletec"),
                rs.getString("roleter"),
                rs.getString("correia"),
                rs.getString("cavaletec"),
                rs.getString("cavaleter"),
                rs.getString("descricao"),
                rs.getString("rolo"),
                rs.getString("mancal"),
                rs.getString("motor"),
                rs.getInt("id_linha")
        );
    }
}