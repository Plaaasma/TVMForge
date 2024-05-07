package net.nerdorg.vortexmod.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.entities.custom.DematEffectEntity;
import net.nerdorg.vortexmod.entities.custom.RematEffectEntity;
import net.nerdorg.vortexmod.entities.custom.VortexPortalEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VortexMod.MODID);

    public static final RegistryObject<EntityType<VortexPortalEntity>> VORTEX_PORTAL =
            ENTITY_TYPES.register("vortex_portal", () -> EntityType.Builder.of(VortexPortalEntity::new, MobCategory.CREATURE)
                    .sized(1f, 1f)
                    .build("vortex_portal"));

    public static final RegistryObject<EntityType<DematEffectEntity>> DEMAT_ENTITY =
            ENTITY_TYPES.register("demat_effect", () -> EntityType.Builder.of(DematEffectEntity::new, MobCategory.CREATURE)
                    .sized(1f, 1f)
                    .build("remat_effect"));

    public static final RegistryObject<EntityType<RematEffectEntity>> REMAT_ENTITY =
            ENTITY_TYPES.register("remat_effect", () -> EntityType.Builder.of(RematEffectEntity::new, MobCategory.CREATURE)
                    .sized(1f, 1f)
                    .build("remat_effect"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
