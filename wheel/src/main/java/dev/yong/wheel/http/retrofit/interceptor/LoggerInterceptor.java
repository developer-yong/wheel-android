package dev.yong.wheel.http.retrofit.interceptor;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import dev.yong.wheel.utils.Logger;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
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

    private String createRequestMessage(Request request) {
        String requestMessage = "";
        Headers headers = request.headers();

        if (headers.size() > 0) {
            StringBuilder headersMessage = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                headersMessage.append("\n\t").append(headers.name(i)).append(": ").append(headers.value(i));
            }
            if (!"".equals(headersMessage.toString())) {
                requestMessage += "\n\tHeaders: " + headersMessage.toString();
            }
        }

        RequestBody requestBody = request.body();
        if (requestBody != null) {
            String bodyMessage = "";
            if (requestBody.contentType() != null) {
                bodyMessage += "\n\tContent-Type: " + requestBody.contentType();
            }
            try {
                if (requestBody.contentLength() != -1) {
                    bodyMessage += "\n\tContent-Length: " + requestBody.contentType();
                }
            } catch (IOException e) {
                bodyMessage += "\n\tContent-Length: unknown-length";
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
        return mediaType != null && mediaType.subtype() != null && mediaType.subtype().toLowerCase().contains("json");
    }

    private boolean isXml(MediaType mediaType) {
        return mediaType != null && mediaType.subtype() != null && mediaType.subtype().toLowerCase().contains("xml");
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
