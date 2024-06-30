package classicMod.library.blocks.snek;


import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.DirectionalItemBuffer;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.*;
import static mindustry.Vars.content;

public class SorterRevamp extends Block {
    public TextureRegion cross = Core.atlas.find(name+"-cross", "cross-full");
    public boolean invert;

    public SorterRevamp(String name) {
        super(name);
        update = false;
        destructible = true;
        underBullets = true;
        instantTransfer = false;
        group = BlockGroup.transportation;
        configurable = true;
        unloadable = false;
        saveConfig = true;
        clearOnDoubleTap = true;

        config(Item.class, (SorterRevampBuild tile, Item item) -> tile.sortItem = item);
        configClear((SorterRevampBuild tile) -> tile.sortItem = null);

        region = Core.atlas.find(name);
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(plan, plan.config, "center", true);
    }

    @Override
    public boolean outputsItems() {
        return true;
    }

    @Override
    public int minimapColor(Tile tile) {
        var build = (SorterRevampBuild) tile.build;
        return build == null || build.sortItem == null ? 0 : build.sortItem.color.rgba();
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find("source-bottom"), Core.atlas.find(name)};
    }

    public class SorterRevampBuild extends Building {
        public @Nullable Item sortItem;

        @Override
        public void configured(Unit player, Object value) {
            super.configured(player, value);

            if (!headless) {
                renderer.minimap.update(tile);
            }
        }

        @Override
        public void draw() {
            //region = Core.atlas.find(name);
            Draw.rect(Core.atlas.find(name), x, y);
            TextureRegion cross = Core.atlas.find(name+"-cross", "cross-full");
            if (sortItem == null) {
                Draw.rect(cross, x, y);
            } else {
                Draw.color(sortItem.color);
                Fill.square(x, y, tilesize / 2f - 0.00001f);
                Draw.color();
            }

            //super.draw();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            Building to = getTileTarget(item, source, false);

            return to != null && to.acceptItem(this, item) && to.team == team;
        }

        @Override
        public void handleItem(Building source, Item item) {
            getTileTarget(item, source, true).handleItem(this, item);
        }

        public boolean isSame(Building other) {
            return other != null && other.block.instantTransfer;
        }

        public Building getTileTarget(Item item, Building source, boolean flip) {
            int dir = source.relativeTo(tile.x, tile.y);
            if (dir == -1) return null;
            Building to;

            if (((item == sortItem) != invert) == enabled) {
                //don't prevent 3-chains

                to = nearby(dir);
            } else {
                Building a = nearby(Mathf.mod(dir - 1, 4));
                Building b = nearby(Mathf.mod(dir + 1, 4));
                boolean ac = a != null &&
                        a.acceptItem(this, item);
                boolean bc = b != null &&
                        b.acceptItem(this, item);

                if (ac && !bc) {
                    to = a;
                } else if (bc && !ac) {
                    to = b;
                } else if (!bc) {
                    return null;
                } else {
                    to = (rotation & (1 << dir)) == 0 ? a : b;
                    if (flip) rotation ^= (1 << dir);
                }
            }

            return to;
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(SorterRevamp.this, table, content.items(), () -> sortItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public Item config() {
            return sortItem;
        }

        @Override
        public byte version() {
            return 2;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(sortItem == null ? -1 : sortItem.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            sortItem = content.item(read.s());

            if (revision == 1) {
                new DirectionalItemBuffer(20).read(read);
            }
        }
    }
}

