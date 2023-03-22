package classicMod.library.blocks.v6devBlocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;

import static mindustry.Vars.*;

public class ResearchBlock extends StorageBlock{
    public float researchSpeed = 1f;
    public TextureRegion topRegion;

    public ResearchBlock(String name){
        super(name);

        update = true;
        solid = true;
        hasPower = true;
        hasItems = true;
        configurable = true;
        itemCapacity = 0;
        topRegion = Core.atlas.find(name+"-top");
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if(tile == null) return false;

        //only allow placing next to cores
        for(Point2 edge : Edges.getEdges(size)){
            Tile other = tile.nearby(edge);
            if(other != null && other.block() instanceof CoreBlock && other.team() == team){
                return true;
            }
        }
        return false;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        boolean hasCore = canPlaceOn(world.tile(x, y), player.team(), rotation);
        if(!hasCore){
            drawPlaceText(Core.bundle.get("bar.corereq"), x, y, valid);
        }
    }

    public class ResearchBlockBuilding extends StorageBuild{
        public @Nullable UnlockableContent researching;

        @Override
        public void updateTile(){

        }

        @Override
        public void buildConfiguration(Table table){

        }

        @Override
        public void draw(){
            super.draw();

            Draw.mixcol(Color.white, Mathf.absin(10f, 0.2f));
            Draw.rect(topRegion, x, y);
            Draw.reset();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return linkedCore != null && super.acceptItem(source, item);
        }

        @Override
        public boolean configTapped(){
            //TODO select target
            ui.research.show();

            return false;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            if(researching != null){
                write.b(researching.getContentType().ordinal());
                write.s(researching.id);
            }else{
                write.b(-1);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            byte type = read.b();
            if(type != -1){
                researching = Vars.content.getByID(ContentType.all[type], read.s());
            }else{
                researching = null;
            }
        }
    }
}
