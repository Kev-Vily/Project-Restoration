package classicMod.library.ui.menu;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.geom.Vec3;
import mindustry.graphics.g3d.PlanetParams;

import static arc.Core.graphics;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.renderer;

public class SpaceMenuBackground extends MenuBackground {
    public static FrameBuffer menuBuffer;
    public static PlanetParams menuParams;
    public PlanetParams params;

    @Override
    public void render() {
        int size = Math.max(graphics.getWidth(), graphics.getHeight());

        if (menuBuffer == null) {
            menuBuffer = new FrameBuffer(size, size);
        }

        menuBuffer.begin(Color.clear);

        params.alwaysDrawAtmosphere = Core.settings.getBool("atmosphere");
        params.drawUi = false;

        if (menuParams == null) {
            menuParams = params;
        }

        menuParams.camPos.rotate(Vec3.Y, fdelta(250f, 120f));

        renderer.planets.render(menuParams);

        menuBuffer.end();

        Draw.rect(Draw.wrap(menuBuffer.getTexture()), (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());
    }
}