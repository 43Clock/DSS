package data;

import business.*;

import java.sql.*;
import java.util.*;

public class RobotDAO implements Map<Integer, Robot> {
    private static RobotDAO singleton = null;


    private RobotDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Robot (" +
                    "Identificador INT NOT NULL AUTO_INCREMENT ," +
                    "Localizacao INT NOT NULL DEFAULT 0," +
                    "Recolhida TINYINT(1) NOT NULL ," +
                    "Instrucao VARCHAR(100) DEFAULT NULL ," +
                    "idPalete INT DEFAULT NULL ," +
                    "PRIMARY KEY (Identificador));";
            stm.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }


    public static RobotDAO getInstance() {
        if (RobotDAO.singleton == null) {
            RobotDAO.singleton = new RobotDAO();
        }
        return RobotDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM Robot")) {
            if(rs.next()) {
                i = rs.getInt(1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }


    @Override
    public boolean containsKey(Object key) {
        boolean r;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs =
                     stm.executeQuery("SELECT Identificador FROM Robot WHERE Identificador="+key)) {
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public boolean containsValue(Object value) {
        Palete a = (Palete) value;
        return this.containsKey(a.getIdentificador());
    }


    @Override
    public Robot get(Object key) {
        Robot a = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Robot WHERE Identificador="+key)) {
            if (rs.next()) {
                if (rs.getString("Instrucao") == null) {
                    a = new Robot(rs.getInt("Localizacao"),rs.getInt("Identificador"),false,null);
                } else {
                    a = new Robot(rs.getInt("Localizacao"), rs.getInt("Identificador"),rs.getInt("Recolhida") == 1 ,new Instrucao(rs.getString("Instrucao"), rs.getInt("idPalete")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return a;
    }

    @Override
    public Robot put(Integer key, Robot a) {
        Robot res = null;
        Instrucao i = a.getInstrucao();
        int recolhida = 0;
        if (a.getRecolhida()) recolhida++;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            if(i == null)
                stm.executeUpdate(
                        "INSERT INTO Robot VALUES ('"+key+"', '"+a.getLocalizacao()+"', 0 ,NULL ,NULL)" +
                                "ON DUPLICATE KEY UPDATE Localizacao = "+a.getLocalizacao()+",Instrucao = NULL , Recolhida = 0 ,idPalete = NULL ");
            else
                stm.executeUpdate("INSERT INTO Robot VALUES ('"+key+"', '"+a.getLocalizacao()+"', '"+recolhida+"', '"+i.getCaminho()+"', "+i.getiDpalete()+")" +
                        "ON DUPLICATE KEY UPDATE Localizacao = "+a.getLocalizacao()+", Instrucao = '"+i.getCaminho()+"', idPalete = "+i.getiDpalete()+", Recolhida = "+recolhida );
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }

    @Override
    public Robot remove(Object key) {
        Robot t = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM Robot WHERE Identificador='"+key+"'");
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Robot> robot) {
        for(Robot a : robot.values()) {
            this.put(a.getIdentificador(), a);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE Robot");
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Set<Integer> keySet() {
        Set<Integer> r = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Identificador FROM Robot ")) {
            while (rs.next()) {
                r.add(rs.getInt("Identificador"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public Collection<Robot> values() {
        Collection<Robot> col = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Identificador FROM Robot")) {
            while (rs.next()) {
                col.add(this.get(rs.getInt("Identificador")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return col;
    }

    @Override
    public Set<Entry<Integer, Robot>> entrySet() {
        Set<Entry<Integer,Robot>> r = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Robot")) {
            while (rs.next()) {
                r.add(new AbstractMap.SimpleEntry<>(rs.getInt("Identificador"),this.get(rs.getInt("Identificador"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }
}
