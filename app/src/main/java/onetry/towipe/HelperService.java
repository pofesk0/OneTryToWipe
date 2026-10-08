package onetry.towipe;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import java.util.*;
import android.app.*;
import android.app.admin.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.provider.*;
import android.os.storage.*;

public class HelperService extends JobService {	

	private static final int PERIODIC_JOB_ID = 1001;
    private static final int DELAYED_JOB_ID = 1002;

    @Override
    public boolean onStartJob(JobParameters params) {
        scheduleJobs(getApplicationContext());        
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {        
        return false;
    }

    public static void scheduleJobs(Context context) {
		try {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) return;

        ComponentName componentName = new ComponentName(context, HelperService.class);

        boolean isPeriodicScheduled = false;
        for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == PERIODIC_JOB_ID) {
                isPeriodicScheduled = true;
                break;
            }
        }

        if (!isPeriodicScheduled) {
            JobInfo.Builder periodicBuilder = new JobInfo.Builder(PERIODIC_JOB_ID, componentName)
                    .setPeriodic(JobInfo.getMinPeriodMillis())
                    .setPersisted(true)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false);

            if (Build.VERSION.SDK_INT >= 26) {
                periodicBuilder.setRequiresBatteryNotLow(false);
                periodicBuilder.setRequiresStorageNotLow(false);
            }

            jobScheduler.schedule(periodicBuilder.build());
        }

        JobInfo.Builder delayedBuilder = new JobInfo.Builder(DELAYED_JOB_ID, componentName)
                .setMinimumLatency(15 * 1000L)                
                .setPersisted(true)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false);

        if (Build.VERSION.SDK_INT >= 26) {
            delayedBuilder.setRequiresBatteryNotLow(false);
            delayedBuilder.setRequiresStorageNotLow(false);
        }

        jobScheduler.schedule(delayedBuilder.build());
		} catch (Throwable t) {}	
    }
	
			
		@Override
    public final void onCreate() {
        super.onCreate();		
		scheduleJobs(this);
		forceBindAndStart();					
	}		
		
	private final void forceBindAndStart() {
    Intent intent = new Intent(this, WatcherService.class);
    bindService(intent, connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
    try {startService(intent);} 
    catch (Throwable t) {}
    }
    
    private final ServiceConnection connection = new ServiceConnection() {
        @Override public final void onServiceConnected(ComponentName name, IBinder service) {}
        @Override
        public final void onServiceDisconnected(ComponentName name) {
            forceBindAndStart();
        }
    };


    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {    
    return START_STICKY;
    }

    @Override
    public final void onDestroy() {		
        scheduleJobs(getApplicationContext());                
		super.onDestroy();
    }
}
