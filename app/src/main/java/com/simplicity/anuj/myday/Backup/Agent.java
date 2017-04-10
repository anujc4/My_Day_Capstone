package com.simplicity.anuj.myday.Backup;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by anujc on 4/10/2017.
 */

public class Agent extends BackupAgent {
    @Override
    public void onQuotaExceeded(long backupDataBytes, long quotaBytes) {
        super.onQuotaExceeded(backupDataBytes, quotaBytes);
        Toast.makeText(this, "Quota for Backup Exceeded. Please inform Developer of this Incident.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRestoreFinished() {
        super.onRestoreFinished();
        Toast.makeText(this, "Never miss a moment .All your preferences and settings have been restored.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {

    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {

    }
}
