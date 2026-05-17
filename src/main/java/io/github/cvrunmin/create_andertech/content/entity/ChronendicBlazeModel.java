package io.github.cvrunmin.create_andertech.content.entity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;

public class ChronendicBlazeModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart corePos;
    private final ModelPart core;
    private final ModelPart[] lowerRods;
    private final ModelPart[] lowerRodsTop;
    private final ModelPart[] lowerRodsBottom;
    private final ModelPart[] middleRods;
    private final ModelPart[] middleRodsTop;
    private final ModelPart[] middleRodsBottom;
    private final ModelPart[] upperRods;
    private final ModelPart[] upperRodsTop;
    private final ModelPart[] upperRodsBottom;

    public ChronendicBlazeModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.corePos = root.getChild("corePos");
        this.core = this.corePos.getChild("core");
        this.lowerRods = new ModelPart[3];
        this.lowerRodsTop = new ModelPart[3];
        this.lowerRodsBottom = new ModelPart[3];
        this.middleRods = new ModelPart[4];
        this.middleRodsTop = new ModelPart[4];
        this.middleRodsBottom = new ModelPart[4];
        this.upperRods = new ModelPart[4];
        this.upperRodsTop = new ModelPart[4];
        this.upperRodsBottom = new ModelPart[4];
        Arrays.setAll(this.lowerRods, idx -> this.root.getChild("lowerRod%d".formatted(idx + 1)));
        Arrays.setAll(this.lowerRodsTop, idx -> this.lowerRods[idx].getChild("topRod"));
        Arrays.setAll(this.lowerRodsBottom, idx -> this.lowerRods[idx].getChild("bottomRod"));
        Arrays.setAll(this.middleRods, idx -> this.root.getChild("middleRod%d".formatted(idx + 1)));
        Arrays.setAll(this.middleRodsTop, idx -> this.middleRods[idx].getChild("topRod"));
        Arrays.setAll(this.middleRodsBottom, idx -> this.middleRods[idx].getChild("bottomRod"));
        Arrays.setAll(this.upperRods, idx -> this.root.getChild("upperRod%d".formatted(idx + 1)));
        Arrays.setAll(this.upperRodsTop, idx -> this.upperRods[idx].getChild("topRod"));
        Arrays.setAll(this.upperRodsBottom, idx -> this.upperRods[idx].getChild("bottomRod"));
    }

    @Override
    public ModelPart root() {
        return root;
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);

        PartDefinition corePos = partdefinition.addOrReplaceChild("corePos", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));
        corePos.addOrReplaceChild("core", CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        var emptyBuilder = CubeListBuilder.create();
        var topRodBuilder = CubeListBuilder.create().texOffs(0, 16)
                .addBox(-1f, -2.0F, -1f, 2.0F, 4.0F, 2.0F);
        var botRodBuilder = CubeListBuilder.create().texOffs(8, 16)
                .addBox(-1f, -2.0F, -1f, 2.0F, 4.0F, 2.0F);

        for (int i = 0; i < 3; i++) {
            var rodParent = partdefinition.addOrReplaceChild("lowerRod%d".formatted(i + 1), emptyBuilder, PartPose.offset((float) Math.sin(2 * Math.PI / 3 * i) * 4, 20, (float) Math.cos(2 * Math.PI / 3 * i) * 4));
            rodParent.addOrReplaceChild("topRod", topRodBuilder, PartPose.offset(0, -2f, 0));
            rodParent.addOrReplaceChild("bottomRod", botRodBuilder, PartPose.offset(0, 2f, 0));
        }

        for (int i = 0; i < 4; i++) {
            var rodParent = partdefinition.addOrReplaceChild("middleRod%d".formatted(i + 1), emptyBuilder, PartPose.offset((float) Math.sin(2 * Math.PI / 4 * i) * 7, 10, (float) Math.cos(2 * Math.PI / 4 * i) * 7));
            rodParent.addOrReplaceChild("topRod", topRodBuilder, PartPose.offset(0, -2f, 0));
            rodParent.addOrReplaceChild("bottomRod", botRodBuilder, PartPose.offset(0, 2f, 0));
        }

        var l = 7 * Math.sqrt(2);
        for (int i = 0; i < 4; i++) {
            float x = (float) (Math.sin(Math.PI / 4 * (2 * i + 1)) * l);
            float z = (float) (Math.cos(Math.PI / 4 * (2 * i + 1)) * l);
            var rodParent = partdefinition.addOrReplaceChild("upperRod%d".formatted(i + 1), emptyBuilder, PartPose.offset(x, 2, z));
            rodParent.addOrReplaceChild("topRod", topRodBuilder, PartPose.offset(0, -2f, 0));
            rodParent.addOrReplaceChild("bottomRod", botRodBuilder, PartPose.offset(0, 2f, 0));
        }
        
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        if(entity instanceof ChronendicBlaze blaze){
            var coreRot = blaze.coreRot;
            var coreRotA = new Vector3f(blaze.coreRotA);
            var coreRotV = new Vector3f(blaze.coreRotV);
            coreRotV = coreRotV.add(coreRotA.mul(0.05f * partialTick));
            coreRot = coreRot.add(new Quaternionf().fromAxisAngleRad(coreRotV.normalize(new Vector3f()), coreRotV.length()), new Quaternionf()).normalize();
            var coreRotZYX = coreRot.getEulerAnglesZYX(new Vector3f());
            this.core.setRotation(coreRotZYX.x, coreRotZYX.y, coreRotZYX.z);
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        var t = ageInTicks * Math.PI * 0.1f;
        var tickInCycle = ageInTicks % 100;
        float f = 1;
        if(tickInCycle >= 80){
            if(tickInCycle < 85){
                var t1 = (tickInCycle - 80) / 5.0f;
                f = 1 * t1 + (1 - t1) * 5;
            } else if (tickInCycle >= 95) {
                var t1 = (tickInCycle - 95) / 5.0f;
                f = 5 * t1 + (1 - t1) * 1;
            } else{
                f = 5;
            }
        }
        float rodF = 0.2f;

        double topRodY = -2 - 0.5f * (Math.cos(4 * Math.PI * t * rodF) - 1);
        double bottomRodY = 2 + 0.5f * (Math.cos(4 * Math.PI * t * rodF) - 1);
        for (int i = 0; i < 3; i++) {
            double a = 2 * Math.PI / 3 * i + t * f;
            float sa = (float) Math.sin(a);
            float ca = (float) Math.cos(a);
            this.lowerRods[i].x = sa * 4;
            this.lowerRods[i].z = ca * 4;
            var rotAxis = new Vector3f(-ca, 0, sa);
            var e1 = new Quaternionf().rotateAxis((float) (2 * Math.PI * t * rodF), rotAxis).getEulerAnglesZYX(new Vector3f());
            var e2 = new Quaternionf().rotateAxis((float) -(2 * Math.PI * t * rodF), rotAxis).getEulerAnglesZYX(new Vector3f());
            this.lowerRodsTop[i].setRotation(e1.x, e1.y, e1.z);
            this.lowerRodsBottom[i].setRotation(e2.x, e2.y, e2.z);
            this.lowerRodsTop[i].y = (float) topRodY;
            this.lowerRodsBottom[i].y = (float) bottomRodY;
        }

        for (int i = 0; i < 4; i++) {
            double a = 2 * Math.PI / 4 * i + t * f;
            float sa = (float) Math.sin(a);
            float ca = (float) Math.cos(a);
            this.middleRods[i].x = sa * 7;
            this.middleRods[i].z = ca * 7;
            var rotAxis = new Vector3f(-ca, 0, sa);
            var e1 = new Quaternionf().rotateAxis((float) (2 * Math.PI * t * rodF), rotAxis).getEulerAnglesZYX(new Vector3f());
            var e2 = new Quaternionf().rotateAxis((float) -(2 * Math.PI * t * rodF), rotAxis).getEulerAnglesZYX(new Vector3f());
            this.middleRodsTop[i].setRotation(e1.x, e1.y, e1.z);
            this.middleRodsBottom[i].setRotation(e2.x, e2.y, e2.z);
            this.middleRodsTop[i].y = (float) topRodY;
            this.middleRodsBottom[i].y = (float) bottomRodY;
        }

        float l = (float) (7 * Math.sqrt(2));
        for (int i = 0; i < 4; i++) {
            double a = Math.PI / 4 * (2 * i + 1) + t * f;
            float sa = (float) Math.sin(a);
            float ca = (float) Math.cos(a);
            this.upperRods[i].x = sa * l;
            this.upperRods[i].z = ca * l;
            var rotAxis = new Vector3f(-ca, 0, sa);
            var e1 = new Quaternionf().rotateAxis((float) (2 * Math.PI * t * rodF), rotAxis).getEulerAnglesZYX(new Vector3f());
            var e2 = new Quaternionf().rotateAxis((float) -(2 * Math.PI * t * rodF), rotAxis).getEulerAnglesZYX(new Vector3f());
            this.upperRodsTop[i].setRotation(e1.x, e1.y, e1.z);
            this.upperRodsBottom[i].setRotation(e2.x, e2.y, e2.z);
            this.upperRodsTop[i].y = (float) topRodY;
            this.upperRodsBottom[i].y = (float) bottomRodY;
        }

        this.head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        this.head.xRot = headPitch * (float) (Math.PI / 180.0);

    }
}
