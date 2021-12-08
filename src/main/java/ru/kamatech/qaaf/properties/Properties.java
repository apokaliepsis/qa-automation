package ru.kamatech.qaaf.properties;

import com.google.common.io.Files;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Класс описывает работу с файлами ru.kamatech.qa.properties
 */
public class Properties  {
    protected final Logger logger = LogManager.getLogger(this.getClass().getSimpleName());

    /**
     * Метод получает абсолютный путь файла, который находится в каталоге resources
     * @param path относительный путь
     * @return абсолютный путь файла
     */
    public static String getPathFromResources(String path){

        //return Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)).getFile();
        //return Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)).getPath();

/*         File file = null;
        String resource = "/"+path;
        URL res = getClass().getResource(resource);
        if (res.getProtocol().equals("jar")) {
            try {
                InputStream input = getClass().getResourceAsStream(resource);
                file = File.createTempFile("tempfile", ".tmp");
                OutputStream out = new FileOutputStream(file);
                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                out.close();
                file.deleteOnExit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            //this will probably work in your IDE, but not from a JAR
            file = new File(res.getFile());
        }

        if (file != null && !file.exists()) {
            throw new RuntimeException("Error: File " + file + " not found!");
        }

        return String.valueOf(file.toPath());*/

        File file=null;
        String resource = "/"+path;
        URL res = Properties.class.getResource(resource);
        if (res!=null && res.getProtocol().equals("jar")) {
                try {
                    InputStream input = Properties.class.getResourceAsStream(resource);
                    String fileName = FilenameUtils.getName(path);
                    //String extensionFile=FilenameUtils.getExtension(path);

                    //file = File.createTempFile(fileName,"."+extensionFile);
                    file = new File(Files.createTempDir(), fileName);

                    OutputStream out = new FileOutputStream(file);
                    int read;
                    byte[] bytes = new byte[1024];

                    while ((read = input.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    out.close();
                    file.deleteOnExit();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        else {
            //this will probably work in your IDE, but not from a JAR
                file = new File(res.getFile());
        }

        if (!file.exists()) {
            throw new RuntimeException("Error: File " + file + " not found!");
        }
        return String.valueOf(file.toPath());
        //return this.getClass().getResource(path).getPath();

/*        String pathFile = Properties.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath=null;
        try {
            decodedPath = URLDecoder.decode(pathFile, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodedPath;*/
    }


    /**
     * Метод выполняет консольную команду
     * @param command текст консольной команды
     */
    protected String startProcess(String command){
        Process process;
        String text = "";
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor(120, TimeUnit.SECONDS);
            if(process.isAlive()) {
                logger.info("Kill process "+command);
                process.destroy();

            }
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\\A");
            text = scanner.hasNext() ? scanner.next() : "";

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return text.replaceAll("[\\n\\t ]", "").trim();
    }
    public String readSqlFileFromResources(String relativePath){
        BufferedReader br = null;
        String textFile = null;
        try {

            br = new BufferedReader(new FileReader(getPathFromResources(relativePath)));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                if(line.contains("--")){
                    line=line.substring(0, line.indexOf("--"));
                }
                else if(line.contains("//")){
                    line=line.substring(0, line.indexOf("//"));
                }
                sb.append(line);
                sb.append(System.lineSeparator());

                line = br.readLine();
            }
            textFile = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return textFile;
    }

}
