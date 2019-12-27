package com.chenqiao.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.chenqiao.util.annotations.AutoCreate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private FileUtils() {
    }

    private static final byte[] COPY_ASSETS_BUF = new byte[1024];

    public static synchronized boolean copy(@NonNull String srcPath, @NonNull String dstPath, boolean overwrite) {
        return copy(new File(srcPath), new File(dstPath), overwrite);
    }
    /**
     * 复制文件
     *
     * @param src
     * @param dst
     * @param overwrite 若目标文件存在，是否覆盖
     * @return 复制成功，或无须复制
     */
    public static boolean copy(@NonNull File src, @NonNull File dst, boolean overwrite) {
        if (!src.exists()) { // short cut
            return false;
        }
        File parent = dst.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            return false;
        }

        if (dst.exists()) {
            if (!overwrite) {
                return true;
            } else {
                delete(dst);
            }
        }

        File tmp = new File(dst.getAbsolutePath() + ".tmp");
        if (src.isDirectory()) {
            if (tmp.exists() || tmp.mkdirs()) {
                File[] files = src.listFiles();
                for (File subFile:
                     files) {
                    File subDst = new File(tmp, subFile.getName());
                    if(!copy(subFile, subDst, overwrite)) {
                        return false;
                    }
                }
                return tmp.renameTo(dst);
            }
            return false;
        }

        byte[] buffer = new byte[64 * 1024];
        boolean success;
        int read;
        try (InputStream is = new FileInputStream(src); OutputStream os = new FileOutputStream(tmp)) {
            while ((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }

        if (!success) {
            tmp.delete();
        } else {
            tmp.renameTo(dst);
        }

        return success;
    }

    /**
     * 移动文件
     *
     * @param srcPath
     * @param dstPath
     * @return 移动成功，注意返回true表示复制到目标路径成功，但未检查源文件是否删除成功
     */
    public static boolean move(@NonNull String srcPath, @NonNull String dstPath) {
        File src = new File(srcPath);
        File dst = new File(dstPath);
        File srcParent = src.getParentFile();
        if (srcParent != null && srcParent.equals(dst.getParentFile())) {
            return src.renameTo(dst);
        }
        boolean copyed = copy(srcPath, dstPath, true);
        if (copyed) {
            new File(srcPath).delete();
            return true;
        }
        return false;
    }

    /**
     * 删除某个文件或目录（递归删除）
     *
     * @param file
     * @return
     */
    public static boolean delete(File file) {
        if (!file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    if (!delete(subFile)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    public static boolean deleteExcept(File file, File exceptFile) {
        if (!file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    if (!deleteExcept(subFile, exceptFile)) {
                        return false;
                    }
                }
            }
            return file.delete();
        } else {
            if (!exceptFile.getAbsolutePath().equalsIgnoreCase(file.getAbsolutePath())) {
                return file.delete();
            } else {
                return true;
            }
        }
    }

    /**
     * 删除某个文件或目录（递归删除）
     *
     * @param path
     * @return
     */
    public static boolean delete(String path) {
        return delete(new File(path));
    }


    /**
     * 将制定文件读入为字符串
     *
     * @param fileName
     * @return
     */
    public static String readTextFromFile(String fileName) {
        return readTextFromFile(new File(fileName));
    }

    /**
     * 将制定文件读入为字符串
     *
     * @param file
     * @return
     */
    public static String readTextFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder stringBuilder = new StringBuilder();
            String tmpStr;
            while ((tmpStr = reader.readLine()) != null) {
                stringBuilder.append(tmpStr).append("\r\n");
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将字符串写入文件，若文件存在则覆盖原文件内容
     *
     * @param file
     * @param content
     * @return
     */
    public static boolean writeToFile(File file, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将字符串写入文件，若文件存在则覆盖原文件内容
     *
     * @param fileName
     * @param content
     */
    public static boolean writeToFile(String fileName, String content) {
        return writeToFile(new File(fileName), content);
    }

    /**
     * 将asset/{src}下的文件（不包含src），递归复制到制定目录
     * 不存在的目录会自动创建，已经存在的文件将忽略
     * @param src
     * @param dst
     * @return
     */
    public static boolean copyAssets(String src, String dst) {
        Context context = AppInstanceAccessor.getApp();
        AssetManager assetManager = context.getAssets();
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            String[] paths = assetManager.list(src);
            // consider it a directory if paths.length > 0, may be there is a better way to determine that
            if (paths.length == 0) {
                File dstFile = new File(dst);
                if (dstFile.exists()) {
                    return true;
                }
                File tmpFile = new File(dst + ".tmp");
                File parent = dstFile.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    return false;
                }
                fos = new FileOutputStream(tmpFile);
                is = assetManager.open(src);
                int read = 0;
                while ((read = is.read(COPY_ASSETS_BUF)) > 0) {
                    fos.write(COPY_ASSETS_BUF, 0, read);
                }
                fos.flush();
                tmpFile.renameTo(dstFile);
                return true;
            } else {
                File dstFile = new File(dst);
                if (!dstFile.exists() && !dstFile.mkdirs()) {
                    return false;
                }
                for (String path :
                        paths) {
                    if (!copyAssets(src + "/" + path, dst + "/" + path)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(fos);
            closeQuietly(is);
        }
    }

    public static String readUTF8StringFromAsset(String file) {
        Context context = AppInstanceAccessor.getApp();
        AssetManager assets = context.getAssets();
        try (InputStream is = assets.open(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) > 0) {
                bos.write(buffer, 0, read);
            }
            return new String(bos.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
        
    public static <C extends Closeable> C closeQuietly(C closable) {
        if (closable != null) {
            try {
                closable.close();
                closable = null;
            } catch (IOException e) {
            }
        }
        return closable;
    } 
    
    
    public static String getBaseName(String url, boolean removeSuffix) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        int query = url.indexOf('?');
        if (query > 0) {
            url = url.substring(0, query);
        }
        int fragment = url.indexOf('#');
        if (fragment > 0) {
            url = url.substring(0, fragment);
        }
        int slash = url.lastIndexOf('/');
        if (slash > 0) {
            url = url.substring(slash + 1);
        }
        if (removeSuffix) {
            int dot = url.lastIndexOf('.');
            if (dot > 0) {
                url = url.substring(0, dot);
            }
        }
        return url;
    }
    
    public static String getSuffix(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        
        int query = url.indexOf('?');
        if (query > 0) {
            url = url.substring(0, query);
        }
        
        int fragment = url.indexOf('#');
        if (fragment > 0) {
            url = url.substring(0, fragment);
        }

        int dot = url.lastIndexOf('.');
        if (dot > 0) {
            return url.substring(dot + 1);
        }
        return null;
    }


    private static final int STATIC_FINAL_FLAG = Modifier.FINAL | Modifier.STATIC;

    /**
     * 扫描指定类中注解为 {@link AutoCreate}的static final 字段，自动创建文件或目录
     *
     * @param clazz
     */
    public static void autoCreate(Class<?> clazz) {
        if (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            if (fields == null) {
                return;
            }

            for (Field field :
                    fields) {
                int modifiers = field.getModifiers();
                if ((modifiers & STATIC_FINAL_FLAG) == STATIC_FINAL_FLAG) {
                    AutoCreate autoCreate = field.getAnnotation(AutoCreate.class);
                    if (autoCreate == null) {
                        continue;
                    }
                    boolean isDir = AutoCreate.isDirectory;
                    try {
                        field.setAccessible(true);
                        Object value = field.get(null);
                        File file = null;
                        if (value instanceof String) {
                            file = new File((String) value);
                        } else if (value instanceof File) {
                            file = (File) value;
                        }

                        if (file != null && !file.exists()) {
                            if (isDir) {
                                file.mkdirs();
                            } else {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
    }
    /*
    2017-11-12 文件是否存在 -1文件不存在 >=0 文件存在
     */
    public static long getFileSize(String szfile)
    {
        File f = new File(szfile);
        if (f.exists()) {
            return f.length();
        }
        return -1;
    }

    public static void trimDirectoryToSize(String path, long size) {
        File downloadDir = new File(path);
        File[] tmpFiles = downloadDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".tmp");
            }
        });
        if (tmpFiles != null) {
            for (File file :
                    tmpFiles) {
                file.delete();
            }
        }

        File[] allFiles = downloadDir.listFiles();
        if (allFiles != null) {
            long cap = 0;
            for (File file :
                    allFiles) {
                // TODO: 2017/11/17 获取文件实际占用磁盘空间的大小 
                cap += file.length();
            }
            if (cap >= size) {
                Arrays.sort(allFiles, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return (int) (o1.lastModified() - o2.lastModified());
                    }
                });

                for (File file :
                        allFiles) {
                    long len = file.length();
                    if (file.delete()) {
                        cap -= len;
                        if (cap < size) {
                            break;
                        }
                    }
                }

            }
        }
    }
}

