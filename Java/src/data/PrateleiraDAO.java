package data;

import business.Palete;
import business.Prateleira;
import business.QrCode;

import java.sql.*;
import java.util.*;

public class PrateleiraDAO implements Map<Integer,Prateleira>{
    private static PrateleiraDAO singleton = null;


    private PrateleiraDAO() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Prateleira (" +
                    "Identificador INT NOT NULL AUTO_INCREMENT PRIMARY KEY ," +
                    "Localizacao INT NOT NULL ," +
                    "Ocupado TINYINT(1) DEFAULT 0 ," +
                    "Palete INT DEFAULT NULL ," +
                    "foreign key(Palete) references Palete(Identificador))";
            stm.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static PrateleiraDAO getInstance() {
        if (PrateleiraDAO.singleton == null) {
            PrateleiraDAO.singleton = new PrateleiraDAO();
        }
        return PrateleiraDAO.singleton;
    }

    @Override
    public int size() {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM Prateleira")) {
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
                     stm.executeQuery("SELECT Identificador FROM Prateleira WHERE Identificador="+key)) {
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
        Prateleira a = (Prateleira) value;
        return this.containsKey(a.getIdentificador());
    }


    @Override
    public Prateleira get(Object key) {
        Prateleira t = null;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Prateleira WHERE Identificador='"+key+"'")) {
            if (rs.next()) {
                Palete s = null;
                String sql = "SELECT * FROM Palete WHERE Identificador='"+rs.getInt("Palete")+"'";
                try (ResultSet rsa = stm.executeQuery(sql)) {
                    if (rsa.next()) {
                        Boolean b = rsa.getInt("Espera") == 1;
                        s = new Palete(rsa.getInt("Localizacao"), rsa.getString("Material"), rsa.getDouble("Peso"),
                                rsa.getDouble("Preco"),b,rsa.getInt("Identificador"),new QrCode(rsa.getString("QrCode")));
                    } else {
                        // BD inconsistente!! Sala não existe - tratar com excepções.
                    } // catch é feito no try inicial - este try serve para fechar o ResultSet automaticamente
                    // Nota: abrir um novo ResultSet no mesmo Statement fecha o ResultSet anterior
                }
                t = new Prateleira(rs.getInt("Localizacao"), s,(s!=null),rs.getInt("Identificador"));
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public Prateleira put(Integer key, Prateleira a) {
        Prateleira res = null;
        Palete p = a.getPalete();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            if(p == null)
            stm.executeUpdate(
                    "INSERT INTO Prateleira VALUES ('"+key+"', '"+a.getLocalizacao()+"', '"+0+"',NULL)" +
                            "ON DUPLICATE KEY UPDATE Ocupado = 0, Palete = NULL ");
            else
                stm.executeUpdate("INSERT INTO Prateleira VALUES ('"+key+"', '"+a.getLocalizacao()+"', '"+1+"', "+p.getIdentificador()+")" +
                            "ON DUPLICATE KEY UPDATE Ocupado = 1, Palete = "+p.getIdentificador());
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return res;
    }


    @Override
    public Prateleira remove(Object key) {
        Prateleira t = this.get(key);
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM Prateleira WHERE Identificador='"+key+"'");
        } catch (Exception e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return t;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Prateleira> prateleira) {
        for(Prateleira a : prateleira.values()) {
            this.put(a.getIdentificador(), a);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement()) {
            stm.executeUpdate("TRUNCATE Prateleira");
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
             ResultSet rs = stm.executeQuery("SELECT Identificador FROM Prateleira ")) {
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
    public Collection<Prateleira> values() {
        Collection<Prateleira> col = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT Identificador FROM Prateleira")) {
            while (rs.next()) {
                col.add(this.get(rs.getInt("Identificador")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return col;
    }

    public Set<Map.Entry<Integer, Prateleira>> entrySet() {
        Set<Entry<Integer,Prateleira>> r = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT * FROM Prateleira")) {
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
