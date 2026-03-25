#version 300 es
precision highp float;

in vec2 v_uv;
out vec4 fragColor;

uniform sampler2D u_sceneTex;
uniform sampler2D u_velocityTex; // screen-space motion vectors
uniform float u_blurStrength;    // 0..1
uniform float u_vignetteStrength; // 0..1

void main() {
    vec2 velocity = texture(u_velocityTex, v_uv).xy * 2.0 - 1.0;
    vec2 blurDir = velocity * u_blurStrength * 0.04;

    vec3 accum = vec3(0.0);
    const int SAMPLES = 9;
    for (int i = 0; i < SAMPLES; i++) {
        float t = float(i) / float(SAMPLES - 1) - 0.5;
        accum += texture(u_sceneTex, v_uv + blurDir * t).rgb;
    }
    vec3 color = accum / float(SAMPLES);

    vec2 centered = v_uv * 2.0 - 1.0;
    float radius = dot(centered, centered);
    float vignette = smoothstep(1.15, 0.22, radius);
    color *= mix(1.0, vignette, u_vignetteStrength);

    fragColor = vec4(color, 1.0);
}
