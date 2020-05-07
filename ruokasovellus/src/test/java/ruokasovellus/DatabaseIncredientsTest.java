/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruokasovellus;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assert;



/**
 *
 * @author AnssiKetomäki
 */
public class DatabaseIncredientsTest {
    
    public Database kanta;
    public DatabaseIncredients Dincr;
    public DatabasePortions Dport;
    public DatabaseDiary Ddiar;
    public DateTimeFormatter date;
    final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    
    public DatabaseIncredientsTest() throws SQLException {
        kanta = new Database();
        Dincr = new DatabaseIncredients(kanta);
        Dport = new DatabasePortions(kanta, Dincr);
        Ddiar = new DatabaseDiary(kanta, Dport);
        System.setOut(new PrintStream(outContent));

    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        kanta.createTables();
    }    
    
    @After
    public void tearDown() {
        kanta.dropTables();
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void addDeleteAndListIncredientsMethodsWork() throws SQLException {
        
        Dincr.addIncredient("kaurahiutale", 3620, 540, 140, 75);
        Dincr.addIncredient("oliiviöljy", 9000, 0, 0, 1000);
        assertEquals("Ruoka-aineslista \n" +
        "(Nimike: energiaa(kcal), hiilihydraattia, proteiinia, rasvaa (g/100g))\n" +
        "kaurahiutale: 362.0(kcal), h:54.0, p:14.0, r:7.5\n" + 
        "oliiviöljy: 900.0(kcal), h:0.0, p:0.0, r:100.0", Dincr.listIncredientsToString());
        Dincr.deleteIncredient("kaurahiutale");
        Dincr.deleteIncredient("oliiviöljy");
        assertEquals("Ruoka-aineslista \n" +
        "(Nimike: energiaa(kcal), hiilihydraattia, proteiinia, rasvaa (g/100g))", Dincr.listIncredientsToString());
    
    }
    @Test
    public void incredientNamesAreUniqueAndListIncredientsToStringWorks() throws SQLException {
        
        Dincr.addIncredient("kaurahiutale", 3620, 540, 140, 75);
        Dincr.addIncredient("kaurahiutale", 1167, 200, 40, 23);
        Dincr.addIncredient("kaurahiutale", 116, 20, 4, 2);
        Dincr.addIncredient("oliiviöljy", 9000, 0, 0, 1000);
        Dincr.addIncredient("oliiviöljy", 1117, 200, 4460, 423);
        Dincr.addIncredient("oliiviöljy", 1167, 240, 440, 4553);
        assertEquals("Ruoka-aineslista \n" +
        "(Nimike: energiaa(kcal), hiilihydraattia, proteiinia, rasvaa (g/100g))\n" +
        "kaurahiutale: 362.0(kcal), h:54.0, p:14.0, r:7.5\n" + 
        "oliiviöljy: 900.0(kcal), h:0.0, p:0.0, r:100.0", Dincr.listIncredientsToString());
    }
    @Test
    public void getIncredientIdReturnsInt() throws SQLException {
        Dincr.addIncredient("kaurahiutale", 3620, 540, 140, 75);
        Dincr.addIncredient("puolukka", 560, 89, 5, 7);
        assertEquals(1, Dincr.getIncredientId("kaurahiutale"));
        assertEquals(2, Dincr.getIncredientId("puolukka"));
        Dincr.deleteIncredient("kaurahiutale");
        Dincr.deleteIncredient("puolukka");
        }
    @Test
    public void getIncredientDataIdReturnsData() throws SQLException {
        Dincr.addIncredient("kaurahiutale", 3620, 540, 140, 75);
        int [] idata = new int [4];
        idata [0] = 3620;
        idata [1] = 540;
        idata [2] = 140;
        idata [3] = 75;
        int [] kdata = Dincr.getIncredientDataInInt("kaurahiutale");
        assertEquals(idata [0], kdata [0]);
        assertEquals(idata [1], kdata [1]);
        assertEquals(idata [2], kdata [2]);
        assertEquals(idata [3], kdata [3]);
        Dincr.deleteIncredient("kaurahiutale");
    }
    @Test
    public void databaseCatchesErrorInCaseOneAppears() throws SQLException {
        kanta.dropTables();
        assertFalse(Dincr.addIncredient("kaurahiutale", 220, 20, 20, 20));
        assertFalse(Dincr.deleteIncredient("kaurahiutale"));
        assertEquals(-1, Dincr.getIncredientId("kaurahiutale"));
        kanta.createTables();
    }
    @Test
    public void listIncredientsToStringCatchesError() throws SQLException {
        kanta.dropTables();
        assertEquals("VIRHE: Ruoka-aineiden listaaminen epäonnistui.", Dincr.listIncredientsToString());
        kanta.createTables();
    }
    @Test
    public void getIncredientDataInIntCatchesError() throws SQLException {
        kanta.dropTables();
        Dincr.getIncredientDataInInt("kaurahiutale");
        assertEquals("Ei voitu palauttaa päiväkirjadataa int-listana.", outContent.toString());
        kanta.createTables();
    }
}