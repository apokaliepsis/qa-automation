package ru.kamatech.qaaf.screenshot;

import io.qameta.allure.Attachment;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Screenshot  {

    public byte[] captureScreenShot() {
        try {
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
        return "Attachment Content Empty, can't create ru.kamatech.qa.screenshot".getBytes();
    }
    @Attachment(value = "Page ru.kamatech.qa.screenshot", type = "image/png")
    public byte[] saveScreenshot(byte[] screenShot) {
        return screenShot;
    }
}
