package io.github.pigeonmuyz.jxremake.tools;

import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class HttpTool {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();

    public static Response post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("computerId","MTAzLjE0OC43Mi4xLUI4NS1IRDM=")
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response getDefault(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static List<Object> json(){
        return null;
    }

    public static String getData(String url) throws IOException {
        return get(url).body().string();
    }
}
