package classicMod.library.bullets;


import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import classicMod.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;


public class TeslaOrbType extends BulletType { //MIXED VERSION betweem PointBullet and LaserBullet
    //private Array<Vector2> points = new Array<>();
    //private ObjectSet<Enemy> hit = new ObjectSet<>();
    protected @Nullable Teamc[] ArrayTarget;
    protected int moveTimes;
    protected int limitedMoves;
    protected @Nullable Vec2[] ArrayVec2;

    public TeslaOrbType(float range, int damage, int limitedMove){
        this.damage = damage;
        this.range = range;
        this.limitedMoves = limitedMove;
        hitEffect = ExtendedFx.laserhit;
        drawSize = 200f;
        this.lifetime = 30f*60f;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        autoTarget(b);
        //b.keepAlive = true;
        b.type.pierce = true;
        b.type.pierceCap = limitedMoves;
        if (ArrayTarget != null) for (Teamc target : ArrayTarget) {
            float x = target.getX();
            float y = target.getY();
            ArrayVec2 = new Vec2[]{new Vec2(x, y)};
        }
        //if(moveTimes >= limitedMoves) removed(b);
    }

    public void autoTarget(Bullet b){ //from Prog-mats
        Teamc target;
        target = Units.closestTarget(b.team, b.x, b.y, range * b.fout(),
                e -> e.isValid() && e.checkTarget(collidesAir, collidesGround) && !b.collided.contains(e.id),
                t -> t.isValid() && collidesGround && !b.collided.contains(t.id));
        if( target != null ) {
            ArrayTarget = new Teamc[]{target};
        } else {
            removed(b);
        }
    }

    @Override
    public void draw(Bullet b) { //TODO make multi target version

        Draw.color(Color.white);
        Draw.alpha(1f - ((float)moveTimes/limitedMoves));
        Vec2 lastVec = new Vec2(b.x, b.y);
        if(ArrayVec2 != null) for (Vec2 vec2 : ArrayVec2){
            Drawf.line(Color.white, lastVec.x, lastVec.y, vec2.x, vec2.y);
            Draw.rect(Core.atlas.find("restored-mind-circle"), vec2.x, vec2.y);
            b.set(vec2);
            b.vel = new Vec2();
            if(lastVec!=vec2) lastVec = vec2;
            moveTimes++;
        }
        //Drawf.laser(Core.atlas.white(), Core.atlas.find("restored-mind-circle"), b.x, b.y, b.aimX, b.aimY, 3f - Mathf.absin(Time.delta, lifetime*2f));

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
