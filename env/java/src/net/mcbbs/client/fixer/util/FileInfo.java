package net.mcbbs.client.fixer.util;

/**
 * @author yinyangshi
 * plain class to store file's information
 */
public class FileInfo extends LocalFileInfo {
    public final String path;

    public FileInfo(String md5, String name, String path, String dest) {
        super(md5, name, dest);
        this.path = path;
    }
}
