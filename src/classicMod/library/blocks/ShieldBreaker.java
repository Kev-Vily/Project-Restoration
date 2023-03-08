package classicMod.library.blocks;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.Fonts;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;

import mindustry.world.meta.*;

public class ShieldBreaker extends Block{
    public @Nullable Block toDestroy;
    public TextureRegion notify;
    public Effect effect = Fx.shockwave, breakEffect = Fx.reactorExplosion, selfKillEffect = Fx.massiveExplosion;

    public ShieldBreaker(String name){
        super(name);

        solid = update = true;
    }

    @Override
    public boolean canBreak(Tile tile){
        return false;
    }

    public class ShieldBreakerBuild extends Building{
        public boolean NoBlock;
        @Override
        public void updateTile(){
            if(toDestroy != null){
                for(var other : Vars.state.teams.active){
                    if(team != other.team && other.active()){
                        NoBlock = false;
                    }else{
                        NoBlock = true;
                    }
                }
            }
            if(Mathf.equal(efficiency, 1f)){
                if(toDestroy != null){
                    effect.at(this);
                    for(var other : Vars.state.teams.active){
                        if(team != other.team && other.active()){
                            other.getBuildings(toDestroy).copy().each(b -> {
                                breakEffect.at(b);
                                b.kill();
                            });
                        }
                    }
                    selfKillEffect.at(this);
                    kill();
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            notify = Core.atlas.find(name + "-notify");
            Draw.z(Layer.block);
            Draw.rect(region, tile.drawx(), tile.drawy());
            Draw.z(Layer.blockOver);
            if(NoBlock){
                Draw.color(new Color(255,255,255,255));
            }else{
                Draw.color(new Color(255,255,255,0));
            }
            Draw.rect(notify, tile.drawx(), tile.drawy());
        }
    }
}
