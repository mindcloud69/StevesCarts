package vswe.stevescarts.plugins;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vswe.stevescarts.StevesCarts;
import vswe.stevescarts.api.ISCPlugin;
import vswe.stevescarts.api.SCLoadingPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by modmuss50 on 15/11/16.
 */
public class PluginLoader {

    static List<ISCPlugin> pluginList;

    public static APIHelper apiHelper;

    public static void preInit(FMLPreInitializationEvent event){
        StevesCarts.logger.info("Loading plguins");
        pluginList = new ArrayList<>();
        ASMDataTable asmDataTable = event.getAsmData();
        Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(SCLoadingPlugin.class.getCanonicalName());
        for(ASMDataTable.ASMData asmData : asmDataSet){
            StevesCarts.logger.info("Found plugin candidate:" + asmData.getClassName());
            if(asmData.getAnnotationInfo().size() != 0){
                String modId = (String) asmData.getAnnotationInfo().get("dependentMod");
                if(!Loader.isModLoaded(modId)){
                    StevesCarts.logger.info("Plugin was NOT loaded due to mod '" + modId + "' missing, this isn't an error");
                    continue;
                }
            }
            try {
                Object object = Class.forName(asmData.getClassName()).newInstance();
                if(object instanceof ISCPlugin){
                    pluginList.add((ISCPlugin) object);
                    StevesCarts.logger.info("Plugin " + asmData.getClassName() + " was found and has been initialized successfully!");
                } else {
                    StevesCarts.logger.error("Plugin as it not an instanceof ISCPlugin, please contact the mod author of " + asmData.getClassName());
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                StevesCarts.logger.error("Plugin was not loaded due to an error, please contact the mod author of " + asmData.getClassName());
                e.printStackTrace();

            }
        }
        apiHelper = new APIHelper();
        for(ISCPlugin plugin : pluginList){
            plugin.loadAddons(apiHelper);
        }
        StevesCarts.logger.info("Loaded " + pluginList.size() +" plguins");
    }
}
