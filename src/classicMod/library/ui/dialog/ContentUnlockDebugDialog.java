package classicMod.library.ui.dialog;

import arc.Core;
import arc.Events;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import classicMod.ClassicMod;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
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
        addUnlockAllButton();

        rebuild();

        show();
    }

    public void addUnlockAllButton(float width){
        buttons.defaults().size(width, 64f);
        buttons.button("@unlockall", Icon.left, ClassicMod::unlockAll).size(width, 64f);
    }

    public void addUnlockAllButton(){
        addUnlockAllButton(210f);
    }

    void rebuild(){
        float buttonWidth = 92f;
        float buttonHeight = 32f;
        cont.clearChildren();
        cont.pane(new Table() {
            {
                for (var Content : Vars.content.items()){
                    table(Styles.grayPanel, info -> {
                        info.table(details -> {
                            details.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                            details.add(Content.localizedName).left().pad(10f);
                        });

                        info.row();
                        info.table(yes -> {
                            if(Content.unlocked()) yes.button("@lock", () -> {
                                Content.clearUnlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                            else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }).left().pad(2.5f);
                    }).left().pad(10f);
                    row();
                }
            }
        });

        cont.pane(new Table() {
            {
                for (var Content : Vars.content.liquids()){
                    table(Styles.grayPanel, info -> {
                        info.table(details -> {
                            details.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                            details.add(Content.localizedName).left().pad(10f);
                        });

                        info.row();
                        info.table(yes -> {
                            if(Content.unlocked()) yes.button("@lock", () -> {
                                Content.clearUnlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                            else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }).left().pad(2.5f);
                    }).left().pad(10f);
                    row();
                }
            }
        });

        cont.pane(new Table() {
            {
                for (var Content : Vars.content.units()){
                    if(Content.isHidden()) continue;
                    table(Styles.grayPanel, info -> {
                        info.table(details -> {
                            details.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                            details.add(Content.localizedName).left().pad(10f);
                        });

                        info.row();
                        info.table(yes -> {
                            if(Content.unlocked()) yes.button("@lock", () -> {
                                Content.clearUnlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                            else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }).left().pad(2.5f);
                    }).left().pad(10f);
                    row();
                }
            }
        });

        cont.pane(new Table() {
            {
                for (var Content : Vars.content.sectors()){
                    table(Styles.grayPanel, info -> {
                        info.table(details -> {
                            details.image(Icon.icons.get(Content.planet.icon + "Small", Icon.icons.get(Content.planet.icon, Icon.commandRallySmall))).size(32f).scaling(Scaling.fit).pad(10f).left().color(Content.planet.iconColor);
                            details.add(Content.localizedName).left().pad(10f);
                        });

                        info.row();
                        info.table(yes -> {
                            if(Content.unlocked()) {
                                yes.button("@lock", () -> {
                                    Content.clearUnlock();
                                    rebuild();
                                }).size(buttonWidth, buttonHeight).pad(2.5f);
                                yes.button("@launch-to", () -> {
                                    StartSector(Content);
                                    hide();
                                }).size(105f, 64f).pad(2.5f);
                            } else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }).left().pad(2.5f);
                    }).left().pad(10f);
                    row();
                }
            }
        });
    }


    public void StartSector(SectorPreset to){
        var sector = to.sector;

        Events.fire(new EventType.SectorLaunchEvent(sector));
        control.playSector(sector);
    }

}
