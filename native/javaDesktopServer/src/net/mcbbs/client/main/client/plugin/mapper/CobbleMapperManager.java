package net.mcbbs.client.main.client.plugin.mapper;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.inject.Singleton;
import net.mcbbs.client.api.plugin.IPlugin;
import net.mcbbs.client.api.plugin.mapper.Mapper;
import net.mcbbs.client.api.plugin.mapper.MapperManager;
import net.mcbbs.client.util.InvocationHandlerFactory;

@Singleton
public class CobbleMapperManager implements MapperManager {

    Multimap<Class<?>, Mapper<?>> mappers = MultimapBuilder.hashKeys().hashSetValues().build();

    @Override
    public <I extends IPlugin, T> Mapper<T> createMapper(Class<I> plugin, String name, InvocationHandlerFactory factory) {
        Mapper<T> mapper = CobbleMapper.createInstance(factory, name);
        if (!mappers.put(plugin, mapper)) throw new UnsupportedOperationException("Already registered Mapper!");
        return mapper;
    }

    @Override
    public <I extends IPlugin, T> Mapper<T> getMapper(Class<I> plugin, String name) {
        //noinspection unchecked
        return (Mapper<T>) mappers.get(plugin).stream().filter(mapper -> mapper.name().contentEquals(name)).findAny().orElseThrow(() -> new UnsupportedOperationException("Unable to find mapper:".concat(name)));
    }
}