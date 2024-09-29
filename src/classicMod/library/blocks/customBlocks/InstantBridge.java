package classicMod.library.blocks.customBlocks;

import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ItemBridge;

import static mindustry.Vars.world;

public class InstantBridge extends ItemBridge {
    public InstantBridge(String name) {
        super(name);
    }

    public class InstantBridgeBuild extends ItemBridgeBuild{
        @Override
        public void updateTile(){
            if(timer(timerCheckMoved, 30f)){
                wasMoved = moved;
                moved = false;
            }

            //smooth out animation, so it doesn't stop/start immediately
            timeSpeed = Mathf.approachDelta(timeSpeed, wasMoved ? 1f : 0f, 1f / 60f);

            time += timeSpeed * delta();

            checkIncoming();

            Tile other = world.tile(link);
            if(!linkValid(tile, other)){
                doDump();
                warmup = 0f;
            }else{
                var inc = ((ItemBridgeBuild)other.build).incoming;
                int pos = tile.pos();
                if(!inc.contains(pos)){
                    inc.add(pos);
                }

                warmup = 0.5f;
                updateTransport(other.build);
            }
        }

        @Override
        public void updateTransport(Building other) {
            Item item = items.take();
            if (item != null && other.acceptItem(this, item)) {
                other.handleItem(other, item);
            } else if (item != null) {
                items.add(item, 1);
                items.undoFlow(item);
            }
        }
    }
}
