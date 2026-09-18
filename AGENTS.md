# Repository Guidelines

NeoForge 1.21.1 addon for **Create** that raises Create's machine limits (e.g. 256 RPM → 4096 RPM), adds
overclockable variants of Create machines, and a fluid/multiblock "infusion vat" processing chain.

> **Naming trap.** The folder, git repo, IDE module and Gradle project are `Create-Overclocked`, but the mod is
> **`create_andertech`** / display name **Create Andertech** — description "Overclock all Create machines!".
> Use `create_andertech` (never `overclocked`) for the mod id, resource paths, mixin package and
> `@Mod(MODID)`; the base package is `io.github.cvrunmin.create_andertech`.

## Project Overview

- Loader target: NeoForge only (`src/main/templates/META-INF/neoforge.mods.toml`). No Fabric/Forge targets.
- Hard dependency on Create `[6.0.10, 7.0)`; JEI is a dev/compile-only integration (`compat/jei`) and is **not**
  declared in mod metadata, so it stays optional for players.
- 203 Java files, zero Kotlin, one source set, one Gradle module. No configs, no network packets, no tests.
- Integration with Create is mostly **mixin-based** (28 registered mixins) plus Create's documented extension
  points (Registrate, `BoilerHeater`, `BlockStressValues`, `EncasingRegistry`, `MovementBehaviour`,
  `StandardProcessingRecipe`, `CreateRegistries.ARM_INTERACTION_POINT_TYPE`).

## Architecture & Data Flow

**Startup.** `CreateAndertech` (only `@Mod` class) takes `(IEventBus, ModContainer)`; constructor calls
`REGISTRATE.registerEventListeners(modEventBus)` then each `index/*.register()` aggregator, then wires
`commonSetup`, `CADataGen::gatherDataHighPriority` (priority `HIGHEST`) and `CADataGen::gatherData`.
A nested `@Mod(value = MODID, dist = Dist.CLIENT)` class `AndertechClient` handles client bootstrap.

**Registration.** Two mechanisms, chosen by coverage:
- `Registrate` (`CreateAndertech.REGISTRATE`) for blocks, items, fluids, block entities, entity types —
  entries self-register on class init, so `register()` bodies in `index/AllBlocks`, `index/AllItems`, etc. are
  intentional no-ops that only force class loading. Keep that convention.
- `DeferredRegister` for what Registrate does not cover: creative tab, particle types, data components +
  ingredient types, recipe types/serializers (`index/CAAllRecipeTypes`, `index/AllDataComponents`,
  `index/CAAllParticleTypes`, `index/AllCreativeTabs`, `index/CAAllArmInteractionPointTypes`).

**Capabilities.** `CommonEvents.registerCapabilities(RegisterCapabilitiesEvent)` fans out to a static
`registerCapabilities(event)` on each of 9 block entities. Only `Capabilities.ItemHandler.BLOCK` and
`Capabilities.FluidHandler.BLOCK` exist; handlers are lazily built, cached in a field, and invalidated via
`invalidateCapabilities()` (multi-block: also `level.invalidateCapabilities(pos)`). No energy capability.

**State sync / "networking".** There are **no** custom packets, payload registrars or channels. Block-entity
state travels through Create's own `sendData()` / `sendDataImmediately()` / `notifyUpdate()` / `setChanged()`.
The only `StreamCodec` in the repo is the advanced air-flow particle option (`index/CAAllParticleTypes`,
`content/kinetics/fan/AdvancedAirFlowParticleData`).

**Client/server split.** Single source set; separation is by convention only:
`@Mod(dist = Dist.CLIENT)` / `@EventBusSubscriber(value = Dist.CLIENT)` for whole classes,
`@OnlyIn(Dist.CLIENT)` on individual members, and runtime `level.isClientSide` guards.
`CatnipServices.PLATFORM.executeOnClientOnly(...)` is the Create helper used for client audio/particles.
No `DistExecutor`, no class-level `@OnlyIn`, no `client`/`api` source set.

**Datagen.** `infrastructure/data/CADataGen` orchestrates Registrate providers (block tags, lang, blockstates)
and server-side recipe gens (`StandardRecipeGen`, `CADeployingRecipeGen`, `CACuttingRecipeGen`,
`CAMixingRecipeGen`, `CAInfusingRecipeGen`, `AdditionalEntriesProvider`). Output goes to **`src/generated/resources`
which is committed** and also added as a main resource root. Generation is mod-guarded:
`if (!event.getMods().contains(CreateAndertech.MODID)) return;`.

## Key Directories

| Path | Contents |
|---|---|
| `src/main/java/io/github/cvrunmin/create_andertech/` | `CreateAndertech`, `CommonEvents`, `Constants` (`MAX_OVERCLOCKING_SPEED = 4096`) |
| `.../index/` | All registries: `AllBlocks`, `AllBlockEntityTypes`, `AllItems`, `AllFluids`, `AllEntityTypes`, `AllTags`, `CAAllRecipeTypes`, `CAAllPartialModels`, `CAAllSpriteShifts`, `CAAllIcons`-adjacent client bits |
| `.../content/kinetics/` | 97 files — one sub-package per Create machine (belt, fan, saw, drill, press, mixer, deployer, crafter, crusher, gearbox, transmission, motor, spout, mechanicalArm, simpleRelays, chainDrive, speedController) |
| `.../content/fluids/` | `infusion_vat/` (multiblock + recipe), `tank/`, `pump/` |
| `.../content/{entity,misc,decoration,contraptions,processing}/` | Chronendic Blaze entity, nylium transmuter, nylium decoration blocks, mechanical bearing, blaze burner |
| `.../mixin/` (+ `mixin/belt`, `mixin/fluid`) | Create/vanilla patching — the main extension surface |
| `.../foundation/` | Builders, CT/cullable models, `MyLang`, marker interfaces (`IOverspeedKineticBE`, `IGrandSuper*`) |
| `.../infrastructure/` | Custom recipe input/ingredient + `data/` datagen providers |
| `.../compat/jei/` | `@JeiPlugin` using Create's `CreateRecipeCategory.Builder` |
| `src/main/resources/` | Hand-authored: `create_andertech.mixins.json`, `META-INF/`, textures, block models, `lang/zh_cn.json` |
| `src/main/templates/` | `neoforge.mods.toml`, `pack.mcmeta` with `${}` placeholders (never edit the built copies) |
| `src/generated/resources/` | Committed datagen output: blockstates, item models, `en_us`/`en_ud` lang, data (recipes, loot, tags, worldgen) |

## Development Commands

Windows workstation — use `gradlew.bat`; CI uses `./gradlew`.

| Task | Command |
|---|---|
| Build jar (`create_andertech-1.0.0.jar`) | `gradlew.bat build` |
| Clean / refresh deps | `gradlew.bat clean` / `gradlew.bat --refresh-dependencies` |
| Launch game client | `gradlew.bat runClient` |
| Dedicated server (`--nogui`) | `gradlew.bat runServer` |
| Run datagen → `src/generated/resources` | `gradlew.bat runData` |
| Game test server | `gradlew.bat runGameTestServer` (crashes today: no gametests registered) |
| Publish to local `./repo` maven | `gradlew.bat publish` |

All runs write to the gitignored `run/` directory, use `logLevel = DEBUG` and
`forge.logging.markers = REGISTRIES`, and set `neoforge.enabledGameTestNamespaces = create_andertech`
even though no gametests exist.

## Code Conventions & Common Patterns

**Naming.** Prefixes are semantic and consistent — follow them:
- `Chronendite*` — for all blocks and machines that are Chronendite-grade (`ChronenditeMechanicalSawBlock`,
  `ChronenditeDeployerBlockEntity`).
- `Crimschist*` — for all items, blocks and machines that are Crimschist-grade (`CrimschistFluidTankBlock`, `CrimschistGearboxBlockEntity`).
- `OC*` — "overclocked core" reimplementations of Create internals (`OCBeltBlockEntity`, `OCKineticBlockEntity`,
  `OCDeployerHandler`, `OCCrafterHelper`).
- `CA*` — mod-level registries not tied to a material (`CAAllRecipeTypes`, `CAAllPartialModels`, `CAAllIcons`).
- `*Block` / `*BlockEntity` / `*Renderer` / `*Visual` triplets per machine; `All*` for registry aggregators.

**Feature file layout.** A new machine is typically: `AllBlocks` entry (+ stress value, tags, CT, encasing
variant via `.transform(...)` / `.onRegister(...)`) → `AllBlockEntityTypes` entry wired with flywheel
`.visual(...)` and `.renderer(...)` → a `content/.../<pkg>/` package holding block, BE, renderer, visual, and
any movement behaviour — mirroring Create's own class hierarchy (`extends KineticBlockEntity`,
`SmartBlockEntity`, `BeltBlockEntity`, `MovementBehaviour`, `StandardProcessingRecipe`, …).

**Registration extras.** `static { REGISTRATE.setCreativeTab(AllCreativeTabs.MAIN_TAB); }` at the top of
`AllBlocks`/`AllItems`/`AllFluids`; stress via `BlockStressValues.IMPACTS.register(...)`;
tags via `TagGen.pickaxeOnly()` / `axeOrPickaxe()`; connected textures via
`CreateRegistrate.connectedTextures(...)` / `.casingConnectivity(...)`; reusable builder transformers live in
`foundation/VariousBuilders`.

**Mixins.** This is where most behavior changes happen — treat Create as a library you patch, and prefer
mixins over copying Create code.
- Config: `src/main/resources/create_andertech.mixins.json` (`compatibilityLevel: JAVA_21`,
  `required: true`, `injectors.defaultRequire: 1`, `mixinextras.minVersion: 0.5.0`). Registered twice — FML
  `[[mixins]]` metadata **and** the jar manifest `MixinConfigs` attribute. **Every new mixin class must be added
  to that JSON**, or it silently never applies.
- MixinExtras is used heavily and is bundled jar-in-jar: `@Definition`/`@Expression` with
  `@At("MIXINEXTRAS:EXPRESSION")`, `@WrapOperation`, `@ModifyExpressionValue`, `@ModifyArg`, `@Local(name=…)`,
  `@Share`, `LocalRef`/`LocalBooleanRef`. Plain `@Inject` for HEAD/TAIL/cancellable cases.
- Private helpers in mixins are `@Unique` and prefixed `createandertech$`
  (e.g. `createandertech$isLargeCogToChronenditeSpeedController`); accessors use
  `@Accessor(value = "field", remap = false)` and invokers `@Invoker`. Some older injections omit the prefix —
  use the prefixed form for new code.
- Known dead weight (do not copy these patterns): `mixin/belt/BeltHelperMixin`,
  `mixin/belt/BeltInventoryMixin`, `mixin/belt/BeltTunnelInteractionHandlerMixin` exist but are **absent from
  the mixin JSON** and have their bodies commented out; `mixin/DeployerBlockEntityAccessor` is an empty
  `@Mixin` interface with every accessor commented out; `foundation/OverspeedRotationPropagator` is an empty
  unused class.
- `content/kinetics/deployer/DeployerReflections` reflects into Create's private `DeployerBlockEntity` enum
  fields and logs `WARN` on failure — fragile, and duplicated by the accessor-mixin convention used elsewhere.
  Prefer a mixin accessor for new work.

**The `IGrandSuper*` escape hatch.** Mixins on Create's `DeployerBlockEntity`, `BeltBlockEntity` and
`FluidTankBlockEntity` implement `IGrandSuperKineticBlockEntity` / `IGrandSuperSmartBlockEntity` so subclasses
can call `((IGrandSuperKineticBlockEntity) this).grandsuperTick()` when the overridden `super.tick()` would
otherwise be unreachable. Follow the pattern if you subclass an already-subclassed Create BE.

**Overclocking.** `foundation/IOverspeedKineticBE` is an empty marker interface; `RotationPropagatorMixin`
checks it (plus `GaugeBlockEntity`) to lift Create's 256 RPM limit up to `Constants.MAX_OVERCLOCKING_SPEED`
(4096). Any new mod kinetic BE that should exceed vanilla Create speed must implement it.

**Side handling in practice.** Put client-only renderers/visuals/particles in the same package as their
server counterparts and gate them (`level.isClientSide`, `@OnlyIn(Dist.CLIENT)`,
`CatnipServices.PLATFORM.executeOnClientOnly`). Reach for `@Mod(dist = CLIENT)` classes only for bootstrap
work (e.g. `CAAllPartialModels.init()`).

**Style observed.** 4-space indent, braces on the same line, `//` line comments (terse, often informal),
javadoc only on helper/"clone" classes. Nullability annotations are inconsistent — `javax.annotation.Nullable`
and `org.jetbrains.annotations.Nullable` are both in use; match the surrounding file rather than reformatting.
`net.minecraft.MethodsReturnNonnullByDefault` appears only on private `PlacementHelper` inner classes.
Error handling is defensive early-return / `PASS_TO_DEFAULT_BLOCK_INTERACTION`; `DeployerReflections` is the
only logged failure path. Deprecated/commented-out code is common (e.g. the commented `BELT_CONNECTOR` item
entry in `AllItems`); do not replicate it in new files.

**Localization.** Lang keys are `<type>.create_andertech.<path>` (`block.`, `item.`, `entity.`, `fluid.`,
`itemGroup.`) or `create_andertech.<subsystem>.<key>` for UI strings. `en_us`/`en_ud` are **datagen-generated**
into `src/generated/resources`; `zh_cn.json` in `src/main/resources` is hand-maintained and is **not** touched
by datagen. Add English strings in `CADataGen.gatherDataHighPriority` (or the registrate builder).
Nothing enforces lang parity. When a new English string is added. Run datagen once to ensure the `en_us.json`
is up to date. After that add strings for other languages, matching the `en_us.json`.

## Important Files

- `build.gradle` — MDG build: plugins, Java 21 toolchain, parchment mappings, 4 run configs,
  dependencies, jar `MixinConfigs` manifest attribute, `generateModMetadata`, local `maven-publish`.
- `gradle.properties` — single source of truth for versions **and** mod identity (`mod_id`, `mod_name`,
  `mod_group_id`, `mod_version`, `mod_license`, `mod_authors`, `mod_description`).
- `settings.gradle` — only the foojay toolchain resolver; no `rootProject.name`, no subprojects (project name
  is therefore the folder name).
- `src/main/templates/META-INF/neoforge.mods.toml` — mod metadata with `${}` placeholders; declares the 3
  required deps (`neoforge [21.1.219,)`, `minecraft [1.21.1]`, `create [6.0.10, 7.0)`) and the mixin config.
- `src/main/resources/create_andertech.mixins.json` — authoritative mixin list.
- `src/main/java/.../CreateAndertech.java` — entry point, `MODID`, `LOGGER`, `REGISTRATE`, registry wiring,
  `BoilerHeater` registration, nested client class.
- `src/main/java/.../index/AllBlocks.java` (~855 lines) — the densest file; most feature work starts here.
- `src/main/java/.../infrastructure/data/CADataGen.java` — datagen entry point; where lang/tag/blockstate
  additions belong.
- `src/main/java/.../mixin/RotationPropagatorMixin.java` — canonical example of the overclocking mixins.
- `src/main/java/.../content/kinetics/belt/OCBeltBlockEntity.java` — canonical example of a full custom
  machine BE (capsabilities, behaviour swap, controller override).
- `.github/workflows/build.yml` — the only CI config.

## Runtime/Tooling Preferences

- **Java 21 required** (`java.toolchain.languageVersion = 21`, matching MC 1.21.1). The foojay resolver in
  `settings.gradle` auto-provisions it if missing.
- **Gradle 8.14.3** via the wrapper (`gradle/wrapper/gradle-wrapper.properties`, `-bin` distribution). Never
  invoke a system `gradle`.
- **ModDevGradle 2.0.135** (`net.neoforged.moddev`, Legacy mode) — mod runs are MDG-generated
  `runClient`/`runServer`/`runData`/`runGameTestServer` tasks, not ForgeGradle ones.
- **Intellij IDEA MCP Server** - Prefer using MCP server to access files including sources and dependency codes,
  refactoring, running tests, retrieving compile errors, etc.
- Windows-first workstation: `gradlew.bat`; bash/CI: `./gradlew`.
- `gradle.properties` enables daemon, parallel, build caching and `org.gradle.configuration-cache=true`;
  `org.gradle.jvmargs=-Xmx1G` (tight for a NeoForge build).
- No version catalog (`gradle/libs.versions.toml`), no `.editorconfig`, no formatter/linter
  (spotless/checkstyle/PMD/ErrorProne all absent). IntelliJ defaults are the de-facto style; nothing is
  enforced in CI. Only explicit compiler setting is `options.encoding = 'UTF-8'`.
- `src/main/resources/META-INF/accesstransformer.cfg` is a 0-byte placeholder and the `accessTransformers`
  line is commented out — reach Create internals via mixin accessors/invokers instead.
- Repositories: `mavenLocal`, Jared's maven (JEI), `maven.createmod.net` (Create/Ponder/Flywheel),
  `maven.ithundxr.dev/snapshots` (Registrate). Create is pulled as `:slim` with `transitive = false`;
  Flywheel and JEI use the `compileOnly` API + `runtimeOnly` full-artifact split.
- `runData` rewrites `src/generated/resources` **in the working tree**, so datagen produces git diffs —
  commit them. `.gitattributes` forces LF for `src/generated/**/*.json` and the provider cache; do not
  hand-edit files under `src/generated/resources` or `build/generated/sources/modMetadata`.

## Testing & QA

**There is no automated test infrastructure.** No `src/test`, no `src/gametest`, no JUnit/Mockito/AssertJ
dependencies, no `@Test` or `@GameTest` classes, no coverage tooling, no lint gate. `gradlew test` is
`NO-SOURCE`; the `gameTestServer` run config exists but will crash because the `create_andertech` gametest
namespace is empty.

Consequences for any change:

- **Verify if the new changes compiles.** Launch `gradlew.bat build`, or `./gradlew build` in CI
  which proves compilation, mixin application and resource/metadata generation.
- Resources a new feature usually needs regenerated: run `gradlew.bat runData` after touching registry entries,
  tags, recipes or lang strings, and commit the changed `src/generated/resources` files.
- Since there is no lint/format task, keep new code consistent with the surrounding file rather than
  introducing a formatter.
