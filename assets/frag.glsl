#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_diffuseUV;
uniform sampler2D u_diffuseTexture;

uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;
varying vec3 v_lightDiffuse;

float getShadowness(vec2 offset) {
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));
}

float getShadow() {
    return (
        getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
        getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
        getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
        getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))
    ) * 0.25;
}

void main() {
    vec4 color = texture2D(u_diffuseTexture, v_diffuseUV).rgba;
    color.rgb *= color.a;
    color.rgb += 1.0 - color.a;
    color.a = 1.0;
    //color.rgb = getShadow() * (color.rgb * v_lightDiffuse);
    gl_FragColor = color;
}