/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
 */

package com.yandex.disk.client;

import com.yandex.disk.client.exceptions.CancelledDownloadException;
import com.yandex.disk.client.exceptions.CancelledPropfindException;
import com.yandex.disk.client.exceptions.DownloadNoSpaceAvailableException;
import com.yandex.disk.client.exceptions.DuplicateFolderException;
import com.yandex.disk.client.exceptions.RemoteFileNotFoundException;
import com.yandex.disk.client.exceptions.FileModifiedException;
import com.yandex.disk.client.exceptions.FileNotModifiedException;
import com.yandex.disk.client.exceptions.FileTooBigServerException;
import com.yandex.disk.client.exceptions.FilesLimitExceededServerException;
import com.yandex.disk.client.exceptions.IntermediateFolderNotExistException;
import com.yandex.disk.client.exceptions.PreconditionFailedException;
import com.yandex.disk.client.exceptions.RangeNotSatisfiableException;
import com.yandex.disk.client.exceptions.ServerWebdavException;
import com.yandex.disk.client.exceptions.ServiceUnavailableWebdavException;
import com.yandex.disk.client.exceptions.UnknownServerWebdavException;
import com.yandex.disk.client.exceptions.UnsupportedMediaTypeException;
import com.yandex.disk.client.exceptions.WebdavClientInitException;
import com.yandex.disk.client.exceptions.WebdavException;
import com.yandex.disk.client.exceptions.WebdavFileNotFoundException;
import com.yandex.disk.client.exceptions.WebdavForbiddenException;
import com.yandex.disk.client.exceptions.WebdavInvalidUserException;
import com.yandex.disk.client.exceptions.WebdavNotAuthorizedException;
import com.yandex.disk.client.exceptions.WebdavUserNotInitialized;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.yandex.disk.client.exceptions.WebdavSharingForbiddenException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.TextUtils;
import org.xmlpull.v1.XmlPullParserException;

public class TransportClient {
    private static final String TAG = "TransportClient";
    private static final String ATTR_ETAG_FROM_REDIRECT = "yandex.etag-from-redirect";
    protected static URL serverURL;
    protected static final String userAgent = "Webdav Android Client Example/1.0";
    protected static final String LOCATION_HEADER = "Location";
    public static final String NO_REDIRECT_CONTEXT = "yandex.no-redirect";
    protected static final String WEBDAV_PROTO_DEPTH = "Depth";
    protected static final int NETWORK_TIMEOUT = 30000;
    protected static final int UPLOAD_NETWORK_TIMEOUT = 300000;
    protected Credentials creds;
    protected final HttpClient httpClient;
    private static final HttpRequestRetryHandler requestRetryHandler;
    private static final RedirectHandler redirectHandler;
    private static final String PROPFIND_REQUEST = "<?xml version='1.0' encoding='utf-8' ?><d:propfind xmlns:d='DAV:'><d:prop xmlns:m='urn:yandex:disk:meta'><d:resourcetype/><d:displayname/><d:getcontentlength/><d:getlastmodified/><d:getetag/><d:getcontenttype/><m:alias_enabled/><m:visible/><m:shared/><m:readonly/><m:public_url/><m:etime/><m:mediatype/><m:mpfs_file_id/><m:hasthumbnail/></d:prop></d:propfind>";
    private static final int MAX_ITEMS_PER_PAGE = 2147483647;
    private static Pattern CONTENT_RANGE_HEADER_PATTERN;
    private static final String PREVIEW_ARG = "preview";
    private static final String SIZE_ARG = "size";

    public static TransportClient getInstance(Credentials credentials) throws WebdavClientInitException {
        return new TransportClient(credentials, 30000);
    }

    public static TransportClient getUploadInstance(Credentials credentials) throws WebdavClientInitException {
        return new TransportClient(credentials, 300000);
    }

    public TransportClient(Credentials credentials, DefaultHttpClient httpClient) throws WebdavClientInitException {
        this.creds = credentials;
        this.httpClient = httpClient;
        initHttpClient(httpClient);
    }

    protected TransportClient(Credentials credentials, int timeout) throws WebdavClientInitException {
        this(credentials, "Webdav Android Client Example/1.0", timeout);
    }

    protected TransportClient(Credentials credentials, String userAgent, int timeout) throws WebdavClientInitException {
        this.creds = credentials;
        DefaultHttpClient httpClient = getNewHttpClient(userAgent, timeout);
        httpClient.setCookieStore(new BasicCookieStore());
        this.httpClient = httpClient;
        initHttpClient(httpClient);
    }

    public static void initHttpClient(DefaultHttpClient httpClient) {
        httpClient.setHttpRequestRetryHandler(requestRetryHandler);
        httpClient.setRedirectHandler(redirectHandler);
    }

    protected static DefaultHttpClient getNewHttpClient(String userAgent, int timeout) throws WebdavClientInitException {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpConnectionParams.setConnectionTimeout(params, timeout);
        HttpConnectionParams.setSoTimeout(params, timeout);
        ConnManagerParams.setMaxTotalConnections(params, 1);

        SSLSocketFactoryWithTimeout sf;
        try {
            sf = new SSLSocketFactoryWithTimeout(timeout);
        } catch (GeneralSecurityException var7) {
            // // Log.e("TransportClient", "getNewHttpClient", var7);
            throw new WebdavClientInitException();
        }

        sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sf, 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
        DefaultHttpClient res = new DefaultHttpClient(ccm, params);
        if (userAgent != null) {
            res.getParams().setParameter("http.useragent", userAgent);
        }

        res.getParams().setParameter("http.protocol.expect-continue", true);
        res.getParams().setParameter("http.protocol.wait-for-continue", timeout);
        return res;
    }

    protected HttpResponse executeRequest(HttpUriRequest request) throws IOException {
        return this.httpClient.execute(request, (HttpContext)null);
    }

    protected HttpResponse executeRequest(HttpUriRequest request, HttpContext httpContext) throws IOException {
        return this.httpClient.execute(request, httpContext);
    }

    public void shutdown() {
        this.httpClient.getConnectionManager().shutdown();
    }

    public static void shutdown(TransportClient client) {
        if (client != null) {
            client.shutdown();
        }

    }

    protected String getUrl() {
        return serverURL.toExternalForm();
    }

    protected static void consumeContent(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            entity.consumeContent();
        }

    }

    public static String encodeURL(String url) {
        if (url == null) {
            return null;
        } else {
            String[] segments = url.split("/");
            StringBuilder sb = new StringBuilder(20);

            try {
                String[] var3 = segments;
                int var4 = segments.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    String segment = var3[var5];
                    if (!"".equals(segment)) {
                        sb.append("/").append(URLEncoder.encode(segment, "UTF-8"));
                    }
                }

                // Log.d("TransportClient", "url encoded: " + sb.toString());
            } catch (UnsupportedEncodingException var7) {
                // Log.d("TransportClient", "Exception occured: " + var7.getMessage());
            }

            return sb.toString().replace("+", "%20");
        }
    }

    protected static void logMethod(HttpRequestBase method) {
        logMethod(method, (String)null);
    }

    protected static void logMethod(HttpRequestBase method, String add) {
        // Log.d("TransportClient", "logMethod(): " + method.getMethod() + ": " + method.getURI() + (add != null ? " " + add : ""));
    }

    public static byte[] makeHashBytes(File file, TransportClient.HashType hashType) throws IOException {
        FileInputStream is = null;

        try {
            is = new FileInputStream(file);

            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance(hashType.name());
            } catch (NoSuchAlgorithmException var10) {
                throw new RuntimeException(var10);
            }

            byte[] buf = new byte[8192];

            int count;
            while((count = is.read(buf)) > 0) {
                digest.update(buf, 0, count);
            }

            byte[] var6 = digest.digest();
            return var6;
        } finally {
            if (is != null) {
                is.close();
            }

        }
    }

    public static String makeHash(File file, TransportClient.HashType hashType) throws IOException {
        long time = System.currentTimeMillis();
        String hash = hash(makeHashBytes(file, hashType));
        // Log.d("TransportClient", hashType.name() + ": " + file.getAbsolutePath() + " hash=" + hash + " time=" + (System.currentTimeMillis() - time));
        return hash;
    }

    public static String hash(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            StringBuilder out = new StringBuilder();
            byte[] var2 = bytes;
            int var3 = bytes.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte b = var2[var4];
                String n = Integer.toHexString(b & 255);
                if (n.length() == 1) {
                    out.append('0');
                }

                out.append(n);
            }

            return out.toString();
        }
    }

    protected void checkStatusCodes(HttpResponse response, String details) throws WebdavNotAuthorizedException, WebdavUserNotInitialized, FileTooBigServerException, FilesLimitExceededServerException, ServerWebdavException, PreconditionFailedException, UnknownServerWebdavException {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        switch(statusCode) {
        case 401:
            // Log.d("TransportClient", "Not authorized: " + statusLine.getReasonPhrase());
            throw new WebdavNotAuthorizedException(statusLine.getReasonPhrase() != null ? statusLine.getReasonPhrase() : "");
        case 403:
            // Log.d("TransportClient", "User not initialized: " + statusLine.getReasonPhrase());
            throw new WebdavUserNotInitialized("Error (http code 403): " + details);
        case 412:
            // Log.d("TransportClient", "Http code 412 (Precondition failed): " + details);
            throw new PreconditionFailedException("Error (http code 412): " + details);
        case 413:
            // Log.d("TransportClient", "Http code 413 (File too big): " + details);
            throw new FileTooBigServerException();
        case 503:
            // Log.d("TransportClient", "Http code 503 (Service Unavailable): " + details);
            throw new ServiceUnavailableWebdavException();
        case 507:
            // Log.d("TransportClient", "Http code 507 (Insufficient Storage): " + details);
            throw new FilesLimitExceededServerException();
        default:
            if (statusCode >= 500 && statusCode < 600) {
                // Log.d("TransportClient", "Server error " + statusCode);
                throw new ServerWebdavException("Server error while " + details);
            } else {
                // Log.d("TransportClient", "Unknown code " + statusCode);
                throw new UnknownServerWebdavException("Server error while " + details);
            }
        }
    }

    public void getList(String path, ListParsingHandler handler) throws IOException, PreconditionFailedException, UnknownServerWebdavException, WebdavFileNotFoundException, CancelledPropfindException, WebdavUserNotInitialized, ServerWebdavException, WebdavNotAuthorizedException, WebdavForbiddenException, WebdavInvalidUserException {
        this.getList(path, 2147483647, (String)null, (String)null, handler);
    }

    public void getList(String path, int itemsPerPage, ListParsingHandler handler) throws IOException, PreconditionFailedException, UnknownServerWebdavException, WebdavFileNotFoundException, CancelledPropfindException, WebdavUserNotInitialized, ServerWebdavException, WebdavNotAuthorizedException, WebdavForbiddenException, WebdavInvalidUserException {
        this.getList(path, itemsPerPage, (String)null, (String)null, handler);
    }

    public void getList(String path, int itemsPerPage, String sortBy, String orderBy, ListParsingHandler handler) throws IOException, CancelledPropfindException, WebdavNotAuthorizedException, WebdavInvalidUserException, WebdavForbiddenException, WebdavFileNotFoundException, WebdavUserNotInitialized, UnknownServerWebdavException, PreconditionFailedException, ServerWebdavException {
        // Log.d("TransportClient", "getList for " + path);
        boolean itemsFinished = false;

        for(int offset = 0; !itemsFinished; offset += itemsPerPage) {
            if (handler.hasCancelled()) {
                throw new CancelledPropfindException();
            }

            String url = this.getUrl() + encodeURL(path);
            if (itemsPerPage != 2147483647) {
                url = url + "?offset=" + offset + "&amount=" + itemsPerPage;
                if (sortBy != null && orderBy != null) {
                    url = url + "&sort=" + sortBy + "&order=" + orderBy;
                }
            }

            TransportClient.PropFind propFind = new TransportClient.PropFind(url);
            logMethod(propFind);
            this.creds.addAuthHeader(propFind);
            propFind.setHeader("Depth", "1");
            HttpContext httpContext = handler.onCreateRequest(propFind, new StringEntity("<?xml version='1.0' encoding='utf-8' ?><d:propfind xmlns:d='DAV:'><d:prop xmlns:m='urn:yandex:disk:meta'><d:resourcetype/><d:displayname/><d:getcontentlength/><d:getlastmodified/><d:getetag/><d:getcontenttype/><m:alias_enabled/><m:visible/><m:shared/><m:readonly/><m:public_url/><m:etime/><m:mediatype/><m:mpfs_file_id/><m:hasthumbnail/></d:prop></d:propfind>"));
            HttpResponse response = this.executeRequest(propFind, httpContext);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine != null) {
                int code = statusLine.getStatusCode();
                switch(code) {
                case 207:
                    break;
                case 401:
                    consumeContent(response);
                    throw new WebdavNotAuthorizedException(statusLine.getReasonPhrase() != null ? statusLine.getReasonPhrase() : "");
                case 402:
                    consumeContent(response);
                    throw new WebdavInvalidUserException();
                case 403:
                    consumeContent(response);
                    throw new WebdavForbiddenException();
                case 404:
                    consumeContent(response);
                    throw new WebdavFileNotFoundException("Directory not found: " + path);
                default:
                    consumeContent(response);
                    this.checkStatusCodes(response, "PROPFIND " + path);
                }
            }

            HttpEntity entity = response.getEntity();

            int countOnPage;
            try {
                ListParser parser = new ListParser(entity, handler);
                parser.parse();
                countOnPage = parser.getParsedCount();
                // Log.d("TransportClient", "countOnPage=" + countOnPage);
            } catch (XmlPullParserException var19) {
                throw new UnknownServerWebdavException(var19);
            } finally {
                consumeContent(response);
            }

            if (countOnPage != itemsPerPage) {
                itemsFinished = true;
            }
        }

    }

    public long headFile(File file, String dir, String destName, String md5, String sha256) throws IOException, NumberFormatException, WebdavUserNotInitialized, UnknownServerWebdavException, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException {
        String url = this.getUrl() + encodeURL(dir + "/" + destName);
        HttpHead head = new HttpHead(url);
        logMethod(head, ", file " + file);
        this.creds.addAuthHeader(head);
        head.addHeader("Etag", md5);
        if (sha256 != null) {
            head.addHeader("Sha256", sha256);
        }

        head.addHeader("Size", String.valueOf(file.length()));
        HttpResponse response = this.executeRequest(head);
        consumeContent(response);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine != null) {
            int statusCode = statusLine.getStatusCode();
            if (statusLine.getStatusCode() == 200) {
                Header[] headers = response.getHeaders("Content-Length");
                if (headers.length > 0) {
                    String contentLength = headers[0].getValue();
                    return Long.valueOf(contentLength);
                }

                return 0L;
            }

            if (statusCode == 409 || statusCode == 404 || statusCode == 412) {
                // Log.d("TransportClient", statusLine + " for file " + file.getAbsolutePath() + " in dir " + dir);
                return 0L;
            }

            this.checkStatusCodes(response, "HEAD " + url);
        }

        return 0L;
    }

    public void uploadFile(String localPath, String serverDir, ProgressListener progressListener) throws IOException, UnknownServerWebdavException, PreconditionFailedException, IntermediateFolderNotExistException, WebdavUserNotInitialized, ServerWebdavException, WebdavNotAuthorizedException {
        File file = new File(localPath);
        this.uploadFile(file, serverDir, file.getName(), makeHash(file, TransportClient.HashType.MD5), (String)null, progressListener);
    }

    public void uploadFile(File file, String dir, String destFileName, String md5, String sha256, ProgressListener progressListener) throws IntermediateFolderNotExistException, IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException, UnknownServerWebdavException {
        String destName = TextUtils.isEmpty(destFileName) ? file.getName() : destFileName;
        String url = this.getUrl() + encodeURL(dir + "/" + destName);
        // Log.d("TransportClient", "uploadFile: put to " + this.getUrl() + dir + "/" + destName);

        long uploadedSize;
        try {
            uploadedSize = this.headFile(file, dir, destName, md5, sha256);
        } catch (NumberFormatException var15) {
            // Log.w("TransportClient", "Uploading " + file.getAbsolutePath() + " to " + dir + ": HEAD failed", var15);
            uploadedSize = 0L;
        }

        HttpPut put = new HttpPut(url);
        this.creds.addAuthHeader(put);
        put.addHeader("Etag", md5);
        if (sha256 != null) {
            // Log.d("TransportClient", "Sha256: " + sha256);
            put.addHeader("Sha256", sha256);
        }

        if (uploadedSize > 0L) {
            StringBuilder contentRange = new StringBuilder();
            contentRange.append("bytes ").append(uploadedSize).append("-").append(file.length() - 1L).append("/").append(file.length());
            // Log.d("TransportClient", "Content-Range: " + contentRange);
            put.addHeader("Content-Range", contentRange.toString());
        }

        HttpEntity entity = new FileProgressHttpEntity(file, uploadedSize, progressListener);
        put.setEntity(entity);
        logMethod(put, ", file to upload " + file);
        HttpResponse response = this.executeRequest(put);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine != null) {
            consumeContent(response);
            switch(statusLine.getStatusCode()) {
            case 201:
                // Log.d("TransportClient", "File uploaded successfully: " + file);
                return;
            case 409:
                // Log.d("TransportClient", "Parent not exist for dir " + dir);
                throw new IntermediateFolderNotExistException("Parent folder not exists for '" + dir + "'");
            default:
                this.checkStatusCodes(response, "PUT '" + file + "' to " + url);
            }
        }

    }

    public void downloadFile(String path, File saveTo, ProgressListener progressListener) throws IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException, CancelledDownloadException, UnknownServerWebdavException, FileNotModifiedException, DownloadNoSpaceAvailableException, RemoteFileNotFoundException, RangeNotSatisfiableException, FileModifiedException {
        this.downloadFile(path, saveTo, 0L, 0L, progressListener);
    }

    public void downloadFile(String path, final File saveTo, final long length, long fileSize, final ProgressListener progressListener) throws IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException, CancelledDownloadException, UnknownServerWebdavException, FileNotModifiedException, DownloadNoSpaceAvailableException, RemoteFileNotFoundException, RangeNotSatisfiableException, FileModifiedException {
        this.download(path, new DownloadListener() {
            public long getLocalLength() {
                return length;
            }

            public OutputStream getOutputStream(boolean append) throws FileNotFoundException {
                return new FileOutputStream(saveTo, append);
            }

            public void updateProgress(long loaded, long total) {
                progressListener.updateProgress(loaded, total);
            }

            public boolean hasCancelled() {
                return progressListener.hasCancelled();
            }
        });
    }

    public byte[] download(String path) throws IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException, CancelledDownloadException, UnknownServerWebdavException, FileNotModifiedException, DownloadNoSpaceAvailableException, RemoteFileNotFoundException, RangeNotSatisfiableException, FileModifiedException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.download(path, new DownloadListener() {
            public OutputStream getOutputStream(boolean append) throws IOException {
                return outputStream;
            }
        });
        return outputStream.toByteArray();
    }

    public byte[] downloadUrl(String url) throws IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException, CancelledDownloadException, UnknownServerWebdavException, FileNotModifiedException, DownloadNoSpaceAvailableException, RemoteFileNotFoundException, RangeNotSatisfiableException, FileModifiedException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.downloadUrl(url, new DownloadListener() {
            public OutputStream getOutputStream(boolean append) throws IOException {
                return outputStream;
            }
        });
        return outputStream.toByteArray();
    }

    public void download(String path, DownloadListener downloadListener) throws IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException, CancelledDownloadException, UnknownServerWebdavException, FileNotModifiedException, DownloadNoSpaceAvailableException, RemoteFileNotFoundException, RangeNotSatisfiableException, FileModifiedException {
        this.downloadUrl(this.getUrl() + encodeURL(path), downloadListener);
    }

    public void downloadPreview(String path, DownloadListener downloadListener) throws IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException, CancelledDownloadException, UnknownServerWebdavException, FileNotModifiedException, DownloadNoSpaceAvailableException, RemoteFileNotFoundException, RangeNotSatisfiableException, FileModifiedException {
        this.downloadUrl(this.getUrl() + path, downloadListener);
    }

    private void downloadUrl(String url, DownloadListener downloadListener) throws IOException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException, CancelledDownloadException, UnknownServerWebdavException, FileNotModifiedException, RemoteFileNotFoundException, DownloadNoSpaceAvailableException, RangeNotSatisfiableException, FileModifiedException {
        HttpGet get = new HttpGet(url);
        logMethod(get);
        this.creds.addAuthHeader(get);
        long length = downloadListener.getLocalLength();
        String ifTag = "If-None-Match";
        if (length >= 0L) {
            ifTag = "If-Range";
            StringBuilder contentRange = new StringBuilder();
            contentRange.append("bytes=").append(length).append("-");
            // Log.d("TransportClient", "Range: " + contentRange);
            get.addHeader("Range", contentRange.toString());
        }

        String etag = downloadListener.getETag();
        if (etag != null) {
            // Log.d("TransportClient", ifTag + ": " + etag);
            get.addHeader(ifTag, etag);
        }

        boolean partialContent = false;
        BasicHttpContext httpContext = new BasicHttpContext();
        HttpResponse httpResponse = this.executeRequest(get, httpContext);
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine != null) {
            int statusCode = statusLine.getStatusCode();
            switch(statusCode) {
            case 200:
                break;
            case 206:
                partialContent = true;
                break;
            case 304:
                consumeContent(httpResponse);
                throw new FileNotModifiedException();
            case 404:
                consumeContent(httpResponse);
                throw new RemoteFileNotFoundException("error while downloading file " + url);
            case 416:
                consumeContent(httpResponse);
                throw new RangeNotSatisfiableException("error while downloading file " + url);
            default:
                this.checkStatusCodes(httpResponse, "GET '" + url + "'");
            }
        }

        HttpEntity response = httpResponse.getEntity();
        long contentLength = response.getContentLength();
        // Log.d("TransportClient", "download: contentLength=" + contentLength);
        long loaded;
        if (partialContent) {
            ContentRangeResponse contentRangeResponse = this.parseContentRangeHeader(httpResponse.getLastHeader("Content-Range"));
            // Log.d("TransportClient", "download: contentRangeResponse=" + contentRangeResponse);
            if (contentRangeResponse != null) {
                loaded = contentRangeResponse.getStart();
                contentLength = contentRangeResponse.getSize();
            } else {
                loaded = length;
            }
        } else {
            loaded = 0L;
            if (contentLength < 0L) {
                contentLength = 0L;
            }
        }

        String serverEtag = (String)httpContext.getAttribute("yandex.etag-from-redirect");
        if (!partialContent) {
            downloadListener.setEtag(serverEtag);
        } else if (serverEtag != null && !serverEtag.equals(etag)) {
            response.consumeContent();
            throw new FileModifiedException("file changed, new etag is '" + serverEtag + "'");
        }

        downloadListener.setStartPosition(loaded);
        downloadListener.setContentLength(contentLength);
        InputStream content = response.getContent();
        OutputStream fos = downloadListener.getOutputStream(partialContent);

        try {
            byte[] downloadBuffer = new byte[1024];

            int count;
            while((count = content.read(downloadBuffer)) != -1) {
                if (downloadListener.hasCancelled()) {
                    // Log.i("TransportClient", "Downloading " + url + " canceled");
                    get.abort();
                    throw new CancelledDownloadException();
                }

                fos.write(downloadBuffer, 0, count);
                loaded += (long)count;
                downloadListener.updateProgress(loaded, contentLength);
            }
        } catch (CancelledDownloadException var33) {
            throw var33;
        } catch (Exception var34) {
            // Log.w("TransportClient", var34);
            get.abort();
            if (var34 instanceof IOException) {
                throw (IOException)var34;
            }

            if (var34 instanceof RuntimeException) {
                throw (RuntimeException)var34;
            }

            throw new RuntimeException(var34);
        } finally {
            try {
                fos.close();
            } catch (IOException var32) {
            }

            try {
                response.consumeContent();
            } catch (IOException var31) {
                // Log.w("TransportClient", var31);
            }

        }

    }

    private ContentRangeResponse parseContentRangeHeader(Header header) {
        if (header == null) {
            return null;
        } else {
            // Log.d("TransportClient", header.getName() + ": " + header.getValue());
            Matcher matcher = CONTENT_RANGE_HEADER_PATTERN.matcher(header.getValue());
            if (!matcher.matches()) {
                return null;
            } else {
                try {
                    return new ContentRangeResponse(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)));
                } catch (IllegalStateException var4) {
                    // Log.d("TransportClient", "parseContentRangeHeader: " + header, var4);
                    return null;
                } catch (NumberFormatException var5) {
                    // Log.d("TransportClient", "parseContentRangeHeader: " + header, var5);
                    return null;
                }
            }
        }
    }

    private static void checkPath(String path) throws IllegalArgumentException {
        if (path == null || path.contains("?")) {
            throw new IllegalArgumentException();
        }
    }

    public static String makePreviewPath(String path, TransportClient.PreviewSize size) throws IllegalArgumentException {
        checkPath(path);
        if (size == null) {
            throw new IllegalArgumentException();
        } else {
            return path + "?" + "preview" + "&" + "size" + "=" + size.name();
        }
    }

    public static String makePreviewPath(String path, int size) throws IllegalArgumentException {
        checkPath(path);
        if (size <= 0) {
            throw new IllegalArgumentException();
        } else {
            return path + "?" + "preview" + "&" + "size" + "=" + size;
        }
    }

    public static String makePreviewPath(String path, int sizeX, int sizeY) {
        checkPath(path);
        if (sizeX <= 0 && sizeY <= 0) {
            throw new IllegalArgumentException();
        } else {
            StringBuilder size = new StringBuilder();
            if (sizeX > 0) {
                size.append(sizeX);
            }

            size.append("x");
            if (sizeY > 0) {
                size.append(sizeY);
            }

            return path + "?" + "preview" + "&" + "size" + "=" + size;
        }
    }

    public void makeFolder(String dir) throws IOException, DuplicateFolderException, IntermediateFolderNotExistException, WebdavUserNotInitialized, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException, UnsupportedMediaTypeException, UnknownServerWebdavException {
        String url = this.getUrl() + encodeURL(dir);
        TransportClient.HttpMkcol mkcol = new TransportClient.HttpMkcol(url);
        logMethod(mkcol);
        this.creds.addAuthHeader(mkcol);
        HttpResponse response = this.executeRequest(mkcol);
        consumeContent(response);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine != null) {
            int statusCode = statusLine.getStatusCode();
            switch(statusCode) {
            case 201:
                // Log.d("TransportClient", "Folder created successfully");
                return;
            case 405:
                throw new DuplicateFolderException("Folder '" + dir + "' already exists");
            case 409:
                throw new IntermediateFolderNotExistException("Parent folder not exists for '" + dir + "'");
            case 415:
                throw new UnsupportedMediaTypeException("Folder '" + dir + "' creation error (http code 415)");
            default:
                this.checkStatusCodes(response, "MKCOL '" + dir + "'");
            }
        }

    }

    public void delete(String path) throws IOException, WebdavFileNotFoundException, WebdavUserNotInitialized, UnknownServerWebdavException, PreconditionFailedException, WebdavNotAuthorizedException, ServerWebdavException {
        String url = this.getUrl() + encodeURL(path);
        HttpDelete delete = new HttpDelete(url);
        logMethod(delete);
        this.creds.addAuthHeader(delete);
        HttpResponse response = this.executeRequest(delete);
        consumeContent(response);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine != null) {
            switch(statusLine.getStatusCode()) {
            case 200:
                // Log.d("TransportClient", "Delete successfully completed");
                return;
            case 404:
                throw new WebdavFileNotFoundException("'" + path + "' cannot be deleted");
            default:
                this.checkStatusCodes(response, "DELETE '" + path + "'");
            }
        }

    }

    public void move(String src, String dest) throws WebdavException, IOException {
        TransportClient.Move move = new TransportClient.Move(this.getUrl() + encodeURL(src));
        move.setHeader("Destination", encodeURL(dest));
        move.setHeader("Overwrite", "F");
        logMethod(move, "to " + encodeURL(dest));
        this.creds.addAuthHeader(move);
        HttpResponse response = this.executeRequest(move);
        consumeContent(response);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine != null) {
            int statusCode = statusLine.getStatusCode();
            switch(statusCode) {
            case 201:
                // Log.d("TransportClient", "Rename successfully completed");
                return;
            case 202:
            case 207:
                // Log.d("TransportClient", "HTTP code " + statusCode + ": " + statusLine);
                return;
            case 404:
                throw new WebdavFileNotFoundException("'" + src + "' not found");
            case 409:
                throw new DuplicateFolderException("File or folder " + dest + " already exist");
            default:
                this.checkStatusCodes(response, "MOVE '" + src + "' to '" + dest + "'");
            }
        }

    }

    public String publish(String path) throws IOException, WebdavException {
        HttpPost post = new HttpPost(this.getUrl() + encodeURL(path) + "?publish");
        logMethod(post, "(publish)");
        this.creds.addAuthHeader(post);
        HttpContext shareHttpContext = new BasicHttpContext();
        shareHttpContext.setAttribute("yandex.no-redirect", true);
        HttpResponse httpResponse = this.executeRequest(post, shareHttpContext);
        consumeContent(httpResponse);
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine != null) {
            int statusCode = statusLine.getStatusCode();
            switch(statusCode) {
            case 302:
                Header[] locationHeaders = httpResponse.getHeaders("Location");
                if (locationHeaders.length == 1) {
                    String url = httpResponse.getHeaders("Location")[0].getValue();
                    // Log.d("TransportClient", "publish: " + url);
                    return url;
                }

                this.checkStatusCodes(httpResponse, "publish");
                break;
            case 403:
                throw new WebdavSharingForbiddenException("Folder " + path + " can't be shared");
            default:
                this.checkStatusCodes(httpResponse, "publish");
            }
        }

        return null;
    }

    public void unpublish(String path) throws IOException, WebdavException {
        HttpPost post = new HttpPost(this.getUrl() + encodeURL(path) + "?unpublish");
        logMethod(post, "(unpublish)");
        this.creds.addAuthHeader(post);
        HttpResponse httpResponse = this.executeRequest(post);
        consumeContent(httpResponse);
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine == null || statusLine.getStatusCode() != 200) {
            this.checkStatusCodes(httpResponse, "unpublish");
        }
    }

    static {
        try {
            serverURL = new URL("https://webdav.yandex.ru:443");
        } catch (MalformedURLException var1) {
            throw new RuntimeException(var1);
        }

        requestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException ex, int count, HttpContext httpContext) {
                return false;
            }
        };
        redirectHandler = new DefaultRedirectHandler() {
            public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
                Object noRedirect = httpContext.getAttribute("yandex.no-redirect");
                if (noRedirect != null && (Boolean)noRedirect) {
                    return false;
                } else {
                    Header etagHeader = httpResponse.getFirstHeader("Etag");
                    if (etagHeader != null) {
                        httpContext.setAttribute("yandex.etag-from-redirect", etagHeader.getValue());
                    }

                    return super.isRedirectRequested(httpResponse, httpContext);
                }
            }
        };
        CONTENT_RANGE_HEADER_PATTERN = Pattern.compile("bytes\\D+(\\d+)-\\d+/(\\d+)");
    }

    private static class Move extends HttpPut {
        public Move(String url) {
            super(url);
        }

        public String getMethod() {
            return "MOVE";
        }
    }

    private static class HttpMkcol extends HttpPut {
        public HttpMkcol(String url) {
            super(url);
        }

        public String getMethod() {
            return "MKCOL";
        }
    }

    public static class PropFind extends HttpPost {
        public PropFind() {
        }

        public PropFind(String url) {
            super(url);
        }

        public String getMethod() {
            return "PROPFIND";
        }
    }

    public static enum PreviewSize {
        XXXS,
        XXS,
        XS,
        S,
        M,
        L,
        XL,
        XXL,
        XXXL;

        private PreviewSize() {
        }
    }

    public static enum HashType {
        MD5,
        SHA256;

        private HashType() {
        }
    }
}