/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.telecom.cts;

import static android.telecom.CallAudioState.*;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccountHandle;
import android.telecom.RemoteConnection;
import android.telecom.VideoProfile;
import android.telecom.cts.BaseTelecomTestWithMockServices.InvokeCounter;
import android.util.SparseArray;

/**
 * {@link Connection} subclass that immediately performs any state changes that are a result of
 * callbacks sent from Telecom.
 */
public class MockConnection extends Connection {
    public static final int ON_POST_DIAL_WAIT = 1;

    private CallAudioState mCallAudioState =
            new CallAudioState(false, CallAudioState.ROUTE_EARPIECE, ROUTE_EARPIECE | ROUTE_SPEAKER);
    private int mState = STATE_NEW;
    public int videoState = VideoProfile.STATE_AUDIO_ONLY;
    private String mDtmfString = "";
    private MockVideoProvider mMockVideoProvider;
    private PhoneAccountHandle mPhoneAccountHandle;
    private RemoteConnection mRemoteConnection = null;

    private SparseArray<InvokeCounter> mInvokeCounterMap = new SparseArray<>(10);

    @Override
    public void onAnswer() {
        super.onAnswer();
    }

    @Override
    public void onAnswer(int videoState) {
        super.onAnswer(videoState);
        this.videoState = videoState;
        setActive();
        if (mRemoteConnection != null) {
            mRemoteConnection.answer();
        }
    }

    @Override
    public void onReject() {
        super.onReject();
        setDisconnected(new DisconnectCause(DisconnectCause.REJECTED));
        if (mRemoteConnection != null) {
            mRemoteConnection.reject();
        }
    }

    @Override
    public void onHold() {
        super.onHold();
        setOnHold();
        if (mRemoteConnection != null) {
            mRemoteConnection.hold();
        }
    }

    @Override
    public void onUnhold() {
        super.onUnhold();
        setActive();
        if (mRemoteConnection != null) {
            mRemoteConnection.unhold();
        }
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
        setDisconnected(new DisconnectCause(DisconnectCause.LOCAL));
        destroy();
        if (mRemoteConnection != null) {
            mRemoteConnection.disconnect();
        }
    }

    @Override
    public void onAbort() {
        super.onAbort();
        if (mRemoteConnection != null) {
            mRemoteConnection.abort();
        }
    }

    @Override
    public void onPlayDtmfTone(char c) {
        super.onPlayDtmfTone(c);
        mDtmfString += c;
        if (mRemoteConnection != null) {
            mRemoteConnection.playDtmfTone(c);
        }
    }

    @Override
    public void onStopDtmfTone() {
        super.onStopDtmfTone();
        mDtmfString += ".";
        if (mRemoteConnection != null) {
            mRemoteConnection.stopDtmfTone();
        }
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState state) {
        super.onCallAudioStateChanged(state);
        mCallAudioState = state;
        if (mRemoteConnection != null) {
            mRemoteConnection.setCallAudioState(state);
        }
    }

    @Override
    public void onStateChanged(int state) {
        super.onStateChanged(state);
        mState = state;
    }

    @Override
    public void onPostDialContinue(boolean proceed) {
        super.onPostDialContinue(proceed);
        if (mInvokeCounterMap.get(ON_POST_DIAL_WAIT) != null) {
            mInvokeCounterMap.get(ON_POST_DIAL_WAIT).invoke(proceed);
        }
    }

    public int getCurrentState()  {
        return mState;
    }

    public CallAudioState getCurrentCallAudioState() {
        return mCallAudioState;
    }

    public String getDtmfString() {
        return mDtmfString;
    }

    public InvokeCounter getInvokeCounter(int counterIndex) {
        if (mInvokeCounterMap.get(counterIndex) == null) {
            mInvokeCounterMap.put(counterIndex,
                    new InvokeCounter(getCounterLabel(counterIndex)));
        }
        return mInvokeCounterMap.get(counterIndex);
    }

    /**
     * Creates a mock video provider for this connection.
     */
    public void createMockVideoProvider() {
        final MockVideoProvider mockVideoProvider = new MockVideoProvider(this);
        mMockVideoProvider = mockVideoProvider;
        setVideoProvider(mockVideoProvider);
    }

    public void sendMockVideoQuality(int videoQuality) {
        if (mMockVideoProvider == null) {
            return;
        }
        mMockVideoProvider.sendMockVideoQuality(videoQuality);
    }

    public void sendMockCallSessionEvent(int event) {
        if (mMockVideoProvider == null) {
            return;
        }
        mMockVideoProvider.sendMockCallSessionEvent(event);
    }

    public void sendMockPeerWidth(int width) {
        if (mMockVideoProvider == null) {
            return;
        }
        mMockVideoProvider.sendMockPeerWidth(width);
    }

    public void sendMockSessionModifyRequest(VideoProfile request) {
        if (mMockVideoProvider == null) {
            return;
        }
        mMockVideoProvider.sendMockSessionModifyRequest(request);
    }

    public MockVideoProvider getMockVideoProvider() {
        return mMockVideoProvider;
    }

    public void setPhoneAccountHandle(PhoneAccountHandle handle)  {
        mPhoneAccountHandle = handle;
    }

    public PhoneAccountHandle getPhoneAccountHandle()  {
        return mPhoneAccountHandle;
    }

    public void setRemoteConnection(RemoteConnection remoteConnection)  {
        mRemoteConnection = remoteConnection;
    }

    public RemoteConnection getRemoteConnection()  {
        return mRemoteConnection;
    }

    private static String getCounterLabel(int counterIndex) {
        switch (counterIndex) {
            case ON_POST_DIAL_WAIT:
                return "onPostDialWait";
            default:
                return "Callback";
        }
    }
}
