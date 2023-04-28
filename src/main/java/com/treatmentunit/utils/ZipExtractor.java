package com.treatmentunit.utils;

import java.io.*;
import java.util.zip.*;

/**
 * Classe utilisé pour manipuler les .zip
 */
public class ZipExtractor {

    /**
     * Constructeur
     * @param target
     * @param destination
     */
    public ZipExtractor(String target, String destination) {
        String zipFilePath = target;
        String destDir = destination;

        try {
            File destDirFile = new File(destDir);
            if (!destDirFile.exists()) {
                destDirFile.mkdirs();
            }

            FileInputStream fis = new FileInputStream(zipFilePath);
            ZipInputStream zipInputStream = new ZipInputStream(fis);

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                String filePath = destDir + File.separator + entryName;

                if (!entry.isDirectory()) {
                    extractFile(zipInputStream, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }

                zipInputStream.closeEntry();
            }

            zipInputStream.close();
            fis.close();

            System.out.println("[*] Extraction completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param zipInputStream
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipInputStream, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));

        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipInputStream.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }

        bos.close();
    }
}
