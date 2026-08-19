#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;
uniform mat3 CameraRotation;
uniform float Emission;
uniform vec2 LightUv;
uniform float PreviewFullBright;

in vec4 vertexColor;
in vec3 viewPosition;
in vec3 viewNormal;
in vec2 compatibilityUv;
flat in ivec2 compatibilityOverlay;

out vec4 fragColor;

const float FACE_INSET = 0.03125;

float clampFace(float value) {
    return clamp(value, FACE_INSET, 1.0 - FACE_INSET);
}

vec2 cubemapFaceUv(int face, float u, float v) {
    float localU = clampFace(u * 0.5 + 0.5);
    float localV = clampFace(1.0 - (v * 0.5 + 0.5));
    return vec2(localU, (float(face) + localV) / 6.0);
}

vec4 sampleCubeAxisX(vec3 d) {
    float a = max(abs(d.x), 0.000001);
    if (d.x >= 0.0) {
        return texture(Sampler0, cubemapFaceUv(0, -d.z / a, -d.y / a));
    }
    return texture(Sampler0, cubemapFaceUv(1, d.z / a, -d.y / a));
}

vec4 sampleCubeAxisY(vec3 d) {
    float a = max(abs(d.y), 0.000001);
    if (d.y >= 0.0) {
        return texture(Sampler0, cubemapFaceUv(2, d.x / a, d.z / a));
    }
    return texture(Sampler0, cubemapFaceUv(3, d.x / a, -d.z / a));
}

vec4 sampleCubeAxisZ(vec3 d) {
    float a = max(abs(d.z), 0.000001);
    if (d.z >= 0.0) {
        return texture(Sampler0, cubemapFaceUv(4, d.x / a, -d.y / a));
    }
    return texture(Sampler0, cubemapFaceUv(5, -d.x / a, -d.y / a));
}

/**
 * GPU samplerCube performs seamless filtering across cube-face boundaries.
 * Our Viewer assets are stored as a vertical six-face strip, so emulate that
 * behavior by blending only near axis ties. The high exponent keeps each face
 * visually dominant away from a seam while eliminating the large polygonal
 * transitions visible in CP11R.5.
 */
vec4 sampleCubemapStripSeamless(vec3 direction) {
    vec3 d = normalize(direction);
    vec3 a = max(abs(d), vec3(0.000001));

    vec3 weights = pow(a, vec3(10.0));
    float total = weights.x + weights.y + weights.z;
    weights /= max(total, 0.000001);

    return sampleCubeAxisX(d) * weights.x
         + sampleCubeAxisY(d) * weights.y
         + sampleCubeAxisZ(d) * weights.z;
}

void main() {
    vec3 normal = normalize(viewNormal);

    // In view space the camera is at the origin. Reflect the camera->surface
    // incident vector, then rotate that direction back into the environment
    // orientation before sampling the Viewer's static cubemap.
    vec3 incident = normalize(viewPosition);
    vec3 reflectedView = reflect(incident, normal);
    vec3 reflectedEnvironment = normalize(CameraRotation * reflectedView);

    vec4 cube = sampleCubemapStripSeamless(reflectedEnvironment);

    // Filtered draw-time lightmap lookup. Lighting can change every frame
    // without touching cached geometry.
    vec3 sampledLight = texture(Sampler2, LightUv).rgb;
    float preview = clamp(PreviewFullBright, 0.0, 1.0);
    vec3 minecraftLight = mix(sampledLight, vec3(1.0), preview);

    // Viewer _Emission is SELF illumination of the material, not a torch.
    // In F8 preview, force the cubemap to remain fully lit/readable.
    vec3 litCube = cube.rgb * minecraftLight;
    vec3 reflectedColor = mix(litCube, cube.rgb, max(clamp(Emission, 0.0, 1.0), preview));

    // Tiny use of otherwise presentation-only inputs keeps the entity format's
    // attribute layout intact without creating a visible contribution.
    float compatibilityGuard =
            (compatibilityUv.x + float(compatibilityOverlay.x)) * 0.00000000000000000001;

    fragColor = vec4(
            clamp(reflectedColor + vec3(compatibilityGuard), 0.0, 1.0),
            cube.a * vertexColor.a
    );
}
