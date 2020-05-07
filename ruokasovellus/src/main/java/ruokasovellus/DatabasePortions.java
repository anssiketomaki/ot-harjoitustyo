/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruokasovellus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Ruoka-annoksiin liittyvät asiat tietokantaan tekevä luokka.
 * @author AnssiKetomäki
 */
public class DatabasePortions {

    public Database kanta;
    public Connection db;
    public DatabaseIncredients dIncredients;
    
    public DatabasePortions(Database kanta, DatabaseIncredients dincr) throws SQLException {
        this.kanta = kanta;
        this.db = kanta.db;
        this.dIncredients = dincr;
    }
    /**
    * Metodi sulkee ohjelman yhteyden tietokantaan.
     * @throws java.sql.SQLException
    */
    public void closeConnection() throws SQLException {
        db.close();
    }
    /**
    * Metodi avaa ohjelmalle yhteyden tietokantaan.
     * @throws java.sql.SQLException
    */
    public void openConnection() throws SQLException {
        db = DriverManager.getConnection("jdbc:sqlite:ruokasovellus.db");
    }

    /**
     * Metodi palauttaa käyttäjän haluaman aterian ruoka-aineet ja niiden määrät
     * ArrayListina.
     *
     * @param name Käyttäjän antama ruoka-annoksen nimi
     *
     * @return ArrayList, joka koostuu käyttäjän haluaman nimisen ruoka-annoksen
     * osista ja niiden määristä
     */
    public ArrayList<String> getPortionContentsInList(String name) {
        ArrayList<String> portionParts = new ArrayList<>();
        try {
            this.openConnection();
            PreparedStatement p = this.db.prepareStatement("SELECT I.name AS incr, C.amount AS gr from Incredients I, Portions P LEFT JOIN DishContents C ON I.id=C.incredient_id WHERE P.id=C.portion_id AND P.name=? GROUP BY I.name");
            p.setString(1, name);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                String parts = r.getString("incr") + ":" + r.getInt("gr") + "g";
                portionParts.add(parts);
            }
            this.closeConnection();
        } catch (SQLException e) {
            System.out.print("Ei onnistuttu luomaan palautettavaa taulua annoksen osista.");
        }
        return portionParts;
    }

    /**
     * Metodi lisää halutunnimisen uuden ruoka-annoksen ruoka-annoslistaan.
     *
     * @param name Käyttäjän antama ruoka-annoksen nimi
     *
     * @return totuusarvo siitä onnistuivatko metodin tietokantatoiminnot
     */
    public boolean addPortion(String name) {
        try {
            this.openConnection();
            PreparedStatement p = this.db.prepareStatement("INSERT INTO Portions (name) VALUES(?)");
            p.setString(1, name);
            p.executeUpdate();
            this.closeConnection();
            return true;
        } catch (SQLException e) {
            System.out.println("Ruoka-annoksen lisääminen epäonnistui.");
            return false;
        }
    }

    /**
     * Metodi poistaa halutunnimisen ruoka-annoksen ruoka-annoslistasta.
     *
     * @param name Käyttäjän antama ruoka-annoksen nimi
     *
     * @return totuusarvo siitä onnistuivatko metodin tietokantatoiminnot
     */
    public boolean deletePortion(String name) {
        System.out.println("Poistetaan ruoka-annosta " + name + ".");
        try {
            this.openConnection();
            PreparedStatement p = this.db.prepareStatement("DELETE FROM Portions WHERE name=?");
            p.setString(1, name);
            p.executeUpdate();
            this.closeConnection();
            return true;
        } catch (SQLException e) {
            System.out.println("Ruoka-annoksen poistaminen epäonnistui.");
            return false;
        }
    }

    /**
     * Metodi hakee tietokannan ruoka-ateriat -taulusta aterioiden nimet, ja palauttaa ne
     * stringinä aakkosjärjestyksessä omilla riveillään.
     *
     * @return String -merkkijono, jossa on tietokannan ruoka-ateriat -taulun aterian nimet riveittäin
     */
    public String getPortionNames() {
        try {
            this.openConnection();
            String portions = "";
            PreparedStatement p = this.db.prepareStatement("SELECT * FROM Portions ORDER BY name");
            ResultSet r = p.executeQuery();
            while (r.next()) {
                portions = portions + r.getString("name") + "\n";
            }
            this.closeConnection();
            return portions;
        } catch (SQLException e) {
            return "VIRHE: ei onnistuttu etsimään tauluja.";
        }
    }

    /**
     * Metodi palauttaa tietokannan ateriat -taulusta sen aterian id:n, jonka nimen se saa
     * parametrina.
     *
     * @param name nimi sille aterialle, jonka id halutaan
     *
     * @return sen aterian integer id -lukuarvo jonka nimen metodi sai parametrina
     */
    public int getPortionId(String name) {
        try {
            this.openConnection();
            PreparedStatement p = this.db.prepareStatement("SELECT id FROM Portions WHERE name=?");
            p.setString(1, name);
            ResultSet r = p.executeQuery();
            int id = r.getInt("id");
            this.closeConnection();
            return id;
        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     * Metodi palauttaa käyttäjän antaman nimisen aterian ruoka-aineet ja niiden määrät
     * Stringinä ryhmiteltynä samalle riville peräkkäin.
     *
     * @param name Käyttäjän antama ruoka-aterian nimi
     *
     * @return Merkkijono, jossa alussa on aterian nimi, jota seuraa sen ruoka-aineosat ja määrät.
     */
    public String getPortionContentsWithName(String name) {
        String portionContent = name + ":";
        try {
            this.openConnection();
            PreparedStatement p = this.db.prepareStatement("SELECT I.name AS incr, C.amount AS gr from Incredients I, Portions P LEFT JOIN DishContents C ON I.id=C.incredient_id WHERE P.id=C.portion_id AND P.name=? GROUP BY I.name");
            p.setString(1, name);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                portionContent = portionContent + " " + r.getString("incr") + "(" + r.getInt("gr") + "g)";
            }
            this.closeConnection();
            return portionContent;
        } catch (SQLException e) {
            return "VIRHE: ei onnistuttu etsimään annoksen sisältöä.";
        }
    }

    /**
     * Metodi palauttaa koko ruoka-annoksien sisältötaulun, jossa indeksinumerot
     * on korvattuna niiden tekstimuotoisilla nimillä.
     *
     * @return merkkijono, jossa seliterivin alla riveittäin koko ruoka-annoksien sisältötaulu
     */
    public String getDishContentToString() {
        String data = "annos, ruoka-aine , määrä(g):\n";
        try {
            this.openConnection();
            PreparedStatement p = this.db.prepareStatement("SELECT P.name AS pname, I.name AS iname, D.amount AS damount FROM DishContents D, Incredients I, Portions P WHERE I.id=D.incredient_id AND P.id=D.portion_id;");
            ResultSet r = p.executeQuery();
            while (r.next()) {
                data = data + "" + (r.getString("pname")) + " " + (r.getString("iname")) + " " + (r.getInt("damount")) + "\n";
            }
            this.closeConnection();
            return data;
        } catch (SQLException e) {
            return "VIRHE: ei onnistuttu hakemaan annostensisältödataa.";
        }
    }

    /**
     * Metodi poistaa ruoka-annoksen sisältötaulusta käyttäjän haluaman ruoka-aineen
     * koko määrän käyttäjän ilmoittamasta ruoka-annoksesta.
     *
     * @param portion Käyttäjän antama ruoka-annoksen nimi
     * @param incredient Käyttäjän antama ruoka-aineen nimi
     *
     * @return totuusarvo siitä onnistuivatko metodin tietokantatoiminnot
     */
    public boolean deletePortionPart(String portion, String incredient) {
        System.out.println("Poistetaan ruoka-annoksesta" + portion + " ruoka-ainetta " + incredient);
        int portionId = this.getPortionId(portion);
        int incredientId = dIncredients.getIncredientId(incredient);
        System.out.println("annosid " + portionId + " aineid :" + incredientId);
        try {
            this.openConnection();
            PreparedStatement p = this.db.prepareStatement("DELETE FROM DishContents WHERE portion_id=? AND incredient_id=?");
            p.setInt(1, portionId);
            p.setInt(2, incredientId);
            p.executeUpdate();
            this.closeConnection();
            return true;
        } catch (SQLException e) {
            System.out.print("Ruoka-aineen poistaminen epäonnistui.");
            return false;
        }
    }

    /**
     * Metodi lisää ruoka-annoksen sisältötauluun haluttuun ruoka-annokseen
     * halutun määrän haluttua ruoka-ainetta.
     *
     * @param portion Käyttäjän antama ruoka-annoksen nimi
     * @param incredient Käyttäjän antama ruoka-aineen nimi
     * @param amount Käyttäjän antama ruoka-aineen määrä ruoka-annoksessa grammoina
     *
     * @return totuusarvo siitä onnistuivatko metodin tietokantatoiminnot
     */
    public boolean addDishContents(int portion, int incredient, int amount) {
        try {
            this.openConnection();
            PreparedStatement p = this.db.prepareStatement("INSERT INTO DishContents (portion_id, incredient_id, amount) VALUES(?,?,?)");
            p.setInt(1, portion);
            p.setInt(2, incredient);
            p.setInt(3, amount);
            p.executeUpdate();
            this.closeConnection();
            return true;
        } catch (SQLException e) {
            System.out.println("Annoksen osan lisääminen epäonnistui.");
            return false;
        }
    }
    
    
}