package ppmviewer;

import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * Updates the current image file when it's modified on disk.
 */
public class ViewerUpdateThread extends Thread {
    private final ImageView imageView;
    private File imageFile;

    public ViewerUpdateThread(ImageView imageView, File imageFile) {
        this.imageView = imageView;
        this.imageFile = imageFile;
    }

    public void run() {
        System.out.println("Running ViewerUpdateThread");
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            // Register the path with the watch service
            Path path = imageFile.toPath();
            path.getParent().register(watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            System.out.println("Watching on " + imageFile.toPath());

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().equals(path.getFileName())) {
                        System.out.println(event.kind() + ": " + event.context());
                        imageView.setImage(PpmConverter.convert(imageFile));
                        break;
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }
}
