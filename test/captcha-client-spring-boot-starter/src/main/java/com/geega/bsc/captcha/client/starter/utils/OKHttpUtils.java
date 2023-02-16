package com.geega.bsc.captcha.client.starter.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp工具类
 *
 * @author Jun.An3
 * @date 2021/11/26
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
@Slf4j
public class OKHttpUtils {

    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            //30s,读超时
            .readTimeout(30, TimeUnit.SECONDS)
            //30s,写超时
            .writeTimeout(30, TimeUnit.SECONDS)
            //5s,连接超时
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();

    /**
     * 构建Request Builder
     *
     * @param url 请求url
     * @return Request.Builder
     */
    public static Request.Builder requestBuilder(String url) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        return builder;
    }

    /**
     * post方式
     * content-type: Application/json
     *
     * @param url         请求url
     * @param resultClazz 响应数据对象class
     * @param body        http body 数据
     */
    public static <T> T doPost(String url, Class<T> resultClazz, byte[] body) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, body);
        Request request = requestBuilder(url)
                .post(requestBody)
                .build();
        return doExecute(request, resultClazz);
    }

    /**
     * 请求执行
     *
     * @param request     请求对象
     * @param resultClazz 转成什么类型的对象
     * @param <T>         泛型
     * @return 结果
     * @throws IOException io异常
     */
    private static <T> T doExecute(Request request, Class<T> resultClazz) throws IOException {
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        if (body != null) {
            String bodyJsonString = body.string();
            if (StringUtils.isNotBlank(bodyJsonString) && resultClazz != null) {
                return JacksonUtils.jsonToObject(bodyJsonString, resultClazz);
            }
        }
        return null;
    }

    /**
     * Map -> RequestBody
     *
     * @param params 参数（key-value）
     * @return RequestBody
     */
    private static RequestBody getRequestBody(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            Set<String> fields = params.keySet();
            for (String field : fields) {
                builder.add(field, params.get(field));
            }
        }
        return builder.build();
    }

}
