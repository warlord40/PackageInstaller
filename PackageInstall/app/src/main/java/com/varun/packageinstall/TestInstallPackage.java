package com.varun.packageinstall;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestInstallPackage {
    private static final String PACKAGE_INSTALLED_ACTION = "com.varun.packageinstall";
    public static String TAG ="TestInstallPackage";

    public static void packageInstallApk(final String filename, Context mContext)
    {
        PackageInstaller.Session session = null;
        try {
            Log.d(TAG, "installPackageApk " + filename);
            PackageInstaller packageInstaller = mContext.getPackageManager().getPackageInstaller();
            Log.d(TAG, "installPackageApk - got packageInstaller");
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            Log.d(TAG, "installPackageApk - set SessionParams");
            int sessionId = packageInstaller.createSession(params);
            session = packageInstaller.openSession(sessionId);
            Log.d(TAG, "installPackageApk - session opened");

            silentApkInstallation(mContext, filename, session);
            Log.d(TAG, "installPackageApk - apk added to session");

            Intent intent = new Intent(mContext, TestInstallPackage.class);
            intent.setAction(PACKAGE_INSTALLED_ACTION);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
            IntentSender statusReceiver = pendingIntent.getIntentSender();
            session.commit(statusReceiver);
            Log.d(TAG, "installPackageApk - committed");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't install package", e);
        } catch (RuntimeException e) {
            if (session != null) {
                session.abandon();
            }
            throw e;
        }
    }


    private static void silentApkInstallation(Context context, String filename, PackageInstaller.Session session)
    {
        Log.d(TAG, "silentApkInstallation " + filename);
        try {
            OutputStream packageInStream = session.openWrite("package", 0, -1);
            InputStream getInput;
            Uri uri = Uri.parse(filename);
            getInput = context.getContentResolver().openInputStream(uri);

            if(getInput != null) {
                Log.d(TAG, "Input.available: " + getInput.available());
                byte[] buffer = new byte[16384];
                int n;
                while ((n = getInput.read(buffer)) >= 0) {
                    packageInStream.write(buffer, 0, n);
                }
            }
            else {
                Log.d(TAG, "silentApkInstallation failed");
                throw new IOException ("silentApkInstallation");
            }
            packageInStream.close();
            getInput.close();
        }
        catch (Exception e) {
            Log.d(TAG, "silentApkInstallation failed " + e.toString());
        }

    }

    //yet to use.
    public void onGetIntent(Intent intent, Context mContext){
        Bundle extras = intent.getExtras();
        if(PACKAGE_INSTALLED_ACTION.equals((intent.getAction()))){
            int status = extras.getInt((PackageInstaller.EXTRA_STATUS));
            String message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);
            switch (status){
                case PackageInstaller.STATUS_SUCCESS:
                    Toast.makeText(mContext, "Install succeeded!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "PackageInstaller.STATUS_FAILURE_ABORTED");
                    break;
                case PackageInstaller.STATUS_FAILURE_STORAGE:
                    Toast.makeText(mContext, "Install failed! " + status + ", " + message,
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "PackageInstaller.STATUS_FAILURE_STORAGE");

                    break;
                case PackageInstaller.STATUS_FAILURE:
                    Toast.makeText(mContext, "Install failed! Failure " + status + ", " + message,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "PackageInstaller.STATUS_FAILURE");
                    break;
                case PackageInstaller.STATUS_FAILURE_ABORTED:
                    Toast.makeText(mContext, "Install failed! Aborted " + status + ", " + message,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "PackageInstaller.STATUS_FAILURE_ABORTED");
                    break;
                case PackageInstaller.STATUS_FAILURE_BLOCKED:
                    Toast.makeText(mContext, "Install failed! Blocked " + status + ", " + message,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "PackageInstaller.STATUS_FAILURE_ABORTED");
                    break;
                case PackageInstaller.STATUS_FAILURE_CONFLICT:
                    Toast.makeText(mContext, "Install failed! Conflict " + status + ", " + message,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "PackageInstaller.STATUS_FAILURE_ABORTED");
                    break;
                case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
                    Toast.makeText(mContext, "Install failed! Incompatible " + status + ", " + message,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "PackageInstaller.STATUS_FAILURE_ABORTED");
                    break;
                case PackageInstaller.STATUS_FAILURE_INVALID:
                    Toast.makeText(mContext, "Install failed!  Invalid" + status + ", " + message,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "PackageInstaller.STATUS_FAILURE_ABORTED");
                    break;
                default:
                    Toast.makeText(mContext, "Unrecognized status received from installer: " + status,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "PackageInstaller.STATUS_FAILURE_ABORTED");
            }
        }
    }
}