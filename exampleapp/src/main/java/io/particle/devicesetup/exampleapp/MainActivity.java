package io.particle.devicesetup.exampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.io.*;

import org.joda.time.DateTime;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.*;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleUser;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.android.sdk.utils.ui.Ui;

public class MainActivity extends AppCompatActivity {

    static Pattern isParticleIdPattern = Pattern.compile("\\p{XDigit}{24}");

    static boolean isParticleId(String particleId) {
        // e.g., 27001a001847343338333633
        if (particleId != null)
            return isParticleIdPattern.matcher(particleId).matches();
        return false;
    }

    private List<String> particleIds() {
        try {
            final InputStream in = getResources().openRawResource(R.raw.current_p1_log);
            try {
                return CharStreams.readLines(new InputStreamReader(in, Charsets.UTF_8), new LineProcessor<List<String>>() {
                    List<String> particleIds = Lists.newArrayList();
                    Splitter splitter = Splitter.on(Pattern.compile("[^\\p{XDigit}]"));
                    @Override
                    public boolean processLine(String line) throws IOException {
                        for (String s : splitter.split(line)) {
                            if (isParticleId(s))
                                particleIds.add(s);
                        }
                        return true;
                    }
                    @Override
                    public List<String> getResult() {
                        return particleIds;
                    }
                });
            } finally {
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParticleDeviceSetupLibrary.init(this.getApplicationContext(), MainActivity.class, new ParticleDeviceSetupLibrary.DeviceSetupResolver() {
            @Override
            public String getProductSlug(String deviceId) {
                if (particleIds().contains(deviceId))
                    return "cleverpet---p1-020";
                return "cleverpet-01-v010";
            }
        });
        Ui.findView(this, R.id.start_setup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invokeDeviceSetup();
            }
        });
    }

    public void invokeDeviceSetup() {

        String access_token = "insert_access_token_here";
        String expires = "2017-09-21T17:10:12.395780Z";

        DateTime expirationDate = new DateTime(expires);

        ParticleCloud spark = ParticleCloudSDK.getCloud();
        try {
//            spark.setUser("x3@xxx.xxx");
            //###HACK
            Field particleUserField = spark.getClass().getDeclaredField("user");
            particleUserField.setAccessible(true);
            ParticleUser particleUser = ParticleUser.fromNewCredentials("x3@xxx.xxx", "no_password");
            particleUserField.set(spark, particleUser);
            //###HACK_END

            spark.setAccessToken(access_token, expirationDate.toDate());

            ParticleDeviceSetupLibrary.startDeviceSetup(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
