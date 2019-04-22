package net.mcbbs.client.util.network.processor;

import net.mcbbs.client.util.network.processor.user.IDataUser;
import net.mcbbs.client.util.thread.SingleArgumentRunnable;

public final class ChainProcessorFactory {
    public static <A> IDataUser<A> newSimpleDataUser(Class<A> aClass, SingleArgumentRunnable<A> runnable) {
        //fixme: all is error/warn!
        return new IDataUser<>() {
            @Override
            public void onReceived(Object data) {

            }

            @Override
            public void onReceived(A data) {
                runnable.runWithArg(data);
            }

            @Override
            public Class<A> getInputType() {
                return aClass;
            }

            @Override
            public Class<A> getOutputType() {
                return aClass;
            }

            @Override
            public void setValue(Object value) {
            }

            @Override
            public A value() {
                return null;
            }

            @Override
            public Object procChain(Object o) {
                return null;
            }

            @Override
            public A procChain(A o) {
                onReceived(o);
                return o;
            }
        };
    }
}
