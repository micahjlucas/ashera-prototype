package scal.io.ashera_prototype;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import java.io.File;


public class MainActivity extends Activity {

    private static final String sAccessKey = "Te8eJIS48D6N32Ju";
    private static final String sSecretKey = "HI4q8EWv1Rn2Bgfu";

    private static final String sBucketName = "micah_scal_Dog";
    private static final String sBucketKey = "opera";
    private static final String sArchiveAPIEndpoint = "http://s3.us.archive.org/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test();
    }

    private void test() {
        AWSCredentials credential = new BasicAWSCredentials(sAccessKey, sSecretKey);
        ClientConfiguration s3Config = new ClientConfiguration();
        //s3Config.setProxyHost();
        //s3Config.setProxyPort();

        AmazonS3 s3Client = new AmazonS3Client(credential, s3Config);
        s3Client.setEndpoint(sArchiveAPIEndpoint);

        TransferManager manager = new TransferManager(s3Client);

        File file = new File(Environment.getExternalStorageDirectory() + "/opera.mp3");
        if(!file.exists()) {
            return;
        }

        //set metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());

        // Transfer a file to an S3 bucket.
        Upload upload = manager.upload(sBucketName, //path that will appear in URL
                sBucketKey, //actual name of file within bucket
                file);

        try {
            UploadResult result = upload.waitForUploadResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!upload.isDone()) {
            long transfered = upload.getProgress().getBytesTransfered();
            // publish progress

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {

        String existingBucketName = "test_bucket";
        String keyName = "*** Provide object key ***";
        String filePath = "*** Path to and name of the file to upload ***";

        TransferManager tm = new TransferManager(new ProfileCredentialsProvider());
        System.out.println("Hello");
        // TransferManager processes all transfers asynchronously,
        // so this call will return immediately.
        Upload upload = tm.upload(existingBucketName, keyName, new File(filePath));

        try {
            // Or you can block and wait for the upload to finish
            upload.waitForCompletion();
            System.out.println("Upload complete.");
        } catch (AmazonClientException amazonClientException) {
            System.out.println("Unable to upload file, upload was aborted.");
            amazonClientException.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

