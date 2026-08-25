// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class boss_magma_colossus<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "boss_magma_colossus"), "main");
	private final ModelPart bb_main;

	public boss_magma_colossus(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -24.0F, -8.0F, 18.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.0F, -26.0F, -6.0F, 14.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(8.0F, -22.0F, -5.0F, 1.0F, 12.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-9.0F, -22.0F, -5.0F, 1.0F, 12.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, -21.0F, 4.0F, 10.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -21.0F, 6.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, -28.0F, 2.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.0F, -31.0F, 3.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.5F, -30.0F, -1.5F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.5F, -34.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, -28.0F, -4.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.0F, -31.0F, -3.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.5F, -25.0F, -7.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.0F, -22.0F, -9.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -18.0F, -13.0F, 6.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, -14.5F, -17.0F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.0F, -12.0F, -21.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.0F, -18.0F, -12.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, -26.0F, 6.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, -24.0F, 12.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, -19.0F, 8.0F, 8.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.0F, -20.0F, 9.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -20.0F, 9.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.5F, -21.0F, 13.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(1.5F, -21.0F, 13.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.0F, -26.0F, 3.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.0F, -30.0F, 4.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.5F, -33.0F, 5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(4.0F, -26.0F, 3.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.0F, -30.0F, 4.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.5F, -33.0F, 5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, -24.0F, 5.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.0F, -24.0F, 5.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-11.0F, -20.0F, 2.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.0F, -22.0F, 4.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.0F, -20.0F, 2.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(6.0F, -22.0F, 4.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-12.0F, -18.0F, -9.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-11.0F, -20.0F, -7.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(6.0F, -18.0F, -9.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(7.0F, -20.0F, -7.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-9.5F, -20.0F, 3.0F, 5.0F, 8.0F, 4.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-9.0F, -12.0F, 3.5F, 4.0F, 6.0F, 3.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(4.5F, -20.0F, 3.0F, 5.0F, 8.0F, 4.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.0F, -12.0F, 3.5F, 4.0F, 6.0F, 3.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.5F, -17.0F, -9.0F, 5.0F, 7.0F, 4.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.0F, -10.0F, -8.5F, 4.0F, 5.0F, 3.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.5F, -17.0F, -9.0F, 5.0F, 7.0F, 4.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(6.0F, -10.0F, -8.5F, 4.0F, 5.0F, 3.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, -8.0F, 6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.5F, -8.5F, 6.0F, 1.0F, 2.5F, 2.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-9.0F, -8.0F, 6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(8.0F, -8.0F, 6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(6.5F, -8.5F, 6.0F, 1.0F, 2.5F, 2.5F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.0F, -8.0F, 6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.0F, -7.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.5F, -7.5F, -6.0F, 1.0F, 2.5F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.0F, -7.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(9.0F, -7.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(7.5F, -7.5F, -6.0F, 1.0F, 2.5F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(6.0F, -7.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}