package DummyCore.Core;

import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import DummyCore.Utils.CommandTransfer;
import DummyCore.Utils.DummyConfig;
import DummyCore.Utils.DummyDataUtils;
import DummyCore.Utils.DummyEventHandler;
import DummyCore.Utils.DummyPacketHandler;
import DummyCore.Utils.DummyPacketIMSG;
import DummyCore.Utils.DummyPacketIMSG_Tile;
import DummyCore.Utils.DummyTilePacketHandler;
import DummyCore.Utils.NetProxy_Server;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * @author Modbder
 * @version From DummyCore 1.0
 */
@Mod(
        modid = CoreInitialiser.modid,
        name = CoreInitialiser.modname,
        version = CoreInitialiser.version,
        useMetadata = false,
        acceptedMinecraftVersions = "[1.7.10]")
public class CoreInitialiser {

    public static final String modid = "DummyCore";
    public static final String modname = "DummyCore";
    public static final String version = Tags.VERSION;

    public static final Logger logger = LogManager.getLogger(modid);

    public static CoreInitialiser instance;
    public static DummyConfig cfg = new DummyConfig();
    public static SimpleNetworkWrapper network;

    @SidedProxy(clientSide = "DummyCore.Utils.NetProxy_Client", serverSide = "DummyCore.Utils.NetProxy_Server")
    public static NetProxy_Server proxy;

    public static final DummyPacketHandler packetHandler = new DummyPacketHandler();

    public static boolean ENDLESS_IDS_LOADED = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {

        if (instance == null) instance = this;
        network = NetworkRegistry.INSTANCE.newSimpleChannel("DummyCore");
        network.registerMessage(DummyPacketHandler.class, DummyPacketIMSG.class, 0, Side.SERVER);
        network.registerMessage(DummyPacketHandler.class, DummyPacketIMSG.class, 0, Side.CLIENT);
        network.registerMessage(DummyTilePacketHandler.class, DummyPacketIMSG_Tile.class, 1, Side.SERVER);
        network.registerMessage(DummyTilePacketHandler.class, DummyPacketIMSG_Tile.class, 1, Side.CLIENT);
        try {
            Core.registerModAbsolute(getClass(), "DummyCore", e.getModConfigurationDirectory().getAbsolutePath(), cfg);
        } catch (Exception ex) {
            logger.error("Unable to register DummyCore", ex);
            throw new RuntimeException(ex);
        }
        MinecraftForge.EVENT_BUS.register(new DummyEventHandler());
        MinecraftForge.EVENT_BUS.register(new DummyDataUtils());
        FMLCommonHandler.instance().bus().register(new DummyEventHandler());

        ENDLESS_IDS_LOADED = Loader.isModLoaded("endlessids");

        proxy.registerInfo();
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
}
