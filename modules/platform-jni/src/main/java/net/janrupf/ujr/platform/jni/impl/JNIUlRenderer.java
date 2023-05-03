package net.janrupf.ujr.platform.jni.impl;

import net.janrupf.ujr.api.UltralightView;
import net.janrupf.ujr.api.config.UlViewConfig;
import net.janrupf.ujr.core.platform.abstraction.UlRenderer;
import net.janrupf.ujr.core.platform.abstraction.UlSession;
import net.janrupf.ujr.core.platform.abstraction.UlView;
import net.janrupf.ujr.platform.jni.ffi.NativeAccess;

import java.net.InetAddress;

public class JNIUlRenderer implements UlRenderer {
    @NativeAccess
    private final long handle;

    private JNIUlRenderer() {
        throw new RuntimeException("Allocate in native code without calling constructor");
    }

    @Override
    public UlSession createSession(boolean isPersistent, String name) {
        return nativeCreateSession(isPersistent, name);
    }

    private native JNIUlSession nativeCreateSession(boolean isPersistent, String name);

    @Override
    public UlSession defaultSession() {
        return nativeDefaultSession();
    }

    private native JNIUlSession nativeDefaultSession();

    @Override
    public UlView createView(int width, int height, UlViewConfig config, UlSession session) {
        return nativeCreateView(width, height, config, (JNIUlSession) session);
    }

    private native UlView nativeCreateView(int width, int height, UlViewConfig config, JNIUlSession session);

    @Override
    public void update() {
        nativeUpdate();
    }

    private native void nativeUpdate();

    @Override
    public void render() {
        nativeRender();
    }

    private native void nativeRender();

    @Override
    public void renderOnly(UltralightView[] views) {
        nativeRenderOnly(views);
    }

    private native void nativeRenderOnly(UltralightView[] views);

    @Override
    public void purgeMemory() {
        nativePurgeMemory();
    }

    private native void nativePurgeMemory();

    @Override
    public void logMemoryUsage() {
        nativeLogMemoryUsage();
    }

    private native void nativeLogMemoryUsage();

    @Override
    public boolean startRemoteInspectorServer(InetAddress address, int port) {
        return nativeStartRemoteInspectorServer(address.getHostAddress(), port);
    }

    private native boolean nativeStartRemoteInspectorServer(String address, int port);

    @Override
    public void setGamepadDetails(long index, String id, long axisCount, long buttonCount) {
        nativeSetGamepadDetails(index, id, axisCount, buttonCount);
    }

    private native void nativeSetGamepadDetails(long index, String id, long axisCount, long buttonCount);
}
