package DummyCore.Core;

import static DummyCore.Core.CoreInitialiser.mcVersion;
import static DummyCore.Core.CoreInitialiser.modid;
import static DummyCore.Core.CoreInitialiser.modname;
import static DummyCore.Core.CoreInitialiser.version;

import DummyCore.Utils.CommandTransfer;
import DummyCore.Utils.DummyConfig;
import DummyCore.Utils.DummyDataUtils;
import DummyCore.Utils.DummyEventHandler;
import DummyCore.Utils.DummyPacketHandler;
import DummyCore.Utils.DummyPacketIMSG;
import DummyCore.Utils.DummyPacketIMSG_Tile;
import DummyCore.Utils.DummyTilePacketHandler;
import DummyCore.Utils.LoadingUtils;
import DummyCore.Utils.MiscUtils;
import DummyCore.Utils.ModVersionChecker;
import DummyCore.Utils.NetProxy_Server;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import java.util.Arrays;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author Modbder
 * @version From DummyCore 1.0
 */
@Mod(modid = modid, name = modname, version = version, useMetadata = false, acceptedMinecraftVersions = mcVersion)
public class CoreInitialiser {

    public static final String modid = "DummyCore";
    public static final String modname = "DummyCore";
    public static final String mcVersion = "1.7.10";
    public static final String version = "GRADLETOKEN_VERSION";

    public static CoreInitialiser instance;
    public static DummyConfig cfg = new DummyConfig();
    public static SimpleNetworkWrapper network;

    @SidedProxy(clientSide = "DummyCore.Utils.NetProxy_Client", serverSide = "DummyCore.Utils.NetProxy_Server")
    public static NetProxy_Server proxy;

    public static final DummyPacketHandler packetHandler = new DummyPacketHandler();

    public static void initMetadata(ModMetadata meta) {
        meta.autogenerated = false;
        meta.modId = modid;
        meta.name = modname;
        meta.version = version;
        meta.credits = "Modbder";
        meta.authorList = Arrays.asList(
                new String[] {"Dummy Thinking Team", "Modbder", "TheDen2099", "MrDangerDen", "TheMysticDark"});
        meta.description = "Dummy Core is a required package to launch mods made by Dummy Thinking team.";
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {

        initMetadata(e.getModMetadata());
        Core.registerModAbsolute(
                getClass(), "DummyCore", e.getModConfigurationDirectory().getAbsolutePath(), cfg, false);

        if (instance == null) instance = this;
        network = NetworkRegistry.INSTANCE.newSimpleChannel("DummyCore");
        network.registerMessage(DummyPacketHandler.class, DummyPacketIMSG.class, 0, Side.SERVER);
        network.registerMessage(DummyPacketHandler.class, DummyPacketIMSG.class, 0, Side.CLIENT);
        network.registerMessage(DummyTilePacketHandler.class, DummyPacketIMSG_Tile.class, 1, Side.SERVER);
        network.registerMessage(DummyTilePacketHandler.class, DummyPacketIMSG_Tile.class, 1, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(new DummyEventHandler());
        MinecraftForge.EVENT_BUS.register(new DummyDataUtils());
        FMLCommonHandler.instance().bus().register(new DummyEventHandler());

        proxy.registerInfo();

        if (Loader.isModLoaded(modid)) LoadingUtils.knownBigASMModifiers.add("DummyCore");
        if (Loader.isModLoaded("DragonAPI"))
            LoadingUtils.knownBigASMModifiers.add(
                    "DragonAPI"); // <- I have nothing against Reika, I like his mods, I just like to point out that
        // DragonAPI adds quite a lot of ASM hooks
        if (Loader.isModLoaded("Optifine") || Loader.isModLoaded("optifine"))
            LoadingUtils.knownBigASMModifiers.add("Optifine");
        if (Loader.isModLoaded("CoFHCore")) LoadingUtils.knownBigASMModifiers.add("CoFHCore");
        if (Loader.isModLoaded("easycoloredlights")) LoadingUtils.knownBigASMModifiers.add("Easy Colored Lights");
        if (Loader.isModLoaded("thaumicinfusion")) LoadingUtils.knownBigASMModifiers.add("Thaumic Infusion");
        if (MiscUtils.classExists("api.player.forge.PlayerAPIPlugin"))
            LoadingUtils.knownBigASMModifiers.add("Player API");
        if (MiscUtils.classExists("cofh.tweak.CoFHTweaks")) LoadingUtils.knownBigASMModifiers.add("CoFH Tweaks");
        if (Loader.isModLoaded("CoFHCore")) LoadingUtils.knownBigASMModifiers.add("CoFHCore");
        if (MiscUtils.classExists("codechicken.core.asm.CodeChickenCoreModContainer"))
            LoadingUtils.knownBigASMModifiers.add("CodeChickenCore");

        FMLCommonHandler.instance().registerCrashCallable(new DCCrashCallable());
        ModVersionChecker.addRequest(getClass(), "https://www.dropbox.com/s/iwdfv0mc4qns00f/DummyCoreVersion.txt?dl=1");
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        proxy.registerInit();
    }

    @EventHandler
    public void onServerStop(FMLServerStoppedEvent e) {
        DummyDataUtils.stop();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        MinecraftServer mcserver = event.getServer();
        ((CommandHandler) mcserver.getCommandManager()).registerCommand(new CommandTransfer());
    }

    public static void fmlLogMissingTextures() {
        proxy.removeMissingTextureErrors();
    }
}
