package com.finxis.cdm.trainingapp.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class FileWriter {

    public void writeEventToFile(String eventName, String eventId, String data) throws IOException {

        String userDirectory = Paths.get("")
                .toAbsolutePath()
                .toString();
        File udir = new File(userDirectory);
        File pudir = udir.getParentFile();
        String eventlogs = pudir.getPath() + "/eventlogs";

        File logFile = new File(eventlogs);
        final File log_directory = logFile.getAbsoluteFile();
        if (null != log_directory) {
            log_directory.mkdirs();
        }

        File eventfile = new File(eventlogs + "/" + eventName + "_" + eventId + ".txt");
        java.io.FileWriter fr = new java.io.FileWriter(eventfile);

        try {
            boolean result = eventfile.exists();
            if (result) {
                fr.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("writing event file output:" + eventfile);

    }
}
