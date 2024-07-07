package classicMod.library.ui.dialog;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.input.KeyCode;
import arc.math.geom.Vec3;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.util.*;
import classicMod.library.ui.UIExtended;
import classicMod.library.ui.menu.MenuBackground;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.gen.*;
import mindustry.graphics.g3d.*;
import mindustry.ui.Styles;

import static arc.Core.*;
import static classicMod.ClassicMod.*;
import static classicMod.content.ExtendedMusic.*;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.*;

public class epicCreditsDialog extends Dialog {

    Image logo = new Image(new TextureRegionDrawable(Core.atlas.find("restored-mind-logoMod")), Scaling.fit);
    PlanetParams state = new PlanetParams() {{
        planet = Planets.serpulo;
        camPos = new Vec3(5, 0, 0);
        zoom = 0.6f;
    }};
    public final PlanetRenderer planets = renderer.planets;

    Table in = new Table(){{
        add(logo).size(570f, 90f).row();
        image(Tex.clear).height(55).padTop(3f).row();
        row();
        //image(Tex.clear).height(25).padTop(3f).row();
        //image(Core.atlas.find("restored-mind-logoMod")).row();
        //image(Tex.clear).height(25f).padTop(3f).row();

        add(bundle.get("credits.text")).row();
        add(getModBundle.get(resMod.meta.name + "-credits.author")).row();

        int i = 0;
        while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
            add(getModBundle.get(resMod.meta.name + "-credits." + i));
            row();
            i++;
        }
        image(Tex.clear).height(35).padTop(3f).row();
        add(bundle.get("contributors")).row();
        image(Tex.clear).height(35).padTop(3f).row();

        table(contributor -> {
            if(!contributors.isEmpty()){
                int ia = 0;
                for(String c : contributors){
                    add(c);
                    if(++ia % 3 == 0){
                        row();
                    }
                }
            /*contributors.each(a -> {
                add(a);
                row();
            });*/
            }
        }).center();

    }};
    float TableHeight;
    float halfTableHeight;

    Table staticTable = new Table(){{
        add(getModBundle.get(resMod.meta.name + "-credits.mobile" + app.isMobile()));
    }};

    float scrollbar;

    DialogStyle baller = new DialogStyle(){{
        background = Styles.none;
    }};

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

    //ScrollPane pane = new ScrollPane(in);

    public epicCreditsDialog() {
        super();
        scrollbar = 0f;
        playMusic(credits);
        //addCloseButton();
        //staticTable.setTranslation(-(camera.width+128f), -(camera.height+128f));
        //cont.add(staticTable);
        cont.add(in).align(Align.bottom);
        show();
    }

    int doubleTapTimer;
    boolean onHold;
    boolean firstTap;

    @Override
    public void act(float delta) {
        control.sound.stop();
        super.act(delta);
        if(TableHeight <= 0){
            TableHeight = in.getHeight();
            halfTableHeight = TableHeight / 1.75f;
        }
        if(scrollbar * 1.15f >= (TableHeight * 1.462f)){ FinishedCredits(); return; }
        //Log.info("IN HEIGHT " +in.getHeight());
        //Log.info("IN prefHEIGHT " +in.getPrefHeight());
        //Log.info("IN minHEIGHT " +in.getMinHeight());
        //Log.info("IN maxHEIGHT " +in.getMaxHeight());
        var bot = (Vars.mobile) ? 120f : 60f; //alignment for mobile kinda off bud
        scrollbar += fdelta(650f, bot);
        //cont.clearChildren();

        in.update(() -> {
            setTranslation(0f, scrollbar - (halfTableHeight + Core.camera.height));
        });

        setStyle(baller);

        //Log.info("Crolld "+ scrollbar);
        //Log.info("Crollf "+ scrollbar * 1.15f);
        //Log.info("Crollh "+ TableHeight * 1.462f);
        //Log.info(scrollbar * 1.15f >= (TableHeight * 1.462f));
        //Log.info((float) getModBundle.get(resMod.meta.name + "-credits.mobile" + app.isMobile()).length() / 2);

        if(Core.input.keyDown(KeyCode.escape)) FinishedCredits();
        if(Core.app.isMobile()){

            if(app.isAndroid()){
                if(Core.input.keyDown(KeyCode.back)) FinishedCredits();
            }

            if(firstTap){
                if(!Core.input.isTouched()){ onHold = false; }
                if(!onHold) {
                    doubleTapTimer++;
                    if (Core.input.isTouched()) FinishedCredits();
                    if (doubleTapTimer > 100){ firstTap = false; doubleTapTimer = 0; }
                }
            } else {
                if(Core.input.isTouched()){ firstTap = true; onHold = true; }
            }
            //if(((scrollbar > (TableHeight)) && TableHeight > 0) || Core.input.isTouched()) this.hide();
        }
    }

    public void FinishedCredits(){
        control.sound.update();
        stopMusic();
        this.hide();
    }

    @Override
    public void draw() {
        float IE = ((float) graphics.getWidth() / 1000);
        float IA = ((float) graphics.getWidth() / 225);
        staticTable.x = (((float) getModBundle.get(resMod.meta.name + "-credits.mobile" + app.isMobile()).length() / 2) * IA * IE);
        staticTable.y = 20f;

        Styles.black.draw(0, 0, UIExtended.getWidth(), UIExtended.getHeight());
        staticTable.draw();

        planets.render(state); // plz work.

        Draw.flush();
        super.draw();
    }
}
