import com.yandex.disk.client.Credentials;
import com.yandex.disk.client.TransportClient;
import com.yandex.disk.client.ProgressListener;
import com.yandex.disk.client.exceptions.WebdavException;
import com.yandex.disk.client.exceptions.CancelledDownloadException;
import com.yandex.disk.client.exceptions.CancelledUploadingException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CloudLib {

    /** PUBLIC */

    public static boolean load(final String token, final String remoteFullPath, final String localFullPath) {

        CDownloadFileRetained load = new CDownloadFileRetained();

        return load.loadFile(token, remoteFullPath, localFullPath);
    }

    public static boolean upload(final String token, final String remoteFullPath, final String localFullPath) {

        CUploadFileRetained upload = new CUploadFileRetained();

        return upload.uploadFile(token, remoteFullPath, localFullPath);
    }

    /** PRIVATE */

    private static class CDownloadFileRetained implements ProgressListener {

        public boolean loadFile(final String token, final String remoteFullPath, final String localFullPath) {

            AtomicBoolean result = new AtomicBoolean(false);

            Thread thr = new Thread(() -> {

                TransportClient client = null;

                try {

                    File file = new File(localFullPath);
                    if(file.exists()) {
                        result.set(file.delete());
                        if(!result.get())
                            throw new IOException();
                    }

                    result.set(file.createNewFile());
                    if (!result.get())
                        throw new IOException();

                    client = TransportClient.getInstance(new Credentials("", token));
                    client.downloadFile(remoteFullPath, file, CDownloadFileRetained.this);

                    result.set(true);

                    System.out.println("load: success");
                } catch (CancelledDownloadException ex) {
                    System.out.println("load: cancelled");
                } catch (IOException | WebdavException exception) {
                    System.out.println("load: error");
                    exception.printStackTrace();
                } finally {
                    TransportClient.shutdown(client);
                }
            });

            thr.start();

            try {
                thr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                result.set(false);
            }

            return result.get();
        }

        @Override
        public void updateProgress(final long loaded, final long total) {
        }

        @Override
        public boolean hasCancelled() {
            return false;
        }
    }

    private static class CUploadFileRetained implements ProgressListener {

        public boolean uploadFile(final String token, final String remoteFullPath, final String localFullPath) {

            AtomicBoolean result = new AtomicBoolean(false);

            Thread thr = new Thread(() -> {

                TransportClient client = null;

                try {
                    client = TransportClient.getUploadInstance(new Credentials("", token));
                    client.uploadFile(localFullPath, remoteFullPath, CUploadFileRetained.this);

                    result.set(true);

                    System.out.println("upload: success");
                } catch (CancelledUploadingException ex) {
                    System.out.println("upload: cancelled");
                } catch (IOException | WebdavException exception) {
                    System.out.println("upload: error");
                    exception.printStackTrace();
                } finally {
                    TransportClient.shutdown(client);
                }
            });

            thr.start();

            try {
                thr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                result.set(false);
            }

            return result.get();
        }

        @Override
        public void updateProgress(final long loaded, final long total) {
        }

        @Override
        public boolean hasCancelled() {
            return false;
        }
    }
}
