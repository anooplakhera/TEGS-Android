package com.tegs.activities;

import android.os.Environment;

import java.io.File;

/**
 * Created
 * by heena on 30/1/18.
 */

public class trial {
    public void renameFile() {
        File directory = new File(Environment.getExternalStorageDirectory() + "/wetransfer-951a3b");
        if (directory.isDirectory()) {
            String destDirPath = Environment.getExternalStorageDirectory() + "/renamed";
            File destDir = new File(destDirPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            for (File file : directory.listFiles()) {
                String sourceName = file.getName();
                String destName = sourceName.replaceAll("-", "_").toLowerCase();

                file.renameTo(new File(destDir, destName));
            }
        }
    }
}