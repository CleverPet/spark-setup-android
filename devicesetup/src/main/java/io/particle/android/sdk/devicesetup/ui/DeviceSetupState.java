package io.particle.android.sdk.devicesetup.ui;


import com.google.common.collect.*;

import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

// FIXME: Statically defined, global, mutable state...  refactor this thing into oblivion soon.
public class DeviceSetupState {

    static final Set<String> claimedDeviceIds = new ConcurrentSkipListSet<>();
    public static volatile String previouslyConnectedWifiNetwork;
    static volatile Map<String/*product_slug*/, String/*claimCode*/> claimCodes = Maps.newHashMap();
    static volatile PublicKey publicKey;
    static volatile String deviceToBeSetUpId;
    static volatile boolean deviceNeedsToBeClaimed = true;

    static void reset() {
        claimCodes.clear();
        claimedDeviceIds.clear();
        publicKey = null;
        deviceToBeSetUpId = null;
        deviceNeedsToBeClaimed = true;
        previouslyConnectedWifiNetwork = null;
    }
}
