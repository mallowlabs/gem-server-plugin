package org.codefirst.jenkins.gemserverplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

public class GemDirectory {

    private File dir;

    public GemDirectory(File dir) {
        this.dir = dir;
    }

    public File getGemsSubDirectory() {
        return new File(dir, "gems");
    }

    public String[] listGems() {
        File[] files = getGemsSubDirectory().listFiles(new FilenameFilter() {
            public boolean accept(File arg0, String arg1) {
                return arg1.endsWith(".gem");
            }
        });

        if (files == null) {
            files = new File[0];
        }

        String[] ret = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            ret[i] = files[i].getName();
        }

        return ret;
    }

    public boolean generateIndex(String gemPath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(gemPath, "generate_index", "-d", dir.getAbsolutePath());
        Process p = pb.start();
        p.waitFor();
        return (0 == p.exitValue());
    }

    public boolean mkdirs() {
        return getGemsSubDirectory().mkdirs();
    }

    public InputStream getInputStream(String path) throws FileNotFoundException {
        return new FileInputStream(new File(dir, path));
    }

}
