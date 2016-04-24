### A Note on Animations ###

The image subfolders herein (`atomic`, `cannon+blast`, …) are for new, AnimatedSprite-style rendering. Once that is working, all gifs should be done away with entirely.

Furthermore, there are a couple new PNGs that stand in a couple of places in the game, for when there’s nothing animating. These really don’t need their own folder, so they stand alone here. All the PNGs will be used; none of the gifs will.

When determining the numeric timings of the AnimatedSprite, as a rule of thumb, each frame should be displayed for about 0.06 seconds.

There are some exceptions, however: ‘Kitten On Target 0’ should be displayed for two seconds, followed by the rest of the animation in its folder (1..14). Any way this can be implemented is OK—though if it must be special-cased, such should at least be done in a subclass.

- Atomic: play once.
- Cannon: still.
- Cannon + Blast: play once.
- Kitten: loop.
- Kitten + Target: loop; see above.
- Target: still, in place of Kitten + Target.
- Spring Up: play once.
- Slink Away: play once.

Finally, more animated finishing graphics (as promised) are coming down the tubes.
