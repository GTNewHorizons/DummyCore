package DummyCore.Core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.io.HexDump;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import DummyCore.Blocks.BlocksRegistry;
import DummyCore.Client.GuiMainMenuVanilla;
import DummyCore.Client.MainMenuRegistry;
import DummyCore.Items.ItemRegistry;
import DummyCore.Utils.ColoredLightHandler;
import DummyCore.Utils.CommandTransfer;
import DummyCore.Utils.DummyConfig;
import DummyCore.Utils.DummyDataUtils;
import DummyCore.Utils.DummyEventHandler;
import DummyCore.Utils.DummyPacketHandler;
import DummyCore.Utils.DummyPacketIMSG;
import DummyCore.Utils.DummyPacketIMSG_Tile;
import DummyCore.Utils.DummyTilePacketHandler;
import DummyCore.Utils.EnumLightColor;
import DummyCore.Utils.MathUtils;
import DummyCore.Utils.NetProxy_Server;
import DummyCore.Utils.Notifier;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * @author Modbder
 * @version From DummyCore 1.0
 */
@Mod(modid = "DummyCore", name = "DummyCore", version = "1.10", useMetadata = false)
public class CoreInitialiser{
	public static CoreInitialiser instance;
	public static DummyConfig cfg = new DummyConfig();
	public static SimpleNetworkWrapper network;
	@SidedProxy(clientSide = "DummyCore.Utils.NetProxy_Client",serverSide = "DummyCore.Utils.NetProxy_Server")
	public static NetProxy_Server proxy;
	@Metadata(value="DummyCore")
	public static ModMetadata meta = new ModMetadata()
	{
        String modId  = "DummyCore";
        String name  = "DummyCore";
        String version = "1.10";
        String credits = "Modbder";
        List<String> authorList  = Arrays.asList(new String[] {
                "Dummy Thinking Team; Modbder; TheDen2099"
            });
        String description  ="Dummy Core is a required package to launch mods made by Dummy Thinking team.";
	};
	
	public static final DummyPacketHandler packetHandler = new DummyPacketHandler();

	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		e.getModMetadata().modId  = "DummyCore";
		e.getModMetadata().name="Dummy Core";
		e.getModMetadata().version = "1.10";
		e.getModMetadata().credits = "Modbder";
		e.getModMetadata().authorList= Arrays.asList(new String[] {
	            "Dummy Thinking Team; Modbder; TheDen2099"
	        });
		e.getModMetadata().description="Dummy Core is a required package to launch mods made by Dummy Thinking team.";
		if(instance == null) instance = this;
		network = NetworkRegistry.INSTANCE.newSimpleChannel("DummyCore");
	    network.registerMessage(DummyPacketHandler.class, DummyPacketIMSG.class, 0, Side.SERVER);
	    network.registerMessage(DummyPacketHandler.class, DummyPacketIMSG.class, 0, Side.CLIENT);
	    network.registerMessage(DummyTilePacketHandler.class, DummyPacketIMSG_Tile.class, 1, Side.SERVER);
	    network.registerMessage(DummyTilePacketHandler.class, DummyPacketIMSG_Tile.class, 1, Side.CLIENT);
		for(int i = 0; i < 16; ++i)
		{
			Core.lightColors.add(EnumLightColor.values()[i]);
		}
		try
		{
		Core.registerModAbsolute(getClass(), "DummyCore", e.getModConfigurationDirectory().getAbsolutePath(),cfg);
		}catch(Exception ex)
		{
			System.out.println("Oh, come on Forge. If something has got wrong here - this is it.");
			System.out.println("No, reriously, if an exception is bbeing thrown here - nothing is going to load");
			System.out.println("Like, even the gam itself, since an exception here means File System error.");
			System.out.println("And a file system error here -> impossible to create literally any file in directory!");
			System.out.println("But when I'm trying to set a System.exit(-1) here - oww, not allowed?");
			System.out.println("Fine, I can work without it... Reflection time!");
			try
			{
				Class system = System.class;
				Method exit = system.getMethod("exit", int.class);
				exit.invoke(null, -1);
			}catch(Exception exc)
			{
				exc.printStackTrace();
				return;
			}
		}
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		
		MinecraftForge.EVENT_BUS.register(new DummyEventHandler());
		MinecraftForge.EVENT_BUS.register(new DummyDataUtils());
		FMLCommonHandler.instance().bus().register(new DummyEventHandler());
		
		proxy.registerInfo();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.registerInit();
	}
	
	@Deprecated
	@EventHandler
	public void onServerStart(FMLServerAboutToStartEvent e)
	{
		//DummyDataUtils.load(e);
	}
	
	@EventHandler
	public void onServerStop(FMLServerStoppedEvent e)
	{
		DummyDataUtils.stop();
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event)
    {
        MinecraftServer mcserver = event.getServer();
        ((CommandHandler)mcserver.getCommandManager()).registerCommand(new CommandTransfer());
    }
}
