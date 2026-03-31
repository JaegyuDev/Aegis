# Contributing to Aegis

## Project Structure

Aegis is a multi-module NeoForge mod project with strict separation of concerns across three modules:

| Module | Mod ID | Side | Purpose |
|---|---|---|---|
| `aegis-common` | `aegis_common` | Both | Shared network payloads and cross-module code |
| `aegis-bulwark` | `aegis_bulwark` | Server | Admin tools, server protection, backup system |
| `aegis-hearth` | `aegis_hearth` | Both | Player-facing quality of life features |

### What goes where

**`aegis-common`** — Only code that both Bulwark and Hearth need to reference directly. Currently this means network payload records. Do not put game logic here.

**`aegis-bulwark`** — Anything that runs on the server and serves an operator or admin purpose. Examples: backup management, configuration tasks, server-side network handshakes, block interaction logging (planned). If a player can trigger it for fun, it probably belongs in Hearth instead.

**`aegis-hearth`** — Anything player-facing. Enchantments, mob behaviour tweaks, QoL commands, UI mixins, client-side network handlers. If it requires operator permission to use, it probably belongs in Bulwark instead.

## Adding a Feature

Every feature must follow these three steps:

### 1. Add a config flag

Add a method to the relevant config class (`HearthConfig` or `BulwarkConfig`) with a clear, descriptive name:

```java
public boolean isCreeperGriefingDisabled() {
    return config.getOrElse("features.mobs.disable_creeper_griefing", true);
}
```

### 2. Add the default value to `default_config.toml`

```toml
[features.mobs]
disable_creeper_griefing = true
```

### 3. Add a guard clause to every event handler the feature touches

The guard should be the first line of the handler:

```java
@EventHandler
public void onCreeperExplode(EntityExplodeEvent event) {
    if (!config.isCreeperGriefingDisabled()) return;
    // ...
}
```

Config flags are **circuit breakers**, not user-facing toggles. Their purpose is to allow a feature to be disabled at runtime without a server restart if a bug is discovered in production. This is done via the `/bw config set <key> <value>` command. Make sure the feature is fully inert when its flag is `false`.

## Package Layout

### `aegis-bulwark`
```
dev.jaegyu.aegis.bulwark
├── Bulwark.java               # @Mod entry point
├── BulwarkConfig.java         # ModConfigSpec server config
└── network/
    ├── BulwarkHandshakePayload.java   # in aegis-common, not here
    └── BulwarkHandshakeTask.java      # IConfigurationTask for config phase
```

### `aegis-hearth`
```
dev.jaegyu.aegis.hearth
├── Hearth.java                # @Mod entry point
├── BulwarkHandshakeCache.java # Client-side cache for Bulwark handshake data
├── HearthConfig.java          # NightConfig server config
├── commands/                  # BasicCommand implementations
├── enchantments/              # Enchantment definitions (keys, multipliers)
├── listener/                  # Event handlers, one class per concern
└── mixin/                     # Mixins, client-only mixins go in "client" array in mixins.json
```

### `aegis-common`
```
dev.jaegyu.aegis.common
└── network/                   # Shared CustomPacketPayload records
```

## Code Conventions

### Event listeners

One listener class per concern. A listener should handle one feature area — do not put creeper griefing and harvesting in the same class. Name listeners after what they observe, not what they do: `CreeperListener`, not `DisableCreeperGriefingListener`.

### Config keys

Use snake_case. Group keys by feature area using TOML table headers:

```toml
[features.enchantments]
harvesting = true

[features.mobs]
disable_creeper_griefing = true
```

The config key path must match the getter name: `features.mobs.disable_creeper_griefing` → `isCreeperGriefingDisabled()`.

### Network payloads

All `CustomPacketPayload` records live in `aegis-common`. Payload IDs use the `aegis_bulwark` namespace for Bulwark-originated payloads regardless of which module defines the handler:

```java
Identifier.fromNamespaceAndPath("aegis_bulwark", "handshake")
```

### Mixins

Client-only mixins must be declared in the `"client"` array in `aegis_hearth.mixins.json`, not `"mixins"`. This prevents them from being loaded on a dedicated server:

```json
{
  "mixins": [],
  "client": [
    "ModMismatchScreenMixin"
  ]
}
```

### Commands

Bulwark commands use the `/bw` prefix and require the `aegis.bulwark.admin` permission node. Hearth commands (e.g. `/home`) are player-facing and should have no special permission by default.

### Dependency direction

```
aegis-common  ←  aegis-bulwark
aegis-common  ←  aegis-hearth
```

Bulwark and Hearth must never depend on each other. If Hearth needs to react to something Bulwark does, the communication goes through a payload defined in `aegis-common` and a client-side cache in Hearth. Bulwark has no knowledge of Hearth's existence.