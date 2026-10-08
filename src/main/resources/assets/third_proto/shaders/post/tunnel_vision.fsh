#version 330

// W0-4: cinematic tunnel vision (options 3 + 4).
// Outside a hole around the target, the image is smeared into streaks pointing at the target,
// drained of colour and crushed to near black. The hole edge is organic: it crawls with noise and breathes.
// Tweak the constants below, rebuild, then press F3+T in game to reload the shader.

uniform sampler2D InSampler;

layout(std140) uniform TunnelConfig {
    vec2 Center;      // hole centre in texture space (0..1, y up)
    float Radius;     // hole radius, fraction of the screen height (0 = no hole)
    float Feather;    // soft edge width, fraction of the screen height
    float Aspect;     // screen width / height
    float Time;       // seconds
    float Intensity;  // 0 = off, 1 = full (fades in and out)
    float Padding;
};

in vec2 texCoord;

out vec4 fragColor;

const int STREAK_SAMPLES = 24;       // smoothness of the streaks (cost grows with it)
const float STREAK_LENGTH = 0.5;     // how far each pixel smears toward the hole (0..1)
const float DARK_FALLOFF = 0.2;      // width of the smeared band before full darkness (fraction of screen height)
const float DARK_CURVE = 0.6;        // < 1 = the band darkens quickly, > 1 = it stays bright longer
const float EDGE_WOBBLE = 0.35;      // how uneven the hole edge is
const float EDGE_SPEED = 0.35;       // how fast the edge crawls
const float SMOKE_SCALE = 5.0;       // size of the smoky detail along the edge
const float BREATH_AMOUNT = 0.05;    // how much the hole pulses
const float BREATH_SPEED = 2.2;      // pulse speed
const float DESATURATION = 0.9;      // 1 = fully grey periphery
const float PERIPHERY_LIGHT = 0.05;  // brightness left in the far periphery

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float valueNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        value += amplitude * valueNoise(p);
        p *= 2.03;
        amplitude *= 0.5;
    }
    return value;
}

void main() {
    vec3 scene = texture(InSampler, texCoord).rgb;

    vec2 offset = (texCoord - Center) * vec2(Aspect, 1.0);
    float dist = length(offset);
    vec2 dir = dist > 1.0e-5 ? offset / dist : vec2(1.0, 0.0);

    // Organic edge: noise sampled around a circle (seamless all the way round), drifting over time
    float wobble = fbm(dir * 2.5 + vec2(Time * EDGE_SPEED, -Time * EDGE_SPEED * 0.6)) - 0.5;
    float breath = 1.0 + BREATH_AMOUNT * sin(Time * BREATH_SPEED);
    float edge = Radius * breath * (1.0 + wobble * 2.0 * EDGE_WOBBLE);
    float smoke = fbm(texCoord * vec2(Aspect, 1.0) * SMOKE_SCALE + vec2(0.0, Time * 0.4));
    float softness = Feather * (0.5 + smoke);
    // blurMask: where the image starts smearing. darkMask: slower ramp to black, leaving a visible band of streaks
    float blurMask = smoothstep(edge, edge + softness, dist) * Intensity;
    float darkMask = smoothstep(edge, edge + softness + DARK_FALLOFF, dist) * Intensity;

    if (blurMask <= 0.001) {
        fragColor = vec4(scene, 1.0);
        return;
    }

    // Streaks: average samples on the way toward the hole, so the periphery smears into lines pointing at it
    vec2 toCenter = Center - texCoord;
    float jitter = hash(texCoord * 913.0); // fixed per pixel: breaks banding without flicker
    vec3 streaked = vec3(0.0);
    for (int i = 0; i < STREAK_SAMPLES; i++) {
        float t = (float(i) + jitter) / float(STREAK_SAMPLES);
        streaked += texture(InSampler, texCoord + toCenter * (STREAK_LENGTH * blurMask * t)).rgb;
    }
    streaked /= float(STREAK_SAMPLES);

    float luma = dot(streaked, vec3(0.299, 0.587, 0.114));
    vec3 drained = mix(streaked, vec3(luma), DESATURATION * blurMask);
    float light = mix(1.0, PERIPHERY_LIGHT, pow(darkMask, DARK_CURVE));
    fragColor = vec4(mix(scene, drained * light, blurMask), 1.0);
}
