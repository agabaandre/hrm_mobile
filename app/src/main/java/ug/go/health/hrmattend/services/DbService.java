package ug.go.health.hrmattend.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ug.go.health.hrmattend.database.AppDatabase;
import ug.go.health.hrmattend.models.ClockHistory;
import ug.go.health.hrmattend.models.StaffRecord;

public class DbService {
    private static final String TAG = DbService.class.getSimpleName();
    private final AppDatabase database;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public DbService(Context context) {
        database = AppDatabase.getInstance(context);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void getFilteredClockHistoryAsync(String name, Date startDate, Date endDate, Callback<List<ClockHistory>> callback) {

        Long startTimestamp = startDate != null ? startDate.getTime() : null;
        Long endTimestamp = endDate != null ? endDate.getTime() : null;

        executorService.execute(() -> {
            List<ClockHistory> result = database.clockHistoryDao().getFilteredClockHistory(name, startTimestamp, endTimestamp);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getClockHistoryAsync(Callback<List<ClockHistory>> callback) {
        executorService.execute(() -> {
            List<ClockHistory> result = database.clockHistoryDao().getAllClockHistory();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void saveClockHistoryAsync(ClockHistory clockHistory, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                database.clockHistoryDao().insert(clockHistory);
                mainHandler.post(() -> callback.onResult(true));
            } catch (Exception e) {
                Log.e(TAG, "Error saving clock history", e);
                mainHandler.post(() -> callback.onResult(false));
            }
        });
    }

    public void getStaffRecordsAsync(Callback<List<StaffRecord>> callback) {
        executorService.execute(() -> {
            List<StaffRecord> result = database.staffRecordDao().getAllStaffRecords();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void saveStaffRecordAsync(StaffRecord staffRecord, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                database.staffRecordDao().insert(staffRecord);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onResult(true);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error saving staff record", e);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onResult(false);
                    }
                });
            }
        });
    }

    public void updateStaffRecordAsync(StaffRecord staffRecord, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                database.staffRecordDao().update(staffRecord);
                mainHandler.post(() -> callback.onResult(true));
            } catch (Exception e) {
                Log.e(TAG, "Error updating staff record", e);
                mainHandler.post(() -> callback.onResult(false));
            }
        });
    }

    public void getLastClockHistoryAsync(String ihrisPid, Callback<ClockHistory> callback) {
        executorService.execute(() -> {
            ClockHistory result = database.clockHistoryDao().getLastClockHistory(ihrisPid);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getStaffRecordByihrisPIDAsync(String ihrisPid, Callback<StaffRecord> callback) {
        executorService.execute(() -> {
            StaffRecord result = database.staffRecordDao().getStaffRecordByIhrisPid(ihrisPid);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void clearStaffListAsync(Callback<Void> callback) {
        executorService.execute(() -> {
            database.staffRecordDao().deleteAll();
            mainHandler.post(() -> callback.onResult(null));
        });
    }

    public void getStaffRecordsWithEmbeddingsAsync(Callback<List<StaffRecord>> callback) {
        executorService.execute(() -> {
            List<StaffRecord> result = database.staffRecordDao().getStaffRecordsWithEmbeddings();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void enrollUserFaceAsync(float[] embeddings, String ihrisPid, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                StaffRecord staffRecord = database.staffRecordDao().getStaffRecordByIhrisPid(ihrisPid);
                if (staffRecord != null) {
                    staffRecord.setFaceData(embeddings);
                    staffRecord.setFaceEnrolled(true);
                    database.staffRecordDao().update(staffRecord);
                    mainHandler.post(() -> callback.onResult(true));
                } else {
                    mainHandler.post(() -> callback.onResult(false));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error enrolling user face", e);
                mainHandler.post(() -> callback.onResult(false));
            }
        });
    }

    public void getStaffRecordByTemplateAsync(int template, Callback<StaffRecord> callback) {
        executorService.execute(() -> {
            StaffRecord result = database.staffRecordDao().getStaffRecordByTemplate(template);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getUnsyncedStaffRecordsAsync(Callback<List<StaffRecord>> callback) {
        executorService.execute(() -> {
            List<StaffRecord> result = database.staffRecordDao().getUnsyncedStaffRecords();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getSyncedStaffRecordsAsync(Callback<List<StaffRecord>> callback) {
        executorService.execute(() -> {
            List<StaffRecord> result = database.staffRecordDao().getAllStaffRecords();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getStaffRecordsReadyForSyncAsync(Callback<List<StaffRecord>> callback) {
        executorService.execute(() -> {
            List<StaffRecord> result = database.staffRecordDao().getStaffRecordsReadyForSync();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getStaffRecordsMissingInfoAsync(Callback<List<StaffRecord>> callback) {
        executorService.execute(() -> {
            List<StaffRecord> result = database.staffRecordDao().getStaffRecordsMissingInfo();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getUnsyncedClockHistoryAsync(Callback<List<ClockHistory>> callback) {
        executorService.execute(() -> {
            List<ClockHistory> result = database.clockHistoryDao().getUnsyncedClockHistory();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getSyncedClockHistoryAsync(Callback<List<ClockHistory>> callback) {
        executorService.execute(() -> {
            List<ClockHistory> result = database.clockHistoryDao().getAllClockHistory();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void updateClockHistoryAsync(ClockHistory clockHistory, Callback<Void> callback) {
        executorService.execute(() -> {
            database.clockHistoryDao().update(clockHistory);
            mainHandler.post(() -> callback.onResult(null));
        });
    }

    public void countUnsyncedClockRecordsAsync(Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = database.clockHistoryDao().countUnsyncedClockRecords();
            mainHandler.post(() -> callback.onResult(count));
        });
    }

    public void countSyncedClockRecordsAsync(Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = database.clockHistoryDao().countSyncedClockRecords();
            mainHandler.post(() -> callback.onResult(count));
        });
    }

    public void countUnsyncedStaffRecordsAsync(Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = database.staffRecordDao().countUnsyncedStaffRecords();
            mainHandler.post(() -> callback.onResult(count));
        });
    }

    public void countSyncedStaffRecordsAsync(Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = database.staffRecordDao().countSyncedStaffRecords();
            mainHandler.post(() -> callback.onResult(count));
        });
    }

    public void countStaffRecordsAsync(Callback<Integer> callback) {
        executorService.execute(() -> {
            int count = database.staffRecordDao().countStaffRecords();
            mainHandler.post(() -> callback.onResult(count));
        });
    }

    // This method is left as is since it doesn't involve database operations
    private byte[] convertFloatArrayToByteArray(float[] floatArray) {
        // Implement conversion logic here
        return new byte[0];
    }

    public void shutdown() {
        executorService.shutdown();
    }
}