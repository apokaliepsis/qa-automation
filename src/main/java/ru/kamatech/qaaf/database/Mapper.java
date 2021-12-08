package ru.kamatech.qaaf.database;

import com.sun.jna.platform.win32.Netapi32Util;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Mapper {
    @ColumnName("CHATID")
    private String column1;
    @ColumnName("WORD")
    private String column2;
    public Mapper() {
    }
}
