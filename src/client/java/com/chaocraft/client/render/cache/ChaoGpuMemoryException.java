package com.chaocraft.client.render.cache;

/**
 * Signals an OpenGL-side allocation failure that did not surface as a Java
 * {@link OutOfMemoryError}. The GPU cache treats it exactly like native-memory
 * pressure: release idle resources, pause builds briefly and keep rendering the
 * previous stable state when available.
 */
public final class ChaoGpuMemoryException extends RuntimeException {
    public ChaoGpuMemoryException(String message) {
        super(message);
    }
}
