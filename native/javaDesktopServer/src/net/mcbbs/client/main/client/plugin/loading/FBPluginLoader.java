package net.mcbbs.client.main.client.plugin.loading;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import net.mcbbs.client.api.plugin.BoxedPlugin;
import net.mcbbs.client.api.plugin.Client;
import net.mcbbs.client.api.plugin.IPlugin;
import net.mcbbs.client.api.plugin.mapper.Mapper;
import net.mcbbs.client.api.plugin.meta.PluginMetadata;
import net.mcbbs.client.api.plugin.service.ServiceManager;
import net.mcbbs.client.main.client.plugin.service.CobbleServiceManager;
import org.yaml.snakeyaml.Yaml;

import javax.naming.Name;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Only able to be ran in the same thread as Launcher
 */
public class FBPluginLoader extends PluginLoader {
    protected final List<JarFile> plugins = Lists.newArrayList();
    protected final Map<String,JarFile> pluginJar = Maps.newHashMap();
    protected final Map<String,BoxedPlugin<? extends IPlugin>> pluginBoxed = Maps.newHashMap();
    protected final ClassLoader thread_classloader = Thread.currentThread().getContextClassLoader();
    protected final Injector injector = Guice.createInjector((Module) binder ->{
        binder.bind(ServiceManager.class).annotatedWith(Names.named("service_manager")).toInstance(new CobbleServiceManager());
        binder.bind(List.class).annotatedWith(Names.named("plugin_list")).toInstance(new ArrayList<>(pluginBoxed.values()));

        binder.requestStaticInjection(Client.class);
    });
    private URLClassLoader final_classpathLoader;
    protected ScriptEngine js_engine;
    @Override
    public JarFile getPluginJar(String pluginId) {
        return null;
    }

    @Override
    public BoxedPlugin<? extends IPlugin> getPlugin(String pluginId) {
        return null;
    }

    @Override
    protected void loadPlugin(String baseLocation) {
        try {
            Stream<File> files = Files.walk(Paths.get(baseLocation)).map(Path::toFile);
            plugins.addAll(files.map(file-> {
                try {
                    return new JarFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList()));
            Map<PluginMetadata, IPlugin> plugins = Maps.newHashMap();
            files.filter(File::isFile)
                    .filter(file -> file.toPath().getFileName().endsWith(".jar"))
                    .forEach(file-> {
                try {
                    initializeNashorn();
                    loadPlugin(plugins,file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeNashorn() throws ScriptException {
        js_engine = new ScriptEngineManager().getEngineByName("nashorn");
        js_engine.eval(new InputStreamReader(getClass().getResourceAsStream("assets/mcbbsclient/main/config.js")));
    }

    private void loadPlugin(Map<PluginMetadata, IPlugin> plugins, File f) throws Exception {
        JarFile file = new JarFile(f);
        URLClassLoader ucl = new URLClassLoader(new URL[]{f.toURI().toURL()},Thread.currentThread().getContextClassLoader());
        String mainClassLocation;
        PluginMetadata meta;
        if(ucl.getResource("plugin.js")!=null) {
            Bindings bindings = js_engine.createBindings();
            js_engine.eval(new InputStreamReader(file.getInputStream(file.getJarEntry("config.js"))), bindings);
            meta = PluginMetadata.deserializeFrom(this, bindings);
            mainClassLocation = (String) bindings.get("plugin");
        }else if(ucl.getResource("plugin.yml")!=null){
            InputStream is = ucl.getResourceAsStream("plugin.yml");
            Yaml yaml = new Yaml();
            Map<String,Object> bindings = yaml.loadAs(is,Map.class);
            meta = PluginMetadata.deserializeFrom(this,bindings);
            mainClassLocation = (String) bindings.get("plugin");
        }else{
            System.out.println("Unable to instantiate plugin '"+file.getName()+"'.It may be a non-plugin file.Injecting into classpath....");
            return;
        }
        Class<? extends IPlugin> pluginClz = ucl.loadClass(mainClassLocation).asSubclass(IPlugin.class);
        IPlugin instance = pluginClz.getConstructor().newInstance();
        plugins.put(meta, instance);
        pluginJar.put(meta.id,file);
        
    }
}