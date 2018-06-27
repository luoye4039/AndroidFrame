package com.seven.framework.manager.uploadordown;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class UploadFileRequestBody extends RequestBody {
    private MediaType contentType;
    private File mFile;
    private OnUploadFileObserver mOnUploadFilObserver;

    public UploadFileRequestBody(File file) {
        mFile = file;
    }

    public UploadFileRequestBody(MediaType contentType, File file) {
        this.contentType = contentType;
        mFile = file;
    }

    public UploadFileRequestBody(File file, OnUploadFileObserver onUploadFilObserver) {
        mFile = file;
        mOnUploadFilObserver = onUploadFilObserver;
    }


    public UploadFileRequestBody(MediaType contentType, File file, OnUploadFileObserver onUploadFilObserver) {
        this.contentType = contentType;
        mFile = file;
        mOnUploadFilObserver = onUploadFilObserver;
    }


    @Override
    public MediaType contentType() {
        return contentType == null ? MediaType.parse("application/octet-stream") : contentType;
    }

    @Override
    public long contentLength() throws IOException {
        return mFile == null ? 0 : mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = Okio.source(mFile);
        Buffer buf = new Buffer();
        Long total = 0L;
        for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
            sink.write(buf, readCount);
            if (mOnUploadFilObserver != null)
                mOnUploadFilObserver.onProgressChange(mFile, contentLength(), total += readCount);
        }
        source.close();
    }
}
