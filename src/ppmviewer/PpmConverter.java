package ppmviewer;

import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.Scanner;

public class PpmConverter {
    public static WritableImage convert(File ppmFile) {
        try (Scanner scanner = new Scanner(ppmFile)) {
            // Read P3 header
            if (!scanner.hasNext() || !scanner.next().equals("P3")) {
                System.err.println("P3 header not found");
                return null;
            }

            // Read width and height
            int width, height;
            if (!scanner.hasNextInt()) {
                System.err.println("Width not found");
            }
            width = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                System.err.println("Height not found");
            }
            height = scanner.nextInt();

            // Read color range (only support 255 right now)
            int colorRange;
            if (!scanner.hasNextInt()) {
                System.err.println("Color range not found");
            }
            colorRange = scanner.nextInt();

            // Create the buffer for the pixels
            IntBuffer intBuffer = IntBuffer.allocate(width * height);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int pixel = 0;
                    for (int k = 0; k < 3; k++) {
                        if (!scanner.hasNext()) {
                            System.err.println("R, G, or B value missing from pixel in ppm file");
                            return null;
                        }
                        // Shift in byte to pixel
                        pixel <<= 8;
                        pixel |= scanner.nextInt();
                    }
                    // Add opaque alpha value to first byte
                    pixel |= 0xff000000;
//                    System.out.println((pixel & 0xff) + " " + ((pixel >>> 8) & 0xff) + " " + (pixel >>> 16));
                    intBuffer.put(pixel);
                }
            }
            intBuffer.flip();

            // Create pixel buffer
            PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
            PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer<>(width, height,
                    intBuffer, pixelFormat);
            WritableImage writableImage = new WritableImage(pixelBuffer);

            // Return writable image
            return writableImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
