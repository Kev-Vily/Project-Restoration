package classicMod.library.ui.menu;

import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.ctype.ContentType;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

import static arc.Core.settings;
import static mindustry.Vars.content;

public class MenuUI {
    public static MenuBackground Tantros, Erekir, Serpulo, random, solarSystem, SortedPlanet;

    public static Planet lastPlanet = content.getByName(ContentType.planet, settings.getString("lastplanet", "serpulo"));
    public static void load() {
        Erekir = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.erekir;

                zoom = 0.6f;
            }};
        }};
        Serpulo = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.serpulo;
                //camPos = new Vec3(0, 0, 0);
                zoom = 0.6f;
            }};
        }};
        Tantros = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.tantros;
                //camPos = new Vec3(0, 0, 0);
                zoom = 0.6f;
            }};
        }};
        random = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                Seq<Planet> visible = Vars.content.planets().copy().filter(p -> p.visible);
                planet = visible.get(Mathf.floor((float) (Math.random() * visible.size)));
            }};
        }};

        SortedPlanet = new SpaceMenuBackground() {{
            params = new PlanetParams() {{ //Support test for modded planets! +it's sorted into planets so ;)
                Seq<Planet> visible = Vars.content.planets().copy().filter(p -> p.visible);
                if(lastPlanet != null) {
                    for (var c : visible) {
                        if (c.name == lastPlanet.name) {
                            planet = c;
                            zoom = 0.6f;
                        }
                    }
                }else{
                    params = new PlanetParams() {{
                        Seq<Planet> visible = Vars.content.planets().copy().filter(p -> p.visible);
                        planet = visible.get(Mathf.floor((float) (Math.random() * visible.size)));
                    }};
                }
            }};
        }};
        solarSystem = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.sun;
                camPos = new Vec3(0, 0.5, 0);
                zoom = 12f;
            }};
        }};
    }
}
