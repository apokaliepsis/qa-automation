package ru.kamatech.qaaf.video.record;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.monte.media.AudioFormatKeys.EncodingKey;
import static org.monte.media.AudioFormatKeys.FrameRateKey;
import static org.monte.media.AudioFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.AudioFormatKeys.MIME_AVI;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.AudioFormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.*;

public class VideoRecord extends ScreenRecorder {
    public static ScreenRecorder screenRecorder;
    public String name;
    private static final long MAX_DURATION_DEFAULT_MS = 90*60*1000;

    public VideoRecord(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                            Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder, String name)
            throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        this.name = name;

    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {

        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        return new File(movieFolder,
                name + "-" + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));

    }

    public static void startRecording(String videoName, String pathVideo)  {
        File file = new File(pathVideo);

        if(file.exists()){

            File[] listFiles = file.listFiles();
            int daysBack=2;
            long purgeTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000);
            for(File listFile : listFiles) {
                if(listFile.lastModified() < purgeTime) {
                    if(!listFile.delete()) {
                        System.err.println("Unable to delete file: " + listFile);
                    }
                }
            }
        }
        else{
            file.mkdir();
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        Rectangle captureSize = new Rectangle(0, 0, width, height);

        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice()
                .getDefaultConfiguration();

        try {
            screenRecorder = new VideoRecord(gc, captureSize,
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                            Rational.ONE, QualityKey, 0.7f, KeyFrameIntervalKey, 30*60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "white", FrameRateKey, new Rational(30, 1)),
                    null, file, videoName);
            screenRecorder.setMaxRecordingTime(MAX_DURATION_DEFAULT_MS);
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }

        try {
            screenRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void stopRecording()  {
        try {
            screenRecorder.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}