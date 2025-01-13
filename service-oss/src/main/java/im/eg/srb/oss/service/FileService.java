package im.eg.srb.oss.service;

import java.io.InputStream;

public interface FileService {

    /**
     * 文件上傳
     *
     * @param inputStream 文件流
     * @param module      模塊名，對應目錄名
     * @param fileName    原始文件名
     */
    String upload(InputStream inputStream, String module, String fileName);

    /**
     * 刪除文件
     *
     * @param url 文件訪問路徑。
     */
    void remove(String url);
}
