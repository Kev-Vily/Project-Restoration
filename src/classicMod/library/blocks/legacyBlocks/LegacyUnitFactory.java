package classicMod.library.blocks.legacyBlocks;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.UnitTetherBlock;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.*;

import static mindustry.Vars.state;

//Created by: VvSeanGtvV#2295 at Discord!
//Similar to modern unit factory but in an older style mechanics, where it doesn't need payload!

public class LegacyUnitFactory extends Block {
    public UnitType unitType;
    public float produceTime = 60f;
    public float launchVelocity = 0f;
    public TextureRegion topRegion;
    public int maxSpawn = 8; //Default by 4
    public int originMax = maxSpawn;
    public int[] capacities = {};
    public ItemStack[] requirement; //Requirements for the unit

    public LegacyUnitFactory(String name){
        super(name);
        update = true;
        hasPower = true;
        hasItems = true;
        solid = false;
        flags = EnumSet.of(BlockFlag.factory);
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void setStats(){
        stats.remove(Stat.itemCapacity);
        stats.add(Stat.productionTime, produceTime/60f, StatUnit.seconds);
        stats.add(Stat.maxUnits, maxSpawn, StatUnit.none);
        //stats.add(Stat.output, unitType.localizedName);
        stats.add(Stat.output, table -> {
            table.table(Styles.none, t -> {
                if(unitType.isBanned()){
                    t.image(Icon.cancel).color(Pal.remove).size(32);
                    t.add(unitType.localizedName).left().pad(10f);
                    return;
                }

                t.image(unitType.uiIcon).size(32).pad(2.5f).left().scaling(Scaling.fit);
                t.add(unitType.localizedName).left().pad(10f);
            });
        });

        super.setStats();
    }


    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (LegacyUnitFactory.LegacyUnitFactoryBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));
        addBar("units", (LegacyUnitFactory.LegacyUnitFactoryBuild e) -> new Bar(Core.bundle.format("bar.unitcap", Fonts.getUnicodeStr(unitType.name), e.units.size, originMax), Pal.command, e::fractionUnitCap));
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-top")};
    }

    @Override
    public void init(){
        if(maxSpawn < 1 || maxSpawn == 0){ maxSpawn = 4; }
        topRegion = Core.atlas.find(name + "-top");
        if(requirement != null){
            if(unitType == null && (requirement.length > 0)) new UnitFactory.UnitPlan(unitType, 60f * 15, requirement);
            capacities = new int[Vars.content.items().size];
            for(ItemStack stack : requirement){
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
        
                consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));
            }
            consumeItems(requirement);
        }
        originMax = maxSpawn;

        super.init();
    }

    @Override
    public void onUnlock() {
        super.onUnlock();

        if(state.isCampaign()){
            unitType.unlock();
        }
    }

    public class LegacyUnitFactoryBuild extends Building implements UnitTetherBlock {
        public float progress, time, speedScl;
        public int FactoryunitCap;
        public int readUnitId = -1;
        public IntSeq unitIDs = new IntSeq();
        public float buildTime = produceTime;
        public @Nullable Unit unit;
        public Seq<Unit> units = new Seq<>();

        public float fraction(){ return progress / buildTime; }
        public float fractionUnitCap(){ return (float)units.size / (FactoryunitCap); }

        public void updateListUnits(){
            for (int i = 0; i < units.size; i++){
                Unit u = units.get(i);
                if(!(!u.dead && u.isValid() && (u.team == this.team))) units.remove(i);
            }
        }

        @Override
        public void updateTile(){

            if (unitType != null && unitType.useUnitCap) unitType.useUnitCap = false;
            if(!unitIDs.isEmpty()){
                units.clear();
                unitIDs.each(i -> {
                    var unit = Groups.unit.getByID(i);
                    if(unit != null){
                        if(!unit.dead && unit.isValid() && (unit.team == this.team)) units.add(unit);
                    }
                });
                unitIDs.clear();
            }

            if(team == state.rules.waveTeam && !Vars.state.rules.pvp) FactoryunitCap = Integer.MAX_VALUE;
            if(team == state.rules.defaultTeam) FactoryunitCap = maxSpawn;

            if(efficiency > 0 && !(units.size >= FactoryunitCap)){
                time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
                //unit.ammo(unit.type().ammoCapacity * fraction());
                //ambientSound.at(unit);
            }else{
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
                updateListUnits();
            }

            if(progress >= buildTime) {
                progress %= 1f;

                consume();

                LegacyUnitFactory factory = (LegacyUnitFactory) tile.block();
                unit = factory.unitType.create(team);
                if (unit instanceof BuildingTetherc bt) {
                    bt.building(this);
                }
                unit.set(this);
                unit.rotation(90f);
                unit.add();
                unit.vel.y = launchVelocity;
                Fx.producesmoke.at(this);
                Effect.shake(4f * 1.5f, 5f, this);
                Events.fire(new EventType.UnitCreateEvent(unit, this));
                units.add(unit);

                //Call.unitBlockSpawn(this.tile);
                //Unit unit = factory.unitType.create(team);

                //Events.fire(new UnitSpawnEvent(unit));
            }
        }

        @Override
        public void onRemoved() {
            super.onRemoved();

            for(var unit : units){
                unit.remove();
            }
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();

            for(var unit : units){
                unit.dead(true);
            }
        }

        public void spawned(int id){
            Fx.spawn.at(x, y);
            progress = 0f;
            if(Vars.net.client()){
                readUnitId = id;
            }
        }

        @Override
        public boolean shouldConsume(){
            if((float)units.size > FactoryunitCap) return false;
            return enabled && ((float)units.size < FactoryunitCap);
        }

        @Override
        public void draw(){
            super.draw();
            TextureRegion region = Core.atlas.find(name);
            Draw.rect(region, tile.drawx(), tile.drawy());
            TextureRegion unitRegion  = unitType.fullIcon;

            Draw.draw(Layer.blockOver, () -> {
                Shaders.build.region = unitRegion;
                Shaders.build.progress = fraction();
                Shaders.build.color.set(Pal.accent);
                Shaders.build.color.a = speedScl;
                Shaders.build.time = -time / 20f;

                Draw.shader(Shaders.build, true);
                Draw.rect(unitRegion, tile.drawx(), tile.drawy());
                Draw.shader();
            });

            Draw.color(Pal.accent);
            Draw.alpha(speedScl);

            Lines.lineAngleCenter(tile.drawx() + Mathf.sin(time, 20f, Vars.tilesize / 2f * size - 2f), tile.drawy(), 90, size * Vars.tilesize - 4f);

            Draw.reset();

            Draw.rect(topRegion, tile.drawx(), tile.drawy());
        }

        @Override
        public void write(Writes stream){
            super.write(stream);
            stream.f(progress);
            stream.b(units.size);
            for(var unit : units){
                if(!unit.dead && unit.isValid() && (unit.team == this.team)) stream.i(unit.id);
            }
            //stream.i(unit == null ? -1 : unit.id);
        }

        @Override
        public void read(Reads stream, byte revision){
            super.read(stream, revision);
            progress = stream.f();
            //FactoryunitCap = stream.i();
            int count = stream.b();
            unitIDs.clear();
            for(int i = 0; i < count; i++){
                unitIDs.add(stream.i());
            }
        }
    }
}
