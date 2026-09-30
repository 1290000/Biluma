# Maid portrait icons

Generated using the built-in image_gen tool, editing the existing blue_snow_maid and blue_snow_maid_front Play Store references.

Prompt set: preserve each character's identity, expression, pose, blue-white hair, outfit and anime illustration style; use a head-and-shoulders ID portrait with a smaller cyan circle behind the character; let the headdress and upper hair overlap and extend above the circle; retain rounded lower shoulders; transparent outside the portrait and circle; no text or extra objects.

The portrait_native PNGs are the new source artwork. Existing source masters are preserved. The portrait_playstore and portrait_dark_playstore files show white and #090A0C backgrounds. maid_portrait_preview.png compares both characters and appearances.

Run scripts/export_maid_portrait_icons.sh from the repository root with ImageMagick installed to export the existing launcher PNG resources. Adaptive foregrounds fit the trimmed artwork within 64% of the layer's width/height; legacy icons fit within 94%. Monochrome resources use the new alpha silhouette. Existing adaptive background XML resources provide light/dark backgrounds. The announcement/megaphone icon is unchanged.

Validation: visually reviewed generated artwork and light/dark previews; exported existing resource sizes. No Gradle compile, build, packaging or installation was run.
