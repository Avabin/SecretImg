package tk.avabin.secretimg.GUI;

import tk.avabin.secretimg.logic.ImageProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

class ProcessWorker extends Thread {
    private String pass;
    private Collection<File> files;
    private ImageProcessor imageProcessor;
    private boolean mode;
    private long progress[];

    ProcessWorker(Collection files, boolean mode, long progress[]) {
        this.files = files;
        this.mode = mode;
        imageProcessor = new ImageProcessor();
        this.progress = progress;
        progress[0] = 0L;
    }

    @Override
    public void run() {
        if (files == null || pass == null) return;
        if (mode) files.forEach(file -> {
            try {
                imageProcessor.encrypt(file.getAbsolutePath(), pass, file.getAbsolutePath());
                progress[0]++;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        else files.forEach(file -> {
            imageProcessor.decrypt(file.getAbsolutePath(), pass, file.getAbsolutePath());
            progress[0]++;
        });
    }

    void setPass(String pass) {
        this.pass = pass;
    }

    public void setFiles(Collection<File> files) {
        this.files = files;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }
}
