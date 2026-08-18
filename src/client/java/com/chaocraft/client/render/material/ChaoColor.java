package com.chaocraft.client.render.material;

/** Small linear color helper used by the Chao Viewer palette translation. */
public record ChaoColor(float r, float g, float b, float a) {
	public static final ChaoColor WHITE = rgb(255, 255, 255);
	public static final ChaoColor TRANSPARENT_WHITE = rgba(255, 255, 255, 0);
	public static final ChaoColor TRANSPARENT = rgba(0, 0, 0, 0);

	public static ChaoColor rgb(int r, int g, int b) {
		return rgba(r, g, b, 255);
	}

	public static ChaoColor rgba(int r, int g, int b, int a) {
		return new ChaoColor(r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
	}

	public ChaoColor multiply(ChaoColor other) {
		return new ChaoColor(r * other.r, g * other.g, b * other.b, a * other.a);
	}

	public ChaoColor scale(float weight) {
		return new ChaoColor(r * weight, g * weight, b * weight, a * weight);
	}

	public ChaoColor add(ChaoColor other) {
		return new ChaoColor(r + other.r, g + other.g, b + other.b, a + other.a);
	}

	public ChaoColor lerp(ChaoColor target, float amount) {
		float t = Math.max(0.0F, Math.min(1.0F, amount));
		return new ChaoColor(
				r + (target.r - r) * t,
				g + (target.g - g) * t,
				b + (target.b - b) * t,
				a + (target.a - a) * t
		);
	}

	public int red8() { return channel(r); }
	public int green8() { return channel(g); }
	public int blue8() { return channel(b); }
	public int alpha8() { return channel(a); }

	private static int channel(float value) {
		return Math.round(Math.max(0.0F, Math.min(1.0F, value)) * 255.0F);
	}
}
