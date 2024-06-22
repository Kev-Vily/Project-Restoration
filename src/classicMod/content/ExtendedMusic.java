package classicMod.content;

import arc.*;
import arc.audio.*;
import arc.util.*;
import mindustry.game.*;

import static mindustry.Vars.tree;

// WilloIzCitron Music Player Code
public class ExtendedMusic {

    private static @Nullable Music current;
    private static Music lastMusicPlayed;
    public static Music
            credits;


    protected static void playMusic(Music music){
        if(current != null || music == null || !(boolean)(Core.settings.getInt("musicvol") > 0)) return;
        lastMusicPlayed = music;
        current = music;
        current.setVolume(1f);
        current.setLooping(false);
        current.play();

    }

    public static void load(){
        Events.on(EventType.ClientLoadEvent.class, e -> {
            current = null;
            //music loader
            try {
                credits = new Music(tree.get("music/wave1.ogg"));
            } catch (Exception ex) {
                // Music has exception throw, why it was created
                throw new RuntimeException(ex);
            }
        });
    }
}
