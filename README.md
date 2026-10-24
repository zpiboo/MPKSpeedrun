# MPK Speedrun
This is a module for [MPKMod 2](https://github.com/MPKMod/MPKMod2), a Minecraft parkour mod.
It introduces many quality of life features for parkour speedrun.

> [!WARNING]
> i dont know how to code, so don't expect too much (anything) from me regarding code quality (lol)

## Features
In this section, words written as _`italic inline code blocks`_ represent infoVars:
values that you can insert in MPK2 labels (e.g., `Run Ticks: {runTicks}`).

### Run ticks labels
MPKSpeedrun introduces two infoVars that keep track of your run ticks between jumps:
- _`groundtime`_ just counts ticks you spend on ground
- _`runTicks`_ additionally makes sure you're moving: it doesn't count up when you press no WASD
  key, and matches tap durations as well as HH timings (note that the latter is not true in 1.8.9 and 1.12.2,
  due to a minor inconsistency in MPK2's input registration; this is a bug and should hopefully be fixed soon enough).

### Maps system
#### Timer
The module has a tick-based _`timer`_ that is not affected by lag or ping variations like on many servers.
You can use it to time yourself on singleplayer, too.

#### Maps GUI
You can set a keybind to open the **Maps GUI**.
In controls, you will find it under the name `mpkmod.key.maps_gui.desc` in the MPKMod 2 category
(MPK2 doesn't allow yet to customize this name and make it readable).

In this GUI, you can click the **Add Map** button to create a new map.
This will open the map editor. You can always access it again by pressing **Config** next to the map's name in the list.
There, you will find two similar blocks: one to configure the start zone, one for the finish zone.

Each one has the following inputs:
- Manual number inputs to set box boundaries (min x, max y...)
  - A "Set Pos Box" button that automatically fills the previous fields to your exact position
  - A "Set Block Box" button that automatically fills the previous fields to the 1x1x1 box you're in
- A "Trigger Mode" toggle specifying whether the timer should start/stop when
  - you "ENTER" the box
  - you "EXIT" the box
- A "Position Mode" toggle specifying whether you want this detection to use
  - your "POS" (the point described by your coordinates)
  - your "BOX" (the bounding box used for collisions)
- A "Use Landing Position" checkbox. If checked, it will use your horizontal position from a tick earlier. When you land, this is the point where you hit the ground.

  ![collision order diagram: the player moves down first, then horizontally](/assets/collision_order_diagram.png)

The current map data is exposed in the _`currentMap`_ infoVar.

#### Start/Finish "Triggers"
Whenever you enter or exit a box (depending on its configuration), not only will it start or stop the timer, but also update some data.
All of it is stored in _`<TriggerZone>.lastTrigger`_ (e.g., `Made tick by: {currentMap.start.lastTrigger.madeByOffset}`).

These pieces of information can come in handy when speedrunning:
- Offsets: _`madeByOffset`_ and _`missedByOffset`_ respectively show your horizontal distance to the box on the trigger tick and on the tick just before.

  It can be useful to know that you were close to reaching the finish zone a tick earlier, or the start zone a tick later, for example.
- _`subtick`_: more info in the following section
- _`tickIndicator`_: ground ticks if positive; air ticks if negative.
  - Start zone: counts run ticks and air ticks right after starting the map
  - Finish zone: counts run ticks and air ticks right before finishing the map

To avoid needing two separate labels for start and finish, you can use the top-level _`lastTrigger`_ infoVar.

#### Subticks
_Original idea/implementation: Zorty420_

A basic subtick system was implemented by looking at the player's linearly interpolated position where it intersects the start or finish box.

<img width=200 alt="explanatory subtick diagram" src="/assets/subtick_diagram.png">

You can show the timer that takes subticks into account with _`timer.withSubtick`_.
Note that for now, it's only able to calculate subticks on start/finish zones that use the POS position mode.

It's mostly interesting on short maps where each tick counts.
There, subticks allow a slightly better trajectory to give a better score when the time in ticks would be the exact same.

## Installing
To install MPKSpeedrun, first get the module .jar file from the
[releases](https://github.com/Zpiboo/MPKSpeedrun/releases) page (or build it yourself), and place it
in the `mpkmodules` directory located in your `mods` folder (you might need to create it).

If you're curious, you can check [actions](https://github.com/Zpiboo/MPKSpeedrun/actions) to download snapshots and test new features
(keep in mind that snapshots are unstable versions, so expect them to have more bugs than the latest release).

## Planned features
- Map splits
- Map folders
- Dynamic maps (automatic per-server config, for instance on hollowcube we would make it set the end box to the gold pressure plate)
- Ladder utilities (airtime)
- Spherical zones for start/finish (center + radius)
- Ceiling detection

## Support
If you find a bug or want to request a new feature, feel free to [create an issue](https://github.com/Zpiboo/MPKSpeedrun/issues)
where you explain your problem clearly and what should change.

If you need to ask a question that's not a bug report or a feature request, you can DM me on Discord (@zpiboo).
