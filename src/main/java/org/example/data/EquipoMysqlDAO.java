package org.example.data;

import org.example.models.Equipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipoMysqlDAO implements DAO<Equipo> {

    private String url = "jdbc:mysql://localhost:3307/ad";
    private String user = System.getenv("USER");
    private String pwd = System.getenv("PASSWORD");

    private String sqlQuery = "select * from equipo";
    private String sqlFindById = "select * from equipo where id = ?";
    private String sqlInsert = "insert into equipo (nombre, categoria, disponible) values (?,?,?)";
    private String sqlUpdate = "update equipo set nombre = ?, categoria = ?, disponible = ? where id = ?";
    private String sqlDelete = "delete from equipo where id = ?";

    // Consultas preparadas para los métodos adicionales
    private String sqlFindByNombre = "select * from equipo where nombre = ?";
    private String sqlFindByEstado = "select * from equipo where disponible = ?";

    @Override
    public Optional<Equipo> findById(Long id) {
        if( user==null || pwd==null ){
            throw new RuntimeException("Usuario o Password no presente");
        }
        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = conn.prepareStatement(sqlFindById)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Equipo> findAll() {
        List<Equipo> salida = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = conn.prepareStatement(sqlQuery);
             ResultSet rs = ps.executeQuery() )
        {
            while (rs.next()) {
                salida.add(mapper(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return salida;
    }

    @Override
    public Equipo save(Equipo entity) {
        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getCategoria());
            ps.setBoolean(3, entity.getDisponible());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Equipo entity) {
        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getCategoria());
            ps.setBoolean(3, entity.getDisponible());
            ps.setLong(4, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Equipo> findByNombre(String nombre) {
        List<Equipo> salida = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = conn.prepareStatement(sqlFindByNombre)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    salida.add(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return salida;
    }

    public List<Equipo> findByEstado(Boolean status) {
        List<Equipo> salida = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = conn.prepareStatement(sqlFindByEstado)) {
            ps.setBoolean(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    salida.add(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return salida;
    }

    /* Helpers */

    public static Equipo mapper(ResultSet rs) throws SQLException {
        Equipo equipo = new Equipo();
        equipo.setId(rs.getLong("id"));
        equipo.setNombre(rs.getString("nombre"));
        equipo.setCategoria(rs.getString("categoria"));
        equipo.setDisponible(rs.getBoolean("disponible"));
        return equipo;
    }
}
