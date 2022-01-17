

import org.junit.jupiter.api.Test;
import ru.kamatech.qaaf.database.JDBI;

import java.io.File;
import java.sql.*;
import java.util.*;

public class TestMethods extends Properties  {

    @Test
    public void testMethods(){

        JDBI jdbi = new JDBI();
        //DriverManager.getConnection("jdbc:h2:/data/test;AUTO_SERVER=TRUE");
        //jdbi.setDataBaseSettings("jdbc:sqlite:"+Properties.getPathFromResources("englishWords.db"),null,null);
        //jdbi.setDataBaseSettings("jdbc:h2:"+Properties.getPathFromResources("englishWordsH2.mv.db"),"admin","123456");

        jdbi.setDataBaseSettings("jdbc:h2:tcp://localhost/~/englishWordsH2","admin","123456");
        //jdbi.setDataBaseSettings("jdbc:h2:http://127.0.1.1:9092/~/englishWordsH2", "admin", "123456");
        System.out.println(jdbi.getAllRowsFromResponse(Collections.emptyList(),"show tables", false));

//        jdbi.createUpdate(Arrays.asList(2222, "qqqwwwww - ягода"),"insert into words (chatId, word) values (?,?)",false);
//        jdbi.createUpdate(Arrays.asList(43334333, "erwsefdfgdf - авпвапваввввв"),"insert into words (chatId, word) values (?,?)",false);
//        jdbi.createUpdate(Arrays.asList(34555555, "sfdsdfsdfdsfffff - орпавчмссчч"),"insert into words (chatId, word) values (?,?)",false);

        //jdbi.createUpdate(Collections.emptyList(),"insert into words (chatId, word) values (645435345, 'berry [бЭри] - ягода');",false);



    }

}
