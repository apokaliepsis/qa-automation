package ru.kamatech.qaaf.database;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import ru.kamatech.qaaf.database.com.ibatis.common.jdbc.ScriptRunner;
import org.apache.tika.io.IOUtils;
import ru.kamatech.qaaf.properties.Properties;

import java.io.*;
import java.sql.*;
import java.util.*;


public class JDBC extends Properties {

    private Connection dbConnection=null;
    public static Map<String,String> DB_CONFIG;

    /**
     * Метод устанавливает соединение с базой данных Oracle
     * @param user логин пользователя бд
     * @param password пароль
     * @param connection хост соединения
     */
    public void setDataBaseSettings(String connection, String user, String password){
        Map<String,String> map = new HashMap<>();
        map.put("DB_DRIVER","oracle.jdbc.driver.OracleDriver");
        map.put("DB_USER",user);
        map.put("DB_PASSWORD",password);
        //map.put("DB_CONNECTION",switchDBKSMFR().setUrlConnection());
        map.put("DB_CONNECTION",connection);
        DB_CONFIG=map;
        createDBConnection();
    }
    /**
     * Метод создаёт объект соединения
     * @return возвращает объект соединения
     */
    private Connection getDbConnection() {
        if(dbConnection == null){		//если объект еще не создан
            dbConnection = createDBConnection();	//создать новый объект
        }
        return dbConnection;
    }

    /**
     * Устанавливает значения для объекта соединения
     * @param dbConnection значение для объекта соединения
     */
    private void setDbConnection(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
     * Метод создаёт соединение с бд
     * @return возвращает объект соединения
     */
    public Connection createDBConnection(/*String date*/) {
        try {
            Class.forName(DB_CONFIG.get("DB_DRIVER"));
            //Class.forName("org.h2.Driver");
/*            if(DB_CONFIG.get("DB_CONNECTION").toLowerCase().contains("sqlite")){
                setDbConnection(dbConnection = DriverManager.getConnection(DB_CONFIG.get("DB_CONNECTION")));
            }*/
            setDbConnection(dbConnection = DriverManager.getConnection(DB_CONFIG.get("DB_CONNECTION"),
                    DB_CONFIG.get("DB_USER"), DB_CONFIG.get("DB_PASSWORD")));
            return getDbConnection();

        } catch (SQLException|ClassNotFoundException e) {
            System.out.println("Error: Проблема соединения с базой данных");
            e.printStackTrace();
        }
        return getDbConnection();
    }

    /**
     * Возвращает одну строку из ответа запроса бд
     * @param querySQL текст запроса в бд
     * @param column навзвание столбца
     * @return строка из ответа запроса бд
     */
    public String querySQL(String querySQL,String column) {
        String value="";
        //String querySQL = "Select* from CFG_PARAMS";
        try (Connection connection = getDbConnection();
             Statement statement = connection.createStatement()) {
            //выполнить SQL запрос
/*            Reader reader = new BufferedReader(
                    new FileReader(getPathFromResources(querySQL)));*/
            System.out.println(querySQL);

            statement.executeQuery(String.valueOf(querySQL));
            //printingResultSet(rset);
            while (statement.getResultSet().next()){
                value=statement.getResultSet().getString(column).trim();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка выполнения SQL-запроса: " + e.getMessage());
        }

        return value;
    }
    /**
     * Выполнение скрипта (обновление, вставка или удаление) с параметрами.
     * @param pathScript путь к файлу скрипта
     * @param parameters массив параметров со значениями
     */
    public void executeUpdateWithParameters(String pathScript, Object[] parameters) {

        //String querySQL = "Select* from CFG_PARAMS";
        try (Connection connection = createDBConnection();
             Statement statement = connection.createStatement()) {
            //выполнить SQL запрос
            Reader reader = new BufferedReader(
                    new FileReader(getPathFromResources(pathScript)));
            PreparedStatement pstm=connection.prepareStatement(IOUtils.toString(reader));
            for (int i = 0,j=1; i <parameters.length & j<=parameters.length ; i++,j++) {
                pstm.setObject(j,parameters[i]);

            }

            //System.out.println("Текст запроса: "+ ((oracle.jdbc.driver.OracleStatement) ps).getOriginalSql());
            //((OraclePreparedStatementWrapper) preparedStatement).getOriginalSql();
            pstm.executeUpdate();
            connection.commit();
/*            ResultSetMetaData rsmd = rset.getMetaData();
            System.out.println("----------------------------------");
            int columnsNumber = rsmd.getColumnCount();
            while (rset.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rset.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));

                }
                System.out.println(" ");
            }*/

        } catch (SQLException | IOException e) {
            System.out.println("Ошибка выполнения SQL-запроса: " + e.getMessage());
        }

    }

    /**
     * Получение данных из ответа БД. Разбиваем на соответствие "название колонки - ячейка". Помещаем в map, затем каждую map в list
     * @param query путь к скрипту
     * @param limitRows максимальное количество строк
     * @param parameters параметры для вставки в запрос
     * @return возвращает список строк, где в каждой строке - map
     */
    public List<HashMap<String, String>> getListRowsWithParameters(String query, Object limitRows, /*String column,*/ Object[] parameters, boolean readFile) {
        //String value="";
        List<HashMap<String, String>> listData = new ArrayList<>();
        PreparedStatement pstm;
        Connection connection = createDBConnection();
        try {
            if(readFile){
                Reader reader = new BufferedReader(
                        new FileReader(getPathFromResources(query)));
                pstm=connection.prepareStatement(IOUtils.toString(reader));
            }
            else{
                pstm=connection.prepareStatement(query);

            }

            for (int i = 0,j=1; i <parameters.length & j<=parameters.length ; i++,j++) {
                if (parameters[i] == null) {
                    pstm.setNull(j, Types.INTEGER);
                }

                    else{
                    pstm.setObject(j, parameters[i]);
                    }



            }
            int countColumns = pstm.getMetaData().getColumnCount();
            if(limitRows!=null){
                pstm.setMaxRows((Integer) limitRows);
            }

            ResultSetMetaData md = pstm.getMetaData();

            pstm.executeQuery();
            while (pstm.getResultSet().next()){
                HashMap<String, String> row = new HashMap<>(countColumns);
                for(int i=1; i<=countColumns; ++i){
                    String columnName=md.getColumnName(i);
                    String value = String.valueOf(pstm.getResultSet().getObject(columnName));
                    if (!value.equals("null")){
                        row.put(columnName, value);
                    }
                    else{
                        row.put(columnName, null);
                    }

                }
                listData.add(row);
                //value=pstm.getResultSet().getString(column).trim();

            }
        } catch (SQLException e) {
            System.err.println("Failed to Execute " + query
                    + " The error is " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listData;
    }

    /**
     * Получает значение ячейки по столбцу с помощью выполнения запроса, взятого из файла скрипта
     * @param pathScript путь к файлу запроса
     * @param column название столбца
     * @param parameters массив параметров со значениями
     * @return значение ячейки по столбцу
     */
    public String getValueByColumnNameWithParameters(String pathScript, String column, String[] parameters) {
        // Create  Connection
        String value="";

        PreparedStatement pstm;
        Connection connection = createDBConnection();
        try {

            Reader reader = new BufferedReader(
                    new FileReader(getPathFromResources(pathScript)));
            pstm=connection.prepareStatement(IOUtils.toString(reader));
            for (int i = 0,j=1; i <parameters.length & j<=parameters.length ; i++,j++) {
                pstm.setString(j,parameters[i]);

            }
            pstm.executeQuery();
            while (pstm.getResultSet().next()){
                value=pstm.getResultSet().getString(column).trim();
            }
        } catch (SQLException e) {
            System.err.println("Failed to Execute " + pathScript
                    + " The error is " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("Значение указанной колонки: "+value);
        return value;
    }

    /**
     * Чтение файла и выполнение скрипта. Получение значения по названию столбца.
     * @param scriptFilePath путь к файлу скрипта
     * @param column название столбца
     * @return значение ячейки по столбцу
     */
    public String getValueByColumnNameViaRunScript(String scriptFilePath, String column){
        // Create  Connection
        String value="";
        Connection con;

        Statement stmt = null;
        ResultSet rset=null;

        try {
            // Initialize object for ScripRunner
            con=getDbConnection();
            con.createStatement();

            ScriptRunner sr = new ScriptRunner(con);

            // Give the input file to Reader
            Reader reader = new BufferedReader(
                    new FileReader(getPathFromResources(scriptFilePath)));
            sr.runScript(reader);

            while (sr.getResultSet().next()){
                value=sr.getResultSet().getString(column).trim();
            }
            //System.out.println(sr.getResultSet().getRow());


        } catch (Exception e) {
            System.err.println("Failed to Execute " + scriptFilePath
                    + " The error is " + e.getMessage());

        }


        System.out.println("Значение указанной колонки: "+value);
        return value;
    }

    /**
     * Чтение файла и выполнение скрипта. Получение строки
     * @param scriptFilePath путь к файлу скрипта
     * @param column названия колонок
     * @return map, в которой сохранены значения ячеек указаных столбцов
     */
    public HashMap<String,String> getValuesToMapViaRunScript(String scriptFilePath, String[] column){
        HashMap<String, String> hashMap=new HashMap<>();

        // Create  Connection
        String value="";
        Connection con;


        try {
            // Initialize object for ScripRunner
            con=getDbConnection();
            con.createStatement();

            ScriptRunner sr = new ScriptRunner(con);

            // Give the input file to Reader
            Reader reader = new BufferedReader(
                    new FileReader(getPathFromResources(scriptFilePath)));
            sr.runScript(reader);

            while (sr.getResultSet().next()){
                //value= sr.getResultSet().getString();
                for (String s:column){
                    hashMap.put(s,sr.getResultSet().getString(s));
                }

            }
            //System.out.println(sr.getResultSet().getRow());

        } catch (Exception e) {
            System.err.println("Failed to Execute " + scriptFilePath
                    + " The error is " + e.getMessage());

        }
        System.out.println(Arrays.asList(hashMap));
        return hashMap;
    }

    /**
     * Выполнение процедуры
     * @param procedureQuerySQL путь к скрипту
     * @throws SQLException
     */
    public void executeProcedure(String procedureQuerySQL) throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection;
        Statement statement = null;
        ResultSet rset;

        //String querySQL = "Select* from CFG_PARAMS";
        try {
            connection = createDBConnection();
            statement = connection.createStatement();
            //выполнить SQL запрос
//            callableStatement = dbConnection.prepareCall(procedureQuerySQL);
//            callableStatement.execute();
//            rset = (ResultSet) callableStatement.getObject(1);
//            printingResultSet(rset);

            callableStatement = dbConnection.prepareCall(procedureQuerySQL);
            if(callableStatement.execute()){
                rset=callableStatement.getResultSet();
                printingResultSet(rset);

            }
        } catch (SQLException e) {
            System.out.println("Ошибка выполнения SQL-запроса: "+e.getClass()+": " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
//            if (rset != null) {
//                rset.close();
//            }

            if (callableStatement != null) {
                callableStatement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }

        }


    }

    /**
     * Выполнение процедуры с параметрами
     * @param procedureQuerySQL путь к скрипту
     * @param parameters параметры для подстановки в запросе
     * @throws SQLException
     */
    public void executeProcedureWithParameters(String procedureQuerySQL, Object[] parameters) throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection;
        Statement statement = null;
        ResultSet rset;

        //String querySQL = "Select* from CFG_PARAMS";
        try {
            connection = createDBConnection();
            statement = connection.createStatement();
            //выполнить SQL запрос



            callableStatement = dbConnection.prepareCall(procedureQuerySQL);
            for (int i = 0,j=1; i <parameters.length & j<=parameters.length ; i++,j++) {

                callableStatement.setObject(j,parameters[i]);

            }
            if(callableStatement.execute()){
                rset=callableStatement.getResultSet();
                printingResultSet(rset);

            }
        } catch (SQLException e) {
            System.out.println("Ошибка выполнения SQL-запроса: "+e.getClass()+": " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
//            if (rset != null) {
//                rset.close();
//            }

            if (callableStatement != null) {
                callableStatement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }

        }


    }

    /**
     * Вывод результата запроса
     * @param rset
     * @throws SQLException
     */
    private void printingResultSet(ResultSet rset) throws SQLException {
        ResultSetMetaData rsmd = rset.getMetaData();
        System.out.println("----------------------------------");
        int columnsNumber = rsmd.getColumnCount();
        //if(rset!=null)
        while (rset.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = rset.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));

            }
            System.out.println(" ");
        }
        //rset.first();
        rset.beforeFirst();
    }

    /**
     * Вывод результата запроса по названию столбца
     * @param rset
     * @throws SQLException
     */
    private void printingResultSetByNameColumn(ResultSet rset) throws SQLException {
        ResultSetMetaData rsmd = rset.getMetaData();
        System.out.println("----------------------------------");
        int columnsNumber = rsmd.getColumnCount();
        //if(rset!=null)
        while (rset.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = rset.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));

            }
            System.out.println(" ");
        }
        //rset.first();
        rset.beforeFirst();
    }

    /**
     * Получение количества строк
     * @param rset
     * @return количество строк
     * @throws SQLException
     */
    private int getCountRows(ResultSet rset) throws SQLException {
        ResultSetMetaData rsmd = rset.getMetaData();
        //int columnsNumber = rsmd.getColumnCount();
        int countRows=0;
        while (rset.next()) {
            countRows++;
        }

        rset.beforeFirst();
        return countRows;
    }

    /**
     * Выполнение функции в бд
     * @param pathScript путь к скрипту
     * @return возвращает результат выполнения функции
     * @throws SQLException
     */
    public String executeFunctionDB(String pathScript) throws SQLException {

        Reader reader;
        PreparedStatement pstmt =null;
        try {
            reader = new BufferedReader(
                    new FileReader(getPathFromResources(pathScript)));
            pstmt = getDbConnection().prepareStatement(IOUtils.toString(reader));

        } catch (IOException e) {
            e.printStackTrace();
        }


        //pstm=connection.prepareStatement(IOUtils.toString(reader));
        ResultSet rs = pstmt.executeQuery();

        String result=null;
        while(rs.next()) {
            result= rs.getString(1);

        }
            return result;


    }

    /**
     * Получение списка названий таблиц из скрипта
     * @param scriptPath относительный путь скрипта с запросом
     * @return список названий таблиц, найденных в запросе
     */
    public List<String> getTableNamesFromScript(String scriptPath){
        File file = new File(getPathFromResources(scriptPath));
        StringBuilder fileContents = new StringBuilder((int)file.length());

        try (Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + System.lineSeparator());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        net.sf.jsqlparser.statement.Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(String.valueOf(fileContents));
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }

        Select selectStatement = (Select) statement;
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableList = tablesNamesFinder.getTableList((net.sf.jsqlparser.statement.Statement) selectStatement);
        System.out.println(tableList);
        return tableList;
    }

}