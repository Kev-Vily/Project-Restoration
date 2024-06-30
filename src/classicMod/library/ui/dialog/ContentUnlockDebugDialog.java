package classicMod.library.ui.dialog;

import arc.Core;
import arc.Events;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.*;
import static mindustry.Vars.control;

public class ContentUnlockDebugDialog extends BaseDialog {

    public ContentUnlockDebugDialog() {
        super("@CUD.title");
        addCloseButton();

        cont.pane(new Table() {
            {
                for (var Content : Vars.content.items()){
                    table(Styles.grayPanel, info -> {
                        info.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                        info.add(Content.localizedName).left();

                        info.table(yes -> {
                            yes.button("@lock", Content::clearUnlock).size(64f, 64f).pad(2.5f);
                            yes.button("@unlock", Content::unlock).size(64f, 64f).pad(2.5f);
                        }).right().pad(2.5f);
                    }).left();
                    row();
                }
            }
        });

        cont.pane(new Table() {
            {
                for (var Content : Vars.content.liquids()){
                    table(Styles.grayPanel, info -> {
                        info.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                        info.add(Content.localizedName).left();

                        info.table(yes -> {
                            yes.button("@lock", Content::clearUnlock).size(64f, 64f).pad(2.5f);
                            yes.button("@unlock", Content::unlock).size(64f, 64f).pad(2.5f);
                        }).right().pad(2.5f);
                    }).left();
                    row();
                }
            }
        });

        cont.pane(new Table() {
            {
                for (var Content : Vars.content.units()){
                    table(Styles.grayPanel, info -> {
                        info.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                        info.add(Content.localizedName).left();

                        info.table(yes -> {
                            yes.button("@lock", Content::clearUnlock).size(64f, 64f).pad(2.5f);
                            yes.button("@unlock", Content::unlock).size(52.5f,, 64f).pad(2.5f);
                        }).right().pad(2.5f);
                    }).left();
                    row();
                }
            }
        });

        cont.pane(new Table() {
            {
                for (var Content : Vars.content.sectors()){
                    table(Styles.grayPanel, info -> {
                        info.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left().color(Content.planet.iconColor);
                        info.add(Content.localizedName).left();

                        info.table(yes -> {
                            yes.button("@lock", Content::clearUnlock).size(64f, 64f).pad(2.5f);
                            yes.button("@unlock", Content::unlock).size(64f, 64f).pad(2.5f);
                            yes.button("@launch-to", () -> StartSector(Content)).size(64f, 64f).pad(2.5f);
                        }).right().pad(2.5f);
                    }).left();
                    row();
                }
            }
        });

        show();
    }
    public void StartSector(SectorPreset to){
        var sector = to.sector;

        Events.fire(new EventType.SectorLaunchEvent(sector));
        control.playSector(sector);
    }

}
