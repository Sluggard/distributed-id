package com.geega.bsc.id.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author Jun.An3
 * @date 2022/07/20
 */
public class ResourcesUtil {

    public static Properties getProperties(String name) throws IOException {
        InputStream in = ResourcesUtil.class.getResourceAsStream(name);
        Properties props = new Properties();
        assert in != null;
        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        props.load(inputStreamReader);
        return props;
    }

}
