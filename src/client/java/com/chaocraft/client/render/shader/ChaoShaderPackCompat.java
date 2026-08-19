package com.chaocraft.client.render.shader;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;

/**
 * Optional Iris compatibility bridge with zero hard dependency.
 *
 * <p>Core shaders cannot be assumed to compose safely with arbitrary shader
 * packs. When Iris reports an active pack, ChaoCraft leaves that draw on the
 * normal entity RenderLayer instead of forcing its own GLSL program.</p>
 */
public final class ChaoShaderPackCompat {
    private static final long CHECK_INTERVAL_NANOS = 1_000_000_000L;
    private static final boolean IRIS_LOADED = FabricLoader.getInstance().isModLoaded("iris");

    private static boolean initialized;
    private static boolean apiAvailable;
    private static Object irisApi;
    private static Method isShaderPackInUse;
    private static long lastCheckNanos = Long.MIN_VALUE;
    private static boolean lastResult;

    private ChaoShaderPackCompat() {
    }

    public static boolean isShaderPackInUse() {
        if (!IRIS_LOADED) {
            return false;
        }

        ensureInitialized();
        if (!apiAvailable) {
            // Conservative compatibility fallback: Iris is present but its API
            // could not be resolved, so do not force a custom core shader.
            return true;
        }

        long now = System.nanoTime();
        if (lastCheckNanos != Long.MIN_VALUE && now - lastCheckNanos < CHECK_INTERVAL_NANOS) {
            return lastResult;
        }
        lastCheckNanos = now;

        try {
            lastResult = Boolean.TRUE.equals(isShaderPackInUse.invoke(irisApi));
        } catch (ReflectiveOperationException exception) {
            apiAvailable = false;
            lastResult = true;
        }
        return lastResult;
    }

    private static void ensureInitialized() {
        if (initialized) {
            return;
        }
        initialized = true;

        String[] apiClasses = {
                "net.irisshaders.iris.api.v0.IrisApi",
                "net.coderbot.iris.api.v0.IrisApi"
        };

        for (String className : apiClasses) {
            try {
                Class<?> type = Class.forName(className);
                Method getInstance = type.getMethod("getInstance");
                irisApi = getInstance.invoke(null);
                isShaderPackInUse = type.getMethod("isShaderPackInUse");
                apiAvailable = true;
                return;
            } catch (ReflectiveOperationException ignored) {
                // Try the next known Iris API package.
            }
        }
    }
}
