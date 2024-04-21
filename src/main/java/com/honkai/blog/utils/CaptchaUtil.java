package com.honkai.blog.utils;

import java.io.IOException;
import java.util.Random;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import jakarta.servlet.http.HttpServletResponse;

public class CaptchaUtil {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final int LENGTH = 4;
    private static final Random random = new Random();

    public static String createCaptchaImage(HttpServletResponse response) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String captchaText = generateRandomText(LENGTH);
        int x = 10;
        for (char c : captchaText.toCharArray()) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawString(String.valueOf(c), x, 20 + random.nextInt(20));
            x += 20;
        }
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 20; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(12);
            int y2 = random.nextInt(12);
            g.drawLine(x1, y1, x1 + x2, y1 + y2);
        }
        g.dispose();
        response.setContentType("image/jpeg");
        try {
            ImageIO.write(image, "JPEG", response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return captchaText;
    }

    private static String generateRandomText(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
