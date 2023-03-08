package classicMod.content;

import arc.struct.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.type.*;

import static classicMod.content.ClassicBlocks.*;
import static mindustry.content.Blocks.*;

public class ExtendedErekirTechTree {
    static TechTree.TechNode context = null;

    public static void load() {
        margeNode(breach, () -> {
            node(fracture, () -> {});
            node(horde, () -> {});
            node(ClassicBlocks.barrierProjector, Seq.with(new Objectives.Research(shieldedWall)), () -> {
                node(ClassicBlocks.shieldProjector, () -> {
                    node(ClassicBlocks.largeShieldProjector, () -> {});
                });
                node(ClassicBlocks.shieldBreaker, Seq.with(new Objectives.Research(ClassicBlocks.shieldProjector)), () -> {});
            });
        });
    }


    private static void margeNode(UnlockableContent parent, Runnable children){ //from betamindy!
        context = TechTree.all.find(t -> t.content == parent);
        children.run();
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Runnable children){
        TechTree.TechNode node = new TechTree.TechNode(context, content, requirements);
        if(objectives != null) node.objectives = objectives;

        TechTree.TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Runnable children){
        node(content, requirements, null, children);
    }

    private static void node(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives, children);
    }

    private static void node(UnlockableContent content, Runnable children){
        node(content, content.researchRequirements(), children);
    }

    private static void node(UnlockableContent block){
        node(block, () -> {});
    }

    private static void nodeProduce(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives.add(new Objectives.Produce(content)), children);
    }

    private static void nodeProduce(UnlockableContent content, Runnable children){
        nodeProduce(content, Seq.with(), children);
    }

    private static void nodeProduce(UnlockableContent content){
        nodeProduce(content, Seq.with(), () -> {});
    }
}
