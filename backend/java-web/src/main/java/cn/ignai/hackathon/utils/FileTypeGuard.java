package cn.ignai.hackathon.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class FileTypeGuard {
    private static final Set<String> ALLOWED = new HashSet<String>(Arrays.asList(
            "ppt", "pptx", "pdf", "html", "zip", "mp3", "wav", "png", "jpg", "jpeg", "webp", "doc", "docx"
    ));
    private static final Set<String> VIDEO = new HashSet<String>(Arrays.asList(
            "mp4", "mov", "webm", "avi", "mkv"
    ));

    private FileTypeGuard() {
    }

    public static boolean isAllowed(String fileName) {
        String extension = extensionOf(fileName);
        return ALLOWED.contains(extension) && !VIDEO.contains(extension);
    }

    public static String extensionOf(String fileName) {
        if (fileName == null) {
            return "";
        }
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1).toLowerCase(Locale.ROOT);
    }
}
