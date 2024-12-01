package classicMod.library.blocks.neoplasiaBlocks;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Log;
import classicMod.content.ClassicBlocks;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.world.*;
import mindustry.world.blocks.Attributes;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;

public class NeoplasiaBlock extends Block {

    public boolean source = false;
    public Color beatColor = Color.valueOf("cd6240");

    public boolean isCord = false;

    public NeoplasiaBlock(String name) {
        super(name);

        update = true;
    }

    public class NeoplasiaBuilding extends Building {

        public Seq<Tile> proximityTiles = new Seq<>();

        boolean startBuild = true, initalize = false;
        float beat = 1f, beatTimer = 0, priority = 0;
        boolean ready = false, alreadyBeat = false, grow = false;

        @Override
        public void draw() {
            drawBeat(1, 1);
            super.draw();
        }

        public boolean isSource(){
            return source;
        }

        public void drawBeat(float xscl, float yscl){
            drawBeat(xscl, yscl, 1);
        }

        public void drawBeat(float xscl, float yscl, float offsetSclBeat){
            float xs = (xscl > 0) ? xscl + ((beat - 1f) * offsetSclBeat) : xscl - ((beat - 1f) * offsetSclBeat);
            float ys = (yscl > 0) ? yscl + ((beat - 1f) * offsetSclBeat) : yscl - ((beat - 1f) * offsetSclBeat);
            Draw.scl(xs, ys);
            Draw.color(new Color(1.0F, 1.0F, 1.0F, 1.0F).lerp(beatColor, (beat - 1)));
        }

        public boolean isNeoplasia(Building building){
            if (building == null) return false;
            return building instanceof NeoplasiaBuilding;
        }

        public NeoplasiaBuilding getNeoplasia(Building building){
            if (building instanceof NeoplasiaBuilding cordBuild){
                return cordBuild;
            } else {
                return null;
            }
        }

        public Tile nearbyTile(int rotation, short x, short y) {
            return switch (rotation) {
                case 0 -> Vars.world.tile(x + 1, y);
                case 1 -> Vars.world.tile(x, y + 1);
                case 2 -> Vars.world.tile(x - 1, y);
                case 3 -> Vars.world.tile(x, y - 1);
                default -> null;
            };
        }

        public Building nearby(int rotation, short x, short y) {
            return switch (rotation) {
                case 0 -> Vars.world.build(x + 1, y);
                case 1 -> Vars.world.build(x, y + 1);
                case 2 -> Vars.world.build(x - 1, y);
                case 3 -> Vars.world.build(x, y - 1);
                default -> null;
            };
        }
        public Building nearby(short x, short y, int dx, int dy) {
            return Vars.world.build(x + dx, y + dy);
        }


        public Tile nearbyTile(int rotation, int offsetTrns) {
            Tile var10000;
            int trns = block.size / 2 + 1 + offsetTrns;
            int dx = Geometry.d4(rotation).x * trns, dy = Geometry.d4(rotation).y * trns;
            var10000 = Vars.world.tile(tile.x + dx, tile.y + dy);

            return var10000;
        }

        public Tile nearbyTile(short x, short y, int dx, int dy) {
            return Vars.world.tile(x + dx, y + dy);
        }

        public Tile nearbyTile(int rotation) {
            return nearbyTile(rotation, 0);
        }

        public boolean passable(Block block){
            if (block == null) return false;

            if (block instanceof Floor floor){
                if (floor.liquidDrop != null) return false;
            }

            if (
                    block instanceof StaticWall
            ) return false;

            return block == Blocks.air
                    || block instanceof Prop
                    //|| TODO somethin
            ;
        }

        public void coverVent(Block replacmentBlock, Block cordPlacement){
            float steam = 0;
            if (this.tile.floor().attributes.get(Attribute.steam) >= 1) {
                for (int dy = -1; dy < 2; dy++) {
                    for (int dx = -1; dx < 2; dx++) {
                        Tile tile = Vars.world.tile(this.tile.x + dx, this.tile.y + dy);
                        if (tile.floor() != null && (tile.build == null)) {
                            steam += tile.floor().attributes.get(Attribute.steam);
                            if (tile.floor().attributes.get(Attribute.steam) >= 1) {
                                if (tile.build == null) tile.setBlock(cordPlacement, team, rotation);
                            }
                        }
                    }
                }
            }
            if (steam >= 9f){
                Tile replacement = Vars.world.tile(this.tile.x, this.tile.y);
                replacement.setBlock(replacmentBlock, team, rotation);
            }
        }

        public void coverOre(Block replacmentBlock, Block cordPlacement){
            float ore = 0;
            if (tile.drop() != null) {
                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) {
                        Tile tile = Vars.world.tile(this.tile.x + dx, this.tile.y + dy);
                        if (tile.floor() != null && (tile.build == null)) {
                            ore += (tile.drop() != null) ? 1 : 0;
                            if (tile.floor().attributes.get(Attribute.steam) >= 1) {
                                if (tile.build == null) tile.setBlock(cordPlacement, team, rotation);
                            }
                        }
                    }
                }
            }
            if (ore >= 4f){
                Tile replacement = Vars.world.tile(this.tile.x, this.tile.y);
                replacement.setBlock(replacmentBlock, team, rotation);
            }
        }

        public boolean front(int rot, short x, short y){
            boolean place = true;
            int dxx = Geometry.d4x(rot);
            int dyy = Geometry.d4y(rot);
            if (dxx != 0) {
                for (int fx = 0; fx != -(dxx * 2); fx -= dxx) {
                    for (int dy = dxx; dy != -(dxx * 2); dy -= dxx) {
                        int frontRot = -1;
                        Tile front = nearbyTile(x, y, fx, dy);
                        if (front == null) continue;
                        if (front.build != null) {
                            if (front.build != this) frontRot = front.build.rotation;
                        }
                        if (!passable(front.block()) && front.build != this || frontRot != -1 && front.build != this) place = false;
                    }
                }
            } else {
                for (int fy = 0; fy != -(dyy * 2); fy -= dyy) {
                    for (int dx = dyy; dx != -(dyy * 2); dx -= dyy) {
                        int frontRot = -1;
                        Tile front = nearbyTile(x, y, dx, fy);
                        if (front == null) continue;
                        if (front.build != null) {
                            if (front.build != this) frontRot = front.build.rotation;
                        }
                        if (!passable(front.block()) && front.build != this || frontRot != -1 && front.build != this) place = false;
                    }
                }
            }
            return place;
        }

        public boolean front3(int rot, short x, short y){
            boolean place = true;
            int dxx = Geometry.d4x(rot);
            int dyy = Geometry.d4y(rot);
            if (dxx != 0) {
                for (int dx = dxx; dx != -(dxx * 2); dx -= dxx) {
                    Tile front = nearbyTile(x, y, dx, dxx);
                    if (front == null) place = false;
                    if (front != null && (!passable(front.block()) && front.build != null && front.build != this))
                        place = false;
                }
            } else {
                for (int dy = dyy; dy != -(dyy * 2); dy -= dyy) {
                    Tile front = nearbyTile(x, y, dyy, dy);
                    if (front == null) place = false;
                    if (front != null && (!passable(front.block()) && front.build != null && front.build != this))
                        place = false;
                }
            }

            return place;
        }

        public void growCord(Block block){
            if (!isCord) {
                int randRot = (int) Mathf.range(4);
                Tile tile = nearbyTile(randRot);
                if (tile != null) {
                    if (tile.build == null) {
                        tile.setBlock(block, team, randRot);
                    }
                }
            } else {
                boolean branchOut = Mathf.randomBoolean(0.85f);
                boolean keepDirection = Mathf.randomBoolean(0.5f);
                int randRot = (!keepDirection) ? (Mathf.mod(rotation + Mathf.random(1, 4), 4)) : rotation;
                Seq<tileSafe> safeTiles = new Seq<>();
                Seq<tileSafe> branchTiles = new Seq<>();

                Tile tile = nearbyTile(randRot);
                if (branchOut) {
                    boolean left = Mathf.randomBoolean();
                    if (left) {
                        int rot = (Mathf.mod(randRot - 1, 4));
                        Tile newTile = nearbyTile(rot);
                        if (front3(rot, newTile.x, newTile.y) && newTile.build == null) {
                            branchTiles.add(new tileSafe(newTile, rot));
                        }
                    } else {
                        int rot = (Mathf.mod(randRot + 1, 4));
                        Tile newTile = nearbyTile(rot);
                        if (front3(rot, newTile.x, newTile.y) && newTile.build == null) {
                            branchTiles.add(new tileSafe(newTile, rot));
                        }
                    }
                }
                if (!keepDirection) {
                    for (int i = 0; i < 4; i++) {
                        int rot = (Mathf.mod(randRot + Mathf.random(1, 4), 4));
                        Tile newTile = nearbyTile(rot);
                        if (front3(rot, newTile.x, newTile.y) && newTile.build == null) {
                            safeTiles.add(new tileSafe(newTile, rot));
                        }
                    }
                } else {
                    if (front3(randRot, tile.x, tile.y) && tile.build == null) {
                        safeTiles.add(new tileSafe(tile, randRot));
                    } else {
                        for (int i = 0; i < 4; i++) {
                            int rot = Mathf.mod(randRot + i, 4);
                            Tile newTile = nearbyTile(rot);
                            if (front3(rot, newTile.x, newTile.y) && newTile.build == null) {
                                safeTiles.add(new tileSafe(newTile, rot));
                            }
                        }
                    }
                }
                // TODO grow branch
                /*if (!keepDirection) {
                    for (int i = 0; i < 4; i++) {
                        int rot = Mathf.mod(randRot + i, 4);
                        Tile newTile = nearbyTile(rot);
                        if (front3(rot, newTile.x, newTile.y)) {
                            safeTiles.add(new tileSafe(newTile, rot));
                        }
                    }
                } else {
                    if (front3(randRot, tile.x, tile.y)) {
                        safeTiles.add(new tileSafe(tile, randRot));
                    } else {
                        for (int i = 0; i < 4; i++) {
                            int rot = Mathf.mod(randRot + i, 4);
                            Tile newTile = nearbyTile(rot);
                            if (front3(rot, newTile.x, newTile.y)) {
                                safeTiles.add(new tileSafe(newTile, rot));
                            }
                        }
                    }
                }*/

                if (safeTiles.size > 0) {
                    int select = Mathf.clamp(Mathf.random(0, safeTiles.size), 0, safeTiles.size - 1);
                    tile = safeTiles.get(select).tile;
                    randRot = safeTiles.get(select).rot;

                    for (var tileOre : safeTiles) {
                        if (tileOre.tile.drop() != null){
                            tile = safeTiles.get(select).tile;
                            randRot = safeTiles.get(select).rot;
                        }
                    }

                    if (rotation != randRot) this.tile.setBlock(block, team, randRot);
                    tile.setBlock(block, team, randRot);
                    if (branchTiles.size > 0){
                        int branch = Mathf.clamp(Mathf.random(0, branchTiles.size), 0, branchTiles.size - 1);
                        branchTiles.get(branch).tile.setBlock(block, team, branchTiles.get(branch).rot);
                    }
                }
            }

            grow = false;
        }

        @Override
        public void updateProximity() {
            proximityTiles.clear();
            Point2[] nearby = Edges.getEdges(size);
            for (Point2 point : nearby) {
                Tile other = Vars.world.tile(this.tile.x + point.x, this.tile.y + point.y);
                if (other != null) {
                    proximityTiles.add(other);
                }
            }

            super.updateProximity();
        }

        public void updateBeat(){

        }

        @Override
        public void update() {
            if (!startBuild) {
                if (source) {
                    priority = 0;
                    beatTimer += delta();
                    if (beatTimer >= 30) {
                        beat = 1.5f;
                        beatTimer = 0;
                        growCord(ClassicBlocks.cord);
                    }
                }

                for(int i = 0; i <proximity.size; ++i) {
                    this.incrementDump(proximity.size);
                    Building other = proximity.get((i) % proximity.size);
                    if (other instanceof NeoplasiaBuilding neoplasiaBuilding) {
                        if (neoplasiaBuilding.beat >= 1.2f && !source && !alreadyBeat) {
                            if (neoplasiaBuilding.isSource()) priority = 10;
                            ready = true;
                            grow = true;
                        }
                    }
                }

                if (ready && !alreadyBeat) {
                    if (beatTimer >= 2) {
                        if (priority > 0) priority -= 1;
                        updateBeat();
                        beatTimer = 0;
                        ready = false;
                        alreadyBeat = true;
                        beat = 1.5f;
                    }
                }

                if (alreadyBeat) {
                    if (beatTimer >= 20) {
                        alreadyBeat = false;
                        beatTimer = 0;
                    }
                }
                if (ready || alreadyBeat && !source) beatTimer += delta();


                if (beat > 1.05f) {
                    beat = Mathf.lerpDelta(beat, 1f, 0.1f);
                } else {
                    if (beat > 1) beat = 1;
                }

            } else {
                if ((this.tile.floor().attributes.get(Attribute.steam) >= 1 || tile.drop() != null) && this instanceof Cord.CordBuild) {
                    if (isCord && tile.drop() != null) coverOre(ClassicBlocks.neoplasiaDrill, ClassicBlocks.cord);
                    if (isCord && this.tile.floor().attributes.get(Attribute.steam) >= 1) coverVent(ClassicBlocks.heart, ClassicBlocks.cord);
                }
                if (!initalize) {
                    beat = (float) -block.size / (block.size + 1.25f);
                    initalize = true;
                } else {
                    beat = Mathf.lerpDelta(beat, 1f, 0.1f);
                    if (beat >= 0.95f) {
                        beat = 1f;
                        startBuild = false;
                    }
                }
            }
        }

        public class tileSafe {
            Tile tile;
            int rot;

            public tileSafe(Tile tile, int rot){
                this.tile = tile;
                this.rot = rot;
            }

            @Override
            public String toString() {
                return "TILE : " + tile +
                       " | ROT : " + rot;
            }
        }
    }
}
