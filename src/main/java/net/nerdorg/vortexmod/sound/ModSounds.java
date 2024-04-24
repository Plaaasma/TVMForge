package net.nerdorg.vortexmod.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdorg.vortexmod.VortexMod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, VortexMod.MODID);

    public static final RegistryObject<SoundEvent> THROTTLE_SOUND = registerSoundEvents("throttle_sound");
    public static final RegistryObject<SoundEvent> DESIGNATOR_SWITCH_SOUND = registerSoundEvents("coordpanel_switch");
    public static final RegistryObject<SoundEvent> DESIGNATOR_BUTTON_SOUND = registerSoundEvents("coordpanel_button");

    private static RegistryObject<SoundEvent> registerSoundEvents(String sound) {
        return SOUND_EVENTS.register(sound, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(VortexMod.MODID, sound)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
