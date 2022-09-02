package com.geega.bsc.id.server.local;

import com.geega.bsc.id.common.exception.DistributedIdException;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

/**
 * 缓存本地文件
 *
 * @author Jun.An3
 * @date 2022/07/26
 */
@Builder
public class LocalFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFile.class);

    private final String root;

    private final String fileName;

    public LocalFile(String root, String fileName) {
        this.root = root;
        this.fileName = fileName;
    }

    public synchronized Integer readWorkId() {
        String file = root + File.separator + fileName;
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(file));
            return Integer.valueOf(buffer.readLine());
        } catch (Exception e) {
            //do nothing
            LOGGER.warn("无法从【{}】读取workId", file);
        }
        return -1;
    }

    public synchronized void saveWorkId(Integer workId) {
        File rootFile = new File(root);
        if (!rootFile.exists()) {
            if (!rootFile.mkdirs()) {
                throw new DistributedIdException("无法创建文件夹:" + root);
            }
        }
        String file = root + File.separator + fileName;
        saveWorkId(file, workId);
    }

    private synchronized void saveWorkId(String path, Integer workId) {
        try (PrintStream stream = new PrintStream(path)) {
            //写入的字符串
            stream.print(workId);
            LOGGER.info("写入本地文件，workId：{}", workId);
        } catch (Exception e) {
            throw new DistributedIdException("无法保存workId至文件中，workId：" + workId);
        }
    }

}
