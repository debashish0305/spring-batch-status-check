# spring-batch-status-check

@Bean
public ExecutorService traceableExecutor() {
    ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();

    return new ExecutorService() {
        @Override
        public void execute(Runnable command) {
            virtualExecutor.execute(wrapWithMdc(command));
        }

        // Implement all other ExecutorService methods delegating to virtualExecutor
        // For brevity, just delegate methods:

        @Override
        public void shutdown() {
            virtualExecutor.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return virtualExecutor.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return virtualExecutor.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return virtualExecutor.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return virtualExecutor.awaitTermination(timeout, unit);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return virtualExecutor.submit(wrapWithMdc(task));
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return virtualExecutor.submit(wrapWithMdc(task), result);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return virtualExecutor.submit(wrapWithMdc(task));
        }

        // Other invokeAll, invokeAny methods delegate similarly...

    };
}

private Runnable wrapWithMdc(Runnable runnable) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return () -> {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
        try {
            runnable.run();
        } finally {
            MDC.clear();
        }
    };
}

private <T> Callable<T> wrapWithMdc(Callable<T> callable) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return () -> {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
        try {
            return callable.call();
        } finally {
            MDC.clear();
        }
    };
}
