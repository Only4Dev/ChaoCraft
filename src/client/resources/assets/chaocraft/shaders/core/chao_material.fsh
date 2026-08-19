#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;
uniform vec2 LightUv;
uniform float Emission;
uniform float PreviewFullBright;
uniform vec3 LightDir0;
uniform vec3 LightDir1;

in vec4 vertexColor;
in vec2 texCoord;
in vec3 viewNormal;

out vec4 fragColor;

float vanillaLikeDiffuse(vec3 normal) {
    vec3 n = normalize(normal);
    vec3 l0 = normalize(LightDir0);
    vec3 l1 = normalize(LightDir1);

    float d0 = max(0.0, dot(l0, n));
    float d1 = max(0.0, dot(l1, n));

    // Matches the character of Minecraft's standard entity diffuse lighting:
    // retain ambient readability while restoring curvature/volume from normals.
    return min(1.0, (d0 + d1) * 0.60 + 0.40);
}

void main() {
    vec4 base = texture(Sampler0, texCoord) * vertexColor;
    if (base.a < 0.01) {
        discard;
    }

    // Dynamic day/night/block-light remains draw-time, so lighting changes do
    // not generate new Chao VBOs.
    vec3 sampledLight = texture(Sampler2, LightUv).rgb;
    float preview = clamp(PreviewFullBright, 0.0, 1.0);
    vec3 worldLight = mix(sampledLight, vec3(1.0), preview);

    // Restore normal-based volume in-world, but allow the F8 viewer to force
    // a clean full-bright preview so it never appears dim because of GUI state.
    float diffuse = mix(vanillaLikeDiffuse(viewNormal), 1.0, preview);
    vec3 lit = base.rgb * worldLight * diffuse;

    // Viewer material emission is self-light only. Ordinary Chao batches use 0.
    vec3 finalColor = mix(lit, base.rgb, max(clamp(Emission, 0.0, 1.0), preview));
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), base.a);
}
