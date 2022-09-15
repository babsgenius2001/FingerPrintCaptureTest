/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.LocalAFIS;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.filechooser.FileSystemView;

import com.dermalog.afis.fingercode3.Template;
import com.dermalog.afis.fingercode3.TemplateFormat;
import com.dermalog.demo.multiscannerdemo.FPScanner.Fingerprint;

/**
 *
 * @author BA07190
 */
public class LocalDB {

    public static String StoragePath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\DermalogMultiScannerDemo";
    private static String FILE_DEMOGRAPHIC = "user.txt";

    public static void makeDirectory() {
        File fileStorage = new File(StoragePath);
        if (!fileStorage.exists()) {
            fileStorage.mkdir();
        }
    }

    public static void createUserFolder(LocalUser localUser) {
        LocalDB.makeDirectory();

        try {
            String idString = String.format("%06d", localUser.ID);
            File userFolder = new File(StoragePath, idString);
            if (!userFolder.exists()) {
                userFolder.mkdir();
            }

            FileWriter writer = new FileWriter(new File(userFolder,
                    FILE_DEMOGRAPHIC));
            writer.write(localUser.Name);
            writer.flush();
            writer.close();

            for (int i = 0; i < localUser.Fingerprints.size(); i++) {
                Fingerprint template = localUser.Fingerprints.get(i);
                String templateString = String.format("template%02d.dat",
                        template.Position);

                FileOutputStream fos = new FileOutputStream(new File(
                        userFolder, templateString));
                fos.write(template.Template.GetData());
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Long, LocalUser> convertFoldersToUserList() {
        LocalDB.makeDirectory();

        HashMap<Long, LocalUser> userList = new HashMap<Long, LocalUser>();

        File storageFolder = new File(StoragePath);
        File[] dirs = storageFolder.listFiles();
        if (dirs == null || dirs.length == 0) {
            return userList;
        }

        for (File userFolder : dirs) {
            if (!userFolder.isDirectory()) {
                continue;
            }

            try {
                LocalUser localUser = new LocalUser();
                localUser.ID = Long.parseLong(userFolder.getName());
                localUser.Name = new String(getByteArrayFromFile(new File(userFolder,
                        FILE_DEMOGRAPHIC)));

                FilenameFilter filter = new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.startsWith("template");
                    }
                };

                for (File child : userFolder.listFiles(filter)) {
                    byte[] templateData = getByteArrayFromFile(child);

                    String childName = child.getName();
                    String fingerPos = childName.substring(8, 10);

                    Fingerprint fingerprint = new Fingerprint();
                    fingerprint.Template = new Template();
                    fingerprint.Template.SetData(templateData, TemplateFormat.Dermalog);
                    fingerprint.Position = Integer.parseInt(fingerPos);
                    localUser.Fingerprints.add(fingerprint);
                }

                userList.put(localUser.ID, localUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userList;
    }

    private static byte[] getByteArrayFromFile(File child) throws IOException {
        int size = (int) child.length();
        byte[] data = new byte[size];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(
                child));
        buf.read(data, 0, data.length);
        buf.close();
        return data;
    }

    public static void deleteUserFolder(long userId) {
        
        String idString = String.format("%06d", userId);
        File userFolder = new File(StoragePath, idString);
        
        if (userFolder.isDirectory()) {
            
            deleteRecursive(userFolder);
        }
    }

    public static void deleteRecursive(File fileOrDirectory) {
        
        if (fileOrDirectory.isDirectory()) {
            
            for (File child : fileOrDirectory.listFiles()) {
                
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public static void deleteRecursive(String filePathOrDirectoryPath) {
        
        deleteRecursive(new File(filePathOrDirectoryPath));
    }
}
