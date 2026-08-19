#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in ivec2 UV3;
in ivec2 UV4;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform int FogShape;
uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

uniform float SkinningEnabled;
uniform mat4 Bone0;
uniform mat4 Bone1;
uniform mat4 Bone2;
uniform mat4 Bone3;
uniform mat4 Bone4;
uniform mat4 Bone5;
uniform mat4 Bone6;
uniform mat4 Bone7;
uniform mat4 Bone8;
uniform mat4 Bone9;
uniform mat4 Bone10;
uniform mat4 Bone11;
uniform mat4 Bone12;
uniform mat4 Bone13;
uniform mat4 Bone14;
uniform mat4 Bone15;
uniform mat4 Bone16;
uniform mat4 Bone17;
uniform mat4 Bone18;
uniform mat4 Bone19;
uniform mat4 Bone20;
uniform mat4 Bone21;
uniform mat4 Bone22;
uniform mat4 Bone23;
uniform mat4 Bone24;
uniform mat4 Bone25;
uniform mat4 Bone26;
uniform mat4 Bone27;
uniform mat4 Bone28;
uniform mat4 Bone29;
uniform mat4 Bone30;
uniform mat4 Bone31;
uniform mat4 Bone32;
uniform mat4 Bone33;
uniform mat4 Bone34;
uniform mat4 Bone35;
uniform mat4 Bone36;
uniform mat4 Bone37;
uniform mat4 Bone38;
uniform mat4 Bone39;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;

mat4 boneMatrix(int index) {
    if (index == 0) return Bone0;
    else if (index == 1) return Bone1;
    else if (index == 2) return Bone2;
    else if (index == 3) return Bone3;
    else if (index == 4) return Bone4;
    else if (index == 5) return Bone5;
    else if (index == 6) return Bone6;
    else if (index == 7) return Bone7;
    else if (index == 8) return Bone8;
    else if (index == 9) return Bone9;
    else if (index == 10) return Bone10;
    else if (index == 11) return Bone11;
    else if (index == 12) return Bone12;
    else if (index == 13) return Bone13;
    else if (index == 14) return Bone14;
    else if (index == 15) return Bone15;
    else if (index == 16) return Bone16;
    else if (index == 17) return Bone17;
    else if (index == 18) return Bone18;
    else if (index == 19) return Bone19;
    else if (index == 20) return Bone20;
    else if (index == 21) return Bone21;
    else if (index == 22) return Bone22;
    else if (index == 23) return Bone23;
    else if (index == 24) return Bone24;
    else if (index == 25) return Bone25;
    else if (index == 26) return Bone26;
    else if (index == 27) return Bone27;
    else if (index == 28) return Bone28;
    else if (index == 29) return Bone29;
    else if (index == 30) return Bone30;
    else if (index == 31) return Bone31;
    else if (index == 32) return Bone32;
    else if (index == 33) return Bone33;
    else if (index == 34) return Bone34;
    else if (index == 35) return Bone35;
    else if (index == 36) return Bone36;
    else if (index == 37) return Bone37;
    else if (index == 38) return Bone38;
    else if (index == 39) return Bone39;
    return Bone0;
}

int unsigned16(int value) {
    return value & 65535;
}

void decodeInfluence(int encoded, out int bone, out float weight) {
    int bits = unsigned16(encoded);
    bone = bits & 63;
    weight = float((bits >> 6) & 1023) / 1023.0;
}

void main() {
    vec3 localPosition = Position;
    vec3 localNormal = Normal;

    if (SkinningEnabled > 0.5) {
        int bone0;
        int bone1;
        float weight0;
        float weight1;
        decodeInfluence(UV3.x, bone0, weight0);
        decodeInfluence(UV4.x, bone1, weight1);

        float total = weight0 + weight1;
        if (total > 0.000001) {
            weight0 /= total;
            weight1 /= total;
        } else {
            weight0 = 1.0;
            weight1 = 0.0;
        }

        mat4 m0 = boneMatrix(bone0);
        mat4 m1 = boneMatrix(bone1);

        vec4 p =
              (m0 * vec4(Position, 1.0)) * weight0
            + (m1 * vec4(Position, 1.0)) * weight1;
        vec3 n =
              (mat3(m0) * Normal) * weight0
            + (mat3(m1) * Normal) * weight1;

        localPosition = p.xyz;
        localNormal = normalize(n);
    }

    gl_Position = ProjMat * ModelViewMat * vec4(localPosition, 1.0);

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * localPosition, FogShape);
    vertexColor = minecraft_mix_light(
            Light0_Direction, Light1_Direction, localNormal, Color);
    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * vec4(localNormal, 0.0);
}
