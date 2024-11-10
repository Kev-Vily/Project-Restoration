package classicMod.content;

import arc.struct.Seq;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives.*;
import mindustry.type.ItemStack;

import static classicMod.content.ClassicBlocks.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.SectorPresets.*;
import static mindustry.content.SectorPresets.craters;
import static mindustry.content.TechTree.*;

public class ExtendedSerpuloTechTree {
    static TechTree.TechNode context = null;

    public static void load() {

        // Power
        margeNode(Blocks.rtgGenerator, () -> {
            node(ClassicBlocks.rtgGenerator);
        });

        margeNode(combustionGenerator, () -> {
            node(shineGenerator);
            node(draugFactory, () -> {
                node(spiritFactory, () -> {
                    node(phantomFactory);
                });

                node(daggerFactory, () -> {
                    node(commandCenter, () -> {});
                    node(crawlerFactory, () -> {
                        node(titanFactory, () -> {
                            node(fortressFactory, () -> {

                            });
                        });
                    });

                    node(wraithFactory, () -> {
                        node(chromeWraithFactory);
                        node(ghoulFactory, () -> {
                            node(revenantFactory, () -> {

                            });
                        });
                    });
                });
            });

            node(dartPad, () -> {
                node(deltaPad, () -> {
                    node(javelinPad, () -> {
                        node(tridentPad, () -> {
                            node(glaivePad, () -> {
                                node(halberdPad);
                            });
                        });
                    });

                    node(tauPad, () -> {
                        node(electraPad);
                        node(omegaPad, () -> {

                        });
                    });
                });
            });
        });

        // Turrets
        margeNode(ripple, () -> {
            node(rippleb41);
            node(fuseMKI, () -> {
                node(fuseMKII);
            });
        });

        margeNode(scatter, () -> {
            node(scatterSilo);
        });

        margeNode(cyclone, () -> {
            //node(cycloneb57);
            node(chainTurret);
        });

        margeNode(arc,
                () -> node(arcAir,
                        () -> node(teslaTurret, Seq.with(new SectorComplete(nuclearComplex)))
                )
        );

        // Transportation
        margeNode(phaseConveyor, () -> {
            node(laserConveyor,() -> {

            });
        });

        margeNode(massDriver, () -> {
            node(warpGate,() -> {

            });
            /*node(payloadPropulsionTower,() -> {

            });*/
        });

        margeNode(armoredConveyor, () -> {
            node(electrumConveyor);
        });

        // Productions
        margeNode(plastaniumCompressor, () -> {
            node(advanceCompressor);
        });

        margeNode(graphitePress, () -> {
            node(denseSmelter, Seq.with(new Research(Items.coal)), () -> {
                node(crucible);
                node(arcSmelter, Seq.with(new Research(combustionGenerator)), () -> {
                    node(electrumForge, Seq.with(
                            new Research(Items.titanium),
                            new Research(ClassicItems.goldPowder)
                            )
                    );
                });
            });
        });

        margeNode(phaseWeaver, () -> {
            node(fusion);
        });

        // Drills
        margeNode(pneumaticDrill, () -> {
            node(stoneSeparator, Seq.with(
                    new OnSector(ruinousShores),
                    new Research(Items.coal),
                    new Research(ClassicItems.stone)
            ), () -> {
                node(stoneMelter, () -> {
                    node(stoneFormer);
                });
                node(centrifuge, Seq.with(new SectorComplete(ruinousShores)));
            });

            node(poweredDrill, () -> {
                node(electrumDrill, Seq.with(new SectorComplete(nuclearComplex)), () -> {

                });
            });
        });

        margeNode(blastDrill, () -> {
            node(areaExtractor);
        });

        //wall
        margeNode(copperWallLarge, () -> {
            node(insulatorWall, () -> {
                node(insulatorWallLarge);
                node(wallDense, () -> {
                    node(wallDenseLarge);

                });
            });

        });

        margeNode(titaniumWall, () -> {
            node(wallDirium, () -> {
                node(wallDiriumLarge);
                node(wallComposite, () -> {});
            });
        });

        margeNode(titaniumWallLarge, () -> {
            node(wallShieldedTitanium);
        });

        //Items
        margeNode(Items.lead, () -> {
            nodeProduce(ClassicItems.denseAlloy, () -> {

            });
        });

        margeNode(Items.sand, () -> {
            nodeProduce(ClassicItems.goldPowder);
            nodeProduce(ClassicItems.stone, () -> {

            });
        });

        margeNode(Items.titanium, () -> {
            nodeProduce(ClassicItems.silver);
            nodeProduce(ClassicItems.electrum, () -> {

            });
        });

        //Batterys
        margeNode(battery, () -> {
            node(batteryMedium);
        });

        //Pumps
        margeNode(rotaryPump, () -> {
            node(thermalPump);
        });

        margeNode(vault, () -> {
            node(massRepository, () -> {
                node(electrumVault);
            });
        });

        // Launchpad
        margeNode(planetaryTerminal, () -> {
            nodePlanetary(onset, ClassicBlocks.interplanetaryAccelerator, () -> {});
        });

        margeNode(launchPad, () -> {
            node(launchPadLarge, () -> {
                node(ClassicBlocks.interplanetaryAccelerator, Seq.with(new SectorComplete(planetaryTerminal))); //Endgame bois
            });
        });

    }

    private static void margeNode(UnlockableContent parent, Runnable children){ //from betamindy!
        context = TechTree.all.find(t -> t.content == parent);
        children.run();
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives, Runnable children){
        TechNode node = new TechNode(context, content, requirements);
        if(objectives != null) node.objectives = objectives;

        TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Runnable children){
        node(content, requirements, null, children);
    }

    private static void node(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives, children);
    }

    private static void node(UnlockableContent content, Runnable children){
        node(content, content.researchRequirements(), children);
    }

    private static void node(UnlockableContent content, Seq<Objective> objectives){
        node(content, content.researchRequirements(), objectives, () -> {});
    }

    private static void node(UnlockableContent block){
        node(block, () -> {});
    }

    private static void nodeProduce(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives.add(new Produce(content)), children);
    }

    private static void nodeProduce(UnlockableContent content, Runnable children){
        nodeProduce(content, Seq.with(), children);
    }

    private static void nodeProduce(UnlockableContent content){
        nodeProduce(content, Seq.with(), () -> {});
    }

    private static void nodePlanetary(UnlockableContent content, UnlockableContent thisOne, Runnable children){
            node(content, Seq.with(new VisualObjectives.LaunchedPlanetaryAccelerator(thisOne)), children);
    }
}
