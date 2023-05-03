#include "ujr/Session.hpp"
#include "net_janrupf_ujr_platform_jni_impl_JNIUlSession.h"
#include "net_janrupf_ujr_platform_jni_impl_JNIUlSession_native_access.hpp"

#include "ujr/util/JniEntryGuard.hpp"

JNIEXPORT jboolean JNICALL
Java_net_janrupf_ujr_platform_jni_impl_JNIUlSession_nativeIsPersistent(JNIEnv *env, jobject self) {
    return ujr::jni_entry_guard(env, [&](auto env) {
        using ujr::native_access::JNIUlSession;

        auto *session = reinterpret_cast<ultralight::Session *>(JNIUlSession::HANDLE.get(env, self));

        return session->is_persistent();
    });
}

JNIEXPORT jstring JNICALL Java_net_janrupf_ujr_platform_jni_impl_JNIUlSession_nativeName(JNIEnv *env, jobject self) {
    return ujr::jni_entry_guard(env, [&](auto env) {
        using ujr::native_access::JNIUlSession;

        auto *session = reinterpret_cast<ultralight::Session *>(JNIUlSession::HANDLE.get(env, self));
        auto j_name = ujr::JniLocalRef<jstring>::from_utf16(env, session->name().utf16());

        return j_name.leak();
    });
}

JNIEXPORT jlong JNICALL Java_net_janrupf_ujr_platform_jni_impl_JNIUlSession_nativeId(JNIEnv *env, jobject self) {
    return ujr::jni_entry_guard(env, [&](auto env) {
        using ujr::native_access::JNIUlSession;

        auto *session = reinterpret_cast<ultralight::Session *>(JNIUlSession::HANDLE.get(env, self));

        return static_cast<jlong>(session->id());
    });
}

JNIEXPORT jstring JNICALL
Java_net_janrupf_ujr_platform_jni_impl_JNIUlSession_nativeDiskPath(JNIEnv *env, jobject self) {
    return ujr::jni_entry_guard(env, [&](auto env) {
        using ujr::native_access::JNIUlSession;

        auto *session = reinterpret_cast<ultralight::Session *>(JNIUlSession::HANDLE.get(env, self));
        auto j_disk_path = ujr::JniLocalRef<jstring>::from_utf16(env, session->disk_path().utf16());

        return j_disk_path.leak();
    });
}

namespace ujr {
    SessionCollector::SessionCollector(ultralight::Session *session)
        : session(session) {}

    void SessionCollector::collect() { session->Release(); }
} // namespace ujr
