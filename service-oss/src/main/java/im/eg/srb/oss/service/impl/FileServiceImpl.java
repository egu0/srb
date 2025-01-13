package im.eg.srb.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import im.eg.srb.oss.service.FileService;
import im.eg.srb.oss.util.OSSProperties;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;


@Service
public class FileServiceImpl implements FileService {
    @Override
    public String upload(InputStream inputStream, String module, String fileName) {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(OSSProperties.ENDPOINT,
                    OSSProperties.KEY_ID, OSSProperties.KEY_SECRET);

            // 如果 bucket 不存在則創建
            if (!ossClient.doesBucketExist(OSSProperties.BUCKET_NAME)) {
                CreateBucketRequest req = new CreateBucketRequest(OSSProperties.BUCKET_NAME);
                req.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(req);
            }

            // 上傳文件。最終的路徑示例："passport/2025/1/13/generated_uuid.pdf"
            // 文件名
            if (fileName == null) {
                fileName = "";
            }
            int dotIndex = fileName.lastIndexOf('.');
            String extension = (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
            String finalFileName = UUID.randomUUID() + "." + extension;
            // 最終的存儲路徑
            String timePart = new DateTime().toString("yyyy/MM/dd");
            String storePath = module + "/" + timePart + "/" + finalFileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(OSSProperties.BUCKET_NAME,
                    storePath, inputStream);

            ossClient.putObject(putObjectRequest);
            return "https://" + OSSProperties.BUCKET_NAME + "." + OSSProperties.ENDPOINT + "/" + storePath;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public void remove(String url) {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(OSSProperties.ENDPOINT,
                    OSSProperties.KEY_ID, OSSProperties.KEY_SECRET);

            // 如果 bucket 不存在則直接返回
            if (!ossClient.doesBucketExist(OSSProperties.BUCKET_NAME)) {
                return;
            }

            String hostPath = "https://" + OSSProperties.BUCKET_NAME + "." + OSSProperties.ENDPOINT + "/";
            // bucket 內文件相對路徑，比如 “passport/2025/1/13/generated_uuid.pdf”
            String objectPath = url.substring(hostPath.length());
            ossClient.deleteObject(OSSProperties.BUCKET_NAME, objectPath);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
