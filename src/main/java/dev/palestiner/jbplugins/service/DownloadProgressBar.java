package dev.palestiner.jbplugins.service;

import org.springframework.stereotype.Component;

@Component
public class DownloadProgressBar {

    public void display(long downloadedBytes, long totalBytes) {
        if (totalBytes <= 0) {
            System.out.print("\rDownloading...");
            return;
        }
        int percentage = (int) ((downloadedBytes * 100) / totalBytes);
        int barLength = 50;
        int filledLength = (percentage * barLength) / 100;
        String filled = new String(new char[filledLength]).replace('\0', '#');
        String empty = new String(new char[barLength - filledLength]).replace('\0', '-');

        System.out.printf("\rDownloading: [%s%s] %d%%", filled, empty, percentage);
    }

}
