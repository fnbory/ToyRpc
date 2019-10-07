package common.context;

import common.domain.RpcResponse;
import config.ServiceConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: fnbory
 * @Date: 2019/10/7 16:59
 */
public class RPCThreadSharedContext {
    private static final ConcurrentHashMap<String, CompletableFuture<RpcResponse>> RESPONSES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ServiceConfig<?>> HANDLER_MAP = new ConcurrentHashMap<>();

    public static void registerResponseFuture(String requestId, CompletableFuture<RpcResponse> future) {
        RESPONSES.put(requestId, future);
    }

    public static CompletableFuture<RpcResponse> getAndRemoveResponseFuture(String requestId) {
        return RESPONSES.remove(requestId);
    }

    public static void registerHandler(String name, ServiceConfig serviceConfig) {
        HANDLER_MAP.put(name,
                serviceConfig);
    }

    public static ServiceConfig getAndRemoveHandler(String name) {
        return HANDLER_MAP.remove(name);
    }
}
