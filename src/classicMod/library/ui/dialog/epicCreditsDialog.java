package classicMod.library.ui.dialog;

import arc.Core;
import arc.func.Cons;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Dialog;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.*;
import arc.util.Time;
import classicMod.library.ui.UIExtended;
import mindustry.core.UI;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static arc.Core.bundle;
import static classicMod.ClassicMod.*;

public class epicCreditsDialog extends BaseDialog {
    Table in = new Table();
    float scrollbar = 0f;

    /*public void addCloseListener(){
        closeOnBack();
    }

    public void addCloseButton(float width){
        buttons.defaults().size(width, 64f);
        buttons.button("@back", Icon.left, this::hide).size(width, 64f);

        addCloseListener();
    }

    @Override
    public void addCloseButton(){
        addCloseButton(210f);
    }*/

    ScrollPane pane = new ScrollPane(in);

    public epicCreditsDialog() throws InterruptedException {
        super("Credits");
        addCloseButton();

        in.add(new Table() {{
            center();
            image(Tex.clear).height(25).padTop(3f).row();
            image(Core.atlas.find("restored-mind-logoMod")).row();
            image(Tex.clear).height(25f).padTop(3f).row();

            add(bundle.get("credits.text")).row();

            int i = 0;
            while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
                add(getModBundle.get(resMod.meta.name + "-credits." + i));
                row();
                i++;
            }

            add(bundle.get("contributors"));
            image(Tex.clear).height(55).padTop(3f).row();

            if(!contributors.isEmpty()){
                contributors.each(a -> {
                    add(a);
                    row();
                });
            }
        }});
        //cont.pane(in).growX();

        for (float b = 0f; b < UIExtended.getHeight() * 2; b += 0.000275f  * Time.delta) {
            cont.clearChildren();
            //in.setTranslation(0, b);
            float finalB = b;
            in.update(() -> setTranslation((float) 0, finalB - UIExtended.getHeight()));
            cont.add(in);
            TimeUnit.MILLISECONDS.sleep(500);
        }

        show();

        /*in.center();
        in.image(Tex.clear).height(25).padTop(3f).row();
        in.image(Core.atlas.find("restored-mind-logoMod")).row();
        in.image(Tex.clear).height(25f).padTop(3f).row();

        int i = 0;
        while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
            in.add(getModBundle.get(resMod.meta.name + "-credits." + i));
            in.row();
            i++;
        }
        cont.add(in);

        for (float b = 0f; b < UIExtended.getHeight(); b += 0.275f  * Time.delta) {
            cont.clearChildren();
            //in.setTranslation(0, b);
            float finalB = b;
            in.update(() -> setTranslation((float) UIExtended.getWidth() /2, finalB));
            cont.add(in);
        }*/
    }
}
