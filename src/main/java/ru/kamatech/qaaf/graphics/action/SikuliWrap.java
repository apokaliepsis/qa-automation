package ru.kamatech.qaaf.graphics.action;
/**
 * Класс содержит методы для взаимодействия с приложением на основе опознавания графических изображений на экране
 */


import org.openqa.selenium.NoSuchElementException;
import org.sikuli.script.*;
import ru.kamatech.qaaf.properties.Properties;

public class SikuliWrap extends Properties {
    private static Screen screen;
    private Region region;
    private Location location;
    private Match match;
    private Finder finder;


    public static Screen screen() {
        if (screen == null) {
            screen = new Screen();
        }
        return screen;
    }

    /**
     * Метод проверяет, содержится ли указанное изображение на экране
     * @param imagePath - относительный путь изображения
     * @param sim - коэффициент идентичности изображения
     * @return возвращает true, если изображение присутствует на экране
     */
    public boolean isContainsElement(String imagePath, float sim){

        Pattern pattern = new Pattern(getPathFromResources(imagePath));
        if(sim==100){
            pattern.exact();
        }
        else{
            pattern.similar(sim);
        }
        return screen().exists(pattern) != null;
    }

    /**
     * Метод выполняет клик по центру указанного изображения
     * @param pathImage относительный путь изображения
     */
    public void clickByImage(String pathImage){

        try {
            //imageRecognition().screen().click(new Pattern(getScreensProps().get("FilesSection")));
            logger.info("Click by "+"\""+pathImage+"\"");

            screen().click(getPathFromResources(pathImage));
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }

    /**
     * Выполняется двойной клик по по центру указанного изображения
     * @param pathImage относительный путь изображения
     */
    public void doubleClickByImage(String pathImage){
        try {
            //imageRecognition().screen().click(new Pattern(getScreensProps().get("FilesSection")));
            logger.info("DoubleClick by "+"\""+pathImage+"\"");

            screen().doubleClick(getPathFromResources(pathImage));
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }

    /**
     * Метод выполняет клик по центру изображения с указанием коэффициента идентичности
     * @param pathImage относительный путь изображения
     * @param sim коэффициент идентичности
     */
    public void clickBySimilarImage(String pathImage, float sim){
        try {
            //imageRecognition().screen().click(new Pattern(getScreensProps().get("FilesSection")));
            logger.info("Click by "+"\""+pathImage+"\"");
            if(sim==100){
                screen().click(new Pattern(getPathFromResources(pathImage)).exact());

            }
            else{
                screen().click(new Pattern(getPathFromResources(pathImage)).similar(sim));

            }
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }

    /**
     * Метод выполняет двойной клик по центру изображения с указанием коэффициента идентичности
     * @param pathImage относительный путь изображения
     * @param sim коэффициент идентичности
     */
    public void doubleClickBySimilarImage(String pathImage, float sim){
        try {
            //imageRecognition().screen().click(new Pattern(getScreensProps().get("FilesSection")));
            logger.info("DoubleClick by "+"\""+pathImage+"\"");
            if(sim==100){
                screen().doubleClick(new Pattern(getPathFromResources(pathImage)).exact());

            }
            else{
                screen().doubleClick(new Pattern(getPathFromResources(pathImage)).similar(sim));

            }
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }

    /**
     * Реализация ожидания появления изображения
     * @param path относительный путь изображения
     * @param time время ожидания
     */
    public void waitImage(String path,int time){
        try {
            logger.info("Wait image "+"\""+path+"\"");
            screen().wait(getPathFromResources(path),time);
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }

    /**
     * Реализация ожидания появления изображения с указанием коэффициента идентичности
     * @param path относительный путь изображения
     * @param sim коэффициент идентичности
     * @param time время ожидания
     */
    public void waitImageSimilar(String path, float sim,int time){
        logger.info("Wait image "+"\""+path+"\"");
        try {
            if(sim==100){
                screen().wait(new Pattern(getPathFromResources(path)).exact());

            }
            else{
                screen().wait(new Pattern(getPathFromResources(path)).similar(sim));

            }
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }

    /**
     * Реализация ожидания исчезновения изображения
     * @param path относительный путь изображения
     * @param time время ожидания
     */
    public void waitImageVanish(String path,int time){
        logger.info("Wait image vanish: "+"\""+path+"\"");
        screen().waitVanish(getPathFromResources(path),time);
    }

    /**
     * Реализация ожидания исчезновения изображения с указанием коэффициента идентичности
     * @param path относительный путь изображения
     * @param sim коэффициент идентичности
     * @param time время ожидания
     */
    public void waitImageSimilarVanish(String path, float sim, int time){
        logger.info("Wait image vanish: "+"\""+path+"\"");
        if(sim==100){
            screen().waitVanish(new Pattern(getPathFromResources(path)).exact(),time);

        }
        screen().waitVanish(new Pattern(getPathFromResources(path)).similar(sim),time);
    }

    /**
     * Метод выполняет перемещение элементов из состояния source в target
     * @param source относительный путь изображения, которое указывает исходное состояние элемента
     * @param target относительный путь изображения, которое указывает конечное состояние элемента
     */
    public void dragDrop(String source, String target){
        try {
            logger.info("Drag drop "+"\""+source+"\"");
            screen().dragDrop(getPathFromResources(source), getPathFromResources(target));
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }

    /**
     * Метод выполняет клик по изображению targetImage, находящемуся в регионе изображения specificRegionImage
     * @param specificRegionImage относительный путь региона искомого изображения
     * @param targetImage относительный путь целевого изображения
     */
    public void clickByImageInRegion(String specificRegionImage, String targetImage){
        Region region= null;
        try {
            region = screen().find(new Pattern(getPathFromResources(specificRegionImage)));
        } catch (FindFailed findFailed) {
            logger.info(findFailed.getMessage());
        }
        try {
            assert region != null;
            region.click(getPathFromResources(targetImage));
        } catch (NoSuchElementException |FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }

    /**
     * Метод выполняет перемещение курсора на изображение
     * @param image относительный путь изображения
     */
    public void moveCursorOnImage(String image){

        try {
            screen().mouseMove(getPathFromResources(image));
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }

    /**
     * Выполнение правого клика на изображение
     * @param image относительный путь изображения
     */
    public void rightClickByImage(String image){

        try {
            screen().rightClick(getPathFromResources(image));
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            logger.warn(findFailed.getMessage());
            logger.warn("No image found on screen");
        }
    }


}
