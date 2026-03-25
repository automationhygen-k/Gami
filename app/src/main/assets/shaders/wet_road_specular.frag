#version 300 es
precision highp float;

in vec2 v_uv;
in vec3 v_worldNormal;
in vec3 v_viewDir;
in vec3 v_lightDir;
out vec4 fragColor;

uniform sampler2D u_albedo;
uniform sampler2D u_roughness;
uniform sampler2D u_wetSpecMask; // packed mask texture
uniform float u_rainforestWetness; // 0 for non-rainforest, 1 for rainforest

void main() {
    vec3 albedo = texture(u_albedo, v_uv).rgb;
    float rough = texture(u_roughness, v_uv).r;

    float wetMask = texture(u_wetSpecMask, v_uv * 2.4).r;
    float wetness = wetMask * u_rainforestWetness;

    vec3 N = normalize(v_worldNormal);
    vec3 V = normalize(v_viewDir);
    vec3 L = normalize(v_lightDir);
    vec3 H = normalize(V + L);

    float NdotL = max(dot(N, L), 0.0);
    float NdotH = max(dot(N, H), 0.0);

    float shininess = mix(18.0, 140.0, wetness);
    float spec = pow(NdotH, shininess) * NdotL;
    float specBoost = mix(0.08, 1.15, wetness);

    vec3 diffuse = albedo * NdotL;
    vec3 finalColor = diffuse + vec3(spec * specBoost);

    fragColor = vec4(finalColor, 1.0);
}
