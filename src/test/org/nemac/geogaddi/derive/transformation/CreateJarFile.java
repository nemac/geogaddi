package org.nemac.geogaddi.derive.transformation;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class CreateJarFile {
    public static File buildJar(File dir) throws IOException {
        File result = File.createTempFile("brooklyn-built", ".jar");
        System.out.println(result.getAbsolutePath());
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        JarOutputStream target = new JarOutputStream(new FileOutputStream(result), manifest);
        add(dir, new File(""), target);
        target.close();
        return result;
    }

    private static void add(File rootDir, File relativeSource, JarOutputStream target) throws IOException {
        BufferedInputStream in = null;
        try {
            File absoluteSource = new File(rootDir.getAbsolutePath() + File.separator + relativeSource.getPath());
            if (absoluteSource.isDirectory()) {
                String name = relativeSource.getPath().replace("\\", "/");
                if (!name.isEmpty()) {
                    if (!name.endsWith("/"))
                        name += "/";
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(relativeSource.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile : absoluteSource.listFiles()) {
                    String relativeNestedFileStr = nestedFile.toString().substring(rootDir.toString().length());
                    if (relativeNestedFileStr.startsWith(File.separator))
                        relativeNestedFileStr = relativeNestedFileStr.substring(File.separator.length());
                    File relativeNestedFile = new File(relativeNestedFileStr);
                    add(rootDir, relativeNestedFile, target);
                }
                return;
            }

            JarEntry entry = new JarEntry(relativeSource.getPath().replace("\\", "/"));
            entry.setTime(relativeSource.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(absoluteSource));

            IOUtils.copy(in, target);
            target.closeEntry();
        } finally {
            if (in != null) in.close();
        }
    }
}






