package classicMod.library.ui.dialog;

import arc.Core;
import arc.func.Cons;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Dialog;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.*;
import classicMod.library.ui.UIExtended;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.awt.*;

import static arc.Core.bundle;
import static classicMod.ClassicMod.*;

public class epicCreditsDialog extends Dialog {
    Table in = new Table();
    float scrollbar = 0f;

    public void addCloseListener(){
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
    }

    ScrollPane pane = new ScrollPane(in);

    public epicCreditsDialog() {
        super();
        addCloseButton();
        /*cont.pane(new Table() {{
            center();
            image(Tex.clear).height(25).padTop(3f).row();
            image(Core.atlas.find("restored-mind-logoMod")).row();
            image(Tex.clear).height(25f).padTop(3f).row();

            int i = 0;
            while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
                add(getModBundle.get(resMod.meta.name + "-credits." + i));
                row();
                i++;
            }
        }});*/

        in.center();
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
        show();
        for (int b = 0; b < UIExtended.getHeight()*100; b++) {
            cont.clearChildren();
            in.setTranslation(0, b);
            int finalB = b;
            in.update(() -> setTranslation(0, (float) finalB /100));
            cont.add(in);
        }
    }
}
