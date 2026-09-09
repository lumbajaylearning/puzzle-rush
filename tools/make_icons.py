#!/usr/bin/env python3
"""Rebuild original code-drawn launcher icons. Requires Pillow only for authoring."""
from pathlib import Path
from PIL import Image, ImageDraw

ASSETS = Path(__file__).resolve().parents[1] / "assets"


def symbol(size, background, mono=False, adaptive=False):
    scale = 4
    image = Image.new("RGBA", (size * scale, size * scale), background)
    draw = ImageDraw.Draw(image)
    factor = size * scale / 192
    shrink = 0.63 if adaptive else 1.0

    def p(x, y):
        return ((96 + (x - 96) * shrink) * factor, (96 + (y - 96) * shrink) * factor)

    for points, color, width in [([(43, 126), (115, 54), (68, 54)], "#9af6cc", 17),
                                  ([(115, 54), (115, 101)], "#9af6cc", 17),
                                  ([(83, 142), (147, 78)], "#b3a1ff", 12)]:
        fill = "white" if mono else color
        w = round(width * factor * shrink)
        mapped = [p(*xy) for xy in points]
        draw.line(mapped, fill=fill, width=w, joint="curve")
        for x, y in mapped:
            draw.ellipse((x - w / 2, y - w / 2, x + w / 2, y + w / 2), fill=fill)
    return image.resize((size, size), Image.Resampling.LANCZOS)


if __name__ == "__main__":
    symbol(192, "#0b101a").save(ASSETS / "icon.png")
    symbol(432, (0, 0, 0, 0), adaptive=True).save(ASSETS / "icon_foreground.png")
    symbol(432, (0, 0, 0, 0), mono=True, adaptive=True).save(ASSETS / "icon_monochrome.png")
    Image.new("RGB", (432, 432), "#0b101a").save(ASSETS / "icon_background.png")
