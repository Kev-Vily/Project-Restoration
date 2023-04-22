package classicMod.library.bullets;


import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import classicMod.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;


public class TeslaOrbType extends BulletType {
    /** Array of the listed target **/
    public @Nullable Teamc[] ArrayTarget;
    /** How fast is the timer **/
    public float timeSpeedup;
    /** Array of the listed target's position **/
    public @Nullable Vec2[] ArrayVec2;
    /** Maximum hits before despawning **/
    public int hitCap;

    // Temporary Values
    protected int l = 0;
    protected float moveScl = 0;
    protected Teamc target;

    /**
     * Creates a Tesla orb that jumps other enemy's unit/block.
     * @param range The maximum range that the arc can jump to other team's unit/block.
     * @param damage Damage per tick
     * @param maxHits Maximum hits before despawning immediately.
     * @param timerSpeed How fast is the lifetime
     **/
    public TeslaOrbType(float range, int damage, int maxHits, float timerSpeed){
        this.damage = damage;
        this.range = range;
        hitEffect = ExtendedFx.laserhit;
        drawSize = 200f;
        hitCap = maxHits;
        moveScl = 0;
        this.lifetime = 60f;
        this.timeSpeedup = timerSpeed;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        moveScl = Mathf.lerpDelta(moveScl, 1f, timeSpeedup);
        if(l >= hitCap*2) { //Allows to detect whether if the bullet hit count has reached maximum peak.
            //l = 0;
            ArrayTarget = null;
            ArrayVec2 = null;
            b.type.collides = false;
            //b.time = b.lifetime + 1f;
        }
        if(b.time < lifetime) {
            autoTarget(b);
            b.type.pierce = true;
            b.type.pierceCap = Integer.MAX_VALUE;
            if (ArrayTarget != null) for (Teamc target : ArrayTarget) {
                float x = target.getX();
                float y = target.getY();
                this.ArrayVec2 = new Vec2[]{new Vec2(x, y)};
                l++;
            }
        }
    }

    /** AutoTargets the nearest enemy unit/block while keeping track on a listed array, this could be saved on {@link #ArrayTarget} **/
    public void autoTarget(Bullet b){ //from Prog-mats
        //moveScl = Mathf.lerpDelta(moveScl, 1f, timeSpeedup);
        this.target = Units.closestTarget(b.team, b.x, b.y, range * b.fout(),
                e -> e.isValid() && e.checkTarget(collidesAir, collidesGround) && !b.collided.contains(e.id),
                t -> t.isValid() && collidesGround && !b.collided.contains(t.id));
        if( target != null ) {
            this.ArrayTarget = new Teamc[]{this.target};
        }
    }

    @Override
    public void draw(Bullet b) { //TODO make multi target version
        Draw.color(Color.white);
        Vec2 lastVec = new Vec2(b.x, b.y);
        float g = 0.1f;
        Draw.alpha(1f-b.time/lifetime);
        if(ArrayVec2 != null) for (Vec2 vec2 : ArrayVec2){
            Drawf.light(lastVec.x, lastVec.y, vec2.x, vec2.y);
            Drawf.line(Color.white, lastVec.x, lastVec.y, vec2.x, vec2.y);
            Draw.rect(Core.atlas.find("restored-mind-circle"), vec2.x, vec2.y);
            b.set(vec2);
            b.vel = new Vec2();
            if(lastVec!=vec2) lastVec = vec2;
        }

       //Draw.reset();

        /*if(points.size == 0) return;

        float range = 1f;

        Vec2 previous = vector.set(x, y);

        for(Vec2 enemy : points){


            float x1 = previous.x + Mathf.range(range),
                    y1 = previous.y + Mathf.range(range),
                    x2 = target.x + Mathf.range(range),
                    y2 = enemy.y + Mathf.range(range);

            Draw.color(Color.white);
            Draw.alpha(1f-life/lifetime);

            Lines.stroke(3f - life/lifetime*2f);
            Lines.line(x1, y1, x2, y2);

            float rad = 7f - life/lifetime*5f;

            Draw.rect("circle", x2, y2, rad, rad);

            if(previous.epsilonEquals(x, y, 0.001f)){
                Draw.rect("circle", x, y, rad, rad);
            }

            //Draw.color(Color.WHITE);

            //Draw.stroke(2f - life/lifetime*2f);
            //Draw.line(x1, y1, x2, y2);

            Draw.reset();

            previous = enemy;
        }*/
    }
}
