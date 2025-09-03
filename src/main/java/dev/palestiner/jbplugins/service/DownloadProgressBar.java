package dev.palestiner.jbplugins.service;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component
public class DownloadProgressBar {

    private final PrintWriter writer;

    public DownloadProgressBar(Terminal terminal) {
        this.writer = terminal.writer();
    }

    public void display(long downloadedBytes, long totalBytes) {
        if (totalBytes <= 0) {
            writer.print("\rDownloading...");
            writer.flush();
            return;
        }
        int percentage = Math.min(100, (int) ((downloadedBytes * 100) / totalBytes));
        int barLength = 50;
        int filledLength = (percentage * barLength) / 100;
        String filled = new AttributedString(
                "█".repeat(filledLength),
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
        ).toAnsi();
        String empty = "░".repeat(Math.max(0, barLength - filledLength));
        writer.printf("\rDownloading: [%s%s] %d%%", filled, empty, percentage);
        writer.flush();
    }

}
