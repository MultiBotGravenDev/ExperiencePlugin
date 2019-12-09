package fr.gravendev.images;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageBuilder {
    private static final String FONT_NAME = "Regular";
    private static final int FONT_STYLE = Font.BOLD;
    private static final int FONT_SIZE = 20;

    private BufferedImage image;
    private Graphics graphics;

    public ImageBuilder(BufferedImage image) {
        this.image = image;
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        graphics = image.getGraphics();

        Font font = new Font(FONT_NAME, FONT_STYLE, FONT_SIZE);
        graphics.setFont(font);

        graphics.setColor(new Color(33, 33, 33));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        graphics.setColor(new Color(48, 48, 48));
        ((Graphics2D) graphics).setStroke(new BasicStroke(70));
        graphics.drawRoundRect(0, 0, image.getWidth(), image.getHeight(), 100, 100);

    }

    public void drawString(String text, Color color, int x, int y, int size) {

        Font font = new Font(FONT_NAME, FONT_STYLE, size);
        graphics.setFont(font);

        graphics.setColor(color);
        graphics.drawString(text, x, y);
    }

    public void drawImage(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
        BufferedImage image = ImageIO.read(connection.getInputStream());
        graphics.drawImage(Thumbnails.of(image).size(200, 200).asBufferedImage(), 50, 50, null);
    }

    public void drawProgress(Color color, int currentXP, int xpToLevelUp) {
        int percantOfLevel = currentXP * 100 / xpToLevelUp;
        ((Graphics2D) graphics).setStroke(new BasicStroke(10));
        graphics.setColor(Color.black);
        graphics.drawRoundRect(275, 200, 600, 50, 20, 20);
        graphics.setColor(color);
        graphics.fillRoundRect(280, 205, percantOfLevel * 6, 40, 10, 10);
    }


    public InputStream toInputStream() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ImageIO.write(image, "png", os);

        byte[] byteArray = os.toByteArray();

        return new ByteArrayInputStream(byteArray);
    }
}