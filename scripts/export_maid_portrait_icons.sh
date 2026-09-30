#!/bin/bash
set -euo pipefail
for variant in blue_snow_maid blue_snow_maid_front; do
  source="artwork/app-icon/${variant}_portrait_native.png"
  while IFS= read -r target; do
    name="$(basename "$target" .png)"
    case "$name" in
      ic_launcher_${variant}|ic_launcher_${variant}_round|ic_launcher_${variant}_foreground|ic_launcher_${variant}_monochrome|ic_launcher_${variant}_light|ic_launcher_${variant}_light_round|ic_launcher_${variant}_light_foreground|ic_launcher_${variant}_dark|ic_launcher_${variant}_dark_round|ic_launcher_${variant}_dark_foreground) ;;
      *) continue ;;
    esac
    size="$(magick identify -format '%w' "$target")"
    case "$name" in
      *_foreground|*_monochrome) content=$((size * 64 / 100)) ;;
      *) content=$((size * 94 / 100)) ;;
    esac
    magick "$source" -trim +repage -resize "${content}x${content}" -gravity center -background none -extent "${size}x${size}" "$target"
    case "$name" in
      *_monochrome) magick "$target" -channel RGB -evaluate set 100% +channel "$target" ;;
    esac
  done < <(find app/src/main/res -path '*/mipmap-*/*.png' -name "ic_launcher_${variant}*.png")
  magick "$source" -trim +repage -resize 390x390 -gravity center -background none -extent 512x512 -background white -alpha remove -alpha off "artwork/app-icon/${variant}_portrait_playstore_512.png"
  magick "$source" -trim +repage -resize 390x390 -gravity center -background none -extent 512x512 -background '#090A0C' -alpha remove -alpha off "artwork/app-icon/${variant}_portrait_dark_playstore_512.png"
done
