package ru.kamatech.qaaf.database;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.CaseStrategy;
import org.jdbi.v3.core.mapper.MapMappers;
import org.jdbi.v3.core.statement.*;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import ru.kamatech.qaaf.properties.Properties;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBI extends Properties {
    private static Map<String,String> DB_CONFIG;
    private static Jdbi jdbi;
    private static Handle handle;
    private SqlLogger sqlLogger;

    public SqlLogger getSqlLogger() {
        if(sqlLogger==null){
            sqlLogger = new SqlLogger() {
                @Override
                public void logAfterExecution(StatementContext context) {
                    System.out.println("sql {"+context.getRenderedSql()+"}");
                    System.out.println("parameters {"+context.getBinding().toString()+"}");
                    System.out.println("timeTaken {"+context.getElapsedTime(ChronoUnit.MILLIS)+"} ms");
                }
            };
        }
        return sqlLogger;
    }

    public static Jdbi getJdbi() {
        return jdbi;
    }

    public static Handle getHandle() {
        return handle;
    }

    /**
     * Установка соединения
     * @param connection хост подключения к базе
     * @param user пользователь
     * @param password пароль
     */
    public void setDataBaseSettings(String connection, String user, String password){
        Map<String,String> map = new HashMap<>();
        map.put("DB_DRIVER","oracle.jdbc.driver.OracleDriver");
        //map.put("DB_DRIVER","org.h2.Driver");
        map.put("DB_USER",user);
        map.put("DB_PASSWORD",password);
        //map.put("DB_CONNECTION",switchDBKSMFR().setUrlConnection());
        map.put("DB_CONNECTION",connection);
        DB_CONFIG=map;
        createDBConnection();
    }

    /**
     * Создание соединения
     */
    private void createDBConnection() {
        if(DB_CONFIG.get("DB_CONNECTION").toLowerCase().contains("sqlite")){
            jdbi = Jdbi.create(DB_CONFIG.get("DB_CONNECTION")).installPlugin(new SQLitePlugin());

        }
        else{
            jdbi = Jdbi.create(DB_CONFIG.get("DB_CONNECTION"),DB_CONFIG.get("DB_USER"),DB_CONFIG.get("DB_PASSWORD"));
        }
        jdbi.getConfig(MapMappers.class).setCaseChange(CaseStrategy.UPPER);
        handle = JDBI.getJdbi().open();
        handle.setSqlLogger(getSqlLogger());


    }

    /**
     * Выполнение запроса и получение первой строки
     * @param parameters параметры в запросе для подстановки
     * @param textQuery текст запроса (если readQueryFromFile=true, то здесь передаём относительный путь к скрипту из ресурсов)
     * @param readQueryFromFile включение чтения из файла
     * @return возвращает map с ключами, которые соответствуют названиям столбцов, и значения map - значения столбцов
     */
    public Map<String,Object> getFirstRowFromResponse(List<?> parameters, String textQuery, boolean readQueryFromFile){
        Query query ;
        Map<String,Object> data = new HashMap<>();
        try{
            if(readQueryFromFile){
                query= handle.createQuery(new Properties().readSqlFileFromResources(textQuery));
            }
            else {
                query= handle.createQuery(textQuery);

            }
            for (int i = 0;i <parameters.size(); i++) {
                query.bind(i, parameters.get(i));
            }



            data = query.setMaxRows(1).mapToMap().first();

        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return data;
        }
        return data;
    }
    public List<Map<String, Object>> getAllRowsFromResponse(List<?> parameters, String textQuery, boolean readQueryFromFile){
        Query query ;
        List<Map<String, Object>> data = new ArrayList<>();
        try{
            if(readQueryFromFile){
                query= handle.createQuery(new Properties().readSqlFileFromResources(textQuery));
            }
            else {
                query= handle.createQuery(textQuery);

            }
            for (int i = 0;i <parameters.size(); i++) {
                query.bind(i, parameters.get(i));
            }


            data = query.mapToMap().list();

        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return data;
        }
        return data;

    }
    public void createUpdate(List<?> parameters, String textQuery, boolean readQueryFromFile){
        Update query ;
        try{
            if(readQueryFromFile){
                query= handle.createUpdate(new Properties().readSqlFileFromResources(textQuery));
            }
            else {
                query= handle.createUpdate(textQuery);
            }
            for (int i = 0;i <parameters.size(); i++) {
                query.bind(i, parameters.get(i));
            }
            query.execute();

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * Выполнение запроса функции и получение вывода
     * @param parameters параметры в запросе для подстановки
     * @param textQuery текст запроса (если readQueryFromFile=true, то здесь передаём относительный путь к скрипту из ресурсов)
     * @param readQueryFromFile включение чтения из файла
     * @return возвращает текст вывода функции
     */
    public String getResponseFunction(List<?> parameters, String textQuery, boolean readQueryFromFile){
        Query query ;
        String response = null;
        try{
            if(readQueryFromFile){
                query= handle.createQuery(new Properties().readSqlFileFromResources(textQuery));
            }
            else {
                query= handle.createQuery(textQuery);
            }
            for (int i = 0;i <parameters.size(); i++) {
                query.bind(i, parameters.get(i));
            }
            response = query.setMaxRows(1).mapTo(String.class).first();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return response;
        }
        return response;
    }

    public void executeProcedure(List<?> parameters, String textQuery){
        Call query = handle.createCall(textQuery);
        for (int i = 0;i <parameters.size(); i++) {
            query.bind(i, parameters.get(i));
        }
        query.invoke();

    }

}
