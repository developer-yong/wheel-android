package dev.yong.wheel.http.interceptor;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import dev.yong.wheel.utils.Logger;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author coderyong
 */
public final class LoggerInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String url = request.method() + ' ' + request.url();
        String requestMessage = createRequestMessage(request);

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            Logger.e(e, "HTTP FAILED");
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        url += "\t(" + tookMs + "ms)";

        String responseMessage = createResponseMessage(response);

        Logger.d(url, requestMessage, responseMessage);

        return response;
    }

    private String createRequestMessage(Request request) throws IOException {
        String requestMessage = "";
        Headers headers = request.headers();

        if (headers.size() > 0) {
            StringBuilder headersMessage = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                headersMessage.append("\n\t\t").append(headers.name(i)).append(": ").append(headers.value(i));
            }
            if (!"".equals(headersMessage.toString())) {
                requestMessage += "\n\tHeaders: " + headersMessage.toString();
            }
        }

        RequestBody requestBody = request.body();
        if (requestBody != null) {
            if (requestBody instanceof FormBody) {
                FormBody body = (FormBody) requestBody;
                StringBuilder paramsMessage = new StringBuilder();
                for (int i = 0; i < body.size(); i++) {
                    paramsMessage.append("\n\t\t")
                            .append(body.name(i)).append(": ").append(body.value(i));
                }
                if (!"".equals(paramsMessage.toString())) {
                    requestMessage += "\n\tParameters: {" + paramsMessage.toString() + "\n\t}";
                }
            } else if (requestBody instanceof MultipartBody) {
                MultipartBody body = (MultipartBody) requestBody;

                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                String postParams = buffer.readUtf8();
                String[] split = postParams.split("\n");
                List<String> names = new ArrayList<>();
                for (String s : split) {
                    if (s.contains("Content-Disposition")) {
                        names.add(s.replace("Content-Disposition: form-data; name=", "").replace("\"", ""));
                    }
                }

                StringBuilder paramsMessage = new StringBuilder();
                List<MultipartBody.Part> parts = body.parts();
                for (int i = 0; i < parts.size(); i++) {
                    MultipartBody.Part part = parts.get(i);
                    RequestBody partBody = part.body();
                    if (partBody.contentType() == null) {
                        Buffer partBuffer = new Buffer();
                        partBody.writeTo(partBuffer);
                        if (names.size() > i) {
                            paramsMessage.append("\n\t\t")
                                    .append(names.get(i)).append(": ").append(partBuffer.readUtf8());
                        }
                    } else {
                        if (names.size() > i) {
                            paramsMessage.append("\n\t\t")
                                    .append(names.get(i)).append(": ");
                        }
                    }
                }
                if (!"".equals(paramsMessage.toString())) {
                    requestMessage += "\n\tParameters: {" + paramsMessage.toString() + "\n\t}";
                }
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                try {
                    JSONObject json = new JSONObject(buffer.readUtf8());
                    StringBuilder paramsMessage = new StringBuilder();
                    Iterator<String> names = json.keys();
                    while (names.hasNext()) {
                        String name = names.next();
                        paramsMessage.append("\n\t\t")
                                .append(name).append(": ").append(json.get(name));
                    }
                    if (!"".equals(paramsMessage.toString())) {
                        requestMessage += "\n\tParameters: {" + paramsMessage.toString() + "\n\t}";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String bodyMessage = "";
            if (requestBody.contentType() != null) {
                bodyMessage += "\n\t\tContent-Type: " + requestBody.contentType();
            }
            try {
                if (requestBody.contentLength() != -1) {
                    bodyMessage += "\n\t\tContent-Length: " + requestBody.contentLength();
                }
            } catch (IOException e) {
                bodyMessage += "\n\t\tContent-Length: unknown-length";
            }
            if (!"".equals(bodyMessage)) {
                requestMessage += "\n\tRequest-Body: " + bodyMessage;
            }
        }
        if (!"".equals(requestMessage)) {
            requestMessage = "request:" + requestMessage;
        }
        return requestMessage;
    }

    private String createResponseMessage(Response response) {

        String responseMessage = "Response:";
        responseMessage += "\n\tStatus Code: " + response.code()
                + (response.message().isEmpty() ? "" : " / " + response.message()) + "";

        Headers headers = response.headers();
        if (headers.size() > 0) {
            StringBuilder headersMessage = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                headersMessage.append("\n\t\t").append(headers.name(i)).append(": ").append(headers.value(i));
            }
            responseMessage += "\n\tHeaders: " + headersMessage.toString();
        }

        ResponseBody responseBody = response.body();

        if (responseBody != null) {
            String bodyMessage = "";
            long contentLength = responseBody.contentLength();
            BufferedSource source = responseBody.source();
            try {
                source.request(Long.MAX_VALUE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Buffer buffer = source.buffer();

            if (contentLength != 0) {
                MediaType contentType = responseBody.contentType();
                String body = buffer.clone().readString(Charset.forName("UTF-8"));
                bodyMessage += "\n" + (isJson(contentType) ? jsonFormat(body)
                        : isXml(contentType) ? xmlFormat(body) : body);
            }

            if (!"".equals(bodyMessage)) {
                responseMessage += "\n\tBody: " + bodyMessage;
            }
        }

        return responseMessage;
    }

    private boolean isJson(MediaType mediaType) {
        return mediaType != null && mediaType.subtype().toLowerCase().contains("json");
    }

    private boolean isXml(MediaType mediaType) {
        return mediaType != null && mediaType.subtype().toLowerCase().contains("xml");
    }

    private String jsonFormat(String json) {
        if (TextUtils.isEmpty(json)) {
            return "Empty/Null json content";
        }
        String message;
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                message = jsonObject.toString(4);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                message = jsonArray.toString(4);
            } else {
                message = json;
            }
        } catch (JSONException e) {
            message = json;
        }
        return message;
    }

    private String xmlFormat(String xml) {
        if (TextUtils.isEmpty(xml)) {
            return "Empty/Null xml content";
        }
        String message;
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            message = xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (TransformerException e) {
            message = xml;
        }
        return message;
    }
}
