package net.mcbbs.client.main;

import java.io.IOException;

/**
 * @author yinyangshi InitAuther97
 */
public class Launcher {
    public static void main(String[] args) throws IOException {
        /*int port = 9233;
        SocketServer ss = new SocketServer(port, "desktop-");
        MutableProcessorPipeline<String> mutable = new MutableProcessorPipeline<>();
        mutable.register(ChainProcessorFactory.newSimpleDataUser(String.class, new SingleArgumentRunnable<String>() {
            private String string;

            @Override
            public String argument() {
                return string;
            }

            @Override
            public void setArgument(String arg) {
                string = arg;
            }

            @Override
            public void runWithArg(String arg) {
                CommandDispatcher.dispatch(arg);
            }
        }));
        ss.addPipe("default", mutable.asImmutable());
        ss.enablePipe("default");
        ss.addThrowableProcessor((IExceptionProcessor) Throwable::printStackTrace);
        new Thread(ss).start();*/

    }
}
