package dev.yong.wheel.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadRequester extends Requester {

    private final MultipartBody.Builder mBuilder;

    public UploadRequester(String url, String boundary) {
        super(url, RequestMethod.POST);
        mBuilder = new MultipartBody.Builder(boundary);
    }

    public UploadRequester(String url) {
        super(url, RequestMethod.POST);
        mBuilder = new MultipartBody.Builder();
    }

    /**
     * Set the MIME type. Expected values for `type` are
     * [MultipartBody.MIXED] (the default),
     * [MultipartBody.ALTERNATIVE],
     * [MultipartBody.DIGEST],
     * [MultipartBody.PARALLEL] and
     * [MultipartBody.FORM].
     */
    public UploadRequester setMediaType(@NotNull MediaType mediaType) {
        mBuilder.setType(mediaType);
        return this;
    }

    /**
     * Add a part to the body.
     */
    public UploadRequester addPart(@NotNull MultipartBody.Part part) {
        mBuilder.addPart(part);
        return this;
    }

    /**
     * Add a part to the body.
     */
    public UploadRequester addPart(@NotNull RequestBody body) {
        mBuilder.addPart(MultipartBody.Part.create(body));
        return this;
    }

    /**
     * Add a part to the body.
     */
    public UploadRequester addPart(@NotNull RequestBody body, @Nullable Headers headers) {
        mBuilder.addPart(MultipartBody.Part.create(headers, body));
        return this;
    }

    /**
     * Add a form data part to the body.
     */
    public UploadRequester addFormDataPart(@NotNull String name, @NotNull String... value) {
        for (String v : value) {
            mBuilder.addPart(MultipartBody.Part.createFormData(name, v));
        }
        return this;
    }

    /**
     * Add a form data part to the body.
     */
    public UploadRequester addFormDataPart(@NotNull RequestBody body, @NotNull String name, @Nullable String filename) {
        mBuilder.addPart(MultipartBody.Part.createFormData(name, filename, body));
        return this;
    }

    public UploadRequester addFormDataParts(@Nullable MediaType mediaType, @NotNull String name, @NotNull String... filePaths) {
        for (String path : filePaths) {
            File file = new File(path);
            mBuilder.addPart(
                    MultipartBody.Part.createFormData(
                            name, file.getName(), RequestBody.create(file, mediaType)
                    )
            );
        }
        return this;
    }

    @Override
    public void execute(okhttp3.Callback callback) {
        Map<String, String> globalParams = Transfer.globalParams();
        if (globalParams != null) {
            for (String name : globalParams.keySet()) {
                String value = globalParams.get(name);
                if (value != null) {
                    addFormDataPart(name, value);
                }
            }
        }
        body(mBuilder.build());
        super.execute(callback);
    }
}
