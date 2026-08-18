package com.chaocraft.client.render.material;

import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoMorphResolver;
import com.chaocraft.visual.ChaoMorphWeights;
import com.chaocraft.visual.ChaoVisualType;

/**
 * Translation of the Chao Viewer's Palettes.cs for the two families currently
 * available in ChaoCraft: Child and Neutral Normal.
 */
public final class ChaoPaletteResolver {
	private static final ChaoColor WHITE = ChaoColor.WHITE;
	private static final ChaoColor CLEAR = ChaoColor.TRANSPARENT_WHITE;

	private ChaoPaletteResolver() {
	}

	public static ChaoPaletteState resolve(ChaoAppearanceState state, ChaoMorphWeights weights) {
		if (state.type() == ChaoVisualType.CHILD) {
			return resolveChild(state, weights);
		}
		return resolveNeutralNormal(state, weights);
	}

	private static ChaoPaletteState resolveNeutralNormal(ChaoAppearanceState state, ChaoMorphWeights weights) {
		float young = 1.0F - state.age();
		float normal = weights.normal() / 100.0F;
		float swim = weights.swim() / 100.0F;
		float fly = weights.fly() / 100.0F;
		float run = weights.run() / 100.0F;
		float power = weights.power() / 100.0F;

		ChaoColor bodyCover = mix(
				ChaoColor.rgb(123, 255, 255),
				ChaoColor.rgb(0, 255, 255),
				ChaoColor.rgb(0, 255, 255),
				ChaoColor.rgb(0, 255, 255),
				ChaoColor.rgb(0, 255, 255),
				ChaoColor.rgb(0, 255, 255),
				young, normal, swim, fly, run, power
		);

		return new ChaoPaletteState(
				WHITE,
				ChaoColor.rgb(255, 255, 0),
				ChaoColor.rgb(123, 255, 255),
				CLEAR,
				CLEAR,
				WHITE,
				ChaoColor.rgb(255, 120, 229),
				ChaoColor.rgb(255, 210, 246),
				bodyCover,
				WHITE
		);
	}

	private static ChaoPaletteState resolveChild(ChaoAppearanceState state, ChaoMorphWeights weights) {
		float young = 1.0F - state.age();
		float normal = weights.normal() / 100.0F;
		float swim = weights.swim() / 100.0F;
		float fly = weights.fly() / 100.0F;
		float run = weights.run() / 100.0F;
		float power = weights.power() / 100.0F;

		ChaoPaletteState neutral = childGroup(CN, young, normal, swim, fly, run, power);
		ChaoMorphResolver.AlignmentWeights alignment = ChaoMorphResolver.resolveAlignment(state.alignment());
		if (alignment.hero() > 0.0F) {
			return lerp(neutral, childGroup(CH, young, normal, swim, fly, run, power), alignment.hero() / 100.0F);
		}
		if (alignment.dark() > 0.0F) {
			return lerp(neutral, childGroup(CD, young, normal, swim, fly, run, power), alignment.dark() / 100.0F);
		}
		return neutral;
	}

	private static ChaoPaletteState childGroup(Group group, float young, float normal, float swim, float fly, float run, float power) {
		return new ChaoPaletteState(
				mixChannel(group, Channel.BASE, young, normal, swim, fly, run, power),
				mixChannel(group, Channel.BODY, young, normal, swim, fly, run, power),
				mixChannel(group, Channel.BELLY, young, normal, swim, fly, run, power),
				mixChannel(group, Channel.EXTRA, young, normal, swim, fly, run, power),
				mixChannel(group, Channel.EXTRA2, young, normal, swim, fly, run, power),
				mixChannel(group, Channel.HORNS, young, normal, swim, fly, run, power),
				mixChannel(group, Channel.WINGS, young, normal, swim, fly, run, power),
				mixChannel(group, Channel.WINGS_BASE, young, normal, swim, fly, run, power),
				WHITE,
				WHITE
		);
	}

	private static ChaoColor mixChannel(Group group, Channel channel, float young, float normal, float swim, float fly, float run, float power) {
		return mix(
				group.young.get(channel), group.normal.get(channel), group.swim.get(channel),
				group.fly.get(channel), group.run.get(channel), group.power.get(channel),
				young, normal, swim, fly, run, power
		);
	}

	private static ChaoColor mix(ChaoColor youngColor, ChaoColor normalColor, ChaoColor swimColor,
			ChaoColor flyColor, ChaoColor runColor, ChaoColor powerColor,
			float young, float normal, float swim, float fly, float run, float power) {
		return youngColor.scale(young)
				.add(normalColor.scale(normal))
				.add(swimColor.scale(swim))
				.add(flyColor.scale(fly))
				.add(runColor.scale(run))
				.add(powerColor.scale(power));
	}

	private static ChaoPaletteState lerp(ChaoPaletteState from, ChaoPaletteState to, float amount) {
		return new ChaoPaletteState(
				from.base().lerp(to.base(), amount),
				from.body().lerp(to.body(), amount),
				from.belly().lerp(to.belly(), amount),
				from.extra().lerp(to.extra(), amount),
				from.extra2().lerp(to.extra2(), amount),
				from.horns().lerp(to.horns(), amount),
				from.wings().lerp(to.wings(), amount),
				from.wingsBase().lerp(to.wingsBase(), amount),
				from.bodyCover().lerp(to.bodyCover(), amount),
				from.wingsCover().lerp(to.wingsCover(), amount)
		);
	}

	private enum Channel { BASE, BODY, BELLY, EXTRA, EXTRA2, HORNS, WINGS, WINGS_BASE }

	private record Palette(ChaoColor base, ChaoColor body, ChaoColor belly, ChaoColor extra,
			ChaoColor extra2, ChaoColor horns, ChaoColor wings, ChaoColor wingsBase) {
		ChaoColor get(Channel channel) {
			return switch (channel) {
				case BASE -> base;
				case BODY -> body;
				case BELLY -> belly;
				case EXTRA -> extra;
				case EXTRA2 -> extra2;
				case HORNS -> horns;
				case WINGS -> wings;
				case WINGS_BASE -> wingsBase;
			};
		}
	}

	private record Group(Palette young, Palette normal, Palette swim, Palette fly, Palette run, Palette power) {
	}

	private static Palette p(int[] base, int[] body, int[] belly, int[] extra, int[] extra2, int[] horns, int[] wings, int[] wingsBase) {
		return new Palette(c(base), c(body), c(belly), c(extra), c(extra2), c(horns), c(wings), c(wingsBase));
	}

	private static ChaoColor c(int[] rgba) {
		return ChaoColor.rgba(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	private static int[] v(int r, int g, int b, int a) { return new int[]{r, g, b, a}; }

	private static final Group CN = new Group(
			p(v(132,242,255,255), v(254,253,84,255), v(132,242,255,255), v(132,242,255,0), v(255,255,255,0), v(132,242,255,255), v(255,120,229,255), v(255,210,246,255)),
			p(v(132,242,255,255), v(162,241,26,255), v(0,209,255,255), v(0,209,255,0), v(255,255,255,0), v(132,242,255,255), v(255,120,229,255), v(255,210,246,255)),
			p(v(253,253,179,255), v(254,253,84,255), v(123,243,174,255), v(123,243,174,0), v(255,255,255,0), v(123,243,174,255), v(0,139,255,255), v(0,255,255,255)),
			p(v(254,167,254,255), v(255,148,0,255), v(124,159,255,255), v(124,159,255,0), v(255,255,255,0), v(124,159,255,255), v(255,148,0,255), v(255,255,0,255)),
			p(v(0,255,255,255), v(0,255,0,255), v(0,255,255,255), v(0,255,255,0), v(255,255,255,0), v(0,255,255,255), v(255,82,0,255), v(255,220,0,255)),
			p(v(253,110,110,255), v(255,137,0,255), v(131,109,111,255), v(131,109,111,0), v(255,255,255,0), v(253,110,110,255), v(111,0,77,255), v(0,255,192,255))
	);

	private static final Group CH = new Group(
			p(v(255,255,255,255), v(0,179,255,255), v(255,255,255,255), v(255,255,255,0), v(0,255,255,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
			p(v(255,255,255,255), v(0,137,255,255), v(255,255,93,255), v(255,255,93,0), v(0,213,255,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
			p(v(255,255,255,255), v(255,255,92,255), v(93,255,181,255), v(93,255,181,0), v(201,255,181,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
			p(v(255,255,255,255), v(206,68,255,255), v(93,181,255,255), v(93,181,255,0), v(255,197,255,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
			p(v(255,255,255,255), v(128,255,155,255), v(93,181,255,255), v(93,181,255,0), v(128,255,155,0), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
			p(v(255,255,255,255), v(255,0,0,255), v(255,234,156,255), v(255,234,156,0), v(255,234,156,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255))
	);

	private static final Group CD = new Group(
			p(v(54,10,11,255), v(255,0,0,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(54,10,11,255), v(100,0,0,255), v(255,0,0,255)),
			p(v(54,10,11,255), v(255,0,0,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(54,10,11,255), v(100,0,0,255), v(255,0,0,255)),
			p(v(59,68,36,255), v(77,190,159,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(34,44,19,255), v(255,144,0,255), v(255,236,0,255)),
			p(v(45,18,49,255), v(234,47,216,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(45,18,49,255), v(0,82,86,255), v(0,193,204,255)),
			p(v(0,50,38,255), v(0,226,170,255), v(0,0,0,0), v(0,0,0,255), v(255,255,255,0), v(0,50,38,255), v(55,0,232,255), v(55,0,232,255)),
			p(v(48,28,2,255), v(255,144,0,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(48,28,2,255), v(255,144,0,255), v(255,236,0,255))
	);
}
