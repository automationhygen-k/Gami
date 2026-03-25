# AAA Juice Systems Integration Notes

## 1) Visual Juice & Atmosphere

- **Spring-Arm Camera + Speed FOV**: `SpringArmCameraController` adds inertial lag and smooth FOV widening from 64° to 86° as speed increases.
- **Post FX**: `motion_blur_vignette.frag` performs velocity-vector motion blur and radial vignette in one full-screen pass.
- **Wet road during rainforest only**: `wet_road_specular.frag` uses `u_rainforestWetness` to gate specular amplification and wet mask.
- **Wheel particles by biome**: `WheelParticleManager` outputs Dust for Desert, Water Spray for Rainforest, and no effect for Rural.

## 2) Advanced Procedural Life

- **Seamless biome blending**: `BiomeBlendSystem` linearly blends fog/ambient/sky over **500 units** using smoothstep easing.
- **Logical ambient spawning**: `PropPopulator` uses Poisson disk sampling (Bridson) and maps biome to ambient prop classes:
  - Rainforest -> Trees
  - Desert -> Cacti
  - Rural -> Huts

## 3) Forza-style Interaction Loop

- **Haptics**: `HapticFeedbackManager` integrates Android vibrator APIs:
  - short thump on gear shift.
  - looping low rumble while off-road.
- **Dynamic HUD**: `DynamicHudView` is a translucent glass panel speedometer with redline shake/jitter behavior.
- **Airdrop intro**: `AirdropIntroSequence` starts high altitude and eases down into cockpit perspective.

## 4) Symphony of Speed (Audio)

- **Dynamic engine pitch/volume**: `SoundManager` scales loop pitch and loudness using RPM/gear/throttle and emits shift-pop events.
- **Environmental 3D ambience**: `EnvironmentalAudioController` routes rainforest rain emitter direction/attenuation and speed-driven desert wind whistle.
- **Surface tire feedback**: `SurfaceAudioFeedback` crossfades asphalt hum against gravel/sand crunch loops.

## 5) Visual Weight & Crash Impacts

- **Perlin-style high-speed camera shake**: `CameraShakeController` triggers procedural shake above 150 km/h and off-road.
- **Tail-light streaks**: `TailLightStreakController` enables bloom/ghosting at night or in low-visibility rainforest conditions.
- **Hit-stop**: `ImpactFeedbackController` returns 50ms freeze + radial shake payload on obstacle impacts.

## 6) Discovery UX

- **Location discovered banner**: `LocationDiscoveryBannerController` drives a center-screen minimalist banner with hold/fade timing.
- **World navigation line**: `NavigationLineGuide` builds road-surface line segments pointing to nearest fuel station/garage.

## 7) Performance & CI/CD

- **R8/ProGuard minification** enabled in `app/build.gradle` release type.
- **GitHub Actions final APK build** in `.github/workflows/android-ci.yml` runs as a single PR/manual workflow (with concurrency cancel), performs one compile command (`:app:assembleDebug`), and publishes one installable artifact `Gami-final.apk`; texture validation is still enforced via `preBuild` dependency.
- **Asset validation**: `verifyTexturesPowerOfTwo` task checks PNG/JPG dimensions for power-of-two before build.
- **Compression target**: pipeline guidance is to publish KTX2 with ETC2/ASTC targets for mobile GPUs.
- **Gradle binary compatibility fix**: keep `gradlew` scripts for local use, but CI installs Gradle 8.14.3 via GitHub Actions and runs `gradle ...`, so the binary `gradle-wrapper.jar` is not required in-repo.

## Engine Integration TODO

This repository currently provides systems/modules and shader assets, but not a complete runnable scene. To wire end-to-end:

1. Bind camera controller output to renderer camera every frame.
2. Route active biome + speed into wheel particles and biome blender.
3. Render post-processing with motion vectors and scene color.
4. Toggle `u_rainforestWetness` via biome state.
5. Trigger haptic and sound events from gearbox, RPM, and surface state.
6. Apply camera shake/tail-light/hit-stop based on speed/off-road/collision events.
7. Attach HUD and location discovery UI controllers to active UI scene.
8. Render world navigation line segments on road mesh decals.
9. Run intro sequence before handing off to gameplay camera.
