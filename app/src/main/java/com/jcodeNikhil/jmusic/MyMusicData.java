package com.jcodeNikhil.jmusic;

import java.io.File;

public class MyMusicData {
    String fileName;
    File file;

    public MyMusicData(File file) {
        this.fileName = file.getName().replace(".mp3", "");
        this.file = file;
    }

    public String getFileName() {
        return file.getName().replace(".mp3", "");
    }

    public File getFile() {
        return file;
    }
}