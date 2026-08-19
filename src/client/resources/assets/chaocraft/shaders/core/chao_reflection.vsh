#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec3 viewPosition;
out vec3 viewNormal;
out vec2 compatibilityUv;
flat out ivec2 compatibilityOverlay;

void main() {
    vec4 view = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * view;

    vertexColor = Color;
    viewPosition = view.xyz;
    viewNormal = normalize(mat3(ModelViewMat) * Normal);

    // Keep the full entity vertex format active so this program can draw the
    // exact same reusable VBOs as the normal RenderLayer.
    compatibilityUv = UV0;
    compatibilityOverlay = UV1;
}
