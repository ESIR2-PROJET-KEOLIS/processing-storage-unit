package com.treatmentunit.database;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.*;
import org.junit.Assert.*;

import static org.dbunit.Assertion.assertEquals;


import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.postgresql.util.PSQLException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class DatabaseBindingTest {
    public static final String JDBC_DRIVER = org.h2.Driver.class.getName();
    public static final String JDBC_URL = "jdbc:h2:mem:default;MODE=LEGACY;DB_CLOSE_DELAY=-1;init=runscript from 'classpath:dbunit/schema.sql'";
    public static final String USER = "sa";
    public static final String PASSWORD = "";
    private static IDatabaseTester tester = null;
    private DatabaseBinding dbb;
    private Connection connection;

    @BeforeClass
    public static void setUp() throws Exception {
        tester = initDatabaseTester();
    }

    private static IDatabaseTester initDatabaseTester() throws Exception {
        JdbcDatabaseTester tester = new JdbcDatabaseTester(JDBC_DRIVER, JDBC_URL, USER, PASSWORD);
        tester.setDataSet(initDataSet());
        tester.setSetUpOperation(DatabaseOperation.REFRESH);
        tester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        return tester;
    }

    private static IDataSet initDataSet() throws Exception {
        try (InputStream is = DatabaseBindingTest.class.getClassLoader().getResourceAsStream("dbunit/data.xml")) {
            return new FlatXmlDataSetBuilder().build(is);
        }
    }

    @Before
    public void setup() throws Exception {
        tester.onSetup();
        connection = tester.getConnection().getConnection();
        dbb = new DatabaseBinding();
        dbb.setStatement(connection.createStatement());
        dbb.setCon(connection);
    }

    @After
    public void tearDown() throws Exception {
        tester.onTearDown();
    }

    /*@Test
    public void givenDataSet_whenSelect_thenFirstTitleIsGreyTShirt() throws Exception {
        ResultSet rs = connection.createStatement().executeQuery("select * from ITEMS where id = 1");

        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("title")).isEqualTo("Grey T-Shirt");
    }*/

    @Test
    public void requestInsertTest() throws Exception {
        String query = "INSERT INTO TEST (test_id, test_info_1, test_info_2) VALUES ('2', '2info1', '2info2' )";
        dbb.requestInsert(query);

        query = "SELECT * FROM TEST WHERE test_id='2' AND test_info_1='2info1' AND test_info_2='2info2'";
        ResultSet rs =  tester.getConnection().getConnection().createStatement().executeQuery(query);

        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("test_id")).isEqualTo("2");
        assertThat(rs.getString("test_info_1")).isEqualTo("2info1");
        assertThat(rs.getString("test_info_2")).isEqualTo("2info2");
    }
    /*@Test(expected = org.opentest4j.AssertionFailedError.class )
    public void requestInsertTest_exeptio() throws Exception {
        String query = "INSERT INTO JeNeSuisPasLa (test_id, test_info_1, test_info_2) VALUES ('2', '2info1', '2info2' )";
        dbb.requestInsert(query);

        query = "SELECT * FROM TEST WHERE test_id='2' AND test_info_1='2info1' AND test_info_2='2info2'";
        ResultSet rs =  tester.getConnection().getConnection().createStatement().executeQuery(query);

        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("test_id")).isEqualTo("2");
        assertThat(rs.getString("test_info_1")).isEqualTo("2info1");
        assertThat(rs.getString("test_info_2")).isEqualTo("2info2");
    }*/


    @Test (expected = SQLException.class)
    public void RequestFetchNColumnsTestInvalidQuery() throws SQLException, InterruptedException {
        String query = "SELECT * FROM JeSuisPasLa";
        dbb.requestFetchNColumns(query);
    }

    @Test
    public void RequestFetchNColumnsTest() throws SQLException, InterruptedException {
        String query = "SELECT * FROM TEST";
        ArrayList<ArrayList<String>>  res = dbb.requestFetchNColumns(query);
        Assert.assertEquals(res.get(0).get(0), "1");
        Assert.assertEquals(res.get(0).get(1), "1info1");
        Assert.assertEquals(res.get(0).get(2), "1info2");


    }

    @Test
    public void requestFetchSingleValueTest() throws Exception {
        String query = "SELECT * FROM TEST";
        String  res = dbb.requestFetchSingleValue(query);
        ResultSet rs =  tester.getConnection().getConnection().createStatement().executeQuery(query);
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("test_id").equals(res));
    }
   @Test (expected = SQLException.class)
    public void requestFetchSingleValueTestInvalidQuery() throws SQLException, InterruptedException {
        String query = "SELECT * FROM JeSuisPasLa";
        dbb.requestFetchSingleValue(query);
    }

}

