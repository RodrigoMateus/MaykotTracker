package com.maykot.maykottracker;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * @author Leonardo
 * @since  14/09/15
 */
@ReportsCrashes(
        formUri = "https://eprodutiva.cloudant.com/acra-eprodutiva/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "oughteredereseddistrelly",
        formUriBasicAuthPassword = "3a0e84dc81de1260a08ebc1bdf236178f9777879",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.app_name)
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }

}
